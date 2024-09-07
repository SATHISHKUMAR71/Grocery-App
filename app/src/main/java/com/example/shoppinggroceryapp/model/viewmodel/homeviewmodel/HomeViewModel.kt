package com.example.shoppinggroceryapp.model.viewmodel.homeviewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.ProductDao
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.Product

class HomeViewModel(var productDao: ProductDao):ViewModel() {

    var recentlyViewedList:MutableLiveData<MutableList<Product>> = MutableLiveData()
    fun getRecentlyViewedItems(recentlyViewedItems:SharedPreferences){
        Thread{
            var i = 0
            var j =0
            val list = mutableListOf<Product>()
            while (true) {
                i = recentlyViewedItems.getInt("product$j", -1)
                if(i==-1){
                    break
                }
                list.add(productDao.getProductById(i.toLong()))
                j++
            }
            println("$$$ Product Added $j")
            recentlyViewedList.postValue(list)
        }.start()
    }
}