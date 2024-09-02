package com.example.shoppinggroceryapp.fragments.appfragments.recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment

class SearchViewAdapter(var fragment: Fragment) : RecyclerView.Adapter<SearchViewAdapter.SearchViewHolder>(){
    companion object{
        var searchList = mutableListOf<String>()
    }
    inner class SearchViewHolder(searchView:View):RecyclerView.ViewHolder(searchView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_view_holder,parent,false))
    }

    override fun getItemCount(): Int {
        println(searchList)
        return searchList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.text).text = searchList[position]
        holder.itemView.setOnClickListener {
            InitialFragment.closeSearchView.value = true
            val productListFragment = ProductListFragment(searchList[position])
            productListFragment.arguments = Bundle().apply {
                putBoolean("searchViewOpened",true)
            }
            fragment.parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentMainLayout,productListFragment)
                .addToBackStack("Product List Fragment in Search View")
                .commit()
        }
    }
}