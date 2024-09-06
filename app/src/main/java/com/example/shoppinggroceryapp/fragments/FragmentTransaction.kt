package com.example.shoppinggroceryapp.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.shoppinggroceryapp.R

class FragmentTransaction {
    companion object{

        fun navigateWithBackstack(fragmentManager:FragmentManager,fragment:Fragment,backstack:String){
            fragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,fragment)
                .addToBackStack(backstack)
                .commit()
        }
    }
}