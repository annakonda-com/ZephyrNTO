package ru.myitschool.work.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.myitschool.work.core.OurConstants.DS_AUTH_KEY

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
val AUTH_KEY = stringPreferencesKey(DS_AUTH_KEY)

object DataStoreDataSource {
    fun authFlow(context : Context): Flow<String> = context.dataStore.data.map { preferences ->
        (preferences[AUTH_KEY] ?: 0).toString()
    }
    // TODO: разобраться с контекстом
    suspend fun createAuthCode (context : Context, code : String) {
        context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[AUTH_KEY] = code
            }
        }
    }
}