package com.example.shoppinggroceryapp.fragments.appfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.MainCategoryAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.viewmodel.categoryviewmodel.CategoryViewModel
import com.example.shoppinggroceryapp.model.viewmodel.categoryviewmodel.CategoryViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class CategoryFragment: Fragment() {

    private lateinit var mainCategoryRV:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_category, container, false)
        val handler = Handler(Looper.getMainLooper())
        val categoryViewModel = ViewModelProvider(this,CategoryViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getProductDao()))[CategoryViewModel::class.java]
        mainCategoryRV = view.findViewById(R.id.categoryRecyclerView)

        categoryViewModel.getParentCategory()
        categoryViewModel.parentList.observe(viewLifecycleOwner){
            mainCategoryRV.adapter = MainCategoryAdapter(this,it)
            mainCategoryRV.layoutManager = LinearLayoutManager(requireContext())
        }
//        Thread{
//            val parentList = AppDatabase.getAppDatabase(requireContext()).getProductDao().getParentCategoryList()
//            handler.post {
//                mainCategoryRV.adapter = MainCategoryAdapter(this,parentList)
//                mainCategoryRV.layoutManager = LinearLayoutManager(requireContext())
//            }
//        }.start()

        return view
    }
    override fun onResume() {
        super.onResume()
        InitialFragment.hideSearchBar.value = true
    }

    override fun onPause() {
        super.onPause()
        InitialFragment.hideSearchBar.value = false
    }
}