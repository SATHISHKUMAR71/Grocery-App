package com.example.shoppinggroceryapp.fragments.appfragments.accountfragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.ImageHandler
import com.example.shoppinggroceryapp.fragments.ImageLoaderAndGetter
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.viewmodel.accountviewmodel.EditProfileViewModel
import com.example.shoppinggroceryapp.viewmodel.accountviewmodel.EditProfileViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.File

class AccountFragment : Fragment() {


    private lateinit var editProfile:MaterialButton
    private lateinit var orderHistory:MaterialButton
    private lateinit var help:MaterialButton
    private lateinit var faq:MaterialButton
    private lateinit var savedAddress:MaterialButton
    private lateinit var logoutUser:MaterialButton
    private lateinit var userName:TextView
    private lateinit var imageHandler:ImageHandler
    private lateinit var imageLoader:ImageLoaderAndGetter
    private lateinit var recentlyPurchasedItems:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageHandler = ImageHandler(this)
        imageHandler.initActivityResults()
        imageLoader =ImageLoaderAndGetter()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_account, container, false)
        val editUser = ViewModelProvider(this,
            com.example.shoppinggroceryapp.viewmodel.accountviewmodel.EditProfileViewModelFactory(
                AppDatabase.getAppDatabase(requireContext()).getUserDao()
            )
        )[com.example.shoppinggroceryapp.viewmodel.accountviewmodel.EditProfileViewModel::class.java]
//        view.findViewById<ImageView>(R.id.accountImage).setImageBitmap()
        val name = MainActivity.userFirstName + " "+ MainActivity.userLastName
        val profileView = view.findViewById<ImageView>(R.id.accountImage)
        val image = imageLoader.getImageInApp(requireContext(),MainActivity.userImage)

        if(image!=null){
            profileView.setImageBitmap(imageLoader.getImageInApp(requireContext(),MainActivity.userImage))
            profileView.setPadding(0)
        }
        profileView.setOnClickListener {
            imageHandler.showAlertDialog()
        }
        recentlyPurchasedItems = view.findViewById(R.id.recentlyPurchasedItemsList)
        editUser.getPurchasedProducts(MainActivity.userId.toInt())
        val adapter =ProductListAdapter(this,
            File(requireContext().filesDir,"AppImages"),"P",true)
        editUser.recentlyBoughtList.observe(viewLifecycleOwner){
            if(it!=null){
                if(recentlyPurchasedItems.adapter == null) {
                    recentlyPurchasedItems.adapter = adapter
                    recentlyPurchasedItems.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter.setProducts(it)
                }
            }
        }
        imageHandler.gotImage.observe(viewLifecycleOwner){
            if(it!=null){
                val userImageUri = System.currentTimeMillis().toString()
                var imageDate = imageLoader.storeImageInApp(requireContext(),it,userImageUri)
                editUser.saveUserImage(MainActivity.userEmail,userImageUri)
                with(requireActivity().getSharedPreferences("freshCart",Context.MODE_PRIVATE).edit()){
                    putString("userProfile",userImageUri)
                    apply()
                }
                MainActivity.userImage = userImageUri
                profileView.setImageBitmap(imageLoader.getImageInApp(requireContext(),MainActivity.userImage))
                profileView.setPadding(0)
            }
        }

        userName = view.findViewById(R.id.userName)
        userName.text =name
        editProfile = view.findViewById(R.id.editProfile)
        orderHistory = view.findViewById(R.id.orderHistory)
        help = view.findViewById(R.id.help)
        faq = view.findViewById(R.id.faq)
        savedAddress = view.findViewById(R.id.savedAddress)
        logoutUser = view.findViewById(R.id.logout)
        editProfile.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,EditProfile(),"Edit Profile")
        }
        orderHistory.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,OrderListFragment(),"Order List Fragment")
        }
        help.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,Help(),"Help")
        }
        faq.setOnClickListener {
            Toast.makeText(context,"FAQ Clicked",Toast.LENGTH_SHORT).show()
        }
        savedAddress.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,SavedAddress(),"Saved Address")
        }

        logoutUser.setOnClickListener {
            showAlertDialog()
        }

        return view
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout Confirmation")
            .setMessage("Are you sure to Logout?")
            .setPositiveButton("Yes"){_,_ ->
                restartApp()
            }
            .setNegativeButton("No"){dialog,_ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun restartApp() {
        val intent = Intent(context,MainActivity::class.java)
        CartFragment.selectedAddress = null
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val sharedPreferences = requireActivity().getSharedPreferences("freshCart",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isSigned",false)
        editor.putBoolean("isRetailer",false)
        editor.putString("userFirstName",null)
        editor.putString("userLastName",null)
        editor.putString("userEmail",null)
        editor.putString("userPhone",null)
        editor.putString("userId",null)
        editor.putString("userProfile",null)
        editor.apply()
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        InitialFragment.hideSearchBar.value = true
    }
    override fun onStop() {
        super.onStop()
        InitialFragment.hideSearchBar.value = false
    }

    override fun onDestroyView() {
        imageHandler.gotImage.value = null
        super.onDestroyView()
    }
}