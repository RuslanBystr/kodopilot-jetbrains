package com.github.ruslanbystr.kodopilotplugin.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class KodoPilotSettingsConfigurable : Configurable {

    private var settingsComponent: KodoPilotSettings? = null

    override fun getDisplayName(): String = "KodoPilot"

    override fun getPreferredFocusedComponent(): JComponent? =
        settingsComponent?.getPreferredFocusedComponent()

    override fun createComponent(): JComponent {
        val component = KodoPilotSettings()
        settingsComponent = component
        return component.panel
    }

    override fun isModified(): Boolean {
        return settingsComponent?.apiKey != KodoPilotSettingsState.instance.apiKey
    }

    override fun apply() {
        println("Saving: ${settingsComponent?.apiKey}")
        KodoPilotSettingsState.instance.apiKey = settingsComponent?.apiKey ?: ""
    }

    override fun reset() {
        settingsComponent?.apiKey = KodoPilotSettingsState.instance.apiKey
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}