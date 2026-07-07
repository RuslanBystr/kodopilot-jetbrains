package com.github.ruslanbystr.kodopilotplugin.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class KodoPilotSettingsConfigurable : Configurable {

    private var settingsComponent: KodoPilotSettings? = null

    override fun getDisplayName(): String = "KodoPilot"

    override fun getPreferredFocusedComponent(): JComponent? =
        settingsComponent?.getPreferredFocusedComponent()

    override fun createComponent(): JComponent {
        return KodoPilotSettings().also {
            settingsComponent = it
        }.panel
    }

    override fun isModified(): Boolean {
        val settings = KodoPilotSettingsState.instance
        val component = settingsComponent ?: return false

        return component.provider != settings.provider ||
                component.apiKey != settings.currentApiKey
    }

    override fun apply() {
        val settings = KodoPilotSettingsState.instance
        val component = settingsComponent ?: return

        settings.provider = component.provider ?: return
        settings.setApiKey(apiKey = component.apiKey)
    }

    override fun reset() {
        val settings = KodoPilotSettingsState.instance
        val component = settingsComponent ?: return

        component.provider = settings.provider
        component.apiKey = settings.currentApiKey
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}