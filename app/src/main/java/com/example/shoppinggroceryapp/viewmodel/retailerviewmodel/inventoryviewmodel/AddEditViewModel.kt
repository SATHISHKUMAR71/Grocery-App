package com.example.shoppinggroceryapp.viewmodel.retailerviewmodel.inventoryviewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductDetailFragment
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.example.shoppinggroceryapp.model.dao.ProductDao
import com.example.shoppinggroceryapp.model.dao.RetailerDao
import com.example.shoppinggroceryapp.model.entities.products.BrandData
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductDetailViewModel

class AddEditViewModel(var retailerDao: RetailerDao,var productDao: ProductDao):ViewModel() {

    var brandName:MutableLiveData<String> = MutableLiveData()
    var parentArray:MutableLiveData<Array<String>> = MutableLiveData()
    var parentCategory:MutableLiveData<String> = MutableLiveData()
    var childArray:MutableLiveData<Array<String>> = MutableLiveData()
    companion object {
        var modifiedProduct: MutableLiveData<Product> = MutableLiveData()
    }

    fun getBrandName(brandId:Long){
        Thread{
            synchronized(ProductDetailViewModel.brandLock) {
                brandName.postValue(retailerDao.getBrandName(brandId))
            }
        }.start()
    }

    fun getParentArray(){
        Thread{
            parentArray.postValue(productDao.getParentCategoryName())
        }.start()
    }

    fun getParentCategory(childName:String){
        Thread{
            parentCategory.postValue(productDao.getParentCategoryName(childName))
        }.start()
    }

    fun getChildArray(){
        Thread {
            childArray.postValue(productDao.getChildCategoryName())
        }.start()
    }

    fun updateInventory(brandName:String,isNewProduct:Boolean,product: Product,productId:Long?){
        var brand:BrandData
        Thread{
            synchronized(ProductDetailViewModel.brandLock) {
                println("GGGG Update Started")
                brand = retailerDao.getBrandWithName(brandName)
                if (brand == null) {
                    retailerDao.addNewBrand(BrandData(0, brandName))
                    brand = retailerDao.getBrandWithName(brandName)
                    println("Brand Null: $brand")
                }
                if (isNewProduct) {

                    var prod = product.copy(brandId = brand.brandId)
                    retailerDao.addProduct(prod)
                    modifiedProduct.postValue(prod)
                    ProductListFragment.selectedProduct.postValue(prod)
//                    MainActivity.handler.post {
//                        ProductListFragment.selectedProduct.value = prod
//                    }
                    println("GGGG In IF $prod")
                } else {

                    var prod = product.copy(brandId = brand.brandId, productId = productId!!)
                    retailerDao.updateProduct(prod)
                    modifiedProduct.postValue(prod)
                    ProductListFragment.selectedProduct.postValue(prod)
//                    MainActivity.handler.post {
//                        ProductListFragment.selectedProduct.value = prod
//                    }
                    println("GGGG In else $prod")
                }
                println("IN THREAD: $product ${modifiedProduct.value}")
                println("GGGG Update Finished ${ProductListFragment.selectedProduct.value}")
            }
        }.start()
    }

}