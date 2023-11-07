package com.github.amirkatzster.inlaygwdemo.listeners

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.util.Disposer

/** Each file opened creates its own Editor. */
class InlayDemoEditorFactoryListener : EditorFactoryListener {

    override fun editorCreated(event: EditorFactoryEvent) {
        thisLogger().warn("InlayDemoEditorFactoryListener")
        // TODO: do not register listeners on files that are not Java/Kotlin/etc.
        val editor = event.editor

        val editorDisposable = Disposer.newDisposable("InlayDemoEditorListener")
        EditorUtil.disposeWithEditor(editor, editorDisposable)
       // editor.caretModel.addCaretListener(InlayDemoCaretListener(), editorDisposable)
       editor.document.addDocumentListener(InlayDemoDocumentListener(editor), editorDisposable)
       // editor.selectionModel.addSelectionListener(InlayDemoSelectionListener(), editorDisposable)
    }

}