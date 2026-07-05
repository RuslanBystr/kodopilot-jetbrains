package com.github.ruslanbystr.kodopilotplugin.ai

import com.google.gson.Gson
import com.google.gson.JsonParser
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class GeminiClient(private val apiKey: String) {

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .build()

    fun generateContent(prompt: String): String {
        val requestBodyJson = """
            {
              "contents": [
                {
                  "parts": [ { "text": ${gsonEscape(prompt)} } ]
                }
              ]
            }
        """.trimIndent()

        val model = LLModel.GEMINI_2_5_PRO

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/${model.modelName}:generateContent"))
            .header("Content-Type", "application/json")
            .header("x-goog-api-key", apiKey)
            .timeout(Duration.ofSeconds(120))
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
            .build()

        val response: HttpResponse<String> = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() !in 200..299) {
            throw RuntimeException("Gemini API error ${response.statusCode()}: ${response.body()}")
        }

        val root = JsonParser.parseString(response.body()).asJsonObject
        return root.getAsJsonArray("candidates")[0].asJsonObject
            .getAsJsonObject("content")
            .getAsJsonArray("parts")[0].asJsonObject
            .get("text").asString
    }

    private fun gsonEscape(text: String): String {
        val gson = Gson()
        return gson.toJson(text)
    }
}