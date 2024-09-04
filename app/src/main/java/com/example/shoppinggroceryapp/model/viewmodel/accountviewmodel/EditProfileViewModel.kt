package com.example.shoppinggroceryapp.model.viewmodel.accountviewmodel

import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.user.User

class EditProfileViewModel(var userDao: UserDao):ViewModel() {

    fun saveDetails(oldEmail:String,firstName:String,lastName:String,email:String,phone: String){
        Thread {
            val user = userDao.getUserData(oldEmail)
            userDao.updateUser(User(
                userId = user.userId,
                userImage = user.userImage,
                userFirstName = firstName,
                userLastName = lastName,
                userEmail = email,
                userPhone = phone,
                userPassword = user.userPassword,
                dateOfBirth = user.dateOfBirth,
                isRetailer = user.isRetailer
            ))
        }.start()
    }
}