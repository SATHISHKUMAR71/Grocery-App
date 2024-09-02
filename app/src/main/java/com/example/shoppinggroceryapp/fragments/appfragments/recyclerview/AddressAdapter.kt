package com.example.shoppinggroceryapp.fragments.appfragments.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.model.entities.user.Address

class AddressAdapter(var addressList: List<Address>,var fragment: Fragment):RecyclerView.Adapter<AddressAdapter.AddressHolder>() {

    companion object{
        var clickable = false
    }
    inner class AddressHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val contactName = itemView.findViewById<TextView>(R.id.addressOwnerName)
        val address = itemView.findViewById<TextView>(R.id.address)
        val contactNumber = itemView.findViewById<TextView>(R.id.addressPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        return AddressHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = "${addressList[position].buildingName}, ${addressList[position].streetName}," +
                "${addressList[position].city}, ${addressList[position].state}, ${addressList[position].postalCode}"
            holder.address.text = address
            holder.contactName.text = addressList[position].addressContactName
            holder.contactNumber.text = addressList[position].addressContactNumber

        if(clickable){
            holder.itemView.setOnClickListener {
                CartFragment.selectedAddress = addressList[position]
                clickable=false
                fragment.parentFragmentManager.popBackStack()
            }
        }
    }
}