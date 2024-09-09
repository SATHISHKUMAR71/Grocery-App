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
import com.example.shoppinggroceryapp.model.dataclass.ChildCategoryName
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.example.shoppinggroceryapp.viewmodel.categoryviewmodel.CategoryViewModelFactory
import com.example.shoppinggroceryapp.viewmodel.categoryviewmodel.CategoryViewModel
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
        var childList:List<List<ChildCategoryName>>? = null
        var parentList:List<ParentCategory>? = null
        val categoryViewModel = ViewModelProvider(this,CategoryViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getProductDao()))[CategoryViewModel::class.java]
        mainCategoryRV = view.findViewById(R.id.categoryRecyclerView)

        categoryViewModel.getParentCategory()
        categoryViewModel.parentList.observe(viewLifecycleOwner){
            categoryViewModel.getChildWithParentName()
        }
        categoryViewModel.childList.observe(viewLifecycleOwner){

        }
        categoryViewModel.childList.observe(viewLifecycleOwner){
            childList = it
            if(parentList!=null){
                mainCategoryRV.adapter = MainCategoryAdapter(this,parentList!!,childList!!)
                mainCategoryRV.layoutManager = LinearLayoutManager(requireContext())
            }
        }
        categoryViewModel.getParentCategory()
        categoryViewModel.parentList.observe(viewLifecycleOwner){
            parentList = it
            if(childList!=null){
                mainCategoryRV.adapter = MainCategoryAdapter(this,parentList!!,childList!!)
                mainCategoryRV.layoutManager = LinearLayoutManager(requireContext())
            }
        }

        return view
    }

}