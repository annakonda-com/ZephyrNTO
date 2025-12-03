package ru.myitschool.work.data.entity

data class Employee (
         val name: String,
         val code: String,
         val photoUrl: String,
         val bookingList: MutableList<Booking?>) {

}
