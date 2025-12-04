package ru.myitschool.work.data.source

import android.content.Context
import android.util.Log
import androidx.compose.material3.rememberTimePickerState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.myitschool.work.App
import ru.myitschool.work.core.OurConstants.DS_AUTH_KEY


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
val AUTH_KEY = stringPreferencesKey(DS_AUTH_KEY)

object DataStoreDataSource {
    fun authFlow(): Flow<String> {
        return App.context.dataStore.data.map { preferences ->
            (preferences[AUTH_KEY] ?: 0).toString()
        }
    }

    suspend fun createAuthCode(code: String) {
        App.context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[AUTH_KEY] = code
            }
        }
    }

    suspend fun getAuthCode(): String {
        return App.context.dataStore.data.map { preferences ->
            preferences[AUTH_KEY] ?: ""
        }.first()
    }

    suspend fun logOut() {
        App.context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences.remove(AUTH_KEY)
            }
        }
    }
}