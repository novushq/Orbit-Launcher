package com.prafullkumar.usage.data

import android.Manifest
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.Calendar

object AppUsageDetails {

    fun getAppUsageTimeToday(context: Context, packageName: String): Long? {

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = getCalendar()
        val endTime = Calendar.getInstance().timeInMillis
        val startTime = calendar.timeInMillis

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        Log.d("AppUsageDetails", "usageStatsList: $usageStatsList")
        val appStats = usageStatsList.find { it.packageName == packageName }
        return appStats?.totalTimeVisible ?: 0L
    }

    @RequiresPermission(Manifest.permission.PACKAGE_USAGE_STATS)
    fun getNumberOfTimesAppOpens(context: Context, packageName: String): Int {
        return numberOfTimesAppOpenedInTimeRange(
            context,
            packageName,
            getCalendar().timeInMillis,
            Calendar.getInstance().timeInMillis
        )
    }

    fun numberOfTimesAppOpenedInTimeRange(
        context: Context,
        packageName: String,
        startTime: Long,
        endTime: Long
    ): Int {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val events = usageStatsManager.queryEvents(startTime, endTime)
        var openCount = 0

        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.packageName == packageName &&
                event.eventType == UsageEvents.Event.ACTIVITY_RESUMED
            ) {
                openCount++
            }
        }

        Log.d(
            "AppUsageDetails",
            "App $packageName opened $openCount times between $startTime and $endTime"
        )
        return openCount
    }

    fun getAppOpenCountByDay(
        context: Context,
        packageName: String,
        numberOfDays: Int = 7
    ): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance()

        for (i in 0 until numberOfDays) {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endTime = calendar.timeInMillis

            val dateKey = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
                calendar.get(Calendar.DAY_OF_MONTH)
            }"
            val openCount =
                numberOfTimesAppOpenedInTimeRange(context, packageName, startTime, endTime)
            result[dateKey] = openCount

            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return result
    }

    fun getAppOpenCountByHour(
        context: Context,
        packageName: String,
        targetDate: Calendar = Calendar.getInstance()
    ): Map<Int, Int> {
        val result = mutableMapOf<Int, Int>()
        val calendar = targetDate.clone() as Calendar

        for (hour in 0..23) {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endTime = calendar.timeInMillis

            val openCount =
                numberOfTimesAppOpenedInTimeRange(context, packageName, startTime, endTime)
            result[hour] = openCount
        }

        return result
    }
}

fun getCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar
}