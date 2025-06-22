package com.example.projectmanagerapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    fun formatDueDate(dateLong: Long?): String {
        if (dateLong == null) return "Chưa đặt"
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(dateLong))
    }

    fun formatCommentTimestamp(dateLong: Long): String {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        return sdf.format(Date(dateLong))
    }

}