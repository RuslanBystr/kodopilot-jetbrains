package com.github.ruslanbystr.kodopilotplugin.settings

import com.github.ruslanbystr.kodopilotplugin.ai.LLMProvider
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service(Service.Level.APP)
@State(
    name = "KodoPilotSettings",
    storages = [Storage("KodoPilot.xml")]
)
class KodoPilotSettingsState : PersistentStateComponent<KodoPilotSettingsState.State> {

    data class State(
        var provider: LLMProvider = LLMProvider.GEMINI,

        var geminiApiKey: String = "",
        var openAiApiKey: String = "",
        var anthropicApiKey: String = "",
        var groqApiKey: String = ""
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var provider: LLMProvider
        get() = state.provider
        set(value) {
            state.provider = value
        }

    fun getApiKey(provider: LLMProvider = this.provider): String =
        when (provider) {
            LLMProvider.GEMINI -> state.geminiApiKey
            LLMProvider.OPENAI -> state.openAiApiKey
            LLMProvider.ANTHROPIC -> state.anthropicApiKey
            LLMProvider.GROQ -> state.groqApiKey
        }

    fun setApiKey(
        provider: LLMProvider = this.provider,
        apiKey: String
    ) {
        when (provider) {
            LLMProvider.GEMINI -> state.geminiApiKey = apiKey
            LLMProvider.OPENAI -> state.openAiApiKey = apiKey
            LLMProvider.ANTHROPIC -> state.anthropicApiKey = apiKey
            LLMProvider.GROQ -> state.groqApiKey = apiKey
        }
    }

    var currentApiKey: String
        get() = getApiKey()
        set(value) = setApiKey(apiKey = value)

    companion object {
        val instance: KodoPilotSettingsState
            get() = service()
    }
}
