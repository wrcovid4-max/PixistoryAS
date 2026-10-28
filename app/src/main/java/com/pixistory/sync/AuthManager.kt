package com.pixistory.sync

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException

/** "Sign in with Google": Google Play services shows the account picker, Firebase Auth keeps the session. */
class AuthManager(private val context: Context) {
    data class AccountInfo(val displayName: String, val email: String, val photoUrl: String?)

    private val webClientId = FirebaseGate.webClientId(context)

    /** False until firebase.xml is filled in (see SETUP.md). */
    val isAvailable: Boolean get() = FirebaseGate.isConfigured && webClientId.isNotBlank()

    private val _account = MutableStateFlow<AccountInfo?>(null)
    val account: StateFlow<AccountInfo?> = _account.asStateFlow()

    private val client: GoogleSignInClient by lazy {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, options)
    }

    val signInIntent: Intent get() = client.signInIntent

    val uid: String? get() = if (FirebaseGate.isConfigured) FirebaseAuth.getInstance().currentUser?.uid else null

    fun refresh() {
        _account.value = if (!FirebaseGate.isConfigured) {
            null
        } else {
            FirebaseAuth.getInstance().currentUser?.let { user ->
                AccountInfo(
                    displayName = user.displayName ?: user.email.orEmpty().substringBefore('@'),
                    email = user.email.orEmpty(),
                    photoUrl = user.photoUrl?.toString(),
                )
            }
        }
    }

    /** Turns the sign-in result into a Firebase session; returns the uid. */
    suspend fun completeSignIn(data: Intent?): String {
        val account = try {
            GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
        } catch (error: ApiException) {
            throw IOException("Google sign-in failed (code ${error.statusCode})", error)
        }
        val idToken = account?.idToken
            ?: throw IOException("No ID token: set firebase_web_client_id to the project's Web client ID")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = FirebaseAuth.getInstance().signInWithCredential(credential).await()
        refresh()
        return result.user?.uid ?: throw IOException("Firebase returned no user")
    }

    suspend fun signOut() {
        runCatching { client.signOut().await() }
        if (FirebaseGate.isConfigured) FirebaseAuth.getInstance().signOut()
        refresh()
    }
}
