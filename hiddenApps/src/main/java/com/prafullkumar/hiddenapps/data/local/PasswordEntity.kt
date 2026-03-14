package com.prafullkumar.hiddenapps.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing encrypted password data
 */
@Entity(tableName = "password_table")
data class PasswordEntity(
    @PrimaryKey
    val id: Int = 1, // Single password entry
    val encryptedPassword: String, // Base64 encoded encrypted password
    val createdAt: Long = System.currentTimeMillis()
)
