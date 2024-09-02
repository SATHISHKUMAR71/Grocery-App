package com.example.shoppinggroceryapp.fragments.appfragments.accountfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.OrderListAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.example.shoppinggroceryapp.model.entities.products.CartWithProductData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


class OrderListFragment(var searchBarTop:LinearLayout,var bottomnav:BottomNavigationView) : Fragment() {


    companion object{
        var orderDetailsMap = mutableMapOf<OrderDetails,List<CartWithProductData>>()
        var selectedOrder:OrderDetails? = null
        var correspondingCartList:List<CartWithProductData>? = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_order_list, container, false)
        Thread{
            val orderedItems = AppDatabase.getAppDatabase(requireContext()).getRetailerDao().getOrdersForUser(MainActivity.userId.toInt())
            val cartWithProductsList = mutableListOf<MutableList<CartWithProductData>>()
            for(i in orderedItems){
                val cartItemsForOrderId = AppDatabase.getAppDatabase(requireContext()).getUserDao().getProductsWithCartId(i.cartId)
                cartWithProductsList.add(cartItemsForOrderId.toMutableList())
                orderDetailsMap[i] = cartItemsForOrderId
            }
            MainActivity.handler.post {
                val orderList = view.findViewById<RecyclerView>(R.id.orderList)
                orderList.adapter = OrderListAdapter(orderedItems.toMutableList(),this,searchBarTop,bottomnav)
                orderList.layoutManager = LinearLayoutManager(context)
                OrderListAdapter.cartWithProductList = cartWithProductsList
            }
        }.start()

        view.findViewById<MaterialToolbar>(R.id.materialToolbarOrderList).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onStop() {
        super.onStop()
        searchBarTop.visibility = View.VISIBLE
    }
    override fun onResume() {
        super.onResume()
        searchBarTop.visibility = View.GONE
    }
}