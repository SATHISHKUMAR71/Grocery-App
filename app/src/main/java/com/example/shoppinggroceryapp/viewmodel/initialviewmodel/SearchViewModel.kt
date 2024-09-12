package com.example.shoppinggroceryapp.viewmodel.initialviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao

class SearchViewModel(var userDao: UserDao):ViewModel() {

    var searchedList:MutableLiveData<MutableList<String>> = MutableLiveData()
    fun performSearch(query:String){
        Thread {
            var list = performSearchProduct(query)
            list.addAll(userDao.getProductForQuery(query).toMutableList())
            searchedList.postValue(list)
            performSearchProduct(query)
        }.start()
    }
    private fun performSearchProduct(query:String):MutableList<String>{
        var list = userDao.getProductForQueryName(query).toMutableList()
        for( j in list){
            println("J VALUE: $j")
        }
        return list
    }
}