package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class UserFirestoreProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val provider: String = "Email",
    val bio: String = "Peace & guidance in daily life 🌿",
    val createdAt: Long = System.currentTimeMillis()
)

class FirebaseAuthService(private val context: Context) {

    private val auth: FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()
    private val firestore: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    private val credentialManager = CredentialManager.create(context)

    val currentUser: FirebaseUser?
        get() = auth?.currentUser

    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth?.addAuthStateListener(listener)
        trySend(auth?.currentUser)
        awaitClose { auth?.removeAuthStateListener(listener) }
    }

    suspend fun signInWithGoogleCredential(idToken: String): Result<FirebaseUser> {
        val firebaseAuth = auth ?: return Result.failure(Exception("FirebaseAuth unavailable"))
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                saveUserToFirestore(user, provider = "Google")
                Result.success(user)
            } else {
                Result.failure(Exception("User is null after Google sign in"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Google Sign-In failed", e)
            Result.failure(e)
        }
    }

    suspend fun launchGoogleSignIn(webClientId: String? = null): Result<GetCredentialResponse> {
        return try {
            val serverClientId = webClientId ?: context.getString(R.string.default_web_client_id)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(context, request)
            Result.success(response)
        } catch (e: GetCredentialException) {
            Log.e("FirebaseAuthService", "CredentialManager request failed", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Google Sign-In launcher error", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser> {
        val firebaseAuth = auth ?: return Result.failure(Exception("FirebaseAuth unavailable"))
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user ?: throw Exception("User null after email login")
            saveUserToFirestore(user, provider = "Email")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String, displayName: String): Result<FirebaseUser> {
        val firebaseAuth = auth ?: return Result.failure(Exception("FirebaseAuth unavailable"))
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user ?: throw Exception("User null after sign up")
            
            // Set display name
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profileUpdates).await()

            saveUserToFirestore(user, provider = "Email")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<FirebaseUser> {
        val firebaseAuth = auth ?: return Result.failure(Exception("FirebaseAuth unavailable"))
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            val user = result.user ?: throw Exception("Guest user is null")
            saveUserToFirestore(user, provider = "Guest")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
    }

    suspend fun saveUserToFirestore(user: FirebaseUser, provider: String) {
        val db = firestore ?: return
        try {
            val profile = UserFirestoreProfile(
                uid = user.uid,
                displayName = user.displayName ?: if (user.isAnonymous) "Guest User" else user.email?.substringBefore("@") ?: "Nura Member",
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString() ?: "",
                provider = provider,
                createdAt = System.currentTimeMillis()
            )
            db.collection("users")
                .document(user.uid)
                .set(profile, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Failed to save user profile in Firestore", e)
        }
    }
}
