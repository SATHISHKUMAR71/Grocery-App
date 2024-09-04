package com.example.shoppinggroceryapp.fragments.retailerfragments.inventoryfragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.ImageHandler
import com.example.shoppinggroceryapp.fragments.ImageLoaderAndGetter
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.BrandData
import com.example.shoppinggroceryapp.model.entities.products.Category
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale

class AddEditFragment : Fragment() {


    private lateinit var imageHandler: ImageHandler
    private lateinit var imageLoader:ImageLoaderAndGetter
    private var mainImage:String = ""
    private lateinit var childArray:Array<String>
    private lateinit var parentArray:Array<String>
    var parentCategory = ""
    var count = 0
    var imageList = mutableMapOf<Int,Bitmap>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageHandler = ImageHandler(this)
        imageHandler.initActivityResults()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add_edit, container, false)
        val updateBtn = view.findViewById<MaterialButton>(R.id.updateInventoryBtn)
        var offer = 0f
        var price = 0f
        val productName = view.findViewById<TextInputEditText>(R.id.productNameEditFrag)
        val brandName = view.findViewById<TextInputEditText>(R.id.productBrandEditFrag)
        val productParentCategory = view.findViewById<MaterialAutoCompleteTextView>(R.id.productParentCatEditFrag)
        val productSubCat = view.findViewById<MaterialAutoCompleteTextView>(R.id.productCategoryEditFrag)
        val productDescription = view.findViewById<TextInputEditText>(R.id.productDescriptionEditFrag)
        val productPrice = view.findViewById<TextInputEditText>(R.id.productPriceEditFrag)
        val productOffer = view.findViewById<TextInputEditText>(R.id.productOfferEditFrag)
        val productQuantity = view.findViewById<TextInputEditText>(R.id.productQuantityEditFrag)
        val productAvailableItems = view.findViewById<TextInputEditText>(R.id.productAvailableItemsEditFrag)
        val isVeg = view.findViewById<CheckBox>(R.id.productIsVegEditFrag)
        val productManufactureDate = view.findViewById<TextInputEditText>(R.id.productManufactureEditFrag)
        val productExpiryDate = view.findViewById<TextInputEditText>(R.id.productExpiryEditFrag)
        val imageLayout =view.findViewById<LinearLayout>(R.id.imageLayout)
        val db = AppDatabase.getAppDatabase(requireContext())
        imageLoader = ImageLoaderAndGetter()
        val formatter = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
        ProductListFragment.selectedProduct.value?.let {
            Thread{
                val name= db.getRetailerDao().getBrandName(it.brandId)
                MainActivity.handler.post {
                    brandName.setText(name)
                }
            }.start()
            ImageHandler.gotImage.value = imageLoader.getImageInApp(requireContext(),it.mainImage)
            productName.setText(it.productName)
            productDescription.setText(it.productDescription)
            productPrice.setText(it.price.toString())
            productOffer.setText(it.offer)
            productQuantity.setText(it.productQuantity)
            productAvailableItems.setText(it.availableItems.toString())
            isVeg.isChecked = it.isVeg
            productManufactureDate.setText(it.manufactureDate)
            productExpiryDate.setText(it.expiryDate)

        }

        Thread {
            parentArray=db.getProductDao().getParentCategoryName()
            ProductListFragment.selectedProduct.value?.let {
                parentCategory = db.getProductDao().getParentCategoryName(it.categoryName)
            }
            MainActivity.handler.post {
                productParentCategory.setSimpleItems(parentArray)
                ProductListFragment.selectedProduct.value?.let {
                    productParentCategory.setText(parentCategory)

                }
            }
        }.start()
        Thread {
            childArray =db.getProductDao().getChildCategoryName()
            MainActivity.handler.post {
                productSubCat.setSimpleItems(childArray)
                ProductListFragment.selectedProduct.value?.let {
                    productSubCat.setText(it.categoryName)
                }
            }
        }.start()
        val dateManufacturePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select the Birthday Date")
            .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()

        val dateExpiryPicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select the Birthday Date")
            .setTextInputFormat(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
        dateManufacturePicker.addOnPositiveButtonClickListener {
            productManufactureDate.setText(formatter.format(it))
        }
        dateExpiryPicker.addOnPositiveButtonClickListener {
            productExpiryDate.setText(formatter.format(it))
        }
        productManufactureDate.setOnClickListener {
            dateManufacturePicker.show(parentFragmentManager,"Manufacture Date")
        }
        productExpiryDate.setOnClickListener {
            dateExpiryPicker.show(parentFragmentManager,"Expiry Date")
        }
        ProductListFragment.selectedProduct.observe(viewLifecycleOwner){
            println("***** $it")
        }
        println("**** Frag ${ProductListFragment.selectedProduct.value}")
        updateBtn.setOnClickListener {
            if(productName.text.toString().isNotEmpty()&&
                productDescription.text.toString().isNotEmpty()&&
                productSubCat.text.toString().isNotEmpty()&&
                brandName.text.toString().isNotEmpty()&&
                productParentCategory.text.toString().isNotEmpty()&&
                productPrice.text.toString().isNotEmpty()&&
                productOffer.text.toString().isNotEmpty()&&
                productQuantity.text.toString().isNotEmpty()&&
                productAvailableItems.text.toString().isNotEmpty()&&
                productManufactureDate.text.toString().isNotEmpty()&&
                productExpiryDate.text.toString().isNotEmpty()&&
                imageList.isNotEmpty()
            ){
                println("**** Update btn ${ProductListFragment.selectedProduct.value}")
                val brandNameStr = brandName.text.toString()
                val subCategoryName = productSubCat.text.toString()
                val parentCategoryName = productParentCategory.text.toString()
                var brand:BrandData
                Thread{
                    brand = db.getRetailerDao().getBrandWithName(brandNameStr)
                    if(brand==null){
                        db.getRetailerDao().addNewBrand(BrandData(0,brandNameStr))
                        brand = db.getRetailerDao().getBrandWithName(brandNameStr)
                    }
                    println("**** IN THREAD ${ProductListFragment.selectedProduct.value}")
                    if(ProductListFragment.selectedProduct.value==null) {
                        val product = Product(
                            0,
                            brand.brandId,
                            subCategoryName,
                            productName.text.toString(),
                            productDescription.text.toString(),
                            productPrice.text.toString().toFloat(),
                            productOffer.text.toString(),
                            productQuantity.text.toString(),
                            mainImage,
                            isVeg.isChecked,
                            productManufactureDate.text.toString(),
                            productExpiryDate.text.toString(),
                            productAvailableItems.text.toString().toInt()
                        )
                        println("ON IF${product}")
                        db.getRetailerDao().addProduct(product)
                        MainActivity.handler.post{
                            ProductListFragment.selectedProduct.value = product
                            println("0000 Value Updated $product ${ProductListFragment.selectedProduct.value}")
                        }
                    }
                    else{

                        val product = Product(
                            ProductListFragment.selectedProduct.value!!.productId,
                            brand.brandId,
                            subCategoryName,
                            productName.text.toString(),
                            productDescription.text.toString(),
                            productPrice.text.toString().toFloat(),
                            productOffer.text.toString(),
                            productQuantity.text.toString(),
                            mainImage,
                            isVeg.isChecked,
                            productManufactureDate.text.toString(),
                            productExpiryDate.text.toString(),
                            productAvailableItems.text.toString().toInt()
                        )
                        println("ON ELSE ${product}")
                        db.getRetailerDao().updateProduct(product)
                        MainActivity.handler.post{
                            ProductListFragment.selectedProduct.value = product
                            println("0000 Value Updated $product ${ProductListFragment.selectedProduct.value}")
                        }
                    }
                    println("Product Added Successfully")
                }.start()
                parentFragmentManager.popBackStack()
                Toast.makeText(context,"Updated Successfully",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"All the Fields are mandatory",Toast.LENGTH_SHORT).show()
            }
        }
        view.findViewById<ImageView>(R.id.addNewImage).setOnClickListener {
            imageHandler.showAlertDialog()
        }
        ImageHandler.gotImage.observe(viewLifecycleOwner){
            val newView = LayoutInflater.from(context).inflate(R.layout.image_view,container,false)
            val image = newView.findViewById<ImageView>(R.id.productImage)
            image.setImageBitmap(it)
            println("IMAGES LIST: $it")
            imageList.putIfAbsent(count,it)
            if(mainImage.isEmpty()){
                mainImage = "${System.currentTimeMillis()}"
                if(it!=null) {
                    imageLoader.storeImageInApp(requireContext(), it, mainImage)
                }
                else{
                    mainImage = ""
                }
            }
            println("IMAGES LIST: $imageList")
            val currentCount = count
            newView.findViewById<ImageButton>(R.id.deleteImage).setOnClickListener {
                if(imageList.size>1){
                    imageLayout.removeView(newView)
                    imageList.remove(currentCount)
                    println("IMAGES LIST REMOVED AT:$currentCount $count $imageList")
                }
                else{
                    Toast.makeText(context,"Product Should Contain atLeast one Image",Toast.LENGTH_SHORT).show()
                }
            }
            imageLayout.addView(newView,0)
            count++
        }
        view.findViewById<MaterialToolbar>(R.id.materialToolbarEditProductFrag).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        InitialFragment.hideSearchBar.value = true
        InitialFragment.hideBottomNav.value = true
    }

    override fun onStop() {
        super.onStop()
        InitialFragment.hideSearchBar.value = false
        InitialFragment.hideBottomNav.value = false
    }
}