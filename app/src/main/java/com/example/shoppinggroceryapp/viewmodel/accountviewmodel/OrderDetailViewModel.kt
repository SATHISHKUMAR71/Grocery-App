package com.example.shoppinggroceryapp.viewmodel.accountviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.RetailerDao
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.example.shoppinggroceryapp.model.entities.user.Address

class OrderDetailViewModel(var retailerDao: RetailerDao):ViewModel() {
    var selectedAddress:MutableLiveData<Address> = MutableLiveData()
    fun updateOrderDetails(orderDetails: OrderDetails){
        Thread{
            retailerDao.updateOrderDetails(orderDetails)
        }.start()
    }

    fun getSelectedAddress(addressId:Int){
        Thread{
            selectedAddress.postValue(retailerDao.getAddress(addressId))
        }.start()
    }
}