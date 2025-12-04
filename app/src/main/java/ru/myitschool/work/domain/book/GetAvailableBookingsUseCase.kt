package ru.myitschool.work.domain.book

import ru.myitschool.work.data.entity.Place
import ru.myitschool.work.data.repo.BookingRepository
import java.time.LocalDate

class GetAvailableBookingsUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(): Result<Map<LocalDate, List<Place>>> {
        return repository.getAvailableBookings()
    }
}