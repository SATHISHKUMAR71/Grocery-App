package com.example.shoppinggroceryapp.fragments.appfragments.orderfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton


class PaymentFragment : Fragment() {


    companion object{
        var paymentMode = ""
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_payment, container, false)
        val paymentOption = view.findViewById<AutoCompleteTextView>(R.id.paymentOption)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.paymentToolbar)
        val options = resources.getStringArray(R.array.paymentMode)
        val placeOrder = view.findViewById<MaterialButton>(R.id.placeOrder)
        paymentOption.setOnItemClickListener { parent, view, position, id ->
            paymentOption.setText(options[position])
            paymentMode = options[position]
            if(position==0){
                placeOrder.visibility = View.VISIBLE
            }
            else{
                placeOrder.visibility = View.GONE
            }
        }

        placeOrder.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,OrderSuccessFragment())
                .addToBackStack("Order Success Fragment")
                .commit()
        }
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
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

}