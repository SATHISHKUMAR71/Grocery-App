package com.example.shoppinggroceryapp.fragments.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.OfferFragment
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.viewmodel.filterviewmodel.FilterViewModel
import com.example.shoppinggroceryapp.viewmodel.filterviewmodel.FilterViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton


class FilterFragment(var products:MutableList<Product>) : Fragment() {

    var category:String?= null
    var type:String? = null
    private lateinit var filterViewModel:FilterViewModel
    private lateinit var dis50:CheckBox
    private lateinit var dis40:CheckBox
    private lateinit var dis30:CheckBox
    private lateinit var dis20:CheckBox
    private lateinit var dis10:CheckBox
    private lateinit var availableProducts:TextView

    companion object{
        var list:MutableList<Product>? = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("### LIST Datas")
        category = arguments?.getString("category",null)
        type = arguments?.getString("type")
        val view =  inflater.inflate(R.layout.fragment_filter, container, false)
        dis50 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount50)
        var clearAll:MutableLiveData<Boolean> = MutableLiveData()
        filterViewModel = ViewModelProvider(this,FilterViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[FilterViewModel::class.java]
        dis40 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount40)
        dis30 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount30)
        dis20 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount20)
        dis10 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount10)
        val applyButton = view.findViewById<MaterialButton>(R.id.applyFilterButton)
        val clearAllButton = view.findViewById<MaterialButton>(R.id.clearAllFilterButton)
        availableProducts = view.findViewById<TextView>(R.id.availableProducts)
        availableProducts.text =products.size.toString()
        view.findViewById<MaterialToolbar>(R.id.materialToolbarFilter).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }


        dis50.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                availableProducts.text = filterViewModel.filterList(products,50f).size.toString()
            }
            else{
                availableProducts.text = filterViewModel.filterListBelow(products,50f).size.toString()
            }
            println("$$$$ Discount 50 isChecked $isChecked")
            assignList()
        }
        dis40.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                availableProducts.text = filterViewModel.filterList(products,40f).size.toString()
            }
            else{
                availableProducts.text = filterViewModel.filterListBelow(products,40f).size.toString()
            }
            println("$$$$ Discount 40 isChecked $isChecked")
            assignList()
        }
        dis30.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                availableProducts.text = filterViewModel.filterList(products,30f).size.toString()
            }
            else{
                availableProducts.text = filterViewModel.filterListBelow(products,30f).size.toString()
            }
            println("$$$$ Discount 30 isChecked $isChecked")
            assignList()
        }
        dis20.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                availableProducts.text = filterViewModel.filterList(products,20f).size.toString()
            }
            else{
                availableProducts.text = filterViewModel.filterListBelow(products,20f).size.toString()
            }
            println("$$$$ Discount 20 isChecked $isChecked")
            assignList()
        }
        dis10.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                availableProducts.text = filterViewModel.filterList(products,10f).size.toString()
            }
            else{
                availableProducts.text = filterViewModel.filterListBelow(products,10f).size.toString()
            }
            println("$$$$ Discount 10 isChecked $isChecked")
            assignList()
        }

        clearAllButton.setOnClickListener {
            clearAll.value = true
            availableProducts.text =products.size.toString()
        }



        applyButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
    }



    override fun onResume() {
        super.onResume()
        InitialFragment.hideBottomNav.value = true
        InitialFragment.hideSearchBar.value = true
    }

    override fun onPause() {
        super.onPause()
        InitialFragment.hideBottomNav.value = false
        InitialFragment.hideSearchBar.value = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun assignList(){
        var tmpList:List<Product>
        if(dis50.isChecked){
            tmpList = filterViewModel.filterList(products,50f)
            availableProducts.text = tmpList.size.toString()
        }
        else if(dis40.isChecked){
            tmpList = filterViewModel.filterList(products,40f)
            availableProducts.text = tmpList.size.toString()
        }
        else if(dis30.isChecked){
            tmpList = filterViewModel.filterList(products,30f)
            availableProducts.text = tmpList.size.toString()
        }
        else if(dis20.isChecked){
            tmpList = filterViewModel.filterList(products,20f)
            availableProducts.text = tmpList.size.toString()
        }
        else if(dis10.isChecked){
            tmpList = filterViewModel.filterList(products,10f)
            availableProducts.text = tmpList.size.toString()
        }
        else{
            tmpList = products
            availableProducts.text = tmpList.size.toString()
        }
        list = tmpList.toMutableList()
    }
}