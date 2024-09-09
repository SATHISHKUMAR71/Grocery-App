package com.example.shoppinggroceryapp.fragments.authentication

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.MainActivity.Companion.cartId
import com.example.shoppinggroceryapp.MainActivity.Companion.userId
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.CartMapping
import com.example.shoppinggroceryapp.viewmodel.authenticationviewmodel.LoginViewModel
import com.example.shoppinggroceryapp.viewmodel.authenticationviewmodel.LoginViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class LoginFragment : Fragment() {

    private lateinit var emailPhoneTextLayout:TextInputLayout
    private lateinit var loginButton:MaterialButton
    private lateinit var passwordLayout:TextInputLayout
    private lateinit var emailPhoneText:TextInputEditText
    private lateinit var password:TextInputEditText
    private lateinit var forgotPassword:MaterialButton
    private lateinit var signUp:MaterialButton
    private var login = false
    private lateinit var handler: Handler
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loginViewModel = ViewModelProvider(this,LoginViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[LoginViewModel::class.java]

        val view =  inflater.inflate(R.layout.fragment_login, container, false)
        emailPhoneText = view.findViewById(R.id.emailOrMobile)
        handler = Handler(Looper.getMainLooper())
        password = view.findViewById(R.id.password)
        emailPhoneTextLayout = view.findViewById(R.id.emailLayout)
        passwordLayout = view.findViewById(R.id.passwordLayout)
        signUp = view.findViewById(R.id.signUpBtn)
        forgotPassword = view.findViewById(R.id.forgotPassword)

        loginButton = view.findViewById(R.id.loginButton)
        loginViewModel.user.observe(viewLifecycleOwner){
            if(it==null){
                handler.post {
                    Snackbar.make(view,"Login Failed",Snackbar.LENGTH_SHORT).apply {
                        setBackgroundTint(Color.argb(255,200,20,20))
                        show()
                    }
                }
            }
            else{
                val sharedPreferences = requireActivity().getSharedPreferences("freshCart",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isSigned",true)
                editor.putBoolean("isRetailer",it.isRetailer)
                editor.putString("userFirstName",it.userFirstName)
                editor.putString("userLastName",it.userLastName)
                editor.putString("userEmail",it.userEmail)
                editor.putString("userPhone",it.userPhone)
                editor.putString("userId",it.userId.toString())
                editor.putString("userProfile",it.userImage)
                editor.apply()
                loginViewModel.assignCartForUser()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentBody,InitialFragment())
                    .commit()
            }
        }

        loginButton.setOnClickListener {
            loginViewModel.validateUser(emailPhoneText.text.toString(),password.text.toString())
        }

        signUp.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,SignUpFragment(),"Sign Up Fragment")
        }
        return view
    }


}