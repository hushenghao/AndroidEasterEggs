package com.dede.emoji_svg_xml_convertor

import com.android.ide.common.vectordrawable.Svg2Vector
import com.dede.eggs.jvm_basic.EmojiUtils
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val xmlOutputDir = File("eggs/Tiramisu/res/drawable-anydpi")
    val emojis: Array<String> = arrayOf(
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

    val convertor = EmojiSvg2XmlConvertor(emojis, xmlOutputDir, "t_")
    convertor.convert()
}

class EmojiSvg2XmlConvertor(
    private val emojis: Array<String>,
    private val xmlOutputDir: File,
    private val xmlFileNamePrefix: String? = null
) {

    companion object {
        private const val EMOJI_SVG_URL =
            "https://github.com/googlefonts/noto-emoji/raw/main/svg/%s"
    }

    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    private val svgOutputDir: Path = Files.createTempDirectory("emoji_svg_xml_convertor_")

    fun convert() {
        var c = 0
        val size = emojis.size
        for (emoji in emojis) {
            println("Start convert emoji: $emoji")
            val svgFile = downloadSvg(emoji) ?: continue
            println("Download emoji svg success: $svgFile")

            println("Convert svg 2 xml: $emoji")
            val xmlFile = svg2xml(svgFile)
            println("Convert svg 2 xml success: $xmlFile")
            println("Finish convert emoji: $emoji, [${++c} : $size]")
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
        val svgFileName = EmojiUtils.getEmojiUnicode(
            emoji,
            separator = "_",
            prefix = "emoji_u",
            postfix = ".svg"
        ).toString()
        val url = EMOJI_SVG_URL.format(svgFileName)
        val svgFile = File(svgOutputDir.toFile(), svgFileName)
        val request = createHttpRequest(url)
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(svgFile.toPath()))
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

}