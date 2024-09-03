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

    companion object{
        var selectedEditableProduct:MutableLiveData<Product> = MutableLiveData()
    }
    private lateinit var imageHandler: ImageHandler
    private lateinit var imageLoader:ImageLoaderAndGetter
    private var mainImage:String = ""
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
        val discount = view.findViewById<TextInputEditText>(R.id.productDiscountEditFrag)
        val productAvailableItems = view.findViewById<TextInputEditText>(R.id.productAvailableItemsEditFrag)
        val isVeg = view.findViewById<CheckBox>(R.id.productIsVegEditFrag)
        val productManufactureDate = view.findViewById<TextInputEditText>(R.id.productManufactureEditFrag)
        val productExpiryDate = view.findViewById<TextInputEditText>(R.id.productExpiryEditFrag)
        imageLoader = ImageLoaderAndGetter()
        val formatter = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
        val db = AppDatabase.getAppDatabase(requireContext())
        Thread {
            val array:Array<String> =db.getProductDao().getParentCategoryName()
            MainActivity.handler.post {
                productParentCategory.setSimpleItems(array)
            }
        }.start()
        Thread {
            val array:Array<String> =db.getProductDao().getChildCategoryName()
            MainActivity.handler.post {
                productSubCat.setSimpleItems(array)
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
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                println(s)
                if(s?.isNotEmpty()==true) {
                    val priceInt = s.toString().toFloat()
                    val discountedPrice = (priceInt) - ((offer / 100) * priceInt)
                    price = discountedPrice
                    println(discountedPrice)
                    discount.setText(discountedPrice.toString())
                }
            }
        }
        val offerTextWatcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.isNotEmpty()==true) {
                    val offerInt = s.toString().toFloat()
                    if (offerInt in 0f..100f) {
                        offer = offerInt
                    }
                    val discountedPrice = (price) - ((offer / 100) * price)
                    println("Price: $discountedPrice")
                    discount.setText(discountedPrice.toString())
                }
                else{
                    discount.setText(price.toString())
                }
            }

        }

        productPrice.addTextChangedListener(textWatcher)
        productOffer.addTextChangedListener(offerTextWatcher)
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
                    db.getRetailerDao().addProduct(Product(0,
                        brand.brandId,subCategoryName,productName.text.toString(),productDescription.text.toString(),
                        productPrice.text.toString().toFloat(),productOffer.text.toString(),productQuantity.text.toString(),
                        mainImage,isVeg.isChecked,productManufactureDate.text.toString(),productExpiryDate.text.toString(),
                        productAvailableItems.text.toString().toInt()))
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
        val imageLayout =view.findViewById<LinearLayout>(R.id.imageLayout)
        ImageHandler.gotImage.observe(viewLifecycleOwner){
            val newView = LayoutInflater.from(context).inflate(R.layout.image_view,container,false)
            val image = newView.findViewById<ImageView>(R.id.productImage)
            image.setImageBitmap(it)
            println("IMAGES LIST: $it")
            imageList.putIfAbsent(count,it)
            if(mainImage.isEmpty()){
                mainImage = "${System.currentTimeMillis()}"
                imageLoader.storeImageInApp(requireContext(),it,mainImage)
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