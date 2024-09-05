package com.example.shoppinggroceryapp.model.viewmodel.productviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.order.Cart
import com.example.shoppinggroceryapp.model.entities.products.Product

class ProductListViewModel(var userDao: UserDao):ViewModel() {

    var cartList: MutableLiveData<List<Cart>> = MutableLiveData()
    var productList: MutableLiveData<List<Product>> = MutableLiveData()
    var productCategoryList: MutableLiveData<List<Product>> = MutableLiveData()
    var manufacturedSortedList:MutableLiveData<List<Product>> = MutableLiveData()
    fun getCartItems(cartId: Int) {
        Thread {
            cartList.postValue(userDao.getCartItems(cartId))
        }.start()
    }

    fun getOnlyProducts() {
        Thread {
            productList.postValue(userDao.getOnlyProducts())
        }.start()
    }


    fun getProductsByCategory(category: String) {
        Thread {
            productCategoryList.postValue(userDao.getProductByCategory(category))
        }.start()
    }

    fun getSortedManufacturedLowProductsWithCat(category: String) {
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedManufacturedLowProducts(category))
        }.start()
    }
    fun getSortedManufacturedLowProductsNoCat(){
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedManufacturedLowProductsNoCat())
        }.start()
    }

    fun getSortedManufacturedHighProductsWithCat(category: String) {
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedManufacturedHighProducts(category))
        }.start()
    }
    fun getSortedManufacturedHighProductsNoCat(){
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedManufacturedHighProductsNoCat())
        }.start()
    }

    fun getSortedExpiryLowProductsWithCat(category: String) {
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedExpiryLowProducts(category))
        }.start()
    }
    fun getSortedExpiryLowProductsNoCat(){
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedExpiryLowProductsNoCat())
        }.start()
    }

    fun getSortedExpiryHighProductsWithCat(category: String) {
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedManufacturedHighProducts(category))
        }.start()
    }
    fun getSortedExpiryHighProductsNoCat(){
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedManufacturedHighProductsNoCat())
        }.start()
    }

    fun getSortedPriceHighProductsWithCat(category: String) {
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedPriceHighProducts(category))
        }.start()
    }
    fun getSortedPriceHighProductsNoCat(){
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedPriceHighProductsNoCat())
        }.start()
    }

    fun getSortedPriceLowProductsWithCat(category: String) {
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedPriceLowProducts(category))
        }.start()
    }
    fun getSortedPriceLowProductsNoCat(){
        Thread {
            manufacturedSortedList.postValue(userDao.getSortedPriceLowProductsNoCat())
        }.start()
    }

}