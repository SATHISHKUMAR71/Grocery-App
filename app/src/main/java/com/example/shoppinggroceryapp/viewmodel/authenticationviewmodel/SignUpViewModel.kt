package com.example.shoppinggroceryapp.viewmodel.authenticationviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.user.User

class SignUpViewModel(var userDao: UserDao):ViewModel() {
    var registrationStatus:MutableLiveData<Boolean> = MutableLiveData()
    var registrationStatusInt:MutableLiveData<Int> = MutableLiveData()

    fun registerNewUser(user: User){
        Thread{
            val email = userDao.getUserData(user.userEmail)
            val phone = userDao.getUserData(user.userPhone)
            if(phone==null&&email==null) {
                userDao.addUser(user)
                registrationStatusInt.postValue(0)
                registrationStatus.postValue(true)
            }
            else if(phone!=null && email!=null){
                registrationStatusInt.postValue(1)
//                registrationStatus.postValue(false)
            }
            else if(phone!=null){
                registrationStatusInt.postValue(2)
            }
            else{
                registrationStatusInt.postValue(3)
            }
        }.start()
    }
}