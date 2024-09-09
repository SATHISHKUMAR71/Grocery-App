package com.example.shoppinggroceryapp.fragments.appfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.ImageHandler
import com.example.shoppinggroceryapp.fragments.ImageLoaderAndGetter
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.MainCategoryAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.dataclass.ChildCategoryName
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.example.shoppinggroceryapp.viewmodel.categoryviewmodel.CategoryViewModelFactory
import com.example.shoppinggroceryapp.viewmodel.categoryviewmodel.CategoryViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class CategoryFragment: Fragment() {

    private lateinit var mainCategoryRV:RecyclerView
    private lateinit var imageHandler: ImageHandler
    private lateinit var imageLoader:ImageLoaderAndGetter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageHandler = ImageHandler(this)
        imageHandler.initActivityResults()
        imageLoader = ImageLoaderAndGetter()
    }
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
        imageHandler.gotImage.observe(viewLifecycleOwner){
            var imageString = System.currentTimeMillis().toString()
            imageLoader.storeImageInApp(requireContext(),it,imageString)
            categoryViewModel.parentCategory?.copy(parentCategoryImage = imageString)
                ?.let { it1 -> categoryViewModel.updateParentCategory(it1) }
        }
        categoryViewModel.childList.observe(viewLifecycleOwner){
            childList = it
            if(parentList!=null){
                mainCategoryRV.adapter = MainCategoryAdapter(this,parentList!!,childList!!,imageLoader)
                mainCategoryRV.layoutManager = LinearLayoutManager(requireContext())
            }
        }
        categoryViewModel.getParentCategory()
        categoryViewModel.parentList.observe(viewLifecycleOwner){
            parentList = it
            if(childList!=null){
                mainCategoryRV.adapter = MainCategoryAdapter(this,parentList!!,childList!!,imageLoader)
                mainCategoryRV.layoutManager = LinearLayoutManager(requireContext())
            }
        }
        return view
    }

}