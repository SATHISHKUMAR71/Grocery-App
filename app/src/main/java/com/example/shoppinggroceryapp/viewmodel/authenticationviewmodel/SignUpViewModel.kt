package com.example.shoppinggroceryapp.viewmodel.authenticationviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.user.User

class SignUpViewModel(var userDao: UserDao):ViewModel() {
    var registrationStatus:MutableLiveData<Boolean> = MutableLiveData()

    fun registerNewUser(user: User){
        Thread{
            val email = userDao.getUserData(user.userEmail)
            val phone = userDao.getUserData(user.userPhone)
            if(phone==null&&email==null) {
                userDao.addUser(user)
                registrationStatus.postValue(true)
            }
            else{
                registrationStatus.postValue(false)
            }
        }.start()
    }
}