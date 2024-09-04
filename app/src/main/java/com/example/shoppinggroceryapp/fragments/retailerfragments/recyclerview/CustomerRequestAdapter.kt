package com.example.shoppinggroceryapp.fragments.retailerfragments.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.model.entities.help.CustomerRequest

class CustomerRequestAdapter :RecyclerView.Adapter<CustomerRequestAdapter.CustomerRequestHolder>(){

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
        val name = "Customer Name: ${requestList[position].userId}"
        holder.reqDate.text = date
        holder.name.text = name
        holder.request.text = requestList[position].request
    }
}