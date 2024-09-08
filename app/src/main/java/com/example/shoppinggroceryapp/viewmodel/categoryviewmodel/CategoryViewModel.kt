package com.example.shoppinggroceryapp.viewmodel.categoryviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.ProductDao
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.dataclass.ChildCategoryName
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory

class CategoryViewModel(var productDao: ProductDao):ViewModel() {

    var parentList:MutableLiveData<List<ParentCategory>> = MutableLiveData()
    var childList:MutableLiveData<List<List<ChildCategoryName>>> = MutableLiveData()
    fun getParentCategory(){
        Thread{
            parentList.postValue(productDao.getParentCategoryList())
        }.start()
    }

    fun getChildWithParentName(){
        Thread{
            var list = mutableListOf<List<ChildCategoryName>>()
            for(i in parentList.value!!){
                list.add(productDao.getChildName(i.parentCategoryName))
            }
            childList.postValue(list)
        }.start()
    }
}