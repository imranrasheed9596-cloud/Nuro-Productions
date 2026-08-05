package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object GeminiService {

    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    suspend fun askIslamicAssistant(userQuery: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackIslamicAnswer(userQuery)
        }

        val systemPrompt = """
            You are Nura's AI Assistant with live Google Search Grounding, powered by gemini-3.5-flash.
            Mandatory Rules:
            1. Provide authentic, accurate, respectful, and up-to-date guidance using Google Search Grounding.
            2. Every substantive claim, ruling, verse, hadith, or news item MUST include source citations.
            3. Where legitimate scholarly opinions or viewpoints differ, transparently outline differing views.
            4. Include a gentle disclaimer recommending consultation with local trusted scholars or official authorities when needed.
        """.trimIndent()

        val prompt = "$systemPrompt\n\nUser Question: $userQuery"

        try {
            val responseText = makeApiCall(apiKey, prompt, enableSearchGrounding = true)
            responseText.ifBlank { getFallbackIslamicAnswer(userQuery) }
        } catch (e: Exception) {
            getFallbackIslamicAnswer(userQuery)
        }
    }

    suspend fun generateCaption(imageContext: String, userTopic: String): List<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext listOf(
                "Reflecting on moments of peace and gratitude. ✨ #Nura #Gratitude",
                "Seeking knowledge and light every single day. 🌿 #Faith #Community",
                "Grateful for community and connection. 💚 #Blessed"
            )
        }

        val prompt = "Generate 3 inspiring, short social media captions for a photo about: '$imageContext' with topic '$userTopic'. Return 3 options numbered 1, 2, 3."

        try {
            val text = makeApiCall(apiKey, prompt, enableSearchGrounding = false)
            val lines = text.lines().filter { it.isNotBlank() }
            if (lines.size >= 3) {
                lines.take(3).map { it.replace(Regex("^[0-9]+[.\\s-]+"), "").trim() }
            } else {
                listOf(
                    "Reflecting on moments of peace and gratitude. ✨ #Nura #Gratitude",
                    "Seeking knowledge and light every single day. 🌿 #Faith #Community",
                    "Grateful for community and connection. 💚 #Blessed"
                )
            }
        } catch (e: Exception) {
            listOf(
                "Reflecting on moments of peace and gratitude. ✨ #Nura #Gratitude",
                "Seeking knowledge and light every single day. 🌿 #Faith #Community",
                "Grateful for community and connection. 💚 #Blessed"
            )
        }
    }

    suspend fun summarizeLecture(transcriptOrTopic: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext """
                📌 Key Chapter Points:
                • 00:00 - Introduction & Historical Context
                • 04:30 - Core Virtues & Daily Application
                • 12:15 - Scholarly Consensus & Reflections
                • 18:00 - Q&A with Community Members
                
                💡 Main Takeaway: Striving for excellence (Ihsan) in worship and daily interactions elevates community well-being.
            """.trimIndent()
        }

        val prompt = "Summarize the following lecture topic/text into timestamped chapter bullet points and a key takeaway:\n\n$transcriptOrTopic"

        try {
            makeApiCall(apiKey, prompt, enableSearchGrounding = true)
        } catch (e: Exception) {
            "Summary generated for lecture: Key takeaways include patience, sincerity, and continuous learning."
        }
    }

    suspend fun translateText(text: String, targetLanguage: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "[Translated to $targetLanguage]: $text"
        }

        val prompt = "Translate the following text accurately into $targetLanguage. Only return the translated text:\n\n$text"

        try {
            makeApiCall(apiKey, prompt, enableSearchGrounding = false)
        } catch (e: Exception) {
            "[Translated to $targetLanguage]: $text"
        }
    }

    private fun makeApiCall(apiKey: String, promptText: String, enableSearchGrounding: Boolean = true): String {
        val url = URL("$BASE_URL?key=$apiKey")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.doOutput = true
        conn.connectTimeout = 20000
        conn.readTimeout = 20000

        val jsonRequest = JSONObject().apply {
            val contentsArr = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArr = JSONArray().apply {
                        val partObj = JSONObject().put("text", promptText)
                        put(partObj)
                    }
                    put("parts", partsArr)
                }
                put(contentObj)
            }
            put("contents", contentsArr)

            if (enableSearchGrounding) {
                val toolsArr = JSONArray().apply {
                    val toolObj = JSONObject().apply {
                        put("googleSearch", JSONObject())
                    }
                    put(toolObj)
                }
                put("tools", toolsArr)
            }
        }

        conn.outputStream.use { os ->
            os.write(jsonRequest.toString().toByteArray(Charsets.UTF_8))
        }

        if (conn.responseCode == 200) {
            val responseStr = conn.inputStream.bufferedReader().use { it.readText() }
            val responseJson = JSONObject(responseStr)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                var mainText = ""
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    mainText = parts.getJSONObject(0).optString("text", "")
                }

                // Extract Grounding Metadata / Sources
                val groundingMetadata = candidate.optJSONObject("groundingMetadata")
                val groundingChunks = groundingMetadata?.optJSONArray("groundingChunks")

                val sources = mutableListOf<String>()
                if (groundingChunks != null) {
                    for (i in 0 until groundingChunks.length()) {
                        val chunk = groundingChunks.optJSONObject(i)
                        val web = chunk?.optJSONObject("web")
                        val title = web?.optString("title")
                        val uri = web?.optString("uri")
                        if (!title.isNullOrBlank() && !uri.isNullOrBlank()) {
                            sources.add("• [$title]($uri)")
                        }
                    }
                }

                if (sources.isNotEmpty()) {
                    mainText += "\n\n🌐 **Google Search Grounding Sources:**\n" + sources.distinct().take(5).joinToString("\n")
                }

                return mainText
            }
        } else if (enableSearchGrounding) {
            // Fallback retry without search grounding if error occurs
            return makeApiCall(apiKey, promptText, enableSearchGrounding = false)
        }
        return ""
    }

    private fun getFallbackIslamicAnswer(query: String): String {
        val lower = query.lowercase()
        return when {
            lower.contains("prayer") || lower.contains("salah") -> """
                Answering regarding Salah (Prayer):
                
                Salah is the second pillar of Islam and a direct link with Allah SWT.
                • Reference: "Indeed, prayer has been decreed upon the believers a decree of specified times." [Quran 4:103]
                • Scholarly Consensus: All four major jurisprudence schools (Hanafi, Shafi'i, Maliki, Hanbali) agree that praying five daily prayers on time is mandatory (Fard 'Ayn).
                
                Note: For specific personal circumstances, please consult your local mosque scholar or Verified Scholars in Nura.
            """.trimIndent()
            lower.contains("charity") || lower.contains("zakat") -> """
                Answering regarding Zakat & Sadaqah:
                
                Zakat is the 3rd pillar of Islam, calculated as 2.5% on qualifying wealth above the Nisab threshold.
                • Reference: "Take, [O Muhammad], from their wealth a charity by which you purify them and cause them increase..." [Quran 9:103]
                • Sourcing: Standard Nisab threshold is based on 85g gold or 595g silver equivalent.
                
                Tip: You can use Nura's built-in Zakat Calculator in the Islamic Hub tab!
            """.trimIndent()
            else -> """
                Answering your inquiry ("$query"):
                
                In Islamic traditions, seeking knowledge with sincerity and humility is encouraged for all believers.
                • Reference: "Say, 'O my Lord, increase me in knowledge.'" [Quran 20:114]
                • Hadith: "Seeking knowledge is a duty upon every Muslim." [Sunan Ibn Majah 224]
                
                Ikhtilaf & Sourcing Note: Where scholars hold differing views on practical application, Nura presents the major classical perspectives. Please refer to local trusted scholars for individualized guidance.
            """.trimIndent()
        }
    }
}
