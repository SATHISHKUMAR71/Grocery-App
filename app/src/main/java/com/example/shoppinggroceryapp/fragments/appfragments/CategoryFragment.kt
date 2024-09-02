package com.example.shoppinggroceryapp.fragments.appfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.MainCategoryAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView


class CategoryFragment(private var searchbarTop: LinearLayout, private var bottomNav:BottomNavigationView) : Fragment() {

    private lateinit var mainCategoryRV:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_category, container, false)
        val handler = Handler(Looper.getMainLooper())
        mainCategoryRV = view.findViewById(R.id.categoryRecyclerView)

        Thread{
            val parentList = AppDatabase.getAppDatabase(requireContext()).getProductDao().getParentCategoryList()
            handler.post {
                mainCategoryRV.adapter = MainCategoryAdapter(this,parentList,searchbarTop,bottomNav)
                mainCategoryRV.layoutManager = LinearLayoutManager(requireContext())
            }
        }.start()

        return view
    }
    override fun onResume() {
        super.onResume()
        searchbarTop.visibility = View.GONE
    }
    override fun onStop() {
        super.onStop()
        searchbarTop.visibility = View.VISIBLE
    }
}