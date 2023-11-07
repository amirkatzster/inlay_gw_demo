package com.github.amirkatzster.inlaygwdemo;

import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

public class InlayDemoInlayRenderer(val associatedRequestSuggestedText: String,
                                    val associatedRequestDisplayedText: String,
                                    private val displayedText: String,
                                    private val inlayTextColor: JBColor,
                                    ) : EditorCustomElementRenderer {

    private val displayedTextLines: List<String> = displayedText.lines()

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val fm =
                inlay.editor.contentComponent.getFontMetrics(
                        inlay.editor.colorsScheme.getFont(EditorFontType.PLAIN))

        var maxLineWidth = 1
        for (line in displayedTextLines) {
            maxLineWidth = max(maxLineWidth, fm.stringWidth(line))
        }

        return maxLineWidth
    }

    override fun calcHeightInPixels(inlay: Inlay<*>): Int {
        return inlay.editor.lineHeight * displayedTextLines.size
    }

    // the `paint` method gets called repeatedly, so be careful what you put in there
    override fun paint(inlay: Inlay<*>, g: Graphics, r: Rectangle, textAttributes: TextAttributes) {
        var shouldDisplayInlayDrawingBoundaries = false

        // When a dropdown would hide the suggestion, move the Block to where it may be visible
        val isBlock = inlay.placement == Inlay.Placement.BELOW_LINE
        if (isBlock) {
            val lookup = LookupManager.getActiveLookup(inlay.editor)
            if (lookup != null) {
                val lookupBounds = lookup.component.bounds
                val obstructed = r.intersects(lookupBounds)
                if (obstructed) {
                    shouldDisplayInlayDrawingBoundaries = true
                    val editor = inlay.editor
                    val position = editor.visualPositionToXY(editor.caretModel.visualPosition)
                    r.x = position.x + lookupBounds.width
                }
            }
        }

        if (shouldDisplayInlayDrawingBoundaries) {
            g.color = JBColor.BLUE
            g.drawRect(r.x, r.y, r.width, r.height)
        }

        g.font = EditorUtil.getEditorFont()
        val fm = g.fontMetrics
        // "+2" because for some reason the text is displayed slightly too high, and y-axis is reversed
        val ascent = fm.ascent + 2

        val canvas = g.create()
        canvas.clipRect(r.x, r.y, r.width, r.height) // only draw what is within the region
        drawInlineText(r, ascent, canvas, inlay.editor.lineHeight)
        canvas.dispose()
    }

    private fun drawInlineText(
            r: Rectangle,
            ascent: Int,
            canvas: Graphics,
            lineHeight: Int,
    ) {
        canvas.color = inlayTextColor
        var yOffset = r.y + ascent
        for (line in displayedTextLines) {
            canvas.drawString(line, r.x, yOffset)
            yOffset += lineHeight
        }
    }
}
