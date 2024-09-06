package com.example.shoppinggroceryapp.fragments.appfragments

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.model.viewmodel.homeviewmodel.HomeViewModel
import com.example.shoppinggroceryapp.model.viewmodel.homeviewmodel.HomeViewModelFactory
import com.google.android.material.button.MaterialButton
import java.io.File


class HomeFragment() : Fragment() {
    var essentialItems = listOf("Grains & Pulses","Fresh Fruits","Fresh Vegetables","Milk & Cream","Mixed Nuts","Rice","Spices","Soft Drinks"
        ,"Energy Drinks","Tea & Coffee","Wheat & Flour")
    var essentialSize = essentialItems.size -1
    private lateinit var homeFragNestedScroll:NestedScrollView
    private lateinit var recentItems:RecyclerView
    var recentlyViewedList = mutableListOf<Product>()
    companion object{
        var position = 0
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view =inflater.inflate(R.layout.fragment_home, container, false)
        val homeViewModel = ViewModelProvider(this,HomeViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getProductDao()))[HomeViewModel::class.java]

        recentItems = view.findViewById(R.id.recentlyViewedItemsHomeFrag)
        homeFragNestedScroll =  view.findViewById(R.id.nestedScrollViewHomeFrag)

        homeViewModel.getRecentlyViewedItems(requireContext().getSharedPreferences("recentlyViewedItems", Context.MODE_PRIVATE))
        homeViewModel.recentlyViewedList.observe(viewLifecycleOwner){
            ProductListAdapter.productList = it
            recentItems.adapter = ProductListAdapter(this,
                File(requireContext().filesDir,"AppImages"),"P"
            )
//                recentItems.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            recentItems.layoutManager = LinearLayoutManager(context)
        }

        view.findViewById<MaterialButton>(R.id.viewAllCategoriesBtn).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentMainLayout,CategoryFragment())
                .addToBackStack("Opened Category Fragment")
                .commit()
        }
        val categoryContainer = view.findViewById<LinearLayout>(R.id.categoryLayoutRow)

        var i=0
        while (i<essentialSize){
            addViewToLayout(categoryContainer,i)
            i+=3
        }
        return view
    }

    fun addViewToLayout(container: ViewGroup,index:Int){
        val newView = LayoutInflater.from(requireContext()).inflate(R.layout.category_layout,container,false)
        println(essentialSize)
        val imageView0 = newView.findViewById<ImageView>(R.id.categoryImage0)
        val imageView1 = newView.findViewById<ImageView>(R.id.categoryImage1)
        val imageView2 = newView.findViewById<ImageView>(R.id.categoryImage2)
        val categoryType0 = newView.findViewById<TextView>(R.id.categoryType0)
        val categoryType1 = newView.findViewById<TextView>(R.id.categoryType1)
        val categoryType2 = newView.findViewById<TextView>(R.id.categoryType2)
        println(index)
        if ((index+2) <= essentialSize) {
            categoryType0.text = essentialItems[index]
            categoryType1.text = essentialItems[index+1]
            categoryType2.text = essentialItems[index+2]
            setImageAndTextListener(imageView0,categoryType0)
            setImageAndTextListener(imageView1,categoryType1)
            setImageAndTextListener(imageView2,categoryType2)
        }
        else if ((index+1) <= essentialSize) {
            categoryType0.text = essentialItems[index]
            categoryType1.text = essentialItems[index+1]
            setImageAndTextListener(imageView0,categoryType0)
            setImageAndTextListener(imageView1,categoryType1)
            imageView2.visibility = View.INVISIBLE
            categoryType2.visibility = View.INVISIBLE
        }
        else{
            categoryType0.text = essentialItems[index]
            imageView2.visibility = View.INVISIBLE
            imageView1.visibility = View.INVISIBLE
            setImageAndTextListener(imageView0,categoryType0)
            categoryType1.visibility = View.INVISIBLE
            categoryType2.visibility = View.INVISIBLE
        }
        container.addView(newView)
    }

    private fun setImageAndTextListener(image:ImageView, text:TextView){
        image.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentMainLayout,ProductListFragment(text.text.toString()))
                .addToBackStack("Product List Opened")
                .commit()
        }
        text.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentMainLayout,ProductListFragment(text.text.toString()))
                .addToBackStack("Product List Opened")
                .commit()
        }
    }

    override fun onPause() {
        super.onPause()
        position = homeFragNestedScroll.verticalScrollbarPosition
        println("RECYCLER VIEW POSITION ON PAUSE: ${recentItems.verticalScrollbarPosition}")
    }

    override fun onResume() {
        super.onResume()
        homeFragNestedScroll.verticalScrollbarPosition = position
        println("RECYCLER VIEW POSITION ON RESUME: ${recentItems.verticalScrollbarPosition}")
    }
}