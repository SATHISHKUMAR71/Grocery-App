package com.example.shoppinggroceryapp.fragments.appfragments.orderfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.SavedAddress
import com.example.shoppinggroceryapp.fragments.appfragments.orderfragments.viewpager.ProductViewPager
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.viewmodel.orderviewmodel.OrderSummaryViewModel
import com.example.shoppinggroceryapp.viewmodel.orderviewmodel.OrderSummaryViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.File


class OrderSummaryFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_order_summary, container, false)
        val addressOwnerName = view.findViewById<TextView>(R.id.addressOwnerNameOrderSummary)
        val addressValue = view.findViewById<TextView>(R.id.addressOrderSummary)
        val addressNumber = view.findViewById<TextView>(R.id.addressPhoneOrderSummary)
        val changeAddressButton = view.findViewById<MaterialButton>(R.id.changeAddressButtonOrderSummary)
        val noOfItems = view.findViewById<TextView>(R.id.priceDetailsMrpTotalItemsOrderSummary)
        val mrpPrice = view.findViewById<TextView>(R.id.priceDetailsMrpPriceOrderSummary)
        val totalAmount = view.findViewById<TextView>(R.id.priceDetailsTotalAmountOrderSummary)
        val continueToPayment = view.findViewById<MaterialButton>(R.id.continueButtonOrderSummary)
        val viewProductDetails = view.findViewById<MaterialButton>(R.id.viewPriceDetailsButtonOrderSummary)
        val orderSummaryToolBar = view.findViewById<MaterialToolbar>(R.id.orderSummaryToolbar)
        val orderSummaryViewModel = ViewModelProvider(this,OrderSummaryViewModelFactory(userDao = AppDatabase.getAppDatabase(requireContext()).getUserDao()))[OrderSummaryViewModel::class.java]
        val recyclerViewProducts = view.findViewById<RecyclerView>(R.id.orderListRecyclerView)
        orderSummaryViewModel.getProductsWithCartId(cartId = MainActivity.cartId)
        orderSummaryViewModel.cartItems.observe(viewLifecycleOwner){

            ProductViewPager.productsList = it
            recyclerViewProducts.adapter = ProductViewPager(File(requireContext().filesDir,"AppImages"))
            recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
        val addressVal = "${CartFragment.selectedAddress?.buildingName}, ${CartFragment.selectedAddress?.streetName}, ${CartFragment.selectedAddress?.city}, ${CartFragment.selectedAddress?.state}, ${CartFragment.selectedAddress?.postalCode}"
        addressOwnerName.text = CartFragment.selectedAddress?.addressContactName
        addressValue.text = addressVal
        addressNumber.text = CartFragment.selectedAddress?.addressContactNumber

        val items = "MRP (${CartFragment.cartItemsSize} Items)"
        noOfItems.text = items
        val price = "₹${CartFragment.viewPriceDetailData.value}"
        mrpPrice.text =price
        val viewPriceDetailsText ="$price\nView Price Details"
        viewProductDetails.text = viewPriceDetailsText
        totalAmount.text = price
        changeAddressButton.setOnClickListener {
            val savedAddress = SavedAddress()
            savedAddress.arguments = Bundle().apply {
                putBoolean("clickable",true)
            }
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,savedAddress,"Get the address from saved address")
//            parentFragmentManager.beginTransaction()
//                .setCustomAnimations(
//                    R.anim.fade_in,
//                    R.anim.fade_out,
//                    R.anim.fade_in,
//                    R.anim.fade_out
//                )
//                .replace(R.id.fragmentMainLayout,savedAddress)
//                .addToBackStack("Get the address from saved address")
//                .commit()
        }



        orderSummaryToolBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        continueToPayment.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,PaymentFragment(),"Payment Fragment")
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        InitialFragment.hideBottomNav.value = true
        InitialFragment.hideSearchBar.value = true
    }

    override fun onPause() {
        super.onPause()
        InitialFragment.hideBottomNav.value = false
        InitialFragment.hideSearchBar.value = false
    }
}