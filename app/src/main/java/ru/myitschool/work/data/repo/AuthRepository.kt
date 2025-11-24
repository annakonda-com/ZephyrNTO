package ru.myitschool.work.data.repo

import android.content.Context
import ru.myitschool.work.data.source.DataStoreDataSource.createAuthCode
import ru.myitschool.work.data.source.NetworkDataSource

object AuthRepository {
    private var codeCache: String? = null
    // TODO: разобраться с контекстом
    suspend fun checkAndSave(text: String): Result<Boolean> {
        return NetworkDataSource.checkAuth(text).onSuccess { success ->
            if (success) {
                codeCache = text
                createAuthCode(context = appContext, code = text)
            }
        }
    }
}