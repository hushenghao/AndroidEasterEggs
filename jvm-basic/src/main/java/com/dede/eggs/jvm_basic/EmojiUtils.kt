package com.dede.eggs.jvm_basic

object EmojiUtils {

    /**
     * 计算Emoji的Unicode
     */
    fun getEmojiUnicode(
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