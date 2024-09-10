package com.example.shoppinggroceryapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shoppinggroceryapp.fragments.MIGRATION_3_4
import com.example.shoppinggroceryapp.model.dao.ProductDao
import com.example.shoppinggroceryapp.model.dao.RetailerDao
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.entities.deals.Deals
import com.example.shoppinggroceryapp.model.entities.help.CustomerRequest
import com.example.shoppinggroceryapp.model.entities.help.FAQ
import com.example.shoppinggroceryapp.model.entities.order.Cart
import com.example.shoppinggroceryapp.model.entities.order.CartMapping
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.example.shoppinggroceryapp.model.entities.products.BrandData
import com.example.shoppinggroceryapp.model.entities.products.Category
import com.example.shoppinggroceryapp.model.entities.products.Images
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.model.entities.recentlyvieweditems.RecentlyViewedItems
import com.example.shoppinggroceryapp.model.entities.user.Address
import com.example.shoppinggroceryapp.model.entities.user.User

@Database(entities = [User::class,Address::class, Product::class,Images::class,ParentCategory::class,Category::class,Deals::class,FAQ::class,CustomerRequest::class,BrandData::class,CartMapping::class,Cart::class,OrderDetails::class,RecentlyViewedItems::class], version = 4)
abstract class AppDatabase:RoomDatabase(){

    abstract fun getUserDao():UserDao
    abstract fun getRetailerDao():RetailerDao
    abstract fun getProductDao():ProductDao

    companion object{
        @Volatile
        private var INSTANCE:AppDatabase? = null
        fun getAppDatabase(context: Context):AppDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fresh_cart_database")
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}