package com.dede.android_eggs.cat_editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.cat_editor.Utilities.toColorOrNull
import com.dede.basic.copy

@Composable
internal fun CatSvgCodeDialog(
    visibleState: MutableState<Boolean>,
    cat: Cat,
    svg: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
) {
    var visible by visibleState
    if (!visible) {
        return
    }

    val context = LocalContext.current
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val svgFileName = stringResource(R.string.default_cat_name, cat.seed) + ".svg"
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        modifier = modifier,
        title = {
            Text(
                text = svgFileName,
                style = typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .background(colorScheme.surfaceContainerHighest)
                    .clip(shapes.large)
                    .border(1.dp, colorScheme.outline, shapes.large),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = rememberCatPainter(cat),
                        contentDescription = null,
                        modifier = Modifier.size(width = 34.dp, height = 34.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { context.copy(svg) }) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = stringResource(android.R.string.copy)
                        )
                    }
                }

                val text = buildXmlAnnotatedString(
                    xml = svg,
                    highlightColor = colorScheme.primary,
                    attributeNameColor = Utilities.blendColor(
                        colorScheme.tertiary,
                        colorScheme.surface,
                        0.3f
                    ),
                    attributeColor = colorScheme.tertiary
                )
                Text(
                    style = typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    text = text,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState)
                        .padding(bottom = 6.dp)
                        .padding(horizontal = 12.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { visible = false }) {
                Text(text = stringResource(android.R.string.ok))
            }
        }
    )
}

private fun buildXmlAnnotatedString(
    xml: String,
    highlightColor: Color,
    attributeNameColor: Color,
    attributeColor: Color,
    xmlTags: Set<String> = setOf("svg", "path"),
    colorRenderChar: Char = '■', // fill='#FF0000' will be rendered as fill='■#FF0000'
    transparentColorRenderChar: Char = '□'
): AnnotatedString {
    // https://stackoverflow.com/questions/27834463/android-java-regex-named-groups/27834803#27834803
    val colorAttributeRegex =
        //Regex("(?<name>[\\w_-]+)=[\"'](?<color>#\\w{3,8})[\"']")
        Regex("([\\w_-]+)=[\"'](#\\w{3,8})[\"']")
    var offset = 0
    val xmlBuilder = StringBuilder(xml)
    colorAttributeRegex.findAll(xml).forEach {
        val gColor = it.groups[2]// color
        val gName = it.groups[1]// name
        if (gColor == null || gName == null) {
            return@forEach
        }
        val color = gColor.value.toColorOrNull()
        if (color != null) {
            // fill='#FFFF0000' will be rendered as fill='■#FFFF0000'
            // fill='#00FF0000' will be rendered as fill='□#00FF0000'
            val char = if (color.alpha == 0f) transparentColorRenderChar else colorRenderChar
            val index = gName.range.last + 1 + offset + 2// length of "='"
            xmlBuilder.insert(index, char)
            offset += 1 // for the added character
        }
    }

    val modifiedXml = xmlBuilder.toString()

    val tagsRegexValue = xmlTags.joinToString(prefix = "(", separator = "|", postfix = ")")
    val tagStartRegex = Regex("<$tagsRegexValue?")
    val tagEndRegex = Regex("/?$tagsRegexValue?>")
    val attributeRegex =
        //Regex("(?<name>[\\w_-]+)(?<attrSign>=[\"'](?<color>$colorRenderChar?)(?<value>[^'\"]*)['\"])")
        Regex("([\\w_-]+)(=[\"']($colorRenderChar?)([^'\"]*)['\"])")

    fun AnnotatedString.Builder.addStyle(style: SpanStyle, range: IntRange) {
        addStyle(style, start = range.first, end = range.last + 1)
    }

    return buildAnnotatedString {
        append(modifiedXml)

        val tagStyle = SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)
        tagStartRegex.findAll(modifiedXml).forEach {
            addStyle(tagStyle, it.range)
        }
        tagEndRegex.findAll(modifiedXml).forEach {
            addStyle(tagStyle, it.range)
        }

        val attrNameStyle = SpanStyle(color = attributeNameColor)
        val attrSignStyle = SpanStyle(color = attributeColor)
        val attrValueStyle = SpanStyle(fontWeight = FontWeight.Medium)
        attributeRegex.findAll(modifiedXml).forEach {
            val gName = it.groups[1]// name
            if (gName != null) {
                addStyle(attrNameStyle, gName.range)
            }
            val gAttrSign = it.groups[2]// attrSign
            if (gAttrSign != null) {
                addStyle(attrSignStyle, gAttrSign.range)
            }
            val gValue = it.groups[3]// value
            if (gValue != null) {
                addStyle(attrValueStyle, gValue.range)

                val gColor = it.groups[4]// color
                if (gColor != null) {
                    val color = gValue.value.toColorOrNull()
                    if (color != null && color.alpha > 0f) {
                        addStyle(SpanStyle(color = color), gColor.range)
                    }
                }
            }
        }
    }
}
