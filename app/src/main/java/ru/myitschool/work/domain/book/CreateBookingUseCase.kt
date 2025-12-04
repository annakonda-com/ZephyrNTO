package ru.myitschool.work.domain.book

import ru.myitschool.work.data.repo.BookingRepository
import java.time.LocalDate

class CreateBookingUseCase(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(date: LocalDate, placeId: Long): Result<Boolean> {
        return repository.createBooking(date, placeId)
    }
}