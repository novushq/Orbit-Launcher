package com.prafullkumar.usage.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import com.prafullkumar.usage.domain.AppUsageData
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

/**
 * Utility object for retrieving and processing app usage statistics
 *
 * Function Flow:
 * 1. getUsageStatsManager() - Gets system usage stats service
 * 2. createCalendar() - Creates calendar instance for specific date/time
 * 3. getEachAppsUsageFromTimeToTime() - Fetches usage data between time range
 * 4. getWeekPhoneUsageData() - Gets usage data for specific week with offset
 * 5. getCurrentWeekPhoneUsageData() - Gets current week usage data
 */
object UsageDetails {

    /**
     * Returns the system's UsageStatsManager service
     */
    private fun getUsageStatsManager(context: Context): UsageStatsManager {
        return context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    /**
     * Creates a Calendar instance set to specific date and hour
     * Used to define precise time boundaries for usage queries
     */
    private fun createCalendar(year: Int, month: Int, day: Int, hour: Int): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    /**
     * Retrieves usage statistics for installed apps within a specific time range
     * Filters out system apps and apps with zero usage time
     */
    fun getEachAppsUsageFromTimeToTime(
        context: Context,
        installedApps: Set<String>,
        startTime: Long,
        endTime: Long
    ): Map<String, UsageStats> {
        val usageStatsManager = getUsageStatsManager(context)

        // Query usage stats for the specified time interval
        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, // Use DAILY for more accurate daily data
            startTime,
            endTime
        )

        return usageStatsList
            .asSequence()
            .filter {
                // Only include apps that are installed and have actual usage time
                installedApps.contains(it.packageName) &&
                        it.packageName != "com.prafullkumar.orbit" &&
                        it.totalTimeVisible > 0
            }
            .sortedByDescending { it.totalTimeVisible }.associateBy { it.packageName }
    }

    /**
     * Gets usage data for a complete week (Sunday to Saturday)
     * @param weekOffset: 0 = current week, -1 = previous week, etc.
     * Returns a map where key is day of week (Calendar.SUNDAY to Calendar.SATURDAY)
     */
    fun getWeekPhoneUsageData(
        context: Context,
        installedApps: Set<String>,
        weekOffset: Int = 0
    ): Map<Int, List<AppUsageData>> {
        val usageData = mutableMapOf<Int, List<AppUsageData>>()

        // Calculate the target week based on offset
        val targetCalendar = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, weekOffset)
            // Set to start of week (Sunday)
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Compute LocalDate for the start of the target week once and reuse
        val weekStartDate = targetCalendar.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val usageStatsManager = getUsageStatsManager(context)

        // Get data for each day of the week
        for (dayIndex in 0..6) { // 7 days in a week
            val dayOfWeek = Calendar.SUNDAY + dayIndex

            // Proper date for the day within the target week
            val date = weekStartDate.plusDays(dayIndex.toLong())

            // Fetch usage data for this specific day using proper date
            val dailyStats = getDailyStats(
                usageStatsManager,
                date
            )

            // Convert to AppUsageData and store
            val list = dailyStats
                .filter { stat ->
                    installedApps.contains(stat.packageName) &&
                            stat.packageName != "com.prafullkumar.orbit" &&
                            stat.usageDuration > 0
                }.also {
                    Log.d("UsageDetails", "Day $dayOfWeek: ${it.toString()}")
                }
                .map { stat ->
                    stat.toAppUsageData(context)
                }
            usageData[dayOfWeek] = list
        }

        return usageData
    }

    /**
     * Convenience method to get current week usage data
     * Equivalent to calling getWeekPhoneUsageData with weekOffset = 0
     */
    fun getCurrentWeekPhoneUsageData(
        context: Context,
        installedApps: Set<String>
    ): Map<Int, List<AppUsageData>> {
        return getWeekPhoneUsageData(context, installedApps, 0)
    }

    /**
     * Gets usage data for the current day only
     * Returns the most accurate current day usage data that matches Digital Wellbeing
     */
    fun getCurrentDayPhoneUsageData(
        context: Context,
        installedApps: Set<String>
    ): Long {
        val usageStatsManager = getUsageStatsManager(context)
//        // Get current day boundaries
//        val dayStart = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 0)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }
//
//        val dayEnd = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 23)
//            set(Calendar.MINUTE, 59)
//            set(Calendar.SECOND, 59)
//            set(Calendar.MILLISECOND, 999)
//        }
//        val data = getEachAppsUsageFromTimeToTime(
//            context,
//            installedApps,
//            dayStart.timeInMillis,
//            dayEnd.timeInMillis
//        )
////        Log.d("UsageDetails", "getCurrentDayPhoneUsageData: total apps = ${data.size}")
////        data.forEach { (packageName, usageStats) ->
////            Log.d(
////                "UsageDetails",
////                "Package: $packageName, totalTimeVisible: ${usageStats.totalTimeVisible}, " +
////                        "lastTimeUsed: ${usageStats.lastTimeUsed}, totalTimeForeground: ${usageStats.totalTimeInForeground}"
////            )
////        }
//        val totalTime = data.values.sumOf { it.totalTimeVisible }
////        Log.d("UsageDetails", "getCurrentDayPhoneUsageData: totalTimeVisible sum = $totalTime")

        val dailyStats = getDailyStats(usageStatsManager)


        return dailyStats.sumOf { it.usageDuration }
    }

    private fun getDailyStats(
        usageStatsManager: UsageStatsManager,
        date: LocalDate = LocalDate.now()
    ): List<UsageStatistics> {
        // Compute local day boundaries (midnight -> next midnight) in system default zone
        val zone = ZoneId.systemDefault()
        val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val eventsByPackage = mutableMapOf<String, MutableList<UsageEvents.Event>>()

        val systemEvents = usageStatsManager.queryEvents(start, end)
        while (systemEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            systemEvents.getNextEvent(event)
            // Skip if packageName is null or empty
            val pkg = event.packageName ?: continue
            val list = eventsByPackage.getOrPut(pkg) { mutableListOf() }
            list.add(event)
        }

        val stats = mutableListOf<UsageStatistics>()

        eventsByPackage.forEach { (packageName, events) ->
            // Ensure chronological order
            events.sortBy { it.timeStamp }

            var sessionStart = 0L
            var totalTime = 0L

            fun closeSession(closeAt: Long) {
                val s = if (sessionStart == 0L) start else sessionStart
                val e = closeAt.coerceAtMost(end)
                val clampedStart = s.coerceAtLeast(start)
                val clampedEnd = e.coerceAtLeast(clampedStart)
                if (clampedEnd > clampedStart) {
                    totalTime += clampedEnd - clampedStart
                }
                sessionStart = 0L
            }

            events.forEach { e ->
                when (e.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED,
                    UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                        if (sessionStart == 0L) {
                            sessionStart = e.timeStamp.coerceAtLeast(start)
                        }
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED,
                    UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                        // If we never saw a start, assume it started before 'start'
                        closeSession(e.timeStamp)
                    }
                    else -> {
                        // ignore other events
                    }
                }
            }

            // If a session is still open at end-of-day, close it at 'end'
            if (sessionStart != 0L) {
                closeSession(end)
            }

            stats.add(
                UsageStatistics(
                    packageName = packageName,
                    usageDate = date,
                    usageDuration = totalTime
                )
            )
        }
        Log.d("UsageDetails", "getDailyStats($date): total apps = ${stats.size}")
        return stats
    }
}

data class Stat(val packageName: String, val totalTime: Long)
data class UsageStatistics(
    val packageName: String,
    val usageDate: LocalDate,
    val usageDuration: Long
) {
    fun toAppUsageData(context: Context): AppUsageData {
        val appData = context.packageManager.getApplicationInfo(packageName, 0)
        val appName = context.packageManager.getApplicationLabel(appData).toString()
        val icon = context.packageManager.getApplicationIcon(appData)
        return AppUsageData(
            packageName = packageName,
            appName = appName,
            icon = icon,
            totalTimeInForeground = usageDuration
        )
    }
}