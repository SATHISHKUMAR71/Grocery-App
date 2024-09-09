package com.example.shoppinggroceryapp.fragments.appfragments.accountfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.OrderListAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.example.shoppinggroceryapp.model.entities.products.CartWithProductData
import com.example.shoppinggroceryapp.viewmodel.accountviewmodel.OrderListViewModel
import com.example.shoppinggroceryapp.viewmodel.accountviewmodel.OrderListViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


class OrderListFragment : Fragment() {


    companion object{
        var selectedOrder:OrderDetails? = null
        var correspondingCartList:List<CartWithProductData>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(Help.backPressed){
            parentFragmentManager.popBackStack()
            Help.backPressed = false
        }
        var dataReady:MutableLiveData<Boolean> = MutableLiveData()
        val view =  inflater.inflate(R.layout.fragment_order_list, container, false)
        val clickable = arguments?.getBoolean("isClickable",false)
        val orderListViewModel = ViewModelProvider(this,OrderListViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getRetailerDao()))[com.example.shoppinggroceryapp.viewmodel.accountviewmodel.OrderListViewModel::class.java]
        var cartWithProductsList = mutableListOf<MutableList<CartWithProductData>>()
        var orderedItems:MutableList<OrderDetails> = mutableListOf()
        var orderAdapter = OrderListAdapter(orderedItems.toMutableList(), this, clickable)

        orderListViewModel.orderedItems.observe(viewLifecycleOwner){
            orderedItems = it.toMutableList()
            orderAdapter.setOrders(it.toMutableList())
            orderListViewModel.getCartWithProducts()
        }

        orderListViewModel.cartWithProductList.observe(viewLifecycleOwner){
            if(cartWithProductsList.size == orderedItems.size){
                dataReady.value=true
            }
        }
        orderListViewModel.dataReady.observe(viewLifecycleOwner){
            cartWithProductsList = orderListViewModel.cartWithProductList.value!!
            val orderList = view.findViewById<RecyclerView>(R.id.orderList)
            if(orderList.adapter==null) {
                orderList.adapter = orderAdapter
                orderList.layoutManager = LinearLayoutManager(context)
            }
            OrderListAdapter.cartWithProductList = cartWithProductsList
        }

        if(!MainActivity.isRetailer) {
            if(orderedItems.isEmpty()) {
                orderListViewModel.getOrdersForSelectedUser(MainActivity.userId.toInt())
            }
        }
        else{
            if(orderedItems.isEmpty()) {
                orderListViewModel.getOrderedItemsForRetailer()
            }
        }

        val toolbar = view.findViewById<MaterialToolbar>(R.id.materialToolbarOrderList)
        if(MainActivity.isRetailer){
            toolbar.setTitle("Orders From Customers")
        }
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        if(clickable==true){
            toolbar.setTitle("Select an Order")
        }
        return view
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