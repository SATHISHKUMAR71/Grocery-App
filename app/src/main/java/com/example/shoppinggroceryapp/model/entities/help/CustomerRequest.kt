package com.example.shoppinggroceryapp.model.entities.help

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CustomerRequest(
    @PrimaryKey(autoGenerate = true)
    val helpId:Int,
    val orderId:Int,
    val request:String
)