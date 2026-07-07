package com.github.ruslanbystr.kodopilotplugin.ai

enum class LLMProvider(
    val displayName: String
) {
    GEMINI("Google Gemini"),
    OPENAI("OpenAI"),
    ANTHROPIC("Anthropic"),
    GROQ("Groq");

    override fun toString(): String = displayName
}