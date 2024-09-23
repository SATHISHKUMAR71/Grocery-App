package com.example.shoppinggroceryapp.viewmodel.initialviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.search.SearchHistory

class SearchViewModel(var userDao: UserDao):ViewModel() {

    var searchedList:MutableLiveData<MutableList<String>> = MutableLiveData()
    fun performSearch(query:String){
        Thread {
            println("SEARCH LIST: PERFORM SEARCH CALLED called on not Empty")
            var list = performSearchProduct(query)
            list.addAll(userDao.getProductForQuery(query).toMutableList())
            searchedList.postValue(list)
            performSearchProduct(query)
        }.start()
    }
    private fun performSearchProduct(query:String):MutableList<String>{
        var list = userDao.getProductForQueryName(query).toMutableList()
        for( j in list){
            println("J VALUE: $j")
        }
        return list
    }

    fun addItemInDb(query:String){
        Thread {
            userDao.addSearchQueryInDb(SearchHistory(query))
        }.start()
    }

    fun getSearchedList(){
        Thread{
            val list = mutableListOf<String>()
            println("SEARCH LIST: NOT PERFORM SEARCH CALLED called on not Empty")
            var i = 0
            for(j in userDao.getSearchHistory().reversed()){
                list.add(j.searchText)
                i++
                if(i==10){
                    break
                }
            }
            searchedList.postValue(list)
        }.start()
    }
}