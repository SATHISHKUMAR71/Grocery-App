package com.example.shoppinggroceryapp.fragments

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateGenerator {

    companion object {

        private val months = listOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
        )
        @SuppressLint("NewApi")
        fun getCurrentDate(): String {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return currentDate.format(formatter)
        }

        @SuppressLint("NewApi")
        fun getDeliveryDate(): String {
            val currentDate = LocalDate.now()
            val nextDay = currentDate.plusDays(1)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return nextDay.format(formatter)
        }

        @SuppressLint("NewApi")
        fun compareDeliveryStatus(currentDate:String, deliveryDate:String):String{
            val currentDateList = currentDate.split("-")
            val deliveryDateList = deliveryDate.split("-")
            val current = LocalDate.of(currentDateList[0].toInt(),currentDateList[1].toInt(),currentDateList[2].toInt())
            val delivery = LocalDate.of(deliveryDateList[0].toInt(),deliveryDateList[1].toInt(),deliveryDateList[2].toInt())
            if(current.isBefore(delivery)){
                return "Pending"
            }
            else if(current.isAfter(delivery)){
                return "Delayed"
            }
            else{
                return "Out For Delivery"
            }
        }

        fun getDayAndMonth(date:String):String{
            val currentDateList = date.split("-")
            val month = currentDateList[1].toInt()
            val day = currentDateList[2].toInt()
            return "${months[month-1]} $day, ${currentDateList[0]}"
        }

    }
}