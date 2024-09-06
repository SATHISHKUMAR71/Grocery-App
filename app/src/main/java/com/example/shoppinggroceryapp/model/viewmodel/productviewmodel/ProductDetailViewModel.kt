package com.example.shoppinggroceryapp.model.viewmodel.productviewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.example.shoppinggroceryapp.model.dao.RetailerDao
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.order.Cart

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

    fun addInRecentlyViewedItems(recentlyViewedItems:SharedPreferences){
        Thread {
            val recentList = mutableListOf<Int>()
            var i: Int
            var j = 0
            while (true) {
                i = recentlyViewedItems.getInt("product$j", -1)
                if(i==-1){
                    break
                }
                recentList.add(i)
                j++
            }
            with(recentlyViewedItems.edit()) {
                val productId = ProductListFragment.selectedProduct.value!!.productId.toInt()
                if(productId !in recentList) {
                    putInt(
                        "product$j",
                        ProductListFragment.selectedProduct.value!!.productId.toInt()
                    )
                    println("ProductId: $j $recentList")
                }
                apply()
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