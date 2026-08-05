package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String,
    val isVerified: Boolean = false,
    val isScholar: Boolean = false,
    val mediaUrl: String,
    val caption: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val audience: String = "Public",
    val location: String = ""
)

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey val id: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatarUrl: String,
    val isVerified: Boolean = false,
    val videoTitle: String,
    val audioTitle: String,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val videoUrl: String = ""
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val avatarUrl: String,
    val mediaUrl: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSeen: Boolean = false,
    val isCloseFriends: Boolean = false
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val authorHandle: String,
    val authorName: String,
    val content: String,
    val emoji: String = "✨",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "direct_messages")
data class DirectMessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderHandle: String,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val mediaType: String = "NONE", // NONE, IMAGE, VOICE
    val isVoice: Boolean = false,
    val voiceDurationSec: Int = 0
)

@Entity(tableName = "community_groups")
data class CommunityGroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val memberCount: Int = 1,
    val isJoined: Boolean = false,
    val coverUrl: String = "",
    val isPrivate: Boolean = false,
    val guidelines: String = ""
)

@Entity(tableName = "quran_bookmarks")
data class QuranBookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "zakat_logs")
data class ZakatLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val totalWealth: Double,
    val nisabThreshold: Double,
    val zakatAmount: Double,
    val nisabStandard: String = "Gold", // Gold or Silver
    val timestamp: Long = System.currentTimeMillis()
)
