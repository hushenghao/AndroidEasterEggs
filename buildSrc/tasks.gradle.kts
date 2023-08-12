import com.dede.easter_eggs.EmojiSvg2XmlTask

task<EmojiSvg2XmlTask>("emojiSvg2Xml") {
    xmlOutputDir = File(rootDir, "eggs/T/res/drawable-anydpi")
    xmlFileNamePrefix = "t_"
}