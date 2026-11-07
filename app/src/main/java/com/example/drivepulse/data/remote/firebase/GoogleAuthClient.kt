package com.example.drivepulse.data.remote.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.drivepulse.R
import com.example.drivepulse.data.model.User
import com.example.drivepulse.data.repository.AuthResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * Handles Google Single Sign-On (SSO) using Android Credential Manager API
 * and Firebase Authentication.
 */
class GoogleAuthClient(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()

    /**
     * Triggers Google Sign-In account chooser dialog and authenticates with Firebase.
     */
    suspend fun signInWithGoogle(): AuthResult {
        return try {
            val webClientId = try {
                context.getString(R.string.default_web_client_id)
            } catch (e: Exception) {
                "725406471578-31ueapq0rcnsvt4katj4m5fn9ml2aa00.apps.googleusercontent.com"
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            // Authenticate with Firebase Authentication
            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(authCredential).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    fullName = firebaseUser.displayName ?: "Google User",
                    email = firebaseUser.email ?: "",
                    passwordHash = ""
                )
                Log.d("DrivePulseGoogleAuth", "Google SSO Success for: ${user.email}")
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Google Sign-In failed: Empty Firebase user.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("DrivePulseGoogleAuth", "Google Sign-In cancelled by user.")
            AuthResult.Error("Sign-In cancelled.")
        } catch (e: GetCredentialException) {
            Log.e("DrivePulseGoogleAuth", "Credential Manager error: ${e.message}", e)
            AuthResult.Error("Google Sign-In error: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e("DrivePulseGoogleAuth", "Google SSO unexpected error", e)
            AuthResult.Error("Authentication error: ${e.localizedMessage}")
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
