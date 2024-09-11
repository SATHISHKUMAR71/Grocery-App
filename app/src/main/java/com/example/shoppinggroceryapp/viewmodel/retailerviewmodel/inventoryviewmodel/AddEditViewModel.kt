package com.example.shoppinggroceryapp.viewmodel.retailerviewmodel.inventoryviewmodel

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.fragments.ImageLoaderAndGetter
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductDetailFragment
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.example.shoppinggroceryapp.model.dao.ProductDao
import com.example.shoppinggroceryapp.model.dao.RetailerDao
import com.example.shoppinggroceryapp.model.entities.products.BrandData
import com.example.shoppinggroceryapp.model.entities.products.Images
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductDetailViewModel

class AddEditViewModel(var retailerDao: RetailerDao,var productDao: ProductDao):ViewModel() {

    var brandName:MutableLiveData<String> = MutableLiveData()
    var parentArray:MutableLiveData<Array<String>> = MutableLiveData()
    var imageList:MutableLiveData<List<Images>> = MutableLiveData()
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

    fun getImagesForProduct(productId: Long){
        Thread{
            imageList.postValue(retailerDao.getImagesForProduct(productId))
        }.start()
    }

    fun updateInventory(brandName:String,isNewProduct:Boolean,product: Product,productId:Long?,imageList: List<String>){
        var brand:BrandData
        Thread{
            synchronized(ProductDetailViewModel.brandLock) {
                brand = retailerDao.getBrandWithName(brandName)
                var prod:Product
                var lastProduct:Product
                if (brand == null) {
                    retailerDao.addNewBrand(BrandData(0, brandName))
                    brand = retailerDao.getBrandWithName(brandName)
                }
                if (isNewProduct) {
                    prod = product.copy(brandId = brand.brandId)
                    modifiedProduct.postValue(prod)
                    retailerDao.addProduct(prod)
                    ProductListFragment.selectedProduct.postValue(prod)
                    lastProduct = retailerDao.getLastProduct()
                } else {
                    prod = product.copy(brandId = brand.brandId, productId = productId!!)
                    retailerDao.updateProduct(prod)
                    modifiedProduct.postValue(prod)
                    ProductListFragment.selectedProduct.postValue(prod)
                    lastProduct = prod
                }

                println("LAST PRODUCT: $lastProduct")
                for(j in retailerDao.getImagesForProduct(lastProduct.productId)){
                    retailerDao.deleteImage(j)
                }
                for(i in imageList){
                    retailerDao.addImagesInDb(Images(0,lastProduct.productId,i))
                    println("Images Added")
                }
            }
        }.start()
    }



}