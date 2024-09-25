package com.example.shoppinggroceryapp.fragments.appfragments

import android.graphics.Color
import android.icu.text.Transliterator.Position
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
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.GetAddress
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.SavedAddress
import com.example.shoppinggroceryapp.fragments.appfragments.orderfragments.OrderSummaryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.CartAdapter
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.RecyclerDataClass
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.user.Address
import com.example.shoppinggroceryapp.viewmodel.cartviewmodel.CartViewModel
import com.example.shoppinggroceryapp.viewmodel.cartviewmodel.CartViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.io.File

class CartFragment : Fragment() {

    companion object{
        var viewPriceDetailData = MutableLiveData(49f)
        var cartItemsSize = 0
        var selectedAddress:Address? = null
    }
    private var continuePressed = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var noOfItemsInt = 0
        val view =  inflater.inflate(R.layout.fragment_cart, container, false)
        val newRecyclerView = view.findViewById<RecyclerView>(R.id.multiTypeRecyclerView)
    val cartViewModel = ViewModelProvider(this,CartViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[CartViewModel::class.java]
        val list = listOf(RecyclerDataClass(0),RecyclerDataClass(1),RecyclerDataClass(2))
    var fileDir = File(requireContext().filesDir,"AppImages")
    val adapter = ProductListAdapter(this,fileDir,"C",false)
        newRecyclerView.adapter = CartAdapter(list,cartViewModel,this,adapter)
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        val db = AppDatabase.getAppDatabase(requireContext()).getUserDao()
        val bottomLayout = view.findViewById<LinearLayout>(R.id.linearLayout11)
        val price = view.findViewById<MaterialButton>(R.id.viewPriceDetailsButton)
        val continueButton = view.findViewById<MaterialButton>(R.id.continueButton)

        cartViewModel.getProductsByCartId(MainActivity.cartId)
        cartViewModel.cartProducts.observe(viewLifecycleOwner){
            adapter.setProducts(it)
            println("MRP ITEMS CALLED in Observer ${ProductListAdapter.productList.size}")
            noOfItemsInt = it.size
            println("NO OF ITEMS: in observer $noOfItemsInt")
            val str = "MRP ($noOfItemsInt) Products"
            if(noOfItemsInt<=1){
                println("!!!! cart IN IF $cartItemsSize")
                bottomLayout.setBackgroundColor(Color.TRANSPARENT)
                price.visibility =View.GONE
            }
            else{
                println("!!!! cart IN ELse $cartItemsSize")
                bottomLayout.setBackgroundColor(Color.WHITE)
                price.visibility =View.VISIBLE
            }
        }

        price.setOnClickListener {
            newRecyclerView.scrollToPosition(2)
        }

        viewPriceDetailData.observe(viewLifecycleOwner){
            if(it==49f){
                bottomLayout.visibility =View.GONE
            }
            else{
                bottomLayout.visibility =View.VISIBLE
                cartItemsSize = ProductListAdapter.productList.size
                println("!!!! cart MRP ITEMS CALLED ${ProductListAdapter.productList.size}")
                noOfItemsInt = ProductListAdapter.productList.size
                val str = "MRP ($noOfItemsInt) Products"
            }
            if(noOfItemsInt<=1){
                bottomLayout.setBackgroundColor(Color.TRANSPARENT)
                price.visibility = View.GONE
            }
            else{
                price.visibility = View.VISIBLE
            }
            val str = "₹$it\nView Price Details"
            val grandTot = "₹$it"
            val totalAmt = "₹${it-49}"
            println("!!!! cart $cartItemsSize")
            println("NO OF ITEMS INT: $noOfItemsInt")
            price.text = str
        }
        viewPriceDetailData.value = 49f
        cartViewModel.calculateInitialPrice(MainActivity.cartId)
        cartViewModel.totalPrice.observe(viewLifecycleOwner){
            viewPriceDetailData.value = it
        }
        cartViewModel.getAddressListForUser(MainActivity.userId.toInt())
        cartViewModel.addressList.observe(viewLifecycleOwner){ addressList ->
            if (addressList.isEmpty()) {
            }
            else {
                if(selectedAddress==null){
                    selectedAddress = addressList[0]
                }
                val addressVal = "${selectedAddress?.buildingName}, ${selectedAddress?.streetName}, ${selectedAddress?.city}, ${selectedAddress?.state}\n${selectedAddress?.postalCode}"
            }
        }
        continueButton.setOnClickListener {
            if(selectedAddress==null){
                Snackbar.make(view,"Please Add the Delivery Address to order Items",Toast.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
            }
            else{
                val orderSummaryFragment = OrderSummaryFragment()
                println("NO OF ITEMS: $noOfItemsInt")
                orderSummaryFragment.arguments = Bundle().apply {
                    putInt("noOfItems",noOfItemsInt)
                }
                FragmentTransaction.navigateWithBackstack(parentFragmentManager,orderSummaryFragment,"Order Summary Fragment")
            }
        }
        return view
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onStart() {
        super.onStart()
        InitialFragment.hideSearchBar.value = true
    }
    override fun onPause() {
        super.onPause()
        InitialFragment.hideSearchBar.value = false
    }
}