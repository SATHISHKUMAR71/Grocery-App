package com.example.shoppinggroceryapp.viewmodel.accountviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.RetailerDao
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.example.shoppinggroceryapp.model.entities.user.Address

class OrderDetailViewModel(var retailerDao: RetailerDao):ViewModel() {
    var selectedAddress:MutableLiveData<Address> = MutableLiveData()
    var date:MutableLiveData<Int> = MutableLiveData()
    var timeSlot:MutableLiveData<Int> = MutableLiveData()
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

    fun getMonthlySubscriptionDate(orderId:Int){
        Thread{
            date.postValue(retailerDao.getOrderedDayForMonthlySubscription(orderId).dayOfMonth)
        }.start()
    }

    fun getWeeklySubscriptionDate(orderId:Int){
        Thread{
            date.postValue(retailerDao.getOrderedDayForWeekSubscription(orderId).weekId)
        }.start()
    }

    fun getTimeSlot(orderId: Int){
        Thread{
            timeSlot.postValue(retailerDao.getOrderedTimeSlot(orderId).timeId)
        }.start()
    }
}