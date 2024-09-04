package com.example.shoppinggroceryapp.fragments.appfragments.accountfragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.BrandData
import com.example.shoppinggroceryapp.model.entities.products.Category
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.model.entities.user.User
import com.example.shoppinggroceryapp.model.viewmodel.accountviewmodel.EditProfileViewModel
import com.example.shoppinggroceryapp.model.viewmodel.accountviewmodel.EditProfileViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class EditProfile : Fragment() {

    private lateinit var editProfileTopbar:MaterialToolbar
    private lateinit var firstName:TextInputEditText
    private lateinit var lastName:TextInputEditText
    private lateinit var email:TextInputEditText
    private lateinit var phone:TextInputEditText
    private lateinit var saveDetails:MaterialButton
    private lateinit var db:AppDatabase
    private lateinit var editProfileViewModel: EditProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getAppDatabase(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_edit_profile, container, false)
        val handler = Handler(Looper.getMainLooper())
        editProfileViewModel = ViewModelProvider(this,EditProfileViewModelFactory(db.getUserDao()))[EditProfileViewModel::class.java]
        editProfileTopbar = view.findViewById(R.id.editProfileAppBar)
        firstName = view.findViewById(R.id.editFirstName)
        lastName = view.findViewById(R.id.editLastName)
        email = view.findViewById(R.id.editEmail)
        phone = view.findViewById(R.id.editPhoneNumber)
        saveDetails = view.findViewById(R.id.saveDetailsButton)
        firstName.setText(MainActivity.userFirstName)
        lastName.setText(MainActivity.userLastName)
        email.setText(MainActivity.userEmail)
        phone.setText(MainActivity.userPhone)
        val pref = requireActivity().getSharedPreferences("freshCart",Context.MODE_PRIVATE)
        val editor =pref.edit()
        editProfileTopbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        saveDetails.setOnClickListener {
            val oldEmail = MainActivity.userEmail
            MainActivity.userEmail = email.text.toString()
            MainActivity.userPhone = phone.text.toString()
            MainActivity.userFirstName = firstName.text.toString()
            MainActivity.userLastName = lastName.text.toString()
            editor.putString("userFirstName",MainActivity.userFirstName)
            editor.putString("userLastName",MainActivity.userLastName)
            editor.putString("userEmail",MainActivity.userEmail)
            editor.putString("userPhone",MainActivity.userPhone)
            editor.apply()
            editProfileViewModel.saveDetails(oldEmail = oldEmail,
                firstName = firstName.text.toString(),
                lastName = lastName.text.toString(),
                email = email.text.toString(),
                phone = phone.text.toString())
            Toast.makeText(context,"Data Updated Successfully",Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()

        }
        return view
    }


    override fun onResume() {
        super.onResume()
        InitialFragment.hideSearchBar.value = true
    }
    override fun onStop() {
        super.onStop()
        InitialFragment.hideSearchBar.value = false
    }
}