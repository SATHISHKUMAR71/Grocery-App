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
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.MainActivity.Companion.cartId
import com.example.shoppinggroceryapp.MainActivity.Companion.userId
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderDetailFragment
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderListFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.CartMapping
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton


class OrderSuccessFragment(var searchBarTop: LinearLayout, var bottomNav: BottomNavigationView) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view =  inflater.inflate(R.layout.fragment_order_confirmation, container, false)
        view.findViewById<MaterialToolbar>(R.id.orderSuccessToolbar).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        view.findViewById<MaterialButton>(R.id.materialButtonClose).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        Thread{
            val list = AppDatabase.getAppDatabase(requireContext()).getUserDao().getCartItems(MainActivity.cartId)
            val addressList = AppDatabase.getAppDatabase(requireContext()).getUserDao().getAddressListForUser(MainActivity.userId.toInt())
            val address = CartFragment.selectedAddress
            AppDatabase.getAppDatabase(requireContext()).getRetailerDao().addOrder(
                OrderDetails(orderId = 0,
                    orderedDate = "30/08/2024",
                    deliveryDate = "2/09/2024", cartId = cartId, paymentMode = PaymentFragment.paymentMode, addressId = address!!.addressId, deliveryStatus = "Processing", paymentStatus = "Pending")
            )
            OrderListFragment.selectedOrder = AppDatabase.getAppDatabase(requireContext()).getUserDao().getOrder(cartId)
            OrderListFragment.correspondingCartList = AppDatabase.getAppDatabase(requireContext()).getUserDao().getProductsWithCartId(cartId)
            val db = AppDatabase.getAppDatabase(requireContext()).getUserDao()
            db.updateCartMapping(CartMapping(cartId, userId.toInt(),"not available"))
            val cart: CartMapping? = db.getCartForUser(userId.toInt())
            if (cart == null) {
                db.addCartForUser(CartMapping(0, userId = userId.toInt(), "available"))
                var newCart = db.getCartForUser(userId.toInt())
                cartId = newCart.cartId
            } else {
                cartId = cart.cartId
            }
            MainActivity.handler.post {
                val orderDetailFrag = OrderDetailFragment(searchBarTop,bottomNav)
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
        }.start()
        return view
    }

    override fun onResume() {
        super.onResume()
        searchBarTop.visibility =View.GONE
        bottomNav.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        searchBarTop.visibility = View.VISIBLE
        bottomNav.visibility = View.VISIBLE
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