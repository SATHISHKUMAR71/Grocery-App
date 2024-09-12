package com.example.shoppinggroceryapp.fragments.authentication

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.ImageHandler
import com.example.shoppinggroceryapp.fragments.ImageLoaderAndGetter
import com.example.shoppinggroceryapp.fragments.InputValidator
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.topbar.TopBarFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.user.User
import com.example.shoppinggroceryapp.viewmodel.authenticationviewmodel.LoginViewModel
import com.example.shoppinggroceryapp.viewmodel.authenticationviewmodel.LoginViewModelFactory
import com.example.shoppinggroceryapp.viewmodel.authenticationviewmodel.SignUpViewModel
import com.example.shoppinggroceryapp.viewmodel.authenticationviewmodel.SignUpViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class SignUpFragment : Fragment() {

    private lateinit var firstName:TextInputEditText
    private lateinit var lastName:TextInputEditText
    private lateinit var email:TextInputEditText
    private lateinit var phone:TextInputEditText
    private lateinit var password:TextInputEditText
    private lateinit var confirmedPassword:TextInputEditText
    private lateinit var signUp:MaterialButton
    private lateinit var addProfileImage:ImageView
    private lateinit var addProfileBtn:MaterialButton
    private lateinit var signUpTopbar:MaterialToolbar
    private lateinit var imageHandler:ImageHandler
    private lateinit var imageLoader:ImageLoaderAndGetter
    private var profileUri:String =""
    private lateinit var signUpViewModel:SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageHandler = ImageHandler(this)
        imageLoader = ImageLoaderAndGetter()
        imageHandler.initActivityResults()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        signUpViewModel = ViewModelProvider(this,
            SignUpViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao())
        )[SignUpViewModel::class.java]
        val handler = Handler(Looper.getMainLooper())
        firstName = view.findViewById(R.id.firstName)
        lastName = view.findViewById(R.id.signUpLastName)
        email = view.findViewById(R.id.signUpEmail)
        phone = view.findViewById(R.id.signUpPhoneNumber)
        password =view.findViewById(R.id.signUpPassword)
        confirmedPassword = view.findViewById(R.id.signUpConfirmPassword)
        addProfileBtn = view.findViewById(R.id.addPictureBtn)
        addProfileImage = view.findViewById(R.id.addPictureImage)
        signUpTopbar = view.findViewById(R.id.topbar)
        val db = AppDatabase.getAppDatabase(requireContext()).getUserDao()
        var isRetailer = false
        signUp = view.findViewById(R.id.signUpNewUser)
        if(MainActivity.isRetailer){
            signUpTopbar.title = "Add New Admin"
            isRetailer = true
        }

        signUpTopbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        addProfileBtn.setOnClickListener{
            imageHandler.showAlertDialog()
        }
        addProfileImage.setOnClickListener{
            imageHandler.showAlertDialog()
        }
        imageHandler.gotImage.observe(viewLifecycleOwner){
            var image = it
            var imageName = System.currentTimeMillis().toString()
            addProfileImage.setImageBitmap(image)
            addProfileImage.setPadding(0)
            profileUri = imageName
            imageLoader.storeImageInApp(requireContext(),image,imageName)
        }

        email.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                view.findViewById<TextInputLayout>(R.id.signUpEmailLayout).error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.findViewById<TextInputLayout>(R.id.signUpEmailLayout).error = null
            }

            override fun afterTextChanged(s: Editable?) {
                view.findViewById<TextInputLayout>(R.id.signUpEmailLayout).error = null
            }
        })

        password.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                view.findViewById<TextInputLayout>(R.id.signUpPasswordLayout).error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.findViewById<TextInputLayout>(R.id.signUpPasswordLayout).error = null
            }

            override fun afterTextChanged(s: Editable?) {
                view.findViewById<TextInputLayout>(R.id.signUpPasswordLayout).error = null
            }
        })
        phone.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                view.findViewById<TextInputLayout>(R.id.signUpPhoneNumberLayout).error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.findViewById<TextInputLayout>(R.id.signUpPhoneNumberLayout).error = null
            }

            override fun afterTextChanged(s: Editable?) {
                view.findViewById<TextInputLayout>(R.id.signUpPhoneNumberLayout).error = null
            }
        })
        signUpViewModel.registrationStatus.observe(viewLifecycleOwner){
            if(it){
                var text =""
                if(isRetailer) {
                    text = "Admin Added Successfully"
                }
                else{
                    text ="User Added Successfully"
                }
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            else{
                Toast.makeText(
                    context,
                    "Phone Number or Email is Already Registered",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        signUp.setOnClickListener {
            if(phone.text.toString().length<10){
                view.findViewById<TextInputLayout>(R.id.signUpPhoneNumberLayout).error = "Phone Number atLeast Contains 10 Characters"
                view.findViewById<TextInputLayout>(R.id.signUpPhoneNumberLayout).error
            }
            if(!InputValidator.checkEmail(email.text.toString())){
                view.findViewById<TextInputLayout>(R.id.signUpEmailLayout).error = "Please Enter the Valid Email"
            }
            if(!((password.text.toString().isNotEmpty()) && (confirmedPassword.text.toString() == password.text.toString()))){
                view.findViewById<TextInputLayout>(R.id.signUpPasswordLayout).error ="Check Both the Passwords Are Same"
            }
            else if ((firstName.text.toString().isNotEmpty()) && (email.text.toString()
                    .isNotEmpty()) && (phone.text.toString()
                    .isNotEmpty()) && (password.text?.isNotEmpty() == true) && (confirmedPassword.text.toString() == password.text.toString())
            ) {
                if(!InputValidator.checkMobile(phone.text.toString())){
                    Snackbar.make(view,"Give Valid Mobile Number",Snackbar.LENGTH_SHORT).show()
                }
                else if(!InputValidator.checkEmail(email.text.toString())){
                    Snackbar.make(view,"Give Valid Email",Snackbar.LENGTH_SHORT).show()
                }
                else {
                    signUpViewModel.registerNewUser(

                        User(
                            0,
                            profileUri,
                            firstName.text.toString(),
                            lastName.text.toString(),
                            email.text.toString(),
                            phone.text.toString(),
                            password.text.toString(),
                            "",
                            isRetailer
                        )
                    )
                }
            }
            else{
                Toast.makeText(requireContext(), "Give inputs for Required Field", Toast.LENGTH_SHORT)
                .show()
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
    }
}