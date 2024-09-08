package com.example.shoppinggroceryapp.viewmodel.orderviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.model.dao.RetailerDao
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.order.Cart
import com.example.shoppinggroceryapp.model.entities.order.CartMapping
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.example.shoppinggroceryapp.model.entities.products.CartWithProductData
import java.net.IDN

class OrderSuccessViewModel(var retailerDao: RetailerDao):ViewModel() {
    var lock = Any()
    var gotOrder:MutableLiveData<OrderDetails> = MutableLiveData()
    var cartItems:MutableLiveData<List<CartWithProductData>> = MutableLiveData()
    var newCart:MutableLiveData<CartMapping> = MutableLiveData()
    fun placeOrder(cartId:Int,paymentMode:String,addressId:Int,deliveryStatus:String,paymentStatus:String){
        Thread {
            synchronized(lock) {
                retailerDao.addOrder(
                    OrderDetails(
                        0,
                        orderedDate = DateGenerator.getCurrentDate(),
                        deliveryDate = DateGenerator.getDeliveryDate(),
                        cartId = cartId,
                        paymentMode = paymentMode,
                        paymentStatus = paymentStatus,
                        addressId = addressId,
                        deliveryStatus = deliveryStatus
                    )
                )
            }
        }.start()
    }

    fun getOrderAndCorrespondingCart(cartId:Int){
        Thread {
            synchronized(lock) {
                println(retailerDao.getOrder(cartId))
                println("CartId: $cartId")
                gotOrder.postValue(retailerDao.getOrder(cartId))
                cartItems.postValue(retailerDao.getProductsWithCartId(cartId))
            }

        }.start()
    }

    fun updateAndAssignNewCart(cartId: Int,userId:Int){
        Thread {
            synchronized(lock) {
                retailerDao.updateCartMapping(CartMapping(cartId, userId, "not available"))
                retailerDao.addCartForUser(CartMapping(0, userId, "available"))
                newCart.postValue(retailerDao.getCartForUser(userId))
            }
        }.start()
    }
}