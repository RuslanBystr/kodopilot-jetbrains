package com.github.ruslanbystr.kodopilotplugin.actions

import com.github.ruslanbystr.kodopilotplugin.ai.GeminiClient
import com.github.ruslanbystr.kodopilotplugin.settings.KodoPilotSettingsState
import com.google.gson.JsonParser
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

class KodoPilotFixAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val apiKey = KodoPilotSettingsState.instance.apiKey

        if (apiKey.isNullOrBlank()) {
            val notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("KodoPilot Notifications")
                .createNotification("API-key Gemini is not set. Settings → Tools → KodoPilot", NotificationType.WARNING)
            notification.notify(project)
            return
        }

        val document = editor.document
        val caretLine = document.getLineNumber(editor.caretModel.offset)

        // Context: Before 15 lines and after cursor, with lines numbers
        val contextStartLine = maxOf(0, caretLine - 15)
        val contextEndLine = minOf(document.lineCount - 1, caretLine + 15)

        val contextBuilder = StringBuilder()
        for (line in contextStartLine..contextEndLine) {
            val lineStart = document.getLineStartOffset(line)
            val lineEnd = document.getLineEndOffset(line)
            val marker = if (line == caretLine) " <-- CURSOR HERE" else ""
            contextBuilder.append("${line + 1}: ${document.getText(com.intellij.openapi.util.TextRange(lineStart, lineEnd))}$marker\n")
        }

        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val fileName = virtualFile?.name ?: "Unknown"
        val filePath = virtualFile?.path ?: "Unknown"
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val language = psiFile?.language?.id ?: "Unknown"

        val prompt = """
            You are a code assistant embedded in an IDE.
        
            File name: $fileName
            File path: $filePath
            Language: $language
        
            Below is a snippet of code with line numbers.
            The cursor is marked with "<-- CURSOR HERE".
            Find the most likely problem on or near that line and propose a fix.
        
            Respond ONLY with valid JSON, no markdown, no code fences, in this exact format:
            {"startLine": <int>, "endLine": <int>, "explanation": "<short explanation>", "newText": "<replacement code for those lines, without line numbers>"}
        
            Code:
            $contextBuilder
        """.trimIndent()

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "KodoPilot: analyzing", true) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    val client = GeminiClient(apiKey)
                    val rawResponse = client.generateContent(prompt)

                    val cleaned = rawResponse
                        .trim()
                        .removePrefix("```json")
                        .removePrefix("```")
                        .removeSuffix("```")
                        .trim()

                    val json = JsonParser.parseString(cleaned).asJsonObject
                    val startLine = json.get("startLine").asInt - 1
                    val endLine = json.get("endLine").asInt - 1
                    val newText = json.get("newText").asString
                    val explanation = json.get("explanation").asString

                    ApplicationManager.getApplication().invokeLater {
                        applyFix(project, editor.document, startLine, endLine, newText)
                        notify(project, "KodoPilot: $explanation", NotificationType.INFORMATION)
                    }
                } catch (ex: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        notify(project, "Error KodoPilot: ${ex.message}", NotificationType.ERROR)
                    }
                }
            }
        })
    }

    private fun applyFix(
        project: Project,
        document: com.intellij.openapi.editor.Document,
        startLine: Int,
        endLine: Int,
        newText: String
    ) {
        if (startLine < 0 || endLine >= document.lineCount || startLine > endLine) return

        val startOffset = document.getLineStartOffset(startLine)
        val endOffset = document.getLineEndOffset(endLine)

        WriteCommandAction.runWriteCommandAction(project, "KodoPilot Fix", null, {
            document.replaceString(startOffset, endOffset, newText)
        })
    }

    private fun notify(project: Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("KodoPilot Notifications")
            .createNotification(content, type)
            .notify(project)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null && e.getData(CommonDataKeys.EDITOR) != null
    }
}
