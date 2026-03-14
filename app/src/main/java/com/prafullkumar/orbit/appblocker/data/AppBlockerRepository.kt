package com.prafullkumar.orbit.appblocker.data

import com.prafullkumar.orbit.appblocker.data.local.AppBlockerDao
import com.prafullkumar.orbit.appblocker.data.local.BlockedAppEntity
import com.prafullkumar.orbit.appblocker.data.local.BlockScheduleEntity
import kotlinx.coroutines.flow.Flow

class AppBlockerRepository(private val dao: AppBlockerDao) {

    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>> = dao.getAllBlockedApps()

    suspend fun insertBlockedApp(app: BlockedAppEntity) = dao.insertBlockedApp(app)

    suspend fun deleteBlockedApp(packageName: String) = dao.deleteBlockedApp(packageName)

    suspend fun updateBlockedApp(app: BlockedAppEntity) = dao.updateBlockedApp(app)

    fun getAllSchedules(): Flow<List<BlockScheduleEntity>> = dao.getAllSchedules()

    suspend fun insertSchedule(schedule: BlockScheduleEntity) = dao.insertSchedule(schedule)

    suspend fun deleteSchedule(id: String) = dao.deleteSchedule(id)
}
