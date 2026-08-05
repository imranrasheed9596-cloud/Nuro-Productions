package com.example.data.repository

import com.example.data.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NuraRepository(private val dao: NuraDao) {

    val allPosts: Flow<List<PostEntity>> = dao.getAllPosts()
    val allReels: Flow<List<ReelEntity>> = dao.getAllReels()
    val allStories: Flow<List<StoryEntity>> = dao.getAllStories()
    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    val allGroups: Flow<List<CommunityGroupEntity>> = dao.getAllGroups()
    val allMessages: Flow<List<DirectMessageEntity>> = dao.getAllMessages()
    val quranBookmarks: Flow<List<QuranBookmarkEntity>> = dao.getBookmarks()
    val zakatLogs: Flow<List<ZakatLogEntity>> = dao.getZakatLogs()

    fun getMessagesForConversation(convId: String): Flow<List<DirectMessageEntity>> {
        return dao.getMessagesForConversation(convId)
    }

    suspend fun togglePostLike(id: String, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        dao.updatePostLike(id, !currentLiked, delta)
    }

    suspend fun togglePostSave(id: String, currentSaved: Boolean) {
        dao.updatePostSave(id, !currentSaved)
    }

    suspend fun toggleReelLike(id: String, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        dao.updateReelLike(id, !currentLiked, delta)
    }

    suspend fun toggleGroupJoin(id: String, currentJoined: Boolean) {
        val delta = if (currentJoined) -1 else 1
        dao.updateGroupJoin(id, !currentJoined, delta)
    }

    suspend fun markStorySeen(id: String) {
        dao.markStorySeen(id)
    }

    suspend fun createPost(post: PostEntity) {
        dao.insertPost(post)
    }

    suspend fun createNote(note: NoteEntity) {
        dao.insertNote(note)
    }

    suspend fun sendMessage(message: DirectMessageEntity) {
        dao.insertMessage(message)
    }

    suspend fun addBookmark(surahNumber: Int, surahName: String, verseNumber: Int, note: String) {
        dao.insertBookmark(
            QuranBookmarkEntity(
                surahNumber = surahNumber,
                surahName = surahName,
                verseNumber = verseNumber,
                note = note
            )
        )
    }

    suspend fun addZakatLog(totalWealth: Double, nisab: Double, zakat: Double, standard: String) {
        dao.insertZakatLog(
            ZakatLogEntity(
                totalWealth = totalWealth,
                nisabThreshold = nisab,
                zakatAmount = zakat,
                nisabStandard = standard
            )
        )
    }

    suspend fun seedInitialDataIfEmpty() {
        val currentPosts = allPosts.first()
        if (currentPosts.isNotEmpty()) return

        // Seed Posts
        val initialPosts = listOf(
            PostEntity(
                id = "p1",
                authorName = "Amina Al-Mansoor",
                authorHandle = "@amina.design",
                authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                isVerified = true,
                mediaUrl = "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=800",
                caption = "Peaceful morning light reflected in the courtyard. Gratitude for a new day to learn and grow. 🌿✨ #Nura #Architecture #Peace",
                likesCount = 342,
                commentsCount = 28,
                sharesCount = 14,
                location = "Istanbul, Turkey"
            ),
            PostEntity(
                id = "p2",
                authorName = "Ustadh Yusuf Omar",
                authorHandle = "@yusuf.scholar",
                authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                isVerified = true,
                isScholar = true,
                mediaUrl = "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800",
                caption = "The Quran reminds us: 'Indeed, with hardship comes ease.' [Quran 94:6]. Never lose hope in Allah's mercy. Share this reminder with someone who needs it today.",
                likesCount = 1289,
                commentsCount = 94,
                sharesCount = 312,
                location = "Cairo, Egypt"
            ),
            PostEntity(
                id = "p3",
                authorName = "Fatima Skincare",
                authorHandle = "@fatima.halalbeauty",
                authorAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                isVerified = false,
                mediaUrl = "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800",
                caption = "Crafted with 100% natural organic oils. Pure, ethical, and wholesome. Check out our new winter hydration line! 🌿 #HalalBeauty #EthicalLiving",
                likesCount = 567,
                commentsCount = 41,
                sharesCount = 19,
                location = "London, UK"
            )
        )
        dao.insertPosts(initialPosts)

        // Seed Reels
        val initialReels = listOf(
            ReelEntity(
                id = "r1",
                creatorName = "Qari Bilal Hassan",
                creatorHandle = "@bilal.recitation",
                creatorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                isVerified = true,
                videoTitle = "Surah Ar-Rahman - Heart Soothing Recitation",
                audioTitle = "Original Audio - Qari Bilal Hassan",
                likesCount = 4820,
                commentsCount = 310
            ),
            ReelEntity(
                id = "r2",
                creatorName = "Sarah Revert Journey",
                creatorHandle = "@sarah.revert",
                creatorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                isVerified = false,
                videoTitle = "3 Books that transformed my understanding of Islam 📚",
                audioTitle = "Calm Ambient Acoustic - Nura Sound",
                likesCount = 1940,
                commentsCount = 182
            )
        )
        dao.insertReels(initialReels)

        // Seed Stories
        val initialStories = listOf(
            StoryEntity(
                id = "s1",
                authorName = "Amina Al-Mansoor",
                authorHandle = "@amina.design",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1519817650390-64a93db51149?w=600",
                isSeen = false,
                isCloseFriends = false
            ),
            StoryEntity(
                id = "s2",
                authorName = "Ustadh Yusuf Omar",
                authorHandle = "@yusuf.scholar",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1564769625905-50e93615e769?w=600",
                isSeen = false,
                isCloseFriends = true
            ),
            StoryEntity(
                id = "s3",
                authorName = "Sarah Revert Journey",
                authorHandle = "@sarah.revert",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=600",
                isSeen = true,
                isCloseFriends = false
            )
        )
        dao.insertStories(initialStories)

        // Seed Notes
        val initialNotes = listOf(
            NoteEntity(
                id = "n1",
                authorHandle = "@amina.design",
                authorName = "Amina",
                content = "Fajr reflection group at 6 AM 🌅",
                emoji = "☕"
            ),
            NoteEntity(
                id = "n2",
                authorHandle = "@yusuf.scholar",
                authorName = "Yusuf",
                content = "Live Tafsir Q&A session tonight!",
                emoji = "📖"
            ),
            NoteEntity(
                id = "n3",
                authorHandle = "@sarah.revert",
                authorName = "Sarah",
                content = "Reading Juz 15 today... halfway!",
                emoji = "✨"
            )
        )
        for (note in initialNotes) {
            dao.insertNote(note)
        }

        // Seed Direct Messages
        val initialMessages = listOf(
            DirectMessageEntity(
                id = "m1",
                conversationId = "conv_yusuf",
                senderHandle = "@yusuf.scholar",
                senderName = "Ustadh Yusuf Omar",
                text = "Assalamu Alaikum! Did you review the Tafsir notes for Surah Al-Kahf?",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = true
            ),
            DirectMessageEntity(
                id = "m2",
                conversationId = "conv_yusuf",
                senderHandle = "@current_user",
                senderName = "You",
                text = "Wa Alaikum Assalam Ustadh! Yes, the citations were very insightful.",
                timestamp = System.currentTimeMillis() - 1800000,
                isRead = true
            ),
            DirectMessageEntity(
                id = "m3",
                conversationId = "conv_yusuf",
                senderHandle = "@yusuf.scholar",
                senderName = "Ustadh Yusuf Omar",
                text = "BarakAllahu Feek. Let me know if you have any questions on the scholarly differences.",
                timestamp = System.currentTimeMillis() - 600000,
                isRead = false
            )
        )
        for (msg in initialMessages) {
            dao.insertMessage(msg)
        }

        // Seed Community Groups
        val initialGroups = listOf(
            CommunityGroupEntity(
                id = "g1",
                name = "Daily Quran Study & Tadabbur",
                description = "Reflecting together on one verse a day with authentic classical tafsir and practical life lessons.",
                category = "Quran",
                memberCount = 1420,
                isJoined = true,
                coverUrl = "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=600",
                guidelines = "1. Respectful dialogue\n2. Authentic cited references\n3. No spam"
            ),
            CommunityGroupEntity(
                id = "g2",
                name = "Muslim Tech & AI Innovators",
                description = "Connecting software engineers, product designers, and AI creators building ethical technologies.",
                category = "Technology",
                memberCount = 890,
                isJoined = false,
                coverUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600",
                guidelines = "Sharing projects, job postings, and technical discussions."
            ),
            CommunityGroupEntity(
                id = "g3",
                name = "Local Youth Halaqah - Central Mosque",
                description = "Weekly youth gatherings, community service, sporting events, and brotherhood activities.",
                category = "Local Mosques",
                memberCount = 310,
                isJoined = true,
                coverUrl = "https://images.unsplash.com/photo-1564769625905-50e93615e769?w=600",
                guidelines = "Open to all local youth members."
            ),
            CommunityGroupEntity(
                id = "g4",
                name = "Conscious Parenting & Education",
                description = "Support network for raising children with strong Islamic values, character, and academic success.",
                category = "Parenting",
                memberCount = 650,
                isJoined = false,
                coverUrl = "https://images.unsplash.com/photo-1502086223501-7ea6ecd79368?w=600",
                guidelines = "Supportive and compassionate advice."
            )
        )
        dao.insertGroups(initialGroups)
    }
}
