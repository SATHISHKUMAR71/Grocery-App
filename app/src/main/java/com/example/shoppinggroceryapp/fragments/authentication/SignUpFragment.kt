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
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.ImageHandler
import com.example.shoppinggroceryapp.fragments.ImageLoaderAndGetter
import com.example.shoppinggroceryapp.fragments.InputValidator
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
        signUp = view.findViewById(R.id.signUpNewUser)

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

//        phone.addTextChangedListener(object :TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                TODO("Not yet implemented")
//            }
//
//        })
        signUpViewModel.registrationStatus.observe(viewLifecycleOwner){
            if(it){
                Toast.makeText(context, "User Added Successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            else{
                Toast.makeText(
                    context,
                    "PhoNe Number or Email is Already Registered",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        signUp.setOnClickListener {
            if ((firstName.text.toString().isNotEmpty()) && (email.text.toString()
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
                            false
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




    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
    override fun onDestroy() {
        super.onDestroy()
        parentFragmentManager.popBackStack()
    }
}