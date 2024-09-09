package com.example.shoppinggroceryapp.fragments.appfragments.orderfragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.MainActivity.Companion.cartId
import com.example.shoppinggroceryapp.MainActivity.Companion.userId
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderDetailFragment
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderListFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.CartMapping
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.example.shoppinggroceryapp.viewmodel.orderviewmodel.OrderSuccessViewModel
import com.example.shoppinggroceryapp.viewmodel.orderviewmodel.OrderSuccessViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton


class OrderSuccessFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view =  inflater.inflate(R.layout.fragment_order_confirmation, container, false)
        val orderSuccessViewModel = ViewModelProvider(this,OrderSuccessViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getRetailerDao()))[OrderSuccessViewModel::class.java]
        view.findViewById<MaterialToolbar>(R.id.orderSuccessToolbar).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        view.findViewById<MaterialButton>(R.id.materialButtonClose).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
//        Thread{
//            val list = AppDatabase.getAppDatabase(requireContext()).getUserDao().getCartItems(MainActivity.cartId)
//            val addressList = AppDatabase.getAppDatabase(requireContext()).getUserDao().getAddressListForUser(MainActivity.userId.toInt())
//            val address = CartFragment.selectedAddress
//            AppDatabase.getAppDatabase(requireContext()).getRetailerDao().addOrder(
//                OrderDetails(orderId = 0,
//                    orderedDate = DateGenerator.getCurrentDate(),
//                    deliveryDate = DateGenerator.getDeliveryDate(), cartId = cartId, paymentMode = PaymentFragment.paymentMode, addressId = address!!.addressId, deliveryStatus = "Pending", paymentStatus = "Pending")
//            )
//            OrderListFragment.selectedOrder = AppDatabase.getAppDatabase(requireContext()).getUserDao().getOrder(cartId)
//            OrderListFragment.correspondingCartList = AppDatabase.getAppDatabase(requireContext()).getUserDao().getProductsWithCartId(cartId)
//            val db = AppDatabase.getAppDatabase(requireContext()).getUserDao()
//            db.updateCartMapping(CartMapping(cartId, userId.toInt(),"not available"))
//            val cart: CartMapping? = db.getCartForUser(userId.toInt())
//            if (cart == null) {
//                db.addCartForUser(CartMapping(0, userId = userId.toInt(), "available"))
//                var newCart = db.getCartForUser(userId.toInt())
//                cartId = newCart.cartId
//            } else {
//                cartId = cart.cartId
//            }
//            MainActivity.handler.post {
//                val orderDetailFrag = OrderDetailFragment()
//                orderDetailFrag.arguments = Bundle().apply {
//                    putBoolean("hideToolBar",true)
//                }
//                parentFragmentManager.beginTransaction()
//                    .setCustomAnimations(
//                        R.anim.fade_in,
//                        R.anim.fade_out,
//                        R.anim.fade_in,
//                        R.anim.fade_out
//                    )
//                    .replace(R.id.orderSummaryFragment,orderDetailFrag)
//                    .commit()
//            }
//        }.start()


        val address = CartFragment.selectedAddress
        val tmpCartId = cartId
        orderSuccessViewModel.placeOrder(tmpCartId,PaymentFragment.paymentMode,address!!.addressId,"Pending","Pending")
        orderSuccessViewModel.getOrderAndCorrespondingCart(tmpCartId)
        orderSuccessViewModel.gotOrder.observe(viewLifecycleOwner){
            OrderListFragment.selectedOrder = it
            if(OrderListFragment.correspondingCartList!=null && (OrderListFragment.selectedOrder!=null)){
                doFragmentTransaction()
            }
        }
        orderSuccessViewModel.cartItems.observe(viewLifecycleOwner){
            OrderListFragment.correspondingCartList = it
            if(OrderListFragment.selectedOrder!=null && (OrderListFragment.correspondingCartList!=null)){
                doFragmentTransaction()
            }
        }
        orderSuccessViewModel.updateAndAssignNewCart(cartId, userId.toInt())
        orderSuccessViewModel.newCart.observe(viewLifecycleOwner){
            cartId = it.cartId
        }

        return view
    }

    private fun doFragmentTransaction() {
        val orderDetailFrag = OrderDetailFragment()
        orderDetailFrag.arguments = Bundle().apply {
            putBoolean("hideToolBar",true)
        }
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(R.id.orderSummaryFragment,orderDetailFrag)
            .commit()
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


    override fun onStop() {
        super.onStop()
        restartApp()
    }

    private fun restartApp() {
        val intent = Intent(context,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}