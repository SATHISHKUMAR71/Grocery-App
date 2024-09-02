package com.example.shoppinggroceryapp.fragments.appfragments.orderfragments.viewpager

import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.model.entities.products.CartWithProductData
import com.example.shoppinggroceryapp.model.entities.products.Product

class ProductViewPager():RecyclerView.Adapter<ProductViewPager.ProductViewPagerHolder>() {

    companion object{
        var productsList = listOf<CartWithProductData>()
    }
    inner class ProductViewPagerHolder(productView:View):RecyclerView.ViewHolder(productView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewPagerHolder {
        return ProductViewPagerHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_view_pager,parent,false))
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ProductViewPagerHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.count).text = productsList[position].totalItems.toString()
    }
}