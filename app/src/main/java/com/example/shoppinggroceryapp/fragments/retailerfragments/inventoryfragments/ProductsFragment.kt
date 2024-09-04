package com.example.shoppinggroceryapp.fragments.retailerfragments.inventoryfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ProductsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_products, container, false)
        parentFragmentManager.beginTransaction()
            .replace(R.id.productList,ProductListFragment(null))
            .commit()

        view.findViewById<FloatingActionButton>(R.id.addProductsToInventory).setOnClickListener {
            ProductListFragment.selectedProduct.value = null
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentMainLayout,AddEditFragment())
                .addToBackStack("Edit in Product Fragments")
                .commit()
        }
        return view
    }




}