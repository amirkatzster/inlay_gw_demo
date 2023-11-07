package com.github.amirkatzster.inlaygwdemo.listeners

import com.github.amirkatzster.inlaygwdemo.InlayDemoInlayRenderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.JBColor

class InlayDemoDocumentListener(val editor: Editor) : BulkAwareDocumentListener {
    override fun documentChangedNonBulk(event: DocumentEvent) {
        displayInlaySuggestion(editor)
    }

    private fun displayInlaySuggestion(editor: Editor) {
        ApplicationManager.getApplication().invokeLater {
            val suggestion = "inlayDemo"
            val originalDisplayedText = "inlayDisplayDemo"
            val inlayModel = editor.inlayModel
            val offset = editor.caretModel.offset
            val firstNewLine = originalDisplayedText.indexOf("\n")
            val hasNewLine = firstNewLine != -1
            // First line of the suggestion is inline
            val firstLine =
                    if (hasNewLine) originalDisplayedText.substring(0, firstNewLine) else originalDisplayedText
            inlayModel.addInlineElement(
                    offset,
                    false,
                    InlayDemoInlayRenderer(suggestion, originalDisplayedText, firstLine, JBColor.GRAY))
        }
    }
}