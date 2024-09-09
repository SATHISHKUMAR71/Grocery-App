package com.example.shoppinggroceryapp.fragments.authentication

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.viewmodel.accountviewmodel.EditProfileViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


class ForgotPasswordFrag : Fragment() {


    private lateinit var editProfileViewModel: EditProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        view.findViewById<MaterialToolbar>(R.id.appbarLayoutForgotPass).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        editProfileViewModel = EditProfileViewModel(AppDatabase.getAppDatabase(requireContext()).getUserDao())
        val userData  = view.findViewById<TextInputEditText>(R.id.forgotEmailInput).text
        view.findViewById<MaterialButton>(R.id.materialButtonUpdatePassword).setOnClickListener {
            editProfileViewModel.getUser(userData.toString())
        }
        val newPassword = view.findViewById<TextInputEditText>(R.id.inputNewPassword)
        editProfileViewModel.user.observe(viewLifecycleOwner){
            if(it != null){
                if(newPassword.text.toString().isNotEmpty()){
                    editProfileViewModel.savePassword(it.copy(userPassword = newPassword.text.toString()))
                    Snackbar.make(view,"Password Updated Successfully",Snackbar.LENGTH_SHORT).apply {
                        setBackgroundTint(Color.argb(255,20,230,20))
                            .show()
                    }
                    parentFragmentManager.popBackStack()
                }
                else{
                    Snackbar.make(view,"Password should not be empty",Snackbar.LENGTH_SHORT).apply {
                        setBackgroundTint(Color.argb(255,230,20,20))
                            .show()
                    }
                }
            }
            else{
                Snackbar.make(view,"Email or Mobile Didn't Exist",Snackbar.LENGTH_SHORT).apply {
                    setBackgroundTint(Color.argb(255,230,20,20))
                        .show()
                }
            }
        }
        return view

    }
}