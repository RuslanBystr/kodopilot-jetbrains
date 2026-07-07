package com.github.ruslanbystr.kodopilotplugin.settings

import com.github.ruslanbystr.kodopilotplugin.ai.LLMProvider
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class KodoPilotSettings {

    private val providerCombo =
        ComboBox(LLMProvider.entries.toTypedArray())
    private val apiKeyField = JBPasswordField()

    var provider: LLMProvider? = LLMProvider.GEMINI

    val panel: DialogPanel = panel {

        row("LLM Provider:") {
            cell(providerCombo)
                .bindItem(::provider)
                .comment("Choose the AI provider.")
        }

        row("API Key:") {
            cell(apiKeyField)
                .align(com.intellij.ui.dsl.builder.AlignX.FILL)
                .comment("API key for the selected provider.")
        }
    }

    fun getPreferredFocusedComponent(): JComponent = providerCombo

    var apiKey: String
        get() = String(apiKeyField.password)
        set(value) {
            apiKeyField.text = value
        }
}