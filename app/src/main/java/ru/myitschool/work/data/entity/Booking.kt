package ru.myitschool.work.data.entity

import java.time.LocalDate


data class Booking ( val id: Long,
         val date: LocalDate,
         val place: Place,
         val employeeCode: String){

}