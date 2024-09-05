package com.example.shoppinggroceryapp.model.viewmodel.accountviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.model.dao.RetailerDao
import com.example.shoppinggroceryapp.model.dao.UserDao

class OrderDetailViewModelFactory(var retailerDao: RetailerDao):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrderDetailViewModel(retailerDao) as T
    }
}