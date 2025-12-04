package ru.myitschool.work.data.source

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.myitschool.work.core.Constants
import ru.myitschool.work.data.entity.Employee
import kotlinx.serialization.json.*
import ru.myitschool.work.App
import ru.myitschool.work.R
import ru.myitschool.work.data.entity.Booking
import ru.myitschool.work.data.entity.Place
import java.time.LocalDate

object NetworkDataSource {
    private val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = true
                        encodeDefaults = true
                    }
                )
            }
        }
    }

    suspend fun checkAuth(code: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val response = client.get(getUrl(code, Constants.AUTH_URL))

            when (response.status) {
                HttpStatusCode.OK -> true
                HttpStatusCode.Unauthorized -> error(App.context.getString(R.string.auth_wrong_code))
                else -> error(App.context.getString(R.string.error_request, response.bodyAsText()))
            }
        }
    }

    suspend fun getUserInfo(code: String): Result<Employee> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val response = client.get(getUrl(code, Constants.INFO_URL))

            when (response.status) {
                HttpStatusCode.OK -> {
                    val json = response.bodyAsText()
                    if (json.isBlank()) {
                        error(App.context.getString(R.string.error_empty_server_response))
                    }

                    val jsonObject = try {
                        Json.parseToJsonElement(json).jsonObject
                    } catch (e: Exception) {
                        error(App.context.getString(R.string.error_parsing, e.message))
                    }
                    val name = jsonObject["name"]?.jsonPrimitive?.content
                        ?: error(App.context.getString(R.string.error_missing_name_field))
                    val photoUrl = jsonObject["photoUrl"]?.jsonPrimitive?.content
                        ?: error(App.context.getString(R.string.error_missing_photo_url_field))

                    val bookingJson = jsonObject["booking"]?.jsonObject
                        ?: error(App.context.getString(R.string.error_missing_booking_field))

                    val employee = Employee(
                        name = name,
                        code = code,
                        photoUrl = photoUrl,
                        bookingList = mutableListOf()
                    )
                    val bookingList = mutableListOf<Booking>()
                    for ((dateString, bookingElement) in bookingJson) {
                        val date = LocalDate.parse(dateString)
                        val bookingObj = bookingElement.jsonObject
                        val bookingId = bookingObj["id"]?.jsonPrimitive?.long
                            ?: error(App.context.getString(R.string.error_missing_id_field))
                        val placeString = bookingObj["place"]?.jsonPrimitive?.content
                            ?: error(App.context.getString(R.string.error_missing_place_field, dateString))

                        if (placeString.isBlank()) {
                            error(App.context.getString(R.string.error_empty_place_field, dateString))
                        }

                        val placeId = bookingId
                        val place = Place(placeId, placeString)

                        val booking = Booking(
                            id = bookingId,
                            date = date,
                            place = place,
                            employeeCode = employee.code
                        )
                        bookingList.add(booking)
                    }
                    /* if (bookingList.isEmpty()) {
                        error(App.context.getString(R.string.error_booking_list_empty))
                    }*/
                    employee.bookingList.addAll(bookingList)
                    employee
                }
                else -> error(response.bodyAsText())
            }
        }
    }

    suspend fun getAvailableBookings(code: String): Result<Map<LocalDate, List<Place>>> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val response = client.get(getUrl(code, Constants.BOOKING_URL))

            when (response.status) {
                HttpStatusCode.OK -> {
                    val json = response.bodyAsText()
                    val jsonObject = Json.parseToJsonElement(json).jsonObject
                    val availableBookings = mutableMapOf<LocalDate, List<Place>>()

                    for ((dateString, placesArray) in jsonObject) {
                        val date = LocalDate.parse(dateString)
                        val places = placesArray.jsonArray.map { placeElement ->
                            val placeObj = placeElement.jsonObject
                            val id = placeObj["id"]?.jsonPrimitive?.long
                                ?: error(App.context.getString(R.string.error_missing_id_in_place))
                            val placeName = placeObj["place"]?.jsonPrimitive?.content
                                ?: error(App.context.getString(R.string.error_missing_place_in_place))
                            Place(id, placeName)
                        }
                        if (places.isNotEmpty()) {
                            availableBookings[date] = places
                        }
                    }
                    availableBookings.toSortedMap()
                }

                else -> error(App.context.getString(R.string.error_request, response.bodyAsText()))
            }
        }
    }

    @Serializable
    private data class CreateBookingBody(val date: String, val placeID: Long)

    suspend fun createBooking(code: String, date: LocalDate, placeId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            // Формируем тело запроса
            val requestBody = CreateBookingBody(date.toString(), placeId)

            val response = client.post(getUrl(code, Constants.BOOKING_URL)) { // Используем ту же константу BOOKING_URL
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            when (response.status) {
                HttpStatusCode.OK -> true
                else -> {
                    val errorBody = response.bodyAsText()
                    error(if (errorBody.isNotBlank()) App.context.getString(R.string.error_booking, errorBody) else App.context.getString(R.string.error_booking_default))
                }
            }
        }
    }
    private fun getUrl(code: String, targetUrl: String) = "${Constants.HOST}/api/$code$targetUrl"
}
