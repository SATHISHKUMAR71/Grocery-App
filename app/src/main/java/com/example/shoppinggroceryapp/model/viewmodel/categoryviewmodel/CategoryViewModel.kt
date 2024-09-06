package com.example.shoppinggroceryapp.model.viewmodel.categoryviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.ProductDao
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory

class CategoryViewModel(var productDao: ProductDao):ViewModel() {

    var parentList:MutableLiveData<List<ParentCategory>> = MutableLiveData()
    fun getParentCategory(){
        Thread{
            parentList.postValue(productDao.getParentCategoryList())
        }.start()
    }
}