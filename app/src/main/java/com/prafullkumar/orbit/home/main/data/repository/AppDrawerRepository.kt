package com.prafullkumar.orbit.home.main.data.repository

import android.content.Context
import android.widget.Toast
import com.prafullkumar.hiddenapps.data.local.HiddenAppsDao
import com.prafullkumar.hiddenapps.data.local.HiddenAppsEntity
import com.prafullkumar.orbit.core.data.local.fav.FavDao
import com.prafullkumar.orbit.core.data.local.fav.FavEntity
import com.prafullkumar.orbit.core.model.AppInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppDrawerRepository(private val context: Context) : KoinComponent {

    private val favDao by inject<FavDao>()
    private val hiddenAppsDao by inject<HiddenAppsDao>()
    suspend fun addToFavourites(app: AppInfo) {
        val count = favDao.getFavoriteCount()
        if (count >= 7) {
            Toast.makeText(context, "You can only add up to 7 favorites", Toast.LENGTH_SHORT).show()
            return
        }
        favDao.insertFavorite(
            FavEntity(
                packageName = app.packageName, label = app.label
            )
        )
    }

    suspend fun removeFromFavourites(packageName: String) {
        try {
            favDao.deleteFavorite(packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun addToHiddenApps(packageName: String, label: String) {
        hiddenAppsDao.insert(
            HiddenAppsEntity(
                packageName = packageName,
                label = label,
                timeStamp = System.currentTimeMillis()
            )
        )
    }
}
