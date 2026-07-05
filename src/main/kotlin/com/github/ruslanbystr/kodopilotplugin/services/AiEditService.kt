package com.github.ruslanbystr.kodopilotplugin.services

import com.github.ruslanbystr.kodopilotplugin.ai.GeminiClient
import com.github.ruslanbystr.kodopilotplugin.settings.KodoPilotSettingsState
import com.google.gson.JsonParser
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile

class AiEditService(
    private val project: Project
) {

    fun fix(
        editor: Editor,
        virtualFile: VirtualFile
    ) {
        val prompt = buildFixPrompt(editor, virtualFile)
        execute(editor, prompt)
    }

    fun edit(
        editor: Editor,
        virtualFile: VirtualFile,
        userPrompt: String
    ) {
        val prompt = buildEditPrompt(editor, virtualFile, userPrompt)
        execute(editor, prompt)
    }

    private fun execute(
        editor: Editor,
        prompt: String
    ) {
        val apiKey = KodoPilotSettingsState.instance.apiKey

        if (apiKey.isBlank()) {
            notify(
                "API key required",
                "Configure your Gemini API key in Settings → Tools → KodoPilot.",
                NotificationType.WARNING
            )
            return
        }

        ProgressManager.getInstance().run(
            object : Task.Backgroundable(project, "KodoPilot", true) {

                override fun run(indicator: ProgressIndicator) {
                    try {
                        val response = GeminiClient(apiKey)
                            .generateContent(prompt)

                        val cleaned = response
                            .trim()
                            .removePrefix("```json")
                            .removePrefix("```")
                            .removeSuffix("```")
                            .trim()

                        val json = JsonParser.parseString(cleaned).asJsonObject

                        val startLine = json["startLine"].asInt - 1
                        val endLine = json["endLine"].asInt - 1
                        val newText = json["newText"].asString
                        val explanation = json["explanation"].asString

                        ApplicationManager.getApplication().invokeLater {
                            applyFix(
                                editor,
                                startLine,
                                endLine,
                                newText
                            )

                            notify(
                                "KodoPilot completed",
                                explanation,
                                NotificationType.INFORMATION
                            )
                        }
                    } catch (e: Exception) {
                        ApplicationManager.getApplication().invokeLater {
                            notify(
                                "KodoPilot failed",
                                e.message ?: "Unknown error",
                                NotificationType.ERROR
                            )
                        }
                    }
                }
            }
        )
    }

    private fun buildFixPrompt(
        editor: Editor,
        virtualFile: VirtualFile
    ): String {

        val document = editor.document
        val caretLine = document.getLineNumber(editor.caretModel.offset)

        val start = maxOf(0, caretLine - 15)
        val end = minOf(document.lineCount - 1, caretLine + 15)

        val context = StringBuilder()

        for (line in start..end) {
            val lineStart = document.getLineStartOffset(line)
            val lineEnd = document.getLineEndOffset(line)

            val marker =
                if (line == caretLine) " <-- CURSOR HERE" else ""

            context.append(
                "${line + 1}: ${
                    document.getText(TextRange(lineStart, lineEnd))
                }$marker\n"
            )
        }

        return """
You are an AI code assistant.

Find the most likely problem near the cursor and fix it.

File: ${virtualFile.name}

Return ONLY JSON:

{
  "startLine": int,
  "endLine": int,
  "explanation": "...",
  "newText": "..."
}

Code:

$context
        """.trimIndent()
    }

    private fun buildEditPrompt(
        editor: Editor,
        virtualFile: VirtualFile,
        userPrompt: String
    ): String {

        return """
You are an AI code editor integrated into an IDE.

Your task is to modify the current file according to the user's request.

## User request
$userPrompt

## File
Name: ${virtualFile.name}

## Current file content
${editor.document.text}

## Instructions
- Make only the changes required to satisfy the user's request.
- Preserve the existing coding style, formatting, naming conventions, and architecture whenever possible.
- Do not modify unrelated code.
- Do not add explanations, markdown, or code fences.
- Return only valid JSON.

## Response format
{
  "startLine": <1-based start line>,
  "endLine": <1-based end line>,
  "explanation": "<one short sentence describing the change>",
  "newText": "<replacement code for the specified line range>"
}
""".trimIndent()
    }

    private fun applyFix(
        editor: Editor,
        startLine: Int,
        endLine: Int,
        newText: String
    ) {

        val document = editor.document

        if (
            startLine < 0 ||
            endLine >= document.lineCount ||
            startLine > endLine
        ) {
            return
        }

        val startOffset = document.getLineStartOffset(startLine)
        val endOffset = document.getLineEndOffset(endLine)

        WriteCommandAction.runWriteCommandAction(project) {
            document.replaceString(
                startOffset,
                endOffset,
                newText
            )
        }
    }

    private fun notify(
        title: String,
        content: String,
        type: NotificationType
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("KodoPilot Notifications")
            .createNotification(title, content, type)
            .notify(project)
    }
}
