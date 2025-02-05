package com.android.appclock.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateTimeUtil {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Convert Long (epoch millis) to LocalDateTime
    fun fromEpochMillis(epochMillis: Long): LocalDateTime {
        return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    // Convert LocalDateTime to Long (epoch millis)
    fun toEpochMillis(date: LocalDate, time: LocalTime): Long {
        return try {
            return LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    // Get formatted date string (yyyy-MM-dd)
    fun getFormattedDate(timestamp: Long? = null): String {
        return try {
            val dateTime = Instant.ofEpochMilli(timestamp ?: System.currentTimeMillis())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateTime)
        } catch (e: Exception) {
            DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now())
        }
    }

    // Get formatted date string (yyyy-MM-dd)
    fun getFormattedDate2(timestamp: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // "2025-02-05"
        return formatter.format(Date(timestamp))
    }

    // Get formatted time string (HH:mm)
    fun getFormattedTime(timestamp: Long? = null): String {
        return try {
            val dateTime = Instant.ofEpochMilli(timestamp ?: System.currentTimeMillis())
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
            val time = DateTimeFormatter.ofPattern("HH:mm").format(dateTime)
            time
        } catch (e: Exception) {
            val time = DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.now())
            time
        }
    }

    // Get formatted time string (HH:mm AM)
    fun getFormattedTime2(timestamp: Long): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())  // "09:00 AM"
        return formatter.format(Date(timestamp))
    }

    // Validate and return current date if invalid
    fun validateDate(date: String?): LocalDate {
        return try {
            LocalDate.parse(date, dateFormatter)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    // Validate and return current time if invalid
    fun validateTime(time: String?): LocalTime {
        return try {
            LocalTime.parse(time, timeFormatter)
        } catch (e: Exception) {
            LocalTime.now()
        }
    }
}