package com.dede.easter_eggs

import com.android.ide.common.vectordrawable.Svg2Vector
import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

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

    private lateinit var httpClient: OkHttpClient

    init {
        if (!::svgOutputDir.isInitialized) {
            svgOutputDir = File(project.buildDir, "xml")
        }
    }

    private fun prepare() {
        httpClient = OkHttpClient.Builder().build()
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
        val request = createHttpRequest(url)
        val response = httpClient.newCall(request).execute()
        val responseBody = response.body
        if (response.code != 200 || responseBody == null) {
            println("Download emoji svg failure: ${response.code} -> $url")
            return null
        }

        val svgFile = File(svgOutputDir, svgFileName)
        responseBody.byteStream().use { input ->
            svgFile.outputStream().use {
                input.copyTo(it)
            }
        }
        return svgFile
    }

    private fun createHttpRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .get()
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