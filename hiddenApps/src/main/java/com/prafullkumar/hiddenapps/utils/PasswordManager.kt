package com.prafullkumar.hiddenapps.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Utility class for password encryption/decryption and key management
 */
class PasswordManager(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("hidden_apps_prefs", Context.MODE_PRIVATE)
    
    private companion object {
        const val KEY_ENCRYPTION_KEY = "encryption_key"
        const val ALGORITHM = "AES"
        const val TRANSFORMATION = "AES"
    }
    
    /**
     * Generates and stores encryption key in SharedPreferences
     */
    private fun generateAndStoreKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(256)
        val secretKey = keyGenerator.generateKey()
        
        val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
        sharedPreferences.edit { putString(KEY_ENCRYPTION_KEY, encodedKey) }
        
        return secretKey
    }
    
    /**
     * Retrieves encryption key from SharedPreferences
     */
    private fun getStoredKey(): SecretKey? {
        val encodedKey = sharedPreferences.getString(KEY_ENCRYPTION_KEY, null) ?: return null
        val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)
        return SecretKeySpec(decodedKey, ALGORITHM)
    }
    
    /**
     * Encrypts password and returns Base64 encoded string
     */
    fun encryptPassword(password: String): String {
        val secretKey = getStoredKey() ?: generateAndStoreKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val encryptedBytes = cipher.doFinal(password.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }
    
    /**
     * Decrypts Base64 encoded password
     */
    fun decryptPassword(encryptedPassword: String): String? {
        return try {
            val secretKey = getStoredKey() ?: return null
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            
            val encryptedBytes = Base64.decode(encryptedPassword, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Verifies if entered password matches stored password
     */
    fun verifyPassword(enteredPassword: String, storedEncryptedPassword: String): Boolean {
        val decryptedPassword = decryptPassword(storedEncryptedPassword)
        return decryptedPassword == enteredPassword
    }

    fun isPasswordSet(): Boolean {
        return getStoredKey() != null
    }
}
