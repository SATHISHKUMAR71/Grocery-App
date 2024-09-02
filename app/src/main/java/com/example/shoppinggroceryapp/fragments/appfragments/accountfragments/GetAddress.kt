package com.example.shoppinggroceryapp.fragments.appfragments.accountfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.user.Address
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText


class GetAddress(var searchBar:LinearLayout) : Fragment() {
    private lateinit var fullName: TextInputEditText
    private lateinit var phone: TextInputEditText
    private lateinit var houseNo: TextInputEditText
    private lateinit var street: TextInputEditText
    private lateinit var state: TextInputEditText
    private lateinit var city: TextInputEditText
    private lateinit var postalCode: TextInputEditText
    private lateinit var saveAddress:MaterialButton
    private lateinit var addressTopBar:MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_get_address, container, false)
        val handler = Handler(Looper.getMainLooper())
        fullName = view.findViewById(R.id.fullName)
        phone = view.findViewById(R.id.addPhoneNumber)
        houseNo = view.findViewById(R.id.addAddressHouse)
        street = view.findViewById(R.id.addAddressStreetName)
        state = view.findViewById(R.id.addAddressState)
        city = view.findViewById(R.id.addAddressCity)
        postalCode = view.findViewById(R.id.addAddressPostalCode)
        saveAddress = view.findViewById(R.id.addNewAddress)
        addressTopBar = view.findViewById(R.id.getAddressToolbar)
        addressTopBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        val db = AppDatabase.getAppDatabase(requireContext())

        saveAddress.setOnClickListener {
            if(fullName.text.toString().isNotEmpty() && phone.text.toString().isNotEmpty()
                && houseNo.text.toString().isNotEmpty() && street.text.toString().isNotEmpty()
                && state.text.toString().isNotEmpty() && city.text.toString().isNotEmpty()
                && postalCode.text.toString().isNotEmpty()){
                Thread{
                    db.getUserDao().addAddress(Address(
                        addressId = 0,
                        userId = MainActivity.userId.toInt(),
                        addressContactName = fullName.text.toString(),
                        addressContactNumber = phone.text.toString(),
                        buildingName = houseNo.text.toString(),
                        streetName = street.text.toString(),
                        city = city.text.toString(),
                        state = state.text.toString(),
                        country = "India",
                        postalCode = postalCode.text.toString()
                    ))
                    handler.post {
                        parentFragmentManager.popBackStack()
                        Toast.makeText(context,"Address Added Successfully",Toast.LENGTH_SHORT).show()
                    }
                }.start()

            }

            else{
                Toast.makeText(context,"Add all the Required Fields",Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
    override fun onResume() {
        super.onResume()
        searchBar.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        searchBar.visibility = View.VISIBLE
    }

}