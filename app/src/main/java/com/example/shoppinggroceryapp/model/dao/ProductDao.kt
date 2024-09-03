package com.example.shoppinggroceryapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppinggroceryapp.model.entities.products.Category
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addParentCategory(parentCategory: ParentCategory)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSubCategory(category: Category)

    @Query("SELECT * FROM ParentCategory")
    fun getParentCategoryList():List<ParentCategory>

    @Query("SELECT ParentCategory.parentCategoryName FROM ParentCategory")
    fun getParentCategoryName():Array<String>

    @Query("SELECT Category.categoryName FROM Category")
    fun getChildCategoryName():Array<String>

    @Query("SELECT * FROM Category WHERE Category.parentCategoryName=:parentCategoryName")
    fun getChildCategoryList(parentCategoryName:String):List<Category>

    @Query("SELECT * FROM ParentCategory JOIN Category ON Category.parentCategoryName=ParentCategory.parentCategoryName")
    fun getAllCategoryAndParentCategory():Map<ParentCategory,List<Category>>

}