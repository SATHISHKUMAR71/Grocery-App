package com.example.shoppinggroceryapp.fragments.sort

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.shoppinggroceryapp.R

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class BottomSheetDialog:BottomSheetDialogFragment() {

    companion object {
        var selectedOption: MutableLiveData<Int> = MutableLiveData()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.bottom_sort_layout,container,false)
        view.findViewById<MaterialButton>(R.id.sortByExpiryDate).setOnClickListener {
            selectedOption.value = 1
            Toast.makeText(requireContext(),"Button Expiry Clicked",Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.sortByManufacturedDate).setOnClickListener {
            selectedOption.value = 0
            Toast.makeText(requireContext(),"Button Manufactured Clicked",Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.sortByPriceHightoLow).setOnClickListener {
            selectedOption.value = 3
            Toast.makeText(requireContext(),"Button High To Low Clicked",Toast.LENGTH_SHORT).show()
            dismiss()
        }
        view.findViewById<MaterialButton>(R.id.sortByPriceLowToHigh).setOnClickListener {
            selectedOption.value = 2
            Toast.makeText(requireContext(),"Button Low To High Clicked",Toast.LENGTH_SHORT).show()
            dismiss()
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        println("ON BOTTOM DESTROY")
        selectedOption.value = null
    }
}