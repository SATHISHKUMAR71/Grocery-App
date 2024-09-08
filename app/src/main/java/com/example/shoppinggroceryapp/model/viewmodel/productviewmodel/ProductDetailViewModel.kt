package com.example.shoppinggroceryapp.model.viewmodel.productviewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.example.shoppinggroceryapp.model.dao.RetailerDao
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.order.Cart
import com.example.shoppinggroceryapp.model.entities.recentlyvieweditems.RecentlyViewedItems

class ProductDetailViewModel(var retailerDao: RetailerDao):ViewModel() {



    var brandName:MutableLiveData<String> = MutableLiveData()
    var isCartAvailable:MutableLiveData<Cart> =MutableLiveData()
    var lock = Any()
    companion object{
        var brandLock = Any()
    }
    fun getBrandName(brandId:Long){
        Thread {
            synchronized(brandLock){
                println("GGGG Getting Brand Name")
                brandName.postValue(retailerDao.getBrandName(brandId))
                println("GGGG Got Brand Name")
            }
        }.start()
    }

    fun addInRecentlyViewedItems(productId: Long){
        Thread {
            println("Recently viewed item called")
            if(retailerDao.getProductsInRecentList(productId)==null) {
                retailerDao.addProductInRecentlyViewedItems(RecentlyViewedItems(0, productId))
            }
        }.start()
    }

    fun getCartForSpecificProduct(cartId:Int,productId:Int){
        Thread{
            isCartAvailable.postValue(retailerDao.getSpecificCart(cartId,productId))
        }.start()
    }

    fun addProductInCart(cart:Cart){
        Thread{
            synchronized(lock){
                retailerDao.addItemsToCart(cart)
            }
        }.start()
    }

    fun updateProductInCart(cart:Cart){
        Thread{
            synchronized(lock){
                retailerDao.updateCartItems(cart)
            }
        }.start()
    }

    fun removeProductInCart(cart:Cart){
        Thread{
            synchronized(lock){
                retailerDao.removeProductInCart(cart)
            }
        }.start()
    }
}