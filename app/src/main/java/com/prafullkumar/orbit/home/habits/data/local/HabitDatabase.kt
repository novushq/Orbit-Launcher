
package com.prafullkumar.orbit.home.habits.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.prafullkumar.orbit.home.habits.model.EnvironmentTag
import com.prafullkumar.orbit.home.habits.model.EnergyLevel
import com.prafullkumar.orbit.home.habits.model.FailureReason
import com.prafullkumar.orbit.home.habits.model.HabitDifficulty
import com.prafullkumar.orbit.home.habits.model.HabitType

class HabitConverters {
    @TypeConverter fun habitTypeToString(v: HabitType): String = v.name
    @TypeConverter fun stringToHabitType(v: String): HabitType = HabitType.valueOf(v)

    @TypeConverter fun difficultyToString(v: HabitDifficulty): String = v.name
    @TypeConverter fun stringToDifficulty(v: String): HabitDifficulty = HabitDifficulty.valueOf(v)

    @TypeConverter fun envTagToString(v: EnvironmentTag): String = v.name
    @TypeConverter fun stringToEnvTag(v: String): EnvironmentTag = EnvironmentTag.valueOf(v)

    @TypeConverter fun failureToString(v: FailureReason?): String? = v?.name
    @TypeConverter fun stringToFailure(v: String?): FailureReason? = v?.let { FailureReason.valueOf(it) }

    @TypeConverter fun energyToString(v: EnergyLevel?): String? = v?.name
    @TypeConverter fun stringToEnergy(v: String?): EnergyLevel? = v?.let { EnergyLevel.valueOf(it) }
}

@Database(
    entities = [HabitEntity::class, HabitLogEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(HabitConverters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}
