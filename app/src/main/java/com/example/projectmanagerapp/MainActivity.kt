package com.example.projectmanagerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.projectmanagerapp.ui.theme.ProjectManagerAppTheme
import com.example.projectmanagerapp.utils.AppNavHost
import com.example.projectmanagerapp.utils.FacebookSignInHelper
import com.example.projectmanagerapp.utils.GoogleSignInHelper
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var googleSignInHelper: GoogleSignInHelper
        lateinit var facebookSignInHelper: FacebookSignInHelper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

        initializeHelpers()

        enableEdgeToEdge()
        setContent {
            ProjectManagerAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun initializeHelpers() {
        googleSignInHelper = GoogleSignInHelper(this) { credential ->
        }
        googleSignInHelper.initialize(this)

        facebookSignInHelper = FacebookSignInHelper(this) { credential ->
        }
        facebookSignInHelper.initialize(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookSignInHelper.getCallbackManager().onActivityResult(requestCode, resultCode, data)
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavHostPreview() {
    val navController = rememberNavController()
    AppNavHost(navController = navController, modifier = Modifier)
}