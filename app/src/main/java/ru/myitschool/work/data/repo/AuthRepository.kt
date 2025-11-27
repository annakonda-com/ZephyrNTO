package ru.myitschool.work.data.repo

import android.content.Context
import ru.myitschool.work.data.source.DataStoreDataSource.createAuthCode
import ru.myitschool.work.data.source.NetworkDataSource

object AuthRepository {
    private var codeCache: String? = null
    suspend fun checkAndSave(text: String): Result<Boolean> {
        /* return NetworkDataSource.checkAuth(text).onSuccess { success ->
            if (success) {
                codeCache = text
                createAuthCode(code = text)
            }
        }
    } */
        codeCache = text
        createAuthCode(code = text)
        return Result.success(true) // TODO: ВЕРНУТЬ СЕТЕВОЙ ЗАПРОС
    }
}