package com.example.shoppinggroceryapp.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.example.shoppinggroceryapp.model.entities.products.BrandData
import com.example.shoppinggroceryapp.model.entities.products.Category
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.example.shoppinggroceryapp.model.entities.products.Product

@Dao
interface RetailerDao:UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addParentCategory(parentCategory: ParentCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSubCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewBrand(brandData: BrandData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProduct(product: Product)

    @Update
    fun updateProduct(product: Product)

    @Query("SELECT * FROM OrderDetails")
    fun getOrderDetails():List<OrderDetails>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addOrder(order:OrderDetails)

    @Delete
    fun deleteOrderDetails(order: OrderDetails)


    @Query("SELECT * FROM OrderDetails WHERE OrderDetails.cartId=:cartId")
    fun getOrderDetailsForSpecificCart(cartId:Int):List<OrderDetails>

//    @Query("SELECT OrderDetails.*,Cart FROM OrderDetails WHERE OrderDetails.=:cartId")
//    fun getOrderDetailsForSpecificUser(cartId:Int):List<OrderDetails>
    @Delete
    fun deleteProduct(product: Product)

    @Query("SELECT BrandData.brandName FROM BrandData where BrandData.brandId=:id")
    fun getBrandName(id:Long):String

    @Query("SELECT * FROM BrandData Where BrandData.brandName=:brandName")
    fun getBrandWithName(brandName:String):BrandData



    @Query("SELECT * FROM ParentCategory WHERE ParentCategory.parentCategoryName=:parentCategoryName")
    fun getParentCategory(parentCategoryName:String):ParentCategory

    @Query("SELECT * FROM Category WHERE Category.categoryName=:subCategoryName")
    fun getSubCategory(subCategoryName:String):Category

}