package com.example.shoppinggroceryapp.viewmodel.filterviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.fragments.appfragments.OfferFragment
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.products.Product

class FilterViewModel(var userDao: UserDao):ViewModel() {

    var totalProducts:MutableLiveData<Int> = MutableLiveData()
    var list:MutableLiveData<MutableList<Product>> = MutableLiveData()
    var lock = Any()
    fun calculateTotalProducts(category:String){
        Thread {
            synchronized(lock) {
                totalProducts.postValue(userDao.getProductByCategory(category).size)
            }
        }.start()
    }



    fun getProducts50WithCat(category: String){
        Thread {
            synchronized(lock) {
                val localList = (userDao.getOnlyProduct50WithCat(category).toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("50  cat Called ${localList.size}")
            }
        }.start()
    }
    fun getProducts50NoCat(){
        Thread {
            synchronized(lock) {
                val localList = (userDao.getOnlyProduct50().toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("50 No cat Called ${localList.size}")
            }
        }.start()
    }

    fun getProducts40WithCat(category: String){
        Thread{
            synchronized(lock) {
                val localList = (userDao.getOnlyProduct40WithCat(category).toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("40 cat Called")
            }
        }.start()
    }

    fun getProducts40NoCat(){
        Thread{
            synchronized(lock) {
                val localList =(userDao.getOnlyProduct40().toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("40 No cat Called")
            }
        }.start()
    }

    fun getProducts30WithCat(category: String){
        Thread{
            synchronized(lock) {
                var localList = (userDao.getOnlyProduct30WithCat(category).toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("30 cat Called")
            }
        }.start()
    }

    fun getProducts30NoCat(){
        Thread{
            synchronized(lock) {
                val localList =(userDao.getOnlyProduct30().toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("30 No cat Called")
            }
        }.start()
    }
    fun getProducts20WithCat(category: String){
        Thread{
            synchronized(lock) {
                val localList = (userDao.getOnlyProduct20WithCat(category).toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("20 cat Called")
            }
        }.start()
    }

    fun getProducts20NoCat(){
        Thread{
            synchronized(lock) {
                val localList = (userDao.getOnlyProduct20().toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("20 No cat Called")
            }
        }.start()
    }
    fun getProducts10WithCat(category: String){
        Thread{
            synchronized(lock) {
                val localList =(userDao.getOnlyProduct10WithCat(category).toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("10 cat Called")
            }
        }.start()
    }

    fun getProducts10NoCat(){
        Thread{
            synchronized(lock) {
                val localList = (userDao.getOnlyProduct10().toMutableList())
                list.postValue(localList)
                totalProducts.postValue(localList.size)
                println("10 No cat Called")
            }
        }.start()
    }

}