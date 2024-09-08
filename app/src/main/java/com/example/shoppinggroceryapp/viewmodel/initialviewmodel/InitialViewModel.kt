package com.example.shoppinggroceryapp.viewmodel.initialviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao

class InitialViewModel(var userDao: UserDao):ViewModel() {

    var searchedList:MutableLiveData<MutableList<String>> = MutableLiveData()
    fun performSearch(query:String){
        Thread {
            searchedList.postValue(userDao.getProductForQuery(query).toMutableList())
        }.start()
    }
}