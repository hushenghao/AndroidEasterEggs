package com.dede.easter_eggs

import com.android.ide.common.vectordrawable.Svg2Vector
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

/**
 * Download emoji svg and convert to android drawable xml.
 * For Android T Easter Egg.
 *
 * 🤫    \u1f92b    emoji_u1f92b.xml
 * 🐟    \u1f41f    emoji_u1f41f.xml
 *
 * @author shhu
 * @since 2023/8/12
 */
open class EmojiSvg2XmlTask : DefaultTask() {

    companion object {
        private const val EMOJI_SVG_URL =
            "https://github.com/googlefonts/noto-emoji/raw/main/svg/%s"
    }

    @OutputDirectory
    lateinit var xmlOutputDir: File

    @OutputDirectory
    lateinit var svgOutputDir: File

    @Input
    var xmlFileNamePrefix: String? = null

    @Input
    var emojis: List<String> = listOf(
        "🍇", "🍈", "🍉", "🍊", "🍋", "🍌", "🍍", "🥭", "🍎", "🍏", "🍐", "🍑",//
        "🍒", "🍓", "🫐", "🥝",//
        "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾",//
        "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃", "🫠", "😉", "😊",
        "😇", "🥰", "😍", "🤩", "😘", "😗", "☺️", "😚", "😙", "🥲", "😋", "😛", "😜",
        "🤪", "😝", "🤑", "🤗", "🤭", "🫢", "🫣", "🤫", "🤔", "🫡", "🤐", "🤨", "😐",
        "😑", "😶", "🫥", "😏", "😒", "🙄", "😬", "🤥", "😌", "😔", "😪", "🤤", "😴", "😷",//
        "🤩", "😍", "🥰", "😘", "🥳", "🥲", "🥹",//
        "🫠",//
        "💘", "💝", "💖", "💗", "💓", "💞", "💕", "❣", "💔", "❤", "🧡", "💛",//
        "💚", "💙", "💜", "🤎", "🖤", "🤍",//
        "👁", "️🫦", "👁️",//
        "👽", "🛸", "✨", "🌟", "💫", "🚀", "🪐", "🌙", "⭐", "🌍",//
        "🌑", "🌒", "🌓", "🌔", "🌕", "🌖", "🌗", "🌘",//
        "🐙", "🪸", "🦑", "🦀", "🦐", "🐡", "🦞", "🐠", "🐟", "🐳", "🐋", "🐬", "🫧", "🌊", "🦈",//
        "🙈", "🙉", "🙊", "🐵", "🐒",//
        "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓",//
        "🕛", "🕧", "🕐", "🕜", "🕑", "🕝", "🕒", "🕞", "🕓", "🕟", "🕔", "🕠", "🕕", "🕡",//
        "🕖", "🕢", "🕗", "🕣", "🕘", "🕤", "🕙", "🕥", "🕚", "🕦",//
        "🌺", "🌸", "💮", "🏵️", "🌼", "🌿",//
        "🐢", "✨", "🌟", "👑"//
    )

    private lateinit var httpClient: HttpClient

    init {
        if (!::svgOutputDir.isInitialized) {
            svgOutputDir = File(project.file(project.layout.buildDirectory), "xml")
        }
    }

    private fun prepare() {
        httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()
        if (!svgOutputDir.exists()) {
            svgOutputDir.mkdirs()
        }
        if (!xmlOutputDir.exists()) {
            xmlOutputDir.mkdirs()
        }
    }

    @TaskAction
    fun action() {
        prepare()

        var c = 0
        val size = emojis.size
        for (emoji in emojis) {
            println("Start process emoji: $emoji")
            val svgFile = downloadSvg(emoji) ?: continue
            println("Download emoji svg success: $svgFile")

            println("Convert svg 2 xml: $emoji")
            val xmlFile = svg2xml(svgFile)
            println("Convert svg 2 xml success: $xmlFile")
            println("Finish process emoji: $emoji, [${++c} : $size]")
        }
    }

    private fun svg2xml(svgFile: File): File? {
        val prefix = xmlFileNamePrefix ?: ""
        val xmlFile = File(xmlOutputDir, prefix + svgFile.nameWithoutExtension + ".xml")
        val error = Svg2Vector.parseSvgToXml(svgFile.toPath(), xmlFile.outputStream())
        if (!error.isNullOrEmpty()) {
            if (xmlFile.exists()) {
                xmlFile.delete()
            }
            println("Convert svg 2 xml failure: $error")
            return null
        }
        return xmlFile
    }

    private fun downloadSvg(emoji: CharSequence): File? {
        val svgFileName = getEmojiUnicode(
            emoji,
            separator = "_",
            prefix = "emoji_u",
            postfix = ".svg"
        ).toString()
        val url = EMOJI_SVG_URL.format(svgFileName)
        val svgFile = File(svgOutputDir, svgFileName)
        val request = createHttpRequest(url)
        val response = httpClient.send(request, BodyHandlers.ofFile(svgFile.toPath()))
        if (response.statusCode() != 200) {
            println("Download emoji svg failure: ${response.statusCode()} -> $url")
            return null
        }
        return response.body().toFile()
    }

    private fun createHttpRequest(url: String): HttpRequest {
        return HttpRequest.newBuilder(URI.create(url))
            .GET()
            .build()
    }

    /**
     * 计算Emoji的Unicode
     */
    private fun getEmojiUnicode(
        emoji: CharSequence,
        separator: CharSequence = "\\u",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        temp: MutableList<CharSequence>? = null,
    ): CharSequence {
        val list: MutableList<CharSequence> = if (temp != null) {
            temp.clear();temp
        } else ArrayList()
        var offset = 0
        while (offset < emoji.length) {
            val codePoint = Character.codePointAt(emoji, offset)
            offset += Character.charCount(codePoint)
            if (codePoint == 0xFE0F) {
                // the codepoint is a emoji style standardized variation selector
                continue
            }
            list.add("%04x".format(codePoint))
        }
        return list.joinToString(separator = separator, prefix = prefix, postfix = postfix)
    }

}