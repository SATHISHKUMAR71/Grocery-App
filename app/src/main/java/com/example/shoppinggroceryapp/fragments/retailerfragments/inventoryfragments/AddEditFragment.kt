package com.example.shoppinggroceryapp.fragments.retailerfragments.inventoryfragments


import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.AppCameraPermissionHandler
import com.example.shoppinggroceryapp.fragments.ImageHandler
import com.example.shoppinggroceryapp.fragments.ImageLoaderAndGetter
import com.example.shoppinggroceryapp.fragments.ImagePermissionHandler
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.example.shoppinggroceryapp.viewmodel.retailerviewmodel.inventoryviewmodel.AddEditViewModel
import com.example.shoppinggroceryapp.viewmodel.retailerviewmodel.inventoryviewmodel.AddEditViewModelFactory
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.dataclass.IntWithCheckedData
import com.example.shoppinggroceryapp.model.entities.products.BrandData
import com.example.shoppinggroceryapp.model.entities.products.Category
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale

class AddEditFragment : Fragment() {


    private lateinit var imageHandler: ImageHandler
    private lateinit var imageLoader:ImageLoaderAndGetter
    private var isNewParentCategory = false
    private var isNewSubCategory = false
    private lateinit var imagePermissionHandler: ImagePermissionHandler
    private var mainImage:String = ""
    private var mainImageBitmap:Bitmap?= null
    private lateinit var childArray:Array<String>
    private lateinit var parentArray:Array<String>
    private var isCategoryImageAdded = true
    var parentCategory = ""
    var count = 0
    var mainImageClicked = false
    var parentCategoryImageClicked = false
    var parentCategoryImage:Bitmap? = null
    var imageList = mutableMapOf<Int,IntWithCheckedData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageHandler = ImageHandler(this)
        imageHandler.initActivityResults()
        imagePermissionHandler = AppCameraPermissionHandler(this,imageHandler)
        imagePermissionHandler.initPermissionResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        parentArray = arrayOf()
        childArray = arrayOf()
        val view =  inflater.inflate(R.layout.fragment_add_edit, container, false)
        val updateBtn = view.findViewById<MaterialButton>(R.id.updateInventoryBtn)
        val addParentCategoryLayout = view.findViewById<LinearLayout>(R.id.addParentCategoryImageLayout)
        val addParentImage = view.findViewById<ImageView>(R.id.addParentCategoryImage)
        val addParentCategoryButton = view.findViewById<MaterialButton>(R.id.addParentCategoryImageButton)
        var offer = 0f
        var price = 0f
        val db1 = AppDatabase.getAppDatabase(requireContext())
        val addEditViewModel = ViewModelProvider(this,
            AddEditViewModelFactory(db1.getRetailerDao(),db1.getProductDao())
        )[AddEditViewModel::class.java]
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
//        val addMainImageBtn = view.findViewById<MaterialButton>(R.id.addMainImageButton)
//        val addMainImage = view.findViewById<ImageView>(R.id.addMainImage)
//        val editImage = view.findViewById<MaterialButton>(R.id.editMainImageButton)
        val db = AppDatabase.getAppDatabase(requireContext())
        imageLoader = ImageLoaderAndGetter()


        addParentCategoryButton.setOnClickListener {
            isNewParentCategory = true
            parentCategoryImageClicked = true
            imagePermissionHandler.checkPermission(true)
//            imageHandler.showAlertDialog()
        }
        addParentImage.setOnClickListener {
            isNewParentCategory = true
            parentCategoryImageClicked = true
            imagePermissionHandler.checkPermission(true)
//            imageHandler.showAlertDialog()
        }

        val formatter = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
        ProductListFragment.selectedProduct.value?.let {
            println("ON ELSE selected IMage ")
            addEditViewModel.getBrandName(it.brandId)
            imageHandler.gotImage.value = imageLoader.getImageInApp(requireContext(),it.mainImage)
            mainImage = it.mainImage
            mainImageClicked = true
            addEditViewModel.getImagesForProduct(it.productId)
            productName.setText(it.productName)
            addEditViewModel.getParentCategoryImage(it.categoryName)
            productDescription.setText(it.productDescription)
            productPrice.setText(it.price.toString())
            productOffer.setText(it.offer.toString())
            productSubCat.setText(it.categoryName)
            productQuantity.setText(it.productQuantity)
            productAvailableItems.setText(it.availableItems.toString())
            isVeg.isChecked = it.isVeg
            productManufactureDate.setText(it.manufactureDate)
            productExpiryDate.setText(it.expiryDate)
        }

