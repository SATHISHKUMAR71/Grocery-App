package com.example.shoppinggroceryapp.fragments.retailerfragments.inventoryfragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.MediaStore
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import androidx.core.view.setPadding
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.ImageHandler
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddEditFragment : Fragment() {


    private lateinit var imageHandler: ImageHandler
    var count = 0
    var imageList = mutableListOf<Bitmap>()
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
        val productName = view.findViewById<TextInputEditText>(R.id.productNameEditFrag)
        val brandName = view.findViewById<TextInputEditText>(R.id.productBrandEditFrag)
        val productParentCategory = view.findViewById<TextInputEditText>(R.id.productParentCatLayout)
        val productSubCat = view.findViewById<TextInputEditText>(R.id.productCategoryEditFrag)
        val productDescription = view.findViewById<TextInputEditText>(R.id.productDescriptionEditFrag)
        val productPrice = view.findViewById<TextInputEditText>(R.id.productPriceEditFrag)
        val productOffer = view.findViewById<TextInputEditText>(R.id.productOfferEditFrag)
        val productQuantity = view.findViewById<TextInputEditText>(R.id.productQuantityEditFrag)
        val productAvailableItems = view.findViewById<TextInputEditText>(R.id.productManufactureEditFrag)
        val isVeg = view.findViewById<CheckBox>(R.id.productIsVegEditFrag)
        val productManufactureDate = view.findViewById<TextInputEditText>(R.id.productAvailableItemsEditFrag)
        val productExpiryDate = view.findViewById<TextInputEditText>(R.id.productExpiryEditFrag)

        updateBtn.setOnClickListener {

        }
        view.findViewById<ImageView>(R.id.addNewImage).setOnClickListener {
            imageHandler.showAlertDialog()
        }
        val imageLayout =view.findViewById<LinearLayout>(R.id.imageLayout)
        ImageHandler.gotImage.observe(viewLifecycleOwner){
            val newView = LayoutInflater.from(context).inflate(R.layout.image_view,container,false)
            val image = newView.findViewById<ImageView>(R.id.productImage)
            image.setImageBitmap(it)
            imageList.add(it)
            newView.findViewById<ImageButton>(R.id.deleteImage).setOnClickListener {
                if(count>1){
                    imageLayout.removeView(newView)
                    imageList.removeAt(count)
                    count--
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