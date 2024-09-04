package com.example.shoppinggroceryapp.fragments.retailerfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val adapter = CustomerRequestAdapter()
        val list = mutableListOf<CustomerRequest>()
        list.add(CustomerRequest(1, 1, DateGenerator.getCurrentDate(), 101, "Hi, could you help me find some almond milk and gluten-free pasta? Also, I need some ripe avocados. Thanks!"));
        list.add(CustomerRequest(2, 1, DateGenerator.getCurrentDate(), 102, "I’m looking for a specific brand of cereal and some fresh strawberries. Can you help me locate them?"));
        list.add(CustomerRequest(3, 1, DateGenerator.getCurrentDate(), 103, "Can you assist me in finding organic chicken breasts and a good quality balsamic vinegar? Thanks!"));
        list.add(CustomerRequest(4, 1, DateGenerator.getCurrentDate(), 104, "I need whole wheat flour and some fresh rosemary. Where can I find those?"));
        list.add(CustomerRequest(5, 1, DateGenerator.getCurrentDate(), 105, "Could you show me where the canned beans are and some fresh lemons? Thank you!"));
        list.add(CustomerRequest(6, 1, DateGenerator.getCurrentDate(), 106, "Hi, I need a dairy-free cheese alternative and some gluten-free bread. Can you help me find these items?"));
        list.add(CustomerRequest(7, 1, DateGenerator.getCurrentDate(), 107, "I’m searching for some high-quality dark chocolate and a bottle of sparkling water. Any idea where I can find them?"));
        list.add(CustomerRequest(8, 1, DateGenerator.getCurrentDate(), 108, "Can you point me to the section with fresh herbs and some high-fiber cereal? Thanks!"));
        list.add(CustomerRequest(9, 1, DateGenerator.getCurrentDate(), 109, "I need a pack of organic eggs and some low-sodium soy sauce. Where can I locate these?"));
        list.add(CustomerRequest(10, 1, DateGenerator.getCurrentDate(), 110, "I’m looking for some fresh salmon and a nice bottle of white wine. Can you help me find these?"));
        CustomerRequestAdapter.requestList = list
        Thread{
            println("%%%% CUSTOMER REQUEST: ${AppDatabase.getAppDatabase(requireContext()).getUserDao().getDataFromCustomerReq()}")
        }.start()
        val customerReqRV = view.findViewById<RecyclerView>(R.id.customerRequestRecyclerView)
        customerReqRV.adapter = adapter
        customerReqRV.layoutManager = LinearLayoutManager(context)

        return view
    }

}