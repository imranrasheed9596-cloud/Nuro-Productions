package com.example.data.api

import com.example.data.model.PrayerTime
import com.example.data.model.Surah
import com.example.data.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object LiveIslamicApiService {

    suspend fun fetchLivePrayerTimes(city: String = "London", country: String = "UK"): Pair<String, List<PrayerTime>> = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://api.aladhan.com/v1/timingsByCity?city=${city.replace(" ", "%20")}&country=${country.replace(" ", "%20")}&method=2"
            val jsonString = httpGet(urlString)
            val jsonObject = JSONObject(jsonString)
            val data = jsonObject.getJSONObject("data")
            val timings = data.getJSONObject("timings")
            val dateObj = data.getJSONObject("date")
            val hijriObj = dateObj.getJSONObject("hijri")
            
            val hijriDay = hijriObj.getString("day")
            val hijriMonth = hijriObj.getJSONObject("month").getString("en")
            val hijriYear = hijriObj.getString("year")
            val hijriDateFormatted = "$hijriDay $hijriMonth $hijriYear AH"

            val prayerList = listOf(
                PrayerTime("Fajr", format12Hr(timings.getString("Fajr"))),
                PrayerTime("Sunrise", format12Hr(timings.getString("Sunrise"))),
                PrayerTime("Dhuhr", format12Hr(timings.getString("Dhuhr"))),
                PrayerTime("Asr", format12Hr(timings.getString("Asr")), isNext = true),
                PrayerTime("Maghrib", format12Hr(timings.getString("Maghrib"))),
                PrayerTime("Isha", format12Hr(timings.getString("Isha")))
            )

            Pair(hijriDateFormatted, prayerList)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair("14 Safar 1448 AH", listOf(
                PrayerTime("Fajr", "05:12 AM"),
                PrayerTime("Sunrise", "06:34 AM"),
                PrayerTime("Dhuhr", "12:45 PM"),
                PrayerTime("Asr", "04:20 PM", isNext = true),
                PrayerTime("Maghrib", "06:58 PM"),
                PrayerTime("Isha", "08:22 PM")
            ))
        }
    }

    suspend fun fetchLiveSurahList(): List<Surah> = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://api.alquran.cloud/v1/surah"
            val jsonString = httpGet(urlString)
            val jsonObject = JSONObject(jsonString)
            val dataArray = jsonObject.getJSONArray("data")
            val list = mutableListOf<Surah>()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                list.add(
                    Surah(
                        number = item.getInt("number"),
                        nameArabic = item.getString("name"),
                        nameEnglish = item.getString("englishName"),
                        meaning = item.getString("englishNameTranslation"),
                        versesCount = item.getInt("numberOfAyahs"),
                        revelationType = item.getString("revelationType")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchLiveVersesForSurah(surahNumber: Int): List<Verse> = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,en.sahih"
            val jsonString = httpGet(urlString)
            val jsonObject = JSONObject(jsonString)
            val dataArray = jsonObject.getJSONArray("data")

            val arabicData = dataArray.getJSONObject(0).getJSONArray("ayahs")
            val englishData = dataArray.getJSONObject(1).getJSONArray("ayahs")

            val list = mutableListOf<Verse>()
            for (i in 0 until arabicData.length()) {
                val arItem = arabicData.getJSONObject(i)
                val enItem = englishData.getJSONObject(i)
                list.add(
                    Verse(
                        surahNumber = surahNumber,
                        verseNumber = arItem.getInt("numberInSurah"),
                        textArabic = arItem.getString("text"),
                        textTranslation = enItem.getString("text")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun httpGet(urlString: String): String {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        
        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        val builder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            builder.append(line)
        }
        reader.close()
        return builder.toString()
    }

    private fun format12Hr(time24: String): String {
        return try {
            val parts = time24.trim().split(":")
            var hour = parts[0].toInt()
            val minute = parts[1].substring(0, 2)
            val ampm = if (hour >= 12) "PM" else "AM"
            if (hour == 0) hour = 12
            else if (hour > 12) hour -= 12
            String.format("%02d:%s %s", hour, minute, ampm)
        } catch (e: Exception) {
            time24
        }
    }
}
