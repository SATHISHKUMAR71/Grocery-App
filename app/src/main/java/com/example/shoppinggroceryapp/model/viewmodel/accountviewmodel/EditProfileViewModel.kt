package com.example.shoppinggroceryapp.model.viewmodel.accountviewmodel

import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.user.User

class EditProfileViewModel(var userDao: UserDao):ViewModel() {

    fun saveDetails(oldEmail:String,firstName:String,lastName:String,email:String,phone: String,image:String){
        Thread {
            val user = userDao.getUserData(oldEmail)
            val userTmp = User(
                userId = user.userId,
                userImage = image,
                userFirstName = firstName,
                userLastName = lastName,
                userEmail = email,
                userPhone = phone,
                userPassword = user.userPassword,
                dateOfBirth = user.dateOfBirth,
                isRetailer = user.isRetailer)
            println(userTmp)
            userDao.updateUser(userTmp)
        }.start()
    }

    fun saveUserImage(oldEmail:String,mainImage:String){
        Thread {
            val user = userDao.getUserData(oldEmail)
            userDao.updateUser(User(
                userId = user.userId,
                userImage = mainImage,
                userFirstName = user.userFirstName,
                userLastName = user.userLastName,
                userEmail = user.userEmail,
                userPhone = user.userPhone,
                userPassword = user.userPassword,
                dateOfBirth = user.dateOfBirth,
                isRetailer = user.isRetailer
            ))
        }.start()
    }
}