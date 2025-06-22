package com.example.projectmanagerapp.utils

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.io.IOException

class CalendarHelper(private val context: Context, private val accountName: String) {

    private val credential = GoogleAccountCredential.usingOAuth2(
        context,
        setOf(CalendarScopes.CALENDAR)
    ).also {
        it.selectedAccountName = accountName
    }

    private val service: Calendar = Calendar.Builder(
        NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        credential
    ).setApplicationName("Project Manager App")
        .build()

    @Throws(IOException::class)
    suspend fun createEvent(
        summary: String,
        description: String,
        startTime: com.google.api.client.util.DateTime,
        endTime: com.google.api.client.util.DateTime
    ) {
        val event = Event().apply {
            this.summary = summary
            this.description = description
            this.start = EventDateTime().setDateTime(startTime)
            this.end = EventDateTime().setDateTime(endTime)
        }

        service.events().insert("primary", event).execute()
    }
} 