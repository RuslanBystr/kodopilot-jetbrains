package com.github.ruslanbystr.kodopilotplugin.settings

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.service

@Service(Service.Level.APP)
@State(
    name = "KodoPilotSettings",
    storages = [Storage("KodoPilot.xml")]
)
class KodoPilotSettingsState : PersistentStateComponent<KodoPilotSettingsState.State> {

    data class State(
        var apiKey: String = ""
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var apiKey: String
        get() = state.apiKey
        set(value) {
            state.apiKey = value
        }

    companion object {
        val instance: KodoPilotSettingsState
            get() = service()
    }
}
