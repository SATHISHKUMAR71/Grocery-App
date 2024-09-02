package com.example.shoppinggroceryapp.fragments.appfragments.accountfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.CartWithProductData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class OrderDetailFragment : Fragment() {


    private var totalPrice = 0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_detail, container, false)
        val productsContainer = view.findViewById<LinearLayout>(R.id.orderedProductViews)

        if(arguments?.getBoolean("hideToolBar")==true){
            view.findViewById<MaterialToolbar>(R.id.materialToolbarOrderDetail).visibility = View.GONE
        }
        view.findViewById<MaterialToolbar>(R.id.materialToolbarOrderDetail).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        Thread{
            val address = AppDatabase.getAppDatabase(requireContext()).getUserDao().getAddress(OrderListFragment.selectedOrder!!.addressId)
            MainActivity.handler.post {
                view.findViewById<TextView>(R.id.addressOwnerName).text = address.addressContactName
                val addressText = with(address){
                    "$buildingName, $streetName, $city, $state, $postalCode"
                }
                view.findViewById<TextView>(R.id.address).text = addressText
                view.findViewById<TextView>(R.id.addressPhone).text = address.addressContactNumber
            }
        }.start()
        view.findViewById<TextView>(R.id.orderIdValue).text = OrderListFragment.selectedOrder?.orderId.toString()
        var totalItems = 0
        val productView = (LayoutInflater.from(requireContext()).inflate(R.layout.ordered_product_layout,productsContainer,false))
        for(i in OrderListFragment.correspondingCartList!!){
            addView(productsContainer,i)
            totalItems++
        }
        val totalItemsStr = "MRP ($totalItems Items)"
        view.findViewById<TextView>(R.id.priceDetailsMrpTotalItems).text = totalItemsStr
        val totalPriceStr = "₹$totalPrice"
        view.findViewById<TextView>(R.id.priceDetailsMrpPrice).text = totalPriceStr
        view.findViewById<TextView>(R.id.priceDetailsTotalAmount).text = totalPriceStr
        return view
    }


    private fun addView(container:LinearLayout,productInfo:CartWithProductData){
        val newView =LayoutInflater.from(requireContext()).inflate(R.layout.ordered_product_layout,container,false)
        newView.findViewById<TextView>(R.id.orderedProductFullName).text = productInfo.productName
        newView.findViewById<TextView>(R.id.orderedProductQuantity).text = productInfo.productQuantity
        totalPrice += (productInfo.totalItems*productInfo.unitPrice)
        val totalPrice = "₹${(productInfo.totalItems*productInfo.unitPrice)}"
        newView.findViewById<TextView>(R.id.orderedProductTotalPrice).text = totalPrice
        val eachPrice = "₹${(productInfo.unitPrice)}"
        newView.findViewById<TextView>(R.id.orderedEachProductPrice).text = eachPrice
        val str = "(${productInfo.totalItems})"
        newView.findViewById<TextView>(R.id.orderedNoOfProducts).text =str
        container.addView(newView)
    }

    override fun onResume() {
        super.onResume()
        InitialFragment.hideSearchBar.value = true
    }
    override fun onStop() {
        super.onStop()
        InitialFragment.hideSearchBar.value = false
    }
}