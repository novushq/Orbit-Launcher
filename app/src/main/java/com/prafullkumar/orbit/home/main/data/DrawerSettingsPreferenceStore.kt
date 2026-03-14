package com.prafullkumar.orbit.home.main.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.prafullkumar.orbit.home.main.presentation.screens.drawerSettings.LayoutType
import com.prafullkumar.orbit.home.main.presentation.screens.drawerSettings.SortingOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "drawer_settings")


class DrawerSettingsPreferenceStore(
    private val context: Context
) {
    private object Keys {
        const val KEY_LAYOUT_TYPE = "layout_type"
        const val KEY_SORTING_ORDER = "sorting_order"
    }

    val layoutType: Flow<LayoutType> = context.dataStore.data.map { preferences ->
        val layoutTypeValue =
            preferences[stringPreferencesKey(Keys.KEY_LAYOUT_TYPE)] ?: LayoutType.LIST.name
        LayoutType.valueOf(layoutTypeValue)
    }

    val sortingOrder: Flow<SortingOrder> = context.dataStore.data.map { preferences ->
        val sortingOrderValue =
            preferences[stringPreferencesKey(Keys.KEY_SORTING_ORDER)]
                ?: SortingOrder.ALPHABETICAL.name
        SortingOrder.valueOf(sortingOrderValue)
    }

    suspend fun setLayoutType(layoutType: LayoutType) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(Keys.KEY_LAYOUT_TYPE)] = layoutType.name
        }
    }

    suspend fun setSortingOrder(order: SortingOrder) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(Keys.KEY_SORTING_ORDER)] = order.name
        }
    }

}