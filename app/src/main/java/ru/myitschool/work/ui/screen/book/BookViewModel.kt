package ru.myitschool.work.ui.screen.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.myitschool.work.App
import ru.myitschool.work.R
import ru.myitschool.work.data.entity.Place
import ru.myitschool.work.data.repo.BookingRepository
import ru.myitschool.work.domain.book.CreateBookingUseCase
import ru.myitschool.work.domain.book.GetAvailableBookingsUseCase
import java.time.LocalDate

class BookViewModel : ViewModel() {
    private val repository by lazy { BookingRepository() }
    private val getAvailableBookingsUseCase by lazy { GetAvailableBookingsUseCase(repository) }
    private val createBookingUseCase by lazy { CreateBookingUseCase(repository) }


    private val _uiState = MutableStateFlow<BookState>(BookState.Loading)
    val uiState: StateFlow<BookState> = _uiState.asStateFlow()

    private val _actionFlow = MutableSharedFlow<BookAction>()
    val actionFlow: SharedFlow<BookAction> = _actionFlow

    private var selectedPlaceId: Long? = null

    init {
        loadBookData()
    }

    fun onIntent(intent: BookIntent) {
        when (intent) {
            is BookIntent.LoadData -> loadBookData()
            is BookIntent.Refresh -> refresh()
            is BookIntent.BookPlace -> bookPlace()
            is BookIntent.SelectDate -> selectDate(intent.date)
            is BookIntent.SelectPlace -> selectPlace(intent.place)
        }
    }

    private fun loadBookData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { BookState.Loading }

            getAvailableBookingsUseCase().fold(
                onSuccess = { bookings ->
                    if (bookings.isEmpty()) {
                        _uiState.update {
                            BookState.Data(
                                isError = true,
                                errorMessage = App.context.getString(R.string.error_no_available_dates)
                            )
                        }
                    } else {
                        val dates = bookings.keys.toList()
                        _uiState.update {
                            BookState.Data(
                                dates = dates,
                                places = bookings,
                                selectedDate = dates.first(),
                                selectedPlace = null,
                                isError = false,
                                errorMessage = null
                            )
                        }
                    }
                },
                onFailure = { error ->
                    error.printStackTrace()
                    _uiState.update {
                        BookState.Data(
                            isError = true,
                            errorMessage = error.message ?: App.context.getString(R.string.error_loading_data)
                        )
                    }
                }
            )
        }
    }

    private fun selectDate(date: LocalDate) {
        _uiState.update { currentState ->
            if (currentState is BookState.Data) {
                currentState.copy(
                    selectedDate = date,
                    selectedPlace = null
                )
            } else {
                currentState
            }
        }
        selectedPlaceId = null
    }

    private fun selectPlace(place: Place) {
        _uiState.update { currentState ->
            if (currentState is BookState.Data) {
                currentState.copy(selectedPlace = place)
            } else {
                currentState
            }
        }
        selectedPlaceId = place.id
    }

    private fun bookPlace() {
        val currentState = _uiState.value
        if (currentState is BookState.Data && currentState.selectedPlace != null && currentState.selectedDate != null) {
            val placeId = selectedPlaceId ?: return
            val date = currentState.selectedDate

            viewModelScope.launch(Dispatchers.IO) {
                createBookingUseCase (date, placeId).fold(
                    onSuccess = {
                        _actionFlow.emit(BookAction.BookSuccess)
                    },
                    onFailure = { error ->
                        error.printStackTrace()
                        _actionFlow.emit(BookAction.ShowError(error.message ?: App.context.getString(R.string.error_booking_default)))
                    }
                )
            }
        }
    }

    private fun refresh() {
        loadBookData()
    }
}
