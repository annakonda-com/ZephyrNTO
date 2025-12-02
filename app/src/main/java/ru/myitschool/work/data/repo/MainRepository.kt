package ru.myitschool.work.data.repo

import ru.myitschool.work.data.entity.Employee
import ru.myitschool.work.data.source.DataStoreDataSource
import ru.myitschool.work.data.source.DataStoreDataSource.createAuthCode
import ru.myitschool.work.data.source.DataStoreDataSource.getAuthCode
import ru.myitschool.work.data.source.NetworkDataSource

class MainRepository {
    private var employee: Employee? = null

    suspend fun getUserInfo(): Result<Employee> {
        return try {
            val code = getCode()
            val result = NetworkDataSource.getUserInfo(code)
            result.onSuccess { success ->
                employee = success
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun getCode(): String {
        return getAuthCode()
    }

    suspend fun logOut(){
        DataStoreDataSource.logOut()
    }
}