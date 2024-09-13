package com.example.shoppinggroceryapp.viewmodel.authenticationviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.order.CartMapping
import com.example.shoppinggroceryapp.model.entities.user.User

class LoginViewModel(var userDao: UserDao) :ViewModel(){
    var user:MutableLiveData<User> = MutableLiveData()
    fun validateUser(email:String,password:String){
        Thread {
            user.postValue(userDao.getUser(email, password))
        }.start()
    }

    fun assignCartForUser(){
        Thread{
            val cart = userDao.getCartForUser(user.value?.userId?:-1)
            if(cart==null){
               userDao.addCartForUser(CartMapping(0,user.value?.userId?:-1,"available"))
               val newCart =  userDao.getCartForUser(user.value?.userId?:-1)
                MainActivity.cartId = newCart.cartId
            }
            else{
                MainActivity.cartId = cart.cartId
            }
        }.start()
    }
}