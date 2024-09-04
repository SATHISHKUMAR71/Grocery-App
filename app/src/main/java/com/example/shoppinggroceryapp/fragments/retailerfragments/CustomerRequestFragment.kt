package com.example.shoppinggroceryapp.fragments.retailerfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.fragments.retailerfragments.recyclerview.CustomerRequestAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.help.CustomerRequest

class CustomerRequestFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer_request, container, false)
        val customerReqRV = view.findViewById<RecyclerView>(R.id.customerRequestRecyclerView)
        Thread{
            val list = AppDatabase.getAppDatabase(requireContext()).getUserDao().getDataFromCustomerReq()

            MainActivity.handler.post {
                val adapter = CustomerRequestAdapter(this)
                customerReqRV.adapter = adapter
                customerReqRV.layoutManager = LinearLayoutManager(context)
                CustomerRequestAdapter.requestList = list.toMutableList()
            }
        }.start()
        return view
    }

}