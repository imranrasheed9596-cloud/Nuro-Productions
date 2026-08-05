package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NuraDao {

    // Posts
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Query("UPDATE posts SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updatePostLike(id: String, isLiked: Boolean, delta: Int)

    @Query("UPDATE posts SET isSaved = :isSaved WHERE id = :id")
    suspend fun updatePostSave(id: String, isSaved: Boolean)

    // Reels
    @Query("SELECT * FROM reels")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReels(reels: List<ReelEntity>)

    @Query("UPDATE reels SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updateReelLike(id: String, isLiked: Boolean, delta: Int)

    // Stories
    @Query("SELECT * FROM stories ORDER BY timestamp DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("UPDATE stories SET isSeen = 1 WHERE id = :id")
    suspend fun markStorySeen(id: String)

    // Notes
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    // Direct Messages
    @Query("SELECT * FROM direct_messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: String): Flow<List<DirectMessageEntity>>

    @Query("SELECT * FROM direct_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<DirectMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DirectMessageEntity)

    // Community Groups
    @Query("SELECT * FROM community_groups")
    fun getAllGroups(): Flow<List<CommunityGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<CommunityGroupEntity>)

    @Query("UPDATE community_groups SET isJoined = :isJoined, memberCount = memberCount + :delta WHERE id = :id")
    suspend fun updateGroupJoin(id: String, isJoined: Boolean, delta: Int)

    // Quran Bookmarks
    @Query("SELECT * FROM quran_bookmarks ORDER BY timestamp DESC")
    fun getBookmarks(): Flow<List<QuranBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: QuranBookmarkEntity)

    // Zakat Logs
    @Query("SELECT * FROM zakat_logs ORDER BY timestamp DESC")
    fun getZakatLogs(): Flow<List<ZakatLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZakatLog(log: ZakatLogEntity)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    fun getUserProfileFlow(id: String = "local_user"): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    suspend fun getUserProfile(id: String = "local_user"): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfileEntity)
}
