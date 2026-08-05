package com.example.data.repository

import android.util.Log
import com.example.data.db.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NuraFirestoreRepository {

    private val firestore: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    private val auth: FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()

    val currentUserId: String?
        get() = auth?.currentUser?.uid

    /**
     * Real-time stream of community posts stored in Cloud Firestore.
     */
    fun getFirestorePosts(): Flow<List<PostEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreRepo", "Error listening to posts", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        try {
                            PostEntity(
                                id = doc.id,
                                authorName = doc.getString("authorName") ?: "Community Member",
                                authorHandle = doc.getString("authorHandle") ?: "@nura.member",
                                authorAvatarUrl = doc.getString("authorAvatarUrl") ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                                isVerified = doc.getBoolean("isVerified") ?: false,
                                isScholar = doc.getBoolean("isScholar") ?: false,
                                mediaUrl = doc.getString("mediaUrl") ?: "",
                                caption = doc.getString("caption") ?: "",
                                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                likesCount = (doc.getLong("likesCount") ?: 0L).toInt(),
                                commentsCount = (doc.getLong("commentsCount") ?: 0L).toInt(),
                                sharesCount = (doc.getLong("sharesCount") ?: 0L).toInt(),
                                isLiked = doc.getBoolean("isLiked") ?: false,
                                isSaved = doc.getBoolean("isSaved") ?: false,
                                audience = doc.getString("audience") ?: "Public",
                                location = doc.getString("location") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(posts)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Save post to Cloud Firestore.
     */
    suspend fun publishPostToFirestore(post: PostEntity): Boolean {
        val db = firestore ?: return false
        return try {
            val postMap = hashMapOf(
                "authorName" to post.authorName,
                "authorHandle" to post.authorHandle,
                "authorAvatarUrl" to post.authorAvatarUrl,
                "isVerified" to post.isVerified,
                "isScholar" to post.isScholar,
                "mediaUrl" to post.mediaUrl,
                "caption" to post.caption,
                "timestamp" to post.timestamp,
                "likesCount" to post.likesCount,
                "commentsCount" to post.commentsCount,
                "sharesCount" to post.sharesCount,
                "isLiked" to post.isLiked,
                "isSaved" to post.isSaved,
                "audience" to post.audience,
                "location" to post.location,
                "authorUid" to (currentUserId ?: "")
            )
            db.collection("posts").document(post.id).set(postMap).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Failed to publish post", e)
            false
        }
    }

    /**
     * Save/Sync Note to Cloud Firestore under current user.
     */
    suspend fun saveNoteToFirestore(note: NoteEntity): Boolean {
        val db = firestore ?: return false
        val uid = currentUserId ?: return false
        return try {
            val noteMap = hashMapOf(
                "id" to note.id,
                "authorHandle" to note.authorHandle,
                "authorName" to note.authorName,
                "content" to note.content,
                "emoji" to note.emoji,
                "timestamp" to note.timestamp
            )
            db.collection("users").document(uid)
                .collection("notes").document(note.id)
                .set(noteMap).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Failed to save note to Firestore", e)
            false
        }
    }

    /**
     * Save Quran Bookmark to Cloud Firestore.
     */
    suspend fun saveBookmarkToFirestore(bookmark: QuranBookmarkEntity): Boolean {
        val db = firestore ?: return false
        val uid = currentUserId ?: return false
        return try {
            val bookmarkMap = hashMapOf(
                "surahNumber" to bookmark.surahNumber,
                "surahName" to bookmark.surahName,
                "verseNumber" to bookmark.verseNumber,
                "note" to bookmark.note,
                "timestamp" to bookmark.timestamp
            )
            db.collection("users").document(uid)
                .collection("quran_bookmarks")
                .add(bookmarkMap).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Failed to save bookmark", e)
            false
        }
    }

    /**
     * Save Zakat Log to Cloud Firestore.
     */
    suspend fun saveZakatLogToFirestore(log: ZakatLogEntity): Boolean {
        val db = firestore ?: return false
        val uid = currentUserId ?: return false
        return try {
            val zakatMap = hashMapOf(
                "totalWealth" to log.totalWealth,
                "nisabThreshold" to log.nisabThreshold,
                "zakatAmount" to log.zakatAmount,
                "nisabStandard" to log.nisabStandard,
                "timestamp" to log.timestamp
            )
            db.collection("users").document(uid)
                .collection("zakat_logs")
                .add(zakatMap).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Failed to save zakat log", e)
            false
        }
    }

    /**
     * Save Direct / Community Message to Cloud Firestore.
     */
    suspend fun sendMessageToFirestore(message: DirectMessageEntity): Boolean {
        val db = firestore ?: return false
        return try {
            val msgMap = hashMapOf(
                "id" to message.id,
                "conversationId" to message.conversationId,
                "senderHandle" to message.senderHandle,
                "senderName" to message.senderName,
                "text" to message.text,
                "timestamp" to message.timestamp,
                "isRead" to message.isRead,
                "mediaType" to message.mediaType,
                "isVoice" to message.isVoice,
                "voiceDurationSec" to message.voiceDurationSec
            )
            db.collection("conversations")
                .document(message.conversationId)
                .collection("messages")
                .document(message.id)
                .set(msgMap).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Failed to send message to Firestore", e)
            false
        }
    }
}
