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
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.BrandData
import com.example.shoppinggroceryapp.model.entities.products.Category
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.model.entities.user.User
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class EditProfile(val searchBarTop:LinearLayout) : Fragment() {

    private lateinit var editProfileTopbar:MaterialToolbar
    private lateinit var firstName:TextInputEditText
    private lateinit var lastName:TextInputEditText
    private lateinit var email:TextInputEditText
    private lateinit var phone:TextInputEditText
    private lateinit var saveDetails:MaterialButton
    private lateinit var db:AppDatabase

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
            Thread{
                val userOld = db.getUserDao().getUserData(oldEmail)
                val user = User(
                    userId = userOld.userId,
                    userImage = userOld.userImage,
                    userFirstName = firstName.text.toString(),
                    userLastName = lastName.text.toString(),
                    userEmail = email.text.toString(),
                    userPhone = phone.text.toString(),
                    userPassword = userOld.userPassword,
                    dateOfBirth = userOld.dateOfBirth,
                    isRetailer = userOld.isRetailer
                )
                db.getUserDao().updateUser(user)
                handler.post {
                    Toast.makeText(context,"Data Updated Successfully",Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }.start()
        }
        return view
    }


    override fun onStop() {
        super.onStop()
        searchBarTop.visibility = View.VISIBLE
    }
    override fun onResume() {
        super.onResume()
        searchBarTop.visibility = View.GONE
    }
}