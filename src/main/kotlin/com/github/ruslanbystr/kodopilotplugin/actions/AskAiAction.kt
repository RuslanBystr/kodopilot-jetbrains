package com.github.ruslanbystr.kodopilotplugin.actions

import com.github.ruslanbystr.kodopilotplugin.services.AiEditService
import com.github.ruslanbystr.kodopilotplugin.ui.PromptPopup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class AskAiAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val aiEditService = AiEditService(project)

        PromptPopup(project, file.name).show { prompt ->
            aiEditService.edit(
                editor,
                file,
                prompt
            )
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible =
            e.project != null && e.getData(CommonDataKeys.EDITOR) != null
    }
}