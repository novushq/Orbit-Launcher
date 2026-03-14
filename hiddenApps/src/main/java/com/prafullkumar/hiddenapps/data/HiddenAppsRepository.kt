package com.prafullkumar.hiddenapps.data

import android.content.Context
import com.prafullkumar.hiddenapps.data.local.HiddenAppsDatabase
import com.prafullkumar.hiddenapps.data.local.HiddenAppsEntity
import com.prafullkumar.hiddenapps.data.local.PasswordEntity
import com.prafullkumar.hiddenapps.utils.PasswordManager
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Repository for hidden apps and password management
 */
class HiddenAppsRepository : KoinComponent {
    
    private val database: HiddenAppsDatabase by inject()
    private val context: Context by inject()
    private val passwordManager = PasswordManager(context)
    
    // Existing hidden apps methods
    fun getAllHiddenApps(): Flow<List<HiddenAppsEntity>> {
        return database.hiddenAppsDao().getAll()
    }
    
    suspend fun deleteHiddenApp(packageName: String) {
        database.hiddenAppsDao().deleteByPackageName(packageName)
    }
    
    // Password management methods
    
    /**
     * Checks if password is already set
     */
    suspend fun isPasswordSet(): Boolean {
        return database.passwordDao().isPasswordSet()
    }
    
    /**
     * Sets new password (encrypted)
     */
    suspend fun setPassword(password: String) {
        val encryptedPassword = passwordManager.encryptPassword(password)
        val passwordEntity = PasswordEntity(encryptedPassword = encryptedPassword)
        database.passwordDao().insertPassword(passwordEntity)
    }
    
    /**
     * Verifies entered password against stored password
     */
    suspend fun verifyPassword(enteredPassword: String): Boolean {
        val storedPassword = database.passwordDao().getPassword()
        return if (storedPassword != null) {
            passwordManager.verifyPassword(enteredPassword, storedPassword.encryptedPassword)
        } else {
            false
        }
    }
    
    /**
     * Updates existing password
     */
    suspend fun updatePassword(newPassword: String) {
        val encryptedPassword = passwordManager.encryptPassword(newPassword)
        val passwordEntity = PasswordEntity(encryptedPassword = encryptedPassword)
        database.passwordDao().insertPassword(passwordEntity)
    }

    suspend fun insertApp(hiddenAppsEntity: HiddenAppsEntity) {
        database.hiddenAppsDao().insert(hiddenAppsEntity)
    }
}