package com.example.projectmanagerapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    fun Long?.formatDueDate(): String {
        if (this == null) return "Chưa đặt"
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return sdf.format(Date(this))
    }
    fun Long.formatCommentTimestamp(): String {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        return sdf.format(Date(this))
    }
}