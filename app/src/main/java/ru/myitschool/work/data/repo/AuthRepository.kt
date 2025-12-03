package ru.myitschool.work.data.repo


import ru.myitschool.work.data.source.DataStoreDataSource.createAuthCode
import ru.myitschool.work.data.source.NetworkDataSource


object AuthRepository {
    private var codeCache: String? = null

    suspend fun checkAndSave(text: String): Result<Boolean> {
        return try {
            val result = NetworkDataSource.checkAuth(text)

            when {
                result.isSuccess && result.getOrNull() == true -> {
                    codeCache = text
                    createAuthCode(code = text)
                    Result.success(true)
                }
                result.isFailure -> {
                    val exception = result.exceptionOrNull()
                    val errorMessage = exception?.message ?: "Ошибка авторизации"
                    Result.failure(Exception(errorMessage))
                }
                else -> {
                    Result.success(false)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}