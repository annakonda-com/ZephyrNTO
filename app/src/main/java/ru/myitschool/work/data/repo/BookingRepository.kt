package ru.myitschool.work.data.repo

import ru.myitschool.work.data.entity.Place
import ru.myitschool.work.data.source.DataStoreDataSource
import ru.myitschool.work.data.source.NetworkDataSource
import java.time.LocalDate

class BookingRepository {

    suspend fun getAvailableBookings(): Result<Map<LocalDate, List<Place>>> {
        val code = DataStoreDataSource.getAuthCode()
        if (code.isEmpty() || code == "0") {
            return Result.failure(Exception("Auth code not found"))
        }
        return NetworkDataSource.getAvailableBookings(code)
    }
}
