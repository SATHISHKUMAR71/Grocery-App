package com.example.shoppinggroceryapp.fragments.appfragments

import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.SavedAddress
import com.example.shoppinggroceryapp.fragments.appfragments.orderfragments.OrderSummaryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.user.Address
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.io.File

class CartFragment : Fragment() {

    companion object{
        var viewPriceDetailData = MutableLiveData(0f)
        var cartItemsSize = 0
        var selectedAddress:Address? = null
    }
    private var continuePressed = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_cart, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.cartList)
        var fileDir = File(requireContext().filesDir,"AppImages")
        val db = AppDatabase.getAppDatabase(requireContext()).getUserDao()
        val deliveryAddressNotFound = view.findViewById<LinearLayout>(R.id.deliveryAddressLayoutNotFound)
        val deliveryAddressFound = view.findViewById<LinearLayout>(R.id.deliveryAddressLayout)
        val addressOwnerName = view.findViewById<TextView>(R.id.addressOwnerName)
        val address = view.findViewById<TextView>(R.id.address)
        val addNewAddress = view.findViewById<MaterialButton>(R.id.addNewAddressButton)
        val changeAddress = view.findViewById<MaterialButton>(R.id.changeAddressButton)
        val addMoreGrocery = view.findViewById<MaterialButton>(R.id.addMoreGroceryButton)
        val addressContactNumber = view.findViewById<TextView>(R.id.addressPhone)
        val bottomLayout = view.findViewById<LinearLayout>(R.id.linearLayout11)
        val price = view.findViewById<MaterialButton>(R.id.viewPriceDetailsButton)
        val priceDetails = view.findViewById<LinearLayout>(R.id.linearLayout12)
        val noOfItems = view.findViewById<TextView>(R.id.priceDetailsMrpTotalItems)
        val emptyCart = view.findViewById<ImageView>(R.id.emptyCartImage)
        val totalAmount =view.findViewById<TextView>(R.id.priceDetailsMrpPrice)
        val continueButton = view.findViewById<MaterialButton>(R.id.continueButton)
        val discountedAmount = view.findViewById<TextView>(R.id.priceDetailsDiscountedAmount)
        val grandTotalAmount = view.findViewById<TextView>(R.id.priceDetailsTotalAmount)
        addMoreGrocery.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,CategoryFragment())
                .addToBackStack("Added More Groceries")
                .commit()
        }

            val adapter = ProductListAdapter(this,fileDir,"C")
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            Thread{
                val list = db.getProductsByCartId(MainActivity.cartId)
                MainActivity.handler.post {
                    adapter.setProducts(list)
                }
            }.start()
        viewPriceDetailData.observe(viewLifecycleOwner){
            if(it==0f){
                recyclerView.visibility = View.GONE
                priceDetails.visibility =View.GONE
                bottomLayout.visibility =View.GONE
                emptyCart.visibility = View.VISIBLE
            }
            else{
                recyclerView.visibility = View.VISIBLE
                priceDetails.visibility =View.VISIBLE
                bottomLayout.visibility =View.VISIBLE
                emptyCart.visibility = View.GONE
                cartItemsSize = ProductListAdapter.productList.size
                val str = "MRP (${ProductListAdapter.productList.size}) Items"
                noOfItems.text =str
            }
            val str = "₹$it\nView Price Details"
            val str2 = "₹$it"
            totalAmount.text =str2
            price.text = str
        }
        viewPriceDetailData.value = 0f
        Thread{
            val list = db.getCartItems(MainActivity.cartId)
            for(cart in list){
                MainActivity.handler.post {
                    viewPriceDetailData.value = viewPriceDetailData.value!!+((cart.totalItems) * (cart.unitPrice))
                }
            }
        }.start()

        Thread {
            val addressList = db.getAddressListForUser(MainActivity.userId.toInt())
            MainActivity.handler.post {
                if (addressList.isEmpty()) {
                    deliveryAddressNotFound.visibility = View.VISIBLE
                    deliveryAddressFound.visibility = View.GONE
                } else {
                    deliveryAddressFound.visibility = View.VISIBLE
                    deliveryAddressNotFound.visibility = View.GONE
                    if(selectedAddress==null){
                        selectedAddress = addressList[0]
                    }
                    addressOwnerName.text = selectedAddress?.addressContactName
                    val addressVal = "${selectedAddress?.buildingName}, ${selectedAddress?.streetName}, ${selectedAddress?.city}, ${selectedAddress?.state}\n${selectedAddress?.postalCode}"
                    address.text =addressVal
                    addressContactNumber.text = selectedAddress?.addressContactNumber
                }
            }
        }.start()

        continueButton.setOnClickListener {
            if(selectedAddress==null){
                Snackbar.make(view,"Please Add the Delivery Address to order Items",Toast.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
            }
            else{
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                    .replace(R.id.fragmentMainLayout,OrderSummaryFragment())
                    .addToBackStack("Order Summary Fragment")
                    .commit()
            }
        }
        addNewAddress.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,SavedAddress())
                .addToBackStack("Add New Address")
                .commit()
        }

        changeAddress.setOnClickListener {
            val savedAddressFragment = SavedAddress()
            savedAddressFragment.arguments = Bundle().apply {
                putBoolean("clickable",true)
            }
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,savedAddressFragment)
                .addToBackStack("Add New Address")
                .commit()
        }

        return view
    }
}