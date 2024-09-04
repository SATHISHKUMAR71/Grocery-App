package com.example.shoppinggroceryapp.fragments.appfragments.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.Help
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderDetailFragment
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderListFragment
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.example.shoppinggroceryapp.model.entities.products.CartWithProductData
import com.google.android.material.bottomnavigation.BottomNavigationView

class OrderListAdapter(var orderedItems:MutableList<OrderDetails>, var fragment:Fragment,var clickable:Boolean?):RecyclerView.Adapter<OrderListAdapter.OrderLayoutViewHolder>() {

    companion object{
        var cartWithProductList = mutableListOf<MutableList<CartWithProductData>>()
    }

    inner class OrderLayoutViewHolder(orderView:View):RecyclerView.ViewHolder(orderView){
        val productNames = orderView.findViewById<TextView>(R.id.orderedProductsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderLayoutViewHolder {
        return OrderLayoutViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.order_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return orderedItems.size
    }

    override fun onBindViewHolder(holder: OrderLayoutViewHolder, position: Int) {
        if(orderedItems[position].deliveryStatus=="Pending"){
            val screen = "Expected On: ${DateGenerator.getDayAndMonth(orderedItems[position].deliveryDate)}"
            holder.itemView.findViewById<TextView>(R.id.deliveryDate).text = screen
        }
        else{
            val screen = "Delivered On: ${DateGenerator.getDayAndMonth(orderedItems[position].deliveryDate)}"
            holder.itemView.findViewById<TextView>(R.id.deliveryDate).text = screen
        }
        var productName=""
        var i =0
        for(cartWithProductData in cartWithProductList[position]){
            if(i==0){
                productName += cartWithProductData.productName+" (${cartWithProductData.totalItems}) "
                i=1
            }
            else{
                productName += ", "+cartWithProductData.productName+" (${cartWithProductData.totalItems})"
            }
        }
        holder.productNames.text = productName

        holder.itemView.setOnClickListener {
            if(clickable==true){
                Help.selectedOrder = orderedItems[position]
                fragment.parentFragmentManager.popBackStack()
            }
            else {
                OrderListFragment.selectedOrder = orderedItems[position]
                OrderListFragment.correspondingCartList = cartWithProductList[position]
                fragment.parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                    .replace(R.id.fragmentMainLayout, OrderDetailFragment())
                    .addToBackStack("Order Detail Fragment")
                    .commit()
            }
        }
    }
}