package ru.myitschool.work.data.repo


import android.util.Log
import io.ktor.client.statement.bodyAsText
import ru.myitschool.work.data.source.DataStoreDataSource.createAuthCode
import ru.myitschool.work.data.source.NetworkDataSource


object AuthRepository {
    private var codeCache: String? = null

    suspend fun checkAndSave(text: String): Result<Boolean> {
        val result = NetworkDataSource.checkAuth(text)
        if (result.isSuccess) {
            codeCache = text
            createAuthCode(code = text)
        }
        return result
    }
}