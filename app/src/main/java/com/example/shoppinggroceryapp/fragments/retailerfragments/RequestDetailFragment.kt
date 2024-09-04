package com.example.shoppinggroceryapp.fragments.retailerfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderDetailFragment
import com.google.android.material.appbar.MaterialToolbar

class RequestDetailFragment : Fragment() {
    companion object{
        var open = false
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request_detail, container, false)
        val orderDetailFrag = OrderDetailFragment()
        open = true
        orderDetailFrag.arguments = Bundle().apply {
            putBoolean("hideToolBar",true)
        }
        view.findViewById<MaterialToolbar>(R.id.customerRequestToolbar).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        view.findViewById<TextView>(R.id.customerName).text = CustomerRequestFragment.customerName
        view.findViewById<TextView>(R.id.customerRequestText).text = CustomerRequestFragment.customerRequest
        view.findViewById<TextView>(R.id.requestedDate).text = CustomerRequestFragment.requestedDate
        parentFragmentManager.beginTransaction()
            .replace(R.id.orderDetailsFragment,orderDetailFrag)
            .commit()
        return view
    }

    override fun onStop() {
        super.onStop()
        open = false
        InitialFragment.hideSearchBar.value = false
    }
}