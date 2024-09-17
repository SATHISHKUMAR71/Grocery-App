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
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.user.Address
import com.example.shoppinggroceryapp.viewmodel.accountviewmodel.GetAddressViewModel
import com.example.shoppinggroceryapp.viewmodel.accountviewmodel.GetAddressViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText


class GetAddress : Fragment() {
    private lateinit var fullName: TextInputEditText
    private lateinit var phone: TextInputEditText
    private lateinit var houseNo: TextInputEditText
    private lateinit var street: TextInputEditText
    private lateinit var state: TextInputEditText
    private lateinit var city: TextInputEditText
    private lateinit var postalCode: TextInputEditText
    private lateinit var saveAddress:MaterialButton
    private lateinit var addressTopBar:MaterialToolbar
    private lateinit var getAddressViewModel: com.example.shoppinggroceryapp.viewmodel.accountviewmodel.GetAddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_get_address, container, false)
        val handler = Handler(Looper.getMainLooper())
        getAddressViewModel = ViewModelProvider(this,
            com.example.shoppinggroceryapp.viewmodel.accountviewmodel.GetAddressViewModelFactory(
                AppDatabase.getAppDatabase(requireContext()).getUserDao()
            )
        )[com.example.shoppinggroceryapp.viewmodel.accountviewmodel.GetAddressViewModel::class.java]
        fullName = view.findViewById(R.id.fullName)
        phone = view.findViewById(R.id.addPhoneNumber)
        houseNo = view.findViewById(R.id.addAddressHouse)
        street = view.findViewById(R.id.addAddressStreetName)
        state = view.findViewById(R.id.addAddressState)
        city = view.findViewById(R.id.addAddressCity)
        postalCode = view.findViewById(R.id.addAddressPostalCode)
        saveAddress = view.findViewById(R.id.addNewAddress)
        addressTopBar = view.findViewById(R.id.getAddressToolbar)
        if(SavedAddress.editAddress!=null){
            SavedAddress.editAddress?.let {
                fullName.setText(it.addressContactName)
                phone.setText(it.addressContactNumber)
                houseNo.setText(it.buildingName)
                street.setText(it.streetName)
                state.setText(it.state)
                city.setText(it.city)
                postalCode.setText(it.postalCode)
            }
        }
        addressTopBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        saveAddress.setOnClickListener {
            if(fullName.text.toString().isNotEmpty() && phone.text.toString().isNotEmpty()
                && houseNo.text.toString().isNotEmpty() && street.text.toString().isNotEmpty()
                && state.text.toString().isNotEmpty() && city.text.toString().isNotEmpty()
                && postalCode.text.toString().isNotEmpty()){

                if(SavedAddress.editAddress!=null){
                    getAddressViewModel.updateAddress(
                    Address(
                        addressId = SavedAddress.editAddress!!.addressId,
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

                    Toast.makeText(context,"Address Edited Successfully",Toast.LENGTH_SHORT).show()
                }
                else {
                    getAddressViewModel.addAddress(
                        Address(
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
                        )
                    )
                    Toast.makeText(context,"Address Added Successfully",Toast.LENGTH_SHORT).show()
                }

                parentFragmentManager.popBackStack()
            }
            else{
                Toast.makeText(context,"Add all the Required Fields",Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
    override fun onResume() {
        super.onResume()
        InitialFragment.hideSearchBar.value = true
        InitialFragment.hideBottomNav.value = true
    }
    override fun onStop() {
        super.onStop()
        InitialFragment.hideSearchBar.value = false
        InitialFragment.hideBottomNav.value = false
        SavedAddress.editAddress = null
    }

}