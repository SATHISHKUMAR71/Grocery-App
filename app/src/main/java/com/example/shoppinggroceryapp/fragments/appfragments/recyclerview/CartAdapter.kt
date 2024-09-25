package com.example.shoppinggroceryapp.fragments.appfragments.recyclerview


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment.Companion.cartItemsSize
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment.Companion.selectedAddress
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment.Companion.viewPriceDetailData
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.GetAddress
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.SavedAddress
import com.example.shoppinggroceryapp.viewmodel.cartviewmodel.CartViewModel
import com.google.android.material.button.MaterialButton
import java.io.File

class CartAdapter(var list:List<RecyclerDataClass>,var cartViewModel: CartViewModel,var fragment: Fragment,var adapter:ProductListAdapter):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var noOfItemsInt = 0

    inner class CartInitialViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }
    inner class CartRecyclerViewViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }
    inner class CartPriceDataViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> {
                CartInitialViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cart_initial_data,parent,false))
            }
            1 -> {
                CartRecyclerViewViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cart_recycler_view,parent,false))
            }
            2-> {
                CartPriceDataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cart_price_data,parent,false))
            }
            else -> {throw IllegalArgumentException("INVALID TYPE")}
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        println("DATA SET @#@#")
        when(holder){
            is CartInitialViewHolder -> {
                var view = holder.itemView
                val deliveryAddressNotFound = view.findViewById<LinearLayout>(R.id.deliveryAddressLayoutNotFound)
                val deliveryAddressFound = view.findViewById<LinearLayout>(R.id.deliveryAddressLayout)
                val addressOwnerName = view.findViewById<TextView>(R.id.addressOwnerName)
                val address = view.findViewById<TextView>(R.id.address)
                val addNewAddress = view.findViewById<MaterialButton>(R.id.addNewAddressButton)
                val changeAddress = view.findViewById<MaterialButton>(R.id.changeAddressButton)
                val addMoreGrocery = view.findViewById<MaterialButton>(R.id.addMoreGroceryButton)
                val addressContactNumber = view.findViewById<TextView>(R.id.addressPhone)
                cartViewModel.getAddressListForUser(MainActivity.userId.toInt())
                addMoreGrocery.setOnClickListener {
                    FragmentTransaction.navigateWithBackstack(fragment.parentFragmentManager,
                        CategoryFragment(),"Added More Groceries")
                }
                cartViewModel.addressList.observe(fragment.viewLifecycleOwner){ addressList ->
                    if (addressList.isEmpty()) {
                deliveryAddressNotFound.visibility = View.VISIBLE
                deliveryAddressFound.visibility = View.GONE
                    } else {
                deliveryAddressFound.visibility = View.VISIBLE
                deliveryAddressNotFound.visibility = View.GONE
                        if(selectedAddress ==null){
                            selectedAddress = addressList[0]
                        }
                addressOwnerName.text = selectedAddress?.addressContactName
                        val addressVal = "${selectedAddress?.buildingName}, ${selectedAddress?.streetName}, ${selectedAddress?.city}, ${selectedAddress?.state}\n${selectedAddress?.postalCode}"
                address.text =addressVal
                addressContactNumber.text = selectedAddress?.addressContactNumber
                    }
                }
                addNewAddress.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(fragment.parentFragmentManager, GetAddress(),"Add New Address")
//            FragmentTransaction.navigateWithBackstack(parentFragmentManager,SavedAddress(),"Add New Address")
        }
                        changeAddress.setOnClickListener {
            val savedAddressFragment = SavedAddress()
            savedAddressFragment.arguments = Bundle().apply {
                putBoolean("clickable",true)
            }
            FragmentTransaction.navigateWithBackstack(fragment.parentFragmentManager,savedAddressFragment,"Add New Address")
        }
            }
            is CartPriceDataViewHolder -> {
                var view = holder.itemView
                val priceDetails = view.findViewById<LinearLayout>(R.id.linearLayout12)
                val noOfItems = view.findViewById<TextView>(R.id.priceDetailsMrpTotalItems)
                val emptyCart = view.findViewById<ImageView>(R.id.emptyCartImage)
                val totalAmount =view.findViewById<TextView>(R.id.priceDetailsMrpPrice)
                val grandTotalAmount = view.findViewById<TextView>(R.id.priceDetailsTotalAmount)
                cartViewModel.calculateInitialPrice(MainActivity.cartId)
                viewPriceDetailData.value = 49f
                viewPriceDetailData.observe(fragment.viewLifecycleOwner){
                    println("Products set 0909 EMPTY IMAGE IS VISIBLE Start $it $noOfItemsInt")
                    if(it==49f){
//                recyclerView.visibility = View.GONE
                        println("EMPTY IMAGE IS VISIBLE")
                        priceDetails.visibility =View.GONE
                        emptyCart.visibility = View.VISIBLE
                    }
                    else{
                        println("EMPTY IMAGE IS VISIBLE on else")
//                recyclerView.visibility = View.VISIBLE
                        priceDetails.visibility =View.VISIBLE
//                        bottomLayout.visibility =View.VISIBLE
                        emptyCart.visibility = View.GONE
                        cartItemsSize = ProductListAdapter.productList.size
                        println("!!!! cart MRP ITEMS CALLED ${ProductListAdapter.productList.size}")
                        noOfItemsInt = ProductListAdapter.productList.size
                        val str = "MRP ($noOfItemsInt) Products"
                        noOfItems.text =str
                    }
                    if(noOfItemsInt<=1){
//                        bottomLayout.setBackgroundColor(Color.TRANSPARENT)
//                        price.visibility = View.GONE
                    }
                    else{
//                        price.visibility = View.VISIBLE
                    }
                    val str = "₹$it\nView Price Details"
                    val grandTot = "₹$it"
                    val totalAmt = "₹${it-49}"
                    println("!!!! cart $cartItemsSize")
                    grandTotalAmount.text = grandTot
                    println("NO OF ITEMS INT: $noOfItemsInt")
                    totalAmount.text =totalAmt
                    println("Products set 0909 EMPTY IMAGE IS VISIBLE End $it $noOfItemsInt")
//                    price.text = str
                }

                cartViewModel.totalPrice.observe(fragment.viewLifecycleOwner){
                    viewPriceDetailData.value = it
                }
            }
            is CartRecyclerViewViewHolder -> {
                val view = holder.itemView
                var recyclerView = view.findViewById<RecyclerView>(R.id.cartList)
                var fileDir = File(fragment.requireContext().filesDir,"AppImages")
//                val adapter = ProductListAdapter(fragment,fileDir,"C",false)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(fragment.requireContext())
//                cartViewModel.getProductsByCartId(MainActivity.cartId)
//                cartViewModel.cartProducts.observe(fragment.viewLifecycleOwner){
//                    println("Products set 0909 $it ${recyclerView.isVisible}")
//                    adapter.setProducts(it)
//
//                    println("MRP ITEMS CALLED in Observer ${ProductListAdapter.productList.size}")
//                    noOfItemsInt = it.size
//                    println("NO OF ITEMS: in observer $noOfItemsInt")
//                    val str = "MRP ($noOfItemsInt) Products"
////            noOfItems.text =str
//                    if(noOfItemsInt<=1){
//                        println("!!!! cart IN IF $cartItemsSize")
//                    }
//                    else{
//                        println("!!!! cart IN ELse $cartItemsSize")
//                    }
//                }
            }
        }
    }


}