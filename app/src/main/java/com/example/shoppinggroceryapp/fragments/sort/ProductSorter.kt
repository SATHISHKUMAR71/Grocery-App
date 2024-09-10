package com.example.shoppinggroceryapp.fragments.sort

import com.example.shoppinggroceryapp.model.entities.products.Product

class ProductSorter {

    fun sortByDate(productList:List<Product>):List<Product>{
        for(i in productList){
            println(i.manufactureDate)
        }
        var sortedList =productList.sortedBy { it.manufactureDate }
        println("Sorted Date")
        for(j in sortedList){
            println(j.manufactureDate)
        }
        return sortedList
    }
    fun sortByExpiryDate(productList:List<Product>):List<Product>{
        for(i in productList){
            println(i.expiryDate)
        }
        var sortedList =productList.sortedBy { it.expiryDate }
        println("Sorted Date")
        for(j in sortedList){
            println(j.expiryDate)
        }
        return sortedList
    }
    fun sortByDiscount(productList:List<Product>):List<Product>{
        for(i in productList){
            println(i.offer)
        }
        var sortedList =productList.sortedBy { it.offer }.reversed()
        println("Sorted Date")
        for(j in sortedList){
            println(j.offer)
        }
        return sortedList
    }
    fun sortByPriceHighToLow(productList:List<Product>):List<Product>{
        for(i in productList){
            println(i.price)
        }
        var sortedList =productList.sortedBy { it.price }.reversed()
        println("Sorted Price High to Low")
        for(j in sortedList){
            println(j.price)
        }
        return sortedList
    }

    fun sortByPriceLowToHigh(productList:List<Product>):List<Product>{
        for(i in productList){
            println(i.price)
        }
        var sortedList =productList.sortedBy { it.price }
        println("Sorted Price Low To High")
        for(j in sortedList){
            println(j.price)
        }
        return sortedList
    }
}