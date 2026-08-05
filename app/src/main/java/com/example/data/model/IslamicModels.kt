package com.example.data.model

data class PrayerTime(
    val name: String,
    val time: String,
    val isNext: Boolean = false,
    val isPassed: Boolean = false
)

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val meaning: String,
    val versesCount: Int,
    val revelationType: String // Meccan or Medinan
)

data class Verse(
    val surahNumber: Int,
    val verseNumber: Int,
    val textArabic: String,
    val textTranslation: String,
    val footnote: String = "",
    val tafsirExcerpt: String = ""
)

data class HadithItem(
    val collection: String, // e.g. Sahih al-Bukhari
    val number: String,
    val grading: String, // e.g. Sahih, Hasan
    val narrator: String,
    val textArabic: String,
    val textTranslation: String,
    val chapter: String
)

data class MosqueListing(
    val id: String,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val jummahTime: String,
    val isVerified: Boolean = false,
    val hasCommunityGroup: Boolean = true
)

data class CharityOrg(
    val id: String,
    val name: String,
    val category: String, // Zakat-eligible, Emergency Relief, Orphan Sponsorship
    val description: String,
    val websiteUrl: String,
    val isVetted: Boolean = true
)

data class LearningPath(
    val id: String,
    val title: String,
    val level: String, // Beginner, Intermediate, Advanced
    val lessonCount: Int,
    val completedLessons: Int,
    val description: String,
    val authorName: String
)

data class VerifiedScholar(
    val id: String,
    val name: String,
    val handle: String,
    val specialization: String,
    val madhhab: String,
    val institution: String,
    val bio: String
)
