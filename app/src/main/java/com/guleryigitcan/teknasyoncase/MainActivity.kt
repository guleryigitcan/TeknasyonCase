package com.guleryigitcan.teknasyoncase

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.guleryigitcan.teknasyoncase.analytics.NotificationOpenTracker
import com.guleryigitcan.teknasyoncase.notification.EXTRA_EARTHQUAKE_ID
import com.guleryigitcan.teknasyoncase.presentation.navigation.AppNavigation
import com.guleryigitcan.teknasyoncase.presentation.navigation.NotificationNavigationRequest
import com.guleryigitcan.teknasyoncase.ui.theme.TeknasyonCaseTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var notificationOpenTracker: NotificationOpenTracker
    private var notificationRequest by mutableStateOf<NotificationNavigationRequest?>(null)
    private var notificationSequence = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) notificationRequest = intent.toNotificationRequest()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            TeknasyonCaseTheme {
                AppNavigation(notificationRequest)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationRequest = intent.toNotificationRequest()
    }

    private fun Intent.toNotificationRequest(): NotificationNavigationRequest? =
        notificationOpenTracker.recordAndGetId(getStringExtra(EXTRA_EARTHQUAKE_ID))?.let { id ->
            NotificationNavigationRequest(id, ++notificationSequence)
        }
}