        addEditViewModel.categoryImage.observe(viewLifecycleOwner){
            it?.let {
                if(it.isNotEmpty()) {
                    val image =imageLoader.getImageInApp(requireContext(), it)
                    println("00990099 image is stored or not: $image $it")
                    addParentImage.setImageBitmap(image)
                    addParentCategoryButton.text = "Change Category Image"
                }
            }
        }

        addEditViewModel.imageList.observe(viewLifecycleOwner){
            println(it)
            for(i in it) {
                imageHandler.gotImage.value = imageLoader.getImageInApp(requireContext(),i.images)
            }
        }
        addEditViewModel.brandName.observe(viewLifecycleOwner){
            brandName.setText(it)
        }

        addEditViewModel.getParentArray()
        addEditViewModel.parentArray.observe(viewLifecycleOwner){
            parentArray = it
            productParentCategory.setSimpleItems(parentArray)
        }
        ProductListFragment.selectedProduct.value?.let {
            addEditViewModel.getParentCategory(it.categoryName)
        }
        addEditViewModel.parentCategory.observe(viewLifecycleOwner){parentCategoryValue ->
            productParentCategory.setText(parentCategoryValue)
        }

//        addEditViewModel.getChildArray()
        addEditViewModel.childArray.observe(viewLifecycleOwner){childItems->
            productSubCat.setSimpleItems(childItems)
            ProductListFragment.selectedProduct.value?.let {
                productSubCat.setText(it.categoryName)
            }
        }

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

        productParentCategory.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isNotEmpty()) {
                    addParentCategoryLayout.visibility = View.VISIBLE
                }
                else{
                    addParentCategoryLayout.visibility = View.GONE
                }
                if(!parentCategoryChecker(s.toString())){
                    isNewParentCategory = true
                }
                else{
                    addEditViewModel.getParentCategoryImageForParent(s.toString())
                    addEditViewModel.getChildArray(s.toString())
                    isNewParentCategory = false
                    println("@@@@  PArentCategory Found $s")
                }
            }
        })

        productSubCat.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if(!subCategoryChecker(s.toString())){
                    isNewSubCategory = true
                }
                else{
                    isNewSubCategory = false
                }
            }
        })

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
                productExpiryDate.text.toString().isNotEmpty()
            ){
                var checkedCount = 0
                var bitmap:Bitmap? = null
                for(i in imageList){
                    if(i.value.isChecked){
                        checkedCount++
                        bitmap = i.value.bitmap
                        mainImageBitmap = bitmap
                    }
                }
                if(checkedCount>1){
                    Snackbar.make(view,"Product Should Contain Only One Main Image",Snackbar.LENGTH_SHORT).apply {
                        setBackgroundTint(Color.argb(255,230,20,20))
                        show()
                    }
                }
                else if(mainImageBitmap==null){
                    Snackbar.make(view,"Product Should Contain atLeast One Main Image",Snackbar.LENGTH_SHORT).apply {
                        setBackgroundTint(Color.argb(255,230,20,20))
                        show()
                    }
                }
                else if(checkedCount==1) {
                    mainImage = "${System.currentTimeMillis()}"
                    imageLoader.storeImageInApp(requireContext(), bitmap!!, mainImage)
                }
                if(mainImage.isNotEmpty()) {
                    val imageListNames = mutableListOf<String>()
                    for (i in imageList) {
                        if (i.value.bitmap != mainImageBitmap) {
                            println("ON IMAGE FOR LOOP")
                            val tmpName = System.currentTimeMillis().toString()
                            imageLoader.storeImageInApp(requireContext(), i.value.bitmap, tmpName)
                            imageListNames.add(tmpName)
                        }
                    }
                    val brandNameStr = brandName.text.toString()
                    val subCategoryName = productSubCat.text.toString()
                    val parentCategoryName = productParentCategory.text.toString()
                    var brand: BrandData
                    if (isNewParentCategory) {
                        println("00990099 IS NEW PARENT CATEGORY")
                        val filName = "${System.currentTimeMillis()}"
                        if (parentCategoryImage != null) {
                            imageLoader.storeImageInApp(
                                requireContext(),
                                parentCategoryImage!!,
                                filName
                            )
                        }
                        if (imageLoader.getImageInApp(requireContext(), filName) == null) {
                            isCategoryImageAdded = false
                            Toast.makeText(
                                requireContext(),
                                "Please Add the Category image for New Category",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        println("00990099 IMage Added Value: ${imageLoader.getImageInApp(requireContext(),filName)}")
                        addEditViewModel.addParentCategory(
                            ParentCategory(
                                productParentCategory.text.toString(),
                                filName,
                                "",
                                false
                            )
                        )
                    }
                    if (isNewSubCategory) {
                        addEditViewModel.addSubCategory(
                            Category(
                                productSubCat.text.toString(),
                                productParentCategory.text.toString(), ""
                            )
                        )
                    }
                    if (isCategoryImageAdded) {
                        addEditViewModel.updateInventory(
                            brandNameStr,
                            (ProductListFragment.selectedProduct.value == null),
                            Product(
                                0,
                                0,
                                subCategoryName,
                                productName.text.toString(),
                                productDescription.text.toString(),
                                productPrice.text.toString().toFloat(),
                                productOffer.text.toString().toFloat(),
                                productQuantity.text.toString(),
                                mainImage,
                                isVeg.isChecked,
                                productManufactureDate.text.toString(),
                                productExpiryDate.text.toString(),
                                productAvailableItems.text.toString().toInt()
                            ),
                            ProductListFragment.selectedProduct.value?.productId,
                            imageListNames
                        )
                        parentFragmentManager.popBackStack()
                        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(context,"All the Fields are mandatory",Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<ImageView>(R.id.addNewImage).setOnClickListener {
            imagePermissionHandler.checkPermission(true)
        }
        view.findViewById<MaterialButton>(R.id.addNewImageButton).setOnClickListener {
            imagePermissionHandler.checkPermission(true)
        }

        imageHandler.gotImage.observe(viewLifecycleOwner){
            val newView = LayoutInflater.from(context).inflate(R.layout.image_view,container,false)
            val image = newView.findViewById<ImageView>(R.id.productImage)
            image.setImageBitmap(it)
            if(parentCategoryImageClicked){
                println("ON ELSE ADDITIONAL IMAGE PARENT CATEGORY IMAGE CLICKED")
                parentCategoryImage = it
                isCategoryImageAdded = true
                addParentImage.setImageBitmap(it)
                parentCategoryImageClicked = false
            }
            else {
                imageList.putIfAbsent(count,IntWithCheckedData(it,false))
                println("ON ELSE ADDITIONAL IMAGE $imageList")
                val currentCount = count
                if(mainImageClicked){
                    mainImageBitmap = it
                    newView.findViewById<CheckBox>(R.id.mainImageCheckBox).isChecked = mainImageClicked
                    mainImageClicked = false
                }
                newView.findViewById<CheckBox>(R.id.mainImageCheckBox).setOnCheckedChangeListener { buttonView, isChecked ->
                    if(imageList[currentCount]!=null) {
                        imageList[currentCount]!!.isChecked = isChecked
                    }
                    println("ON ELSE ADDITIONAL IMAGE $imageList")
                }
                newView.findViewById<ImageButton>(R.id.deleteImage).setOnClickListener {
                    imageLayout.removeView(newView)
                    imageList.remove(currentCount)
                }
                imageLayout.addView(newView, 0)
                count++
            }
            println(imageList)
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

    fun parentCategoryChecker(parentCategory: String):Boolean{
        for(i in parentArray){
            if(parentCategory==i){
                return true
            }
        }
        return false
    }

    fun subCategoryChecker(childCategory: String):Boolean{
        for(i in childArray){
            if(childCategory==i){
                return true
            }
        }
        return false
    }
}