package com.example.shoppinggroceryapp.viewmodel.homeviewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.ProductDao
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.Product

class HomeViewModel(var productDao: ProductDao):ViewModel() {

    var recentlyViewedList:MutableLiveData<MutableList<Product>> = MutableLiveData()
    fun getRecentlyViewedItems(){
        Thread{
            val list = mutableListOf<Product>()
            val recentlyViewedProduct = productDao.getRecentlyViewedProducts()
            for(i in recentlyViewedProduct){
                list.add(productDao.getProductById(i.toLong()))
            }
            recentlyViewedList.postValue(list)
        }.start()
    }
}