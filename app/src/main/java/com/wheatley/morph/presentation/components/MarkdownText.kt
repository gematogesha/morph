package com.wheatley.morph.presentation.components

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val typography = MaterialTheme.typography
    val textColor = MaterialTheme.colorScheme.onSurface

    val markwon = remember {
        Markwon.builder(context)
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(CoilImagesPlugin.create(context))
            .build()
    }

    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it).apply {
                textSize = typography.bodyLarge.fontSize.value
                setTextColor(textColor.toArgb())
                movementMethod = LinkMovementMethod.getInstance()
                setTextIsSelectable(true)
            }
        },
        update = { tv ->
            markwon.setMarkdown(tv, markdown)
        }
    )
}
