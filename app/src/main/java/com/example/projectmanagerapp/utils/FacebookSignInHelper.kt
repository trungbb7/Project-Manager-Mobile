package com.example.projectmanagerapp.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider

class FacebookSignInHelper(
    private val context: Context,
    private var onSignInResult: (AuthCredential?) -> Unit
) {
    private lateinit var callbackManager: CallbackManager

    fun initialize(activity: AppCompatActivity) {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    onSignInResult(null)
                }

                override fun onError(exception: FacebookException) {
                    onSignInResult(null)
                }
            })
    }

    fun signIn(activity: AppCompatActivity) {
        LoginManager.getInstance().logInWithReadPermissions(
            activity,
            listOf("email", "public_profile")
        )
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        onSignInResult(credential)
    }

    fun setCallback(callback: (AuthCredential?) -> Unit) {
        onSignInResult = callback
    }

    fun getCallbackManager(): CallbackManager {
        return callbackManager
    }

    fun signOut() {
        LoginManager.getInstance().logOut()
    }
}
