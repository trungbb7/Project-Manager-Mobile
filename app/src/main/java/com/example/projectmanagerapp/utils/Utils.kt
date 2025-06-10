package com.example.projectmanagerapp.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    fun formatDueDate(dateLong: Long?): String {
        if (dateLong == null) return "Chưa đặt"
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return sdf.format(Date(dateLong))
    }
    fun formatCommentTimestamp(dateLong: Long): String {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        return sdf.format(Date(dateLong))
    }

}