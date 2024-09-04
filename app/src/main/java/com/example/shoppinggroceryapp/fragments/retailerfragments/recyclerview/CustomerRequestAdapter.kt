package com.example.shoppinggroceryapp.fragments.retailerfragments.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderListFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.CustomerRequestFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.RequestDetailFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.help.CustomerRequest

class CustomerRequestAdapter(var fragment: Fragment) :RecyclerView.Adapter<CustomerRequestAdapter.CustomerRequestHolder>(){

    companion object {
        var requestList = mutableListOf<CustomerRequest>()
    }
    inner class CustomerRequestHolder(customerView:View):RecyclerView.ViewHolder(customerView){
        val reqDate = customerView.findViewById<TextView>(R.id.requestedDate)
        val name = customerView.findViewById<TextView>(R.id.customerName)
        var request = customerView.findViewById<TextView>(R.id.customerRequestText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerRequestHolder {
        return CustomerRequestHolder(LayoutInflater.from(parent.context).inflate(R.layout.customer_request_view,parent,false))
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: CustomerRequestHolder, position: Int) {
        val date = "Requested On: ${DateGenerator.getDayAndMonth(requestList[position].requestedDate)}"
        Thread{
            val userName = AppDatabase.getAppDatabase(fragment.requireContext()).getUserDao().getUserFirstName(requestList[position].userId)
            val lastName = AppDatabase.getAppDatabase(fragment.requireContext()).getUserDao().getUserLastName(requestList[position].userId)
            val name:String = if(lastName.isEmpty()){
                "Customer Name: $userName"
            } else{
                "Customer Name: $userName $lastName"
            }
            MainActivity.handler.post {
                if(holder.absoluteAdapterPosition==position) {
                    holder.name.text = name
                }
            }
        }.start()
        holder.reqDate.text = date
        holder.request.text = requestList[position].request
        holder.itemView.setOnClickListener {
            Thread {
                val order = AppDatabase.getAppDatabase(fragment.requireContext()).getUserDao().getOrderDetails(
                    requestList[position].orderId)
                val cartData = AppDatabase.getAppDatabase(fragment.requireContext()).getUserDao().getProductsWithCartId(order.cartId)
                val userName = AppDatabase.getAppDatabase(fragment.requireContext()).getUserDao().getUserFirstName(requestList[position].userId)
                val lastName = AppDatabase.getAppDatabase(fragment.requireContext()).getUserDao().getUserLastName(requestList[position].userId)
                val name:String = if(lastName.isEmpty()){
                    "Customer Name: $userName"
                } else{
                    "Customer Name: $userName $lastName"
                }
                MainActivity.handler.post {
                    CustomerRequestFragment.customerName = name
                    CustomerRequestFragment.customerRequest = requestList[position].request
                    CustomerRequestFragment.requestedDate = DateGenerator.getDayAndMonth(requestList[position].requestedDate)
                    OrderListFragment.selectedOrder =  order
                    OrderListFragment.correspondingCartList = cartData
                    fragment.parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentMainLayout, RequestDetailFragment())
                        .addToBackStack("Request Detail Fragment")
                        .commit()
                }
            }.start()
        }
    }
}