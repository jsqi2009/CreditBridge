package com.credit.bridge.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class BirthdayDateItem(
    val year: Int,
    val month: Int,
    val day: Int,
    val displayText: String,
    val formValue: String
)

object BirthdayDateHelper {

    private const val START_YEAR = 1950
    private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000

    private val formValueFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

    private val formFormats = listOf(
        SimpleDateFormat("dd-MM-yyyy", Locale.US),
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("d-M-yyyy", Locale.US),
        SimpleDateFormat("d/M/yyyy", Locale.US)
    )

    private val displayRegex = Regex("""(\d{1,2})\s*-\s*(\d{1,2})\s*-\s*(\d{4})""")
    private val legacyDisplayRegex = Regex("""(\d{1,2})Day\s*(\d{1,2})Month\s*(\d{4})Year""")

    private fun startCalendar(): Calendar = calendarAt(START_YEAR, 1, 1)

    private fun todayCalendar(): Calendar = Calendar.getInstance(Locale.US).apply {
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun calendarAt(year: Int, month: Int, day: Int): Calendar {
        return Calendar.getInstance(Locale.US).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun getDayCount(): Int {
        val start = startCalendar()
        val end = todayCalendar()
        if (end.before(start)) return 0
        val diffMs = end.timeInMillis - start.timeInMillis
        return (diffMs / MILLIS_PER_DAY).toInt() + 1
    }

    fun getItem(dataIndex: Int): BirthdayDateItem {
        val count = getDayCount()
        val safeIndex = dataIndex.coerceIn(0, (count - 1).coerceAtLeast(0))
        val cal = startCalendar()
        cal.add(Calendar.DAY_OF_MONTH, safeIndex)
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH) + 1
        val y = cal.get(Calendar.YEAR)
        return BirthdayDateItem(
            year = y,
            month = m,
            day = d,
            displayText = formatDisplay(d, m, y),
            formValue = formValueFormat.format(cal.time)
        )
    }

    fun formatDisplay(day: Int, month: Int, year: Int): String {
        return String.format(Locale.US, "%02d - %02d - %04d", day, month, year)
    }

    fun toDisplayText(text: String?): String? {
        val calendar = parseToCalendar(text) ?: return null
        return formatDisplay(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
    }

    fun toFormValue(text: String?): String? {
        val calendar = parseToCalendar(text) ?: return null
        return formValueFormat.format(calendar.time)
    }

    fun parseToCalendar(text: String?): Calendar? {
        if (text.isNullOrBlank()) return null
        val trimmed = text.trim()
        parseRegexToCalendar(displayRegex, trimmed)?.let { return it }
        parseRegexToCalendar(legacyDisplayRegex, trimmed)?.let { return it }
        for (format in formFormats) {
            try {
                val date = format.parse(trimmed) ?: continue
                return Calendar.getInstance(Locale.US).apply {
                    time = date
                    set(Calendar.HOUR_OF_DAY, 12)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            } catch (_: Exception) {
            }
        }
        return null
    }

    private fun parseRegexToCalendar(regex: Regex, trimmed: String): Calendar? {
        val match = regex.find(trimmed) ?: return null
        return calendarAt(
            match.groupValues[3].toInt(),
            match.groupValues[2].toInt(),
            match.groupValues[1].toInt()
        )
    }

    fun resolveInitialIndex(initialFormValue: String?): Int {
        val count = getDayCount()
        if (count <= 0) return 0
        val target = parseToCalendar(initialFormValue) ?: todayCalendar()
        val start = startCalendar()
        if (target.before(start)) return 0
        val diffMs = target.timeInMillis - start.timeInMillis
        return (diffMs / MILLIS_PER_DAY).toInt().coerceIn(0, count - 1)
    }
}
