package com.example.shoppinggroceryapp.model.entities.products

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
//    foreignKeys = [ForeignKey(entity = Category::class,
//        parentColumns = ["categoryId"],
//        childColumns = ["categoryId"])]
//)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val productId:Long,
    val brandId:Long,
    val categoryName:String,
    val productName:String,
    val productDescription:String,
    val price:String,
    val offer:String,
    val productQuantity:String,
    val mainImage:String,
    val isVeg:Boolean,
    val manufactureDate:String,
    val expiryDate:String,
    val availableItems:Int
)