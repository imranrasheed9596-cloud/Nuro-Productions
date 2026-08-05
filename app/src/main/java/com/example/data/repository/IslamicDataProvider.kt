package com.example.data.repository

import com.example.data.model.*

object IslamicDataProvider {

    fun getTodayPrayerTimes(): List<PrayerTime> {
        return listOf(
            PrayerTime("Fajr", "05:12 AM", isNext = false, isPassed = true),
            PrayerTime("Sunrise", "06:34 AM", isNext = false, isPassed = true),
            PrayerTime("Dhuhr", "12:45 PM", isNext = false, isPassed = true),
            PrayerTime("Asr", "04:20 PM", isNext = true, isPassed = false),
            PrayerTime("Maghrib", "06:58 PM", isNext = false, isPassed = false),
            PrayerTime("Isha", "08:22 PM", isNext = false, isPassed = false)
        )
    }

    fun getSurahList(): List<Surah> {
        return listOf(
            Surah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
            Surah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan"),
            Surah(3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Medinan"),
            Surah(4, "النساء", "An-Nisa", "The Women", 176, "Medinan"),
            Surah(18, "الكهف", "Al-Kahf", "The Cave", 110, "Meccan"),
            Surah(36, "يس", "Ya-Sin", "Ya-Sin", 83, "Meccan"),
            Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, "Medinan"),
            Surah(67, "الملك", "Al-Mulk", "The Sovereignty", 30, "Meccan"),
            Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, "Meccan"),
            Surah(113, "الفلق", "Al-Falaq", "The Daybreak", 5, "Meccan"),
            Surah(114, "الناس", "An-Nas", "Mankind", 6, "Meccan")
        )
    }

    fun getSampleVersesForSurah(surahNum: Int): List<Verse> {
        return when (surahNum) {
            1 -> listOf(
                Verse(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
                Verse(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -"),
                Verse(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,"),
                Verse(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense."),
                Verse(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help."),
                Verse(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -"),
                Verse(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have earned [Your] anger or of those who are astray.")
            )
            67 -> listOf(
                Verse(67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Blessed is He in whose hand is dominion, and He is over all things competent -"),
                Verse(67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "[He] who created death and life to test you as to which of you is best in deed - and He is the Exalted in Might, the Forgiving -")
            )
            else -> listOf(
                Verse(surahNum, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
                Verse(surahNum, 2, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence.")
            )
        }
    }

    fun getSampleHadiths(): List<HadithItem> {
        return listOf(
            HadithItem(
                collection = "Sahih al-Bukhari",
                number = "1",
                grading = "Sahih (Authentic)",
                narrator = "Narrated by 'Umar bin Al-Khattab",
                textArabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
                textTranslation = "Actions are judged by intentions, and every person will get what they intended.",
                chapter = "Book 1: Revelation"
            ),
            HadithItem(
                collection = "Sahih Muslim",
                number = "2699",
                grading = "Sahih (Authentic)",
                narrator = "Narrated by Abu Hurairah",
                textArabic = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
                textTranslation = "Whoever treads a path seeking knowledge, Allah will make easy for him a path to Paradise.",
                chapter = "Book 35: Dhikr & Supplication"
            ),
            HadithItem(
                collection = "Sunan an-Nasa'i",
                number = "3104",
                grading = "Sahih (Authentic)",
                narrator = "Narrated by Anas ibn Malik",
                textArabic = "الْجَنَّةُ تَحْتَ أَقْدَامِ الأُمَّهَاتِ",
                textTranslation = "Paradise lies under the feet of mothers.",
                chapter = "Book 25: Jihad"
            )
        )
    }

    fun getNearbyMosques(): List<MosqueListing> {
        return listOf(
            MosqueListing("m1", "Central Community Mosque", "124 Peace Ave, Downtown", 0.8, "01:15 PM & 02:00 PM", isVerified = true),
            MosqueListing("m2", "Al-Noor Cultural Center", "58 Light St, Westside", 2.3, "01:30 PM", isVerified = true),
            MosqueListing("m3", "Madina Islamic Society", "210 Hope Blvd, East District", 4.1, "01:15 PM", isVerified = false)
        )
    }

    fun getCharityOrgs(): List<CharityOrg> {
        return listOf(
            CharityOrg("c1", "Global Relief & Zakat Fund", "Zakat-eligible", "Providing emergency food, clean water, and healthcare assistance to vulnerable families worldwide.", "https://globalrelief.org"),
            CharityOrg("c2", "Orphan Care International", "Orphan Sponsorship", "Sponsoring education, nutrition, and psychological support for orphaned children.", "https://orphancare.org"),
            CharityOrg("c3", "Water for Life Initiative", "Emergency Relief", "Building sustainable solar water wells in drought-affected communities.", "https://waterforlife.org")
        )
    }

    fun getLearningPaths(): List<LearningPath> {
        return listOf(
            LearningPath("lp1", "Fiqh of Salah for New Muslims", "Beginner", 6, 2, "A step-by-step practical guide to purification (Wudu) and performing the five daily prayers.", "Ustadh Yusuf Omar"),
            LearningPath("lp2", "Seerah Overview: Life of Prophet Muhammad ﷺ", "Intermediate", 10, 4, "Comprehensive chronological study of the Prophet's life, moral character, and leadership.", "Dr. Miriam Hassan"),
            LearningPath("lp3", "Foundations of Islamic Aqeedah", "Beginner", 8, 1, "Understanding the six pillars of Iman (Faith) with authentic Quranic proofs.", "Sh. Ahmad Al-Khatib")
        )
    }

    fun getVerifiedScholars(): List<VerifiedScholar> {
        return listOf(
            VerifiedScholar("vs1", "Ustadh Yusuf Omar", "@yusuf.scholar", "Quranic Tafsir & Fiqh", "Shafi'i", "Al-Azhar University Graduate", "Specialized in Quranic studies, youth counseling, and comparative jurisprudence."),
            VerifiedScholar("vs2", "Dr. Miriam Hassan", "@dr.miriam", "Hadith & Islamic History", "Hanafi", "Madinah Islamic University Fellow", "Author of multiple books on women in Islamic scholarship and prophetic traditions."),
            VerifiedScholar("vs3", "Sh. Ahmad Al-Khatib", "@ahmad.khatib", "Islamic Finance & Ethics", "Maliki", "Zaytuna College Lecturer", "Advisor on ethical investments, halal trade, and contemporary Zakat jurisprudence.")
        )
    }
}
