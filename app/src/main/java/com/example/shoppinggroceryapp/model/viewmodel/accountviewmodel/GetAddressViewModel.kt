package com.example.shoppinggroceryapp.model.viewmodel.accountviewmodel

import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.user.Address

class GetAddressViewModel(var userDao: UserDao):ViewModel() {
    fun addAddress(address:Address){
        Thread{
            userDao.addAddress(address)
        }.start()
    }
}