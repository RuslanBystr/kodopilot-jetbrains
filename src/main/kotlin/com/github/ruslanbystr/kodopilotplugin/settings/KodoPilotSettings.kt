package com.github.ruslanbystr.kodopilotplugin.settings

import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class KodoPilotSettings {

    private val apiKeyField = JBPasswordField()

    val panel: DialogPanel = panel {
        row("Gemini API Key:") {
            cell(apiKeyField)
                .comment("You can get key here https://aistudio.google.com/apikey")
                .align(com.intellij.ui.dsl.builder.AlignX.FILL)
        }
    }

    fun getPreferredFocusedComponent(): JComponent = apiKeyField

    var apiKey: String
        get() = String(apiKeyField.password)
        set(value) {
            apiKeyField.text = value
        }
}