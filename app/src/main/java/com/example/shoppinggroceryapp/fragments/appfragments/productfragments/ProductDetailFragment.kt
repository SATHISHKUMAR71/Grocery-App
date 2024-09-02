package com.example.shoppinggroceryapp.fragments.appfragments.productfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton


class ProductDetailFragment(private var searchbarTop:LinearLayout, private var bottomNav:BottomNavigationView) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_product_detail, container, false)
        view.findViewById<MaterialButton>(R.id.categoryButton).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,CategoryFragment(searchbarTop,bottomNav))
                .addToBackStack("Category Opened from product Detail")
                .commit()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        searchbarTop.visibility = View.GONE
        bottomNav.visibility = View.GONE
    }
    override fun onStop() {
        super.onStop()
        searchbarTop.visibility = View.VISIBLE
        bottomNav.visibility = View.VISIBLE
    }
}