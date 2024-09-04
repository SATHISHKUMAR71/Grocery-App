package com.example.shoppinggroceryapp.model.viewmodel.authenticationviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.user.User

class LoginViewModel(var userDao: UserDao) :ViewModel(){
    var user:MutableLiveData<User> = MutableLiveData()

    fun validateUser(email:String,password:String){
        Thread {
            user.postValue(userDao.getUser(email, password))
        }.start()
    }


}