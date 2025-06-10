package com.example.projectmanagerapp.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmanagerapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignInHelper(
    private val context: Context,
    private var onSignInResult: (AuthCredential?) -> Unit
) {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    fun initialize(activity: AppCompatActivity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        signInLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }

    fun signIn(forceAccountSelection: Boolean = true) {
        if (forceAccountSelection) {
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                signInLauncher.launch(signInIntent)
            }
        } else {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d("GoogleSignInHelper", "Sign in successful: ${account?.email}")
            val idToken = account?.idToken
            if (idToken != null) {
                Log.d("GoogleSignInHelper", "ID Token received, creating credential")
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                onSignInResult(credential)
            } else {
                Log.e("GoogleSignInHelper", "ID Token is null")
                onSignInResult(null)
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignInHelper", "Sign in failed: ${e.statusCode} - ${e.message}")
            onSignInResult(null)
        }
    }

    fun setCallback(callback: (AuthCredential?) -> Unit) {
        onSignInResult = callback
    }

    fun signOut() {
        googleSignInClient.signOut()
    }
}
