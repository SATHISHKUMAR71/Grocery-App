package com.example.shoppinggroceryapp.fragments.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.OfferFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.model.viewmodel.filterviewmodel.FilterViewModel
import com.example.shoppinggroceryapp.model.viewmodel.filterviewmodel.FilterViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton


class FilterFragment(var category: String?) : Fragment() {


    companion object{
        var totalProducts:MutableLiveData<Int> = MutableLiveData()
        var clearAll:MutableLiveData<Boolean> = MutableLiveData()
        var list:MutableList<Product>? = null
    }
    private lateinit var filterViewModel:FilterViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_filter, container, false)
        val dis50 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount50)
        filterViewModel = ViewModelProvider(this,FilterViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[FilterViewModel::class.java]
        val dis40 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount40)
        val dis30 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount30)
        val dis20 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount20)
        val dis10 = view.findViewById<CheckBox>(R.id.fragmentOptionDiscount10)
        val applyButton = view.findViewById<MaterialButton>(R.id.applyFilterButton)
        val clearAllButton = view.findViewById<MaterialButton>(R.id.clearAllFilterButton)
        val availableProducts = view.findViewById<TextView>(R.id.availableProducts)
        dis50.isChecked = (OfferFragment.dis50Val==true)
        dis40.isChecked = (OfferFragment.dis40Val==true)
        dis30.isChecked = (OfferFragment.dis30Val==true)
        dis20.isChecked = (OfferFragment.dis20Val==true)
        dis10.isChecked = (OfferFragment.dis10Val==true)
        view.findViewById<MaterialToolbar>(R.id.materialToolbarFilter).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        if(category!=null){
            filterViewModel.calculateTotalProducts(category!!)
        }
        else{
            filterViewModel.totalProducts.value = totalProducts.value
        }
        filterViewModel.totalProducts.observe(viewLifecycleOwner){
            println("Observer Called $it")
            var value = it?:0
            availableProducts.text =value.toString()
        }

        dis50.setOnCheckedChangeListener { buttonView, isChecked ->
            OfferFragment.dis50Val = isChecked
            if(isChecked){
                if(category!=null){
                    filterViewModel.getProducts50WithCat(category!!)
                }
                else{
                    filterViewModel.getProducts50NoCat()
                }
            }
            else{
                if(list!=null) {
                    filterViewModel.totalProducts.value = availableProducts.text.toString().toInt() - list!!.size
                }
                assignList(dis10,dis20,dis30,dis40,dis50)
            }
        }
        filterViewModel.list.observe(viewLifecycleOwner){
            list = it
        }
        dis40.setOnCheckedChangeListener { buttonView, isChecked ->
            OfferFragment.dis40Val = isChecked
            if(isChecked){
                if(category!=null){
                    filterViewModel.getProducts40WithCat(category!!)
                }
                else{
                    filterViewModel.getProducts40NoCat()
                }
            }
            else{
                if(list!=null) {
                    filterViewModel.totalProducts.value = availableProducts.text.toString().toInt() - list!!.size
                }
                assignList(dis10,dis20,dis30,dis40,dis50)
            }
        }
        dis30.setOnCheckedChangeListener { buttonView, isChecked ->
            OfferFragment.dis30Val = isChecked
            if(isChecked){
                if(category!=null){
                    filterViewModel.getProducts30WithCat(category!!)
                }
                else{
                    filterViewModel.getProducts30NoCat()
                }
            }
            else{
                if(list!=null) {
                    filterViewModel.totalProducts.value = availableProducts.text.toString().toInt() - list!!.size
                }
                assignList(dis10,dis20,dis30,dis40,dis50)
            }
        }
        dis20.setOnCheckedChangeListener { buttonView, isChecked ->
            OfferFragment.dis20Val = isChecked
            if(isChecked){
                if(category!=null){
                    filterViewModel.getProducts20WithCat(category!!)
                }
                else{
                    filterViewModel.getProducts20NoCat()
                }
            }
            else{
                if(list!=null) {
                    filterViewModel.totalProducts.value = availableProducts.text.toString().toInt() - list!!.size
                }
                assignList(dis10,dis20,dis30,dis40,dis50)
            }
        }
        dis10.setOnCheckedChangeListener { buttonView, isChecked ->
            OfferFragment.dis10Val = isChecked
            if(isChecked){
                if(category!=null){
                    filterViewModel.getProducts10WithCat(category!!)
                }
                else{
                    filterViewModel.getProducts10NoCat()
                }
            }
            else{
                if(list!=null) {
                    filterViewModel.totalProducts.value = availableProducts.text.toString().toInt() - list!!.size
                }
                assignList(dis10,dis20,dis30,dis40,dis50)
            }
        }
        clearAllButton.setOnClickListener {
            clearAll.value = true
        }
        applyButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
    }

    private fun assignList(dis10: CheckBox, dis20: CheckBox, dis30: CheckBox, dis40: CheckBox, dis50: CheckBox) {
        if(dis50.isChecked){
            if(category!=null){
                filterViewModel.getProducts50WithCat(category!!)
            }
            else{
                filterViewModel.getProducts50NoCat()
            }
        }
        else if(dis40.isChecked){
            if(category!=null){
                filterViewModel.getProducts40WithCat(category!!)
            }
            else{
                filterViewModel.getProducts40NoCat()
            }
        }
        else if(dis30.isChecked){
            if(category!=null){
                filterViewModel.getProducts30WithCat(category!!)
            }
            else{
                filterViewModel.getProducts30NoCat()
            }
        }

        else if(dis20.isChecked){
            if(category!=null){
                filterViewModel.getProducts20WithCat(category!!)
            }
            else{
                filterViewModel.getProducts20NoCat()
            }
        }
        else if(dis10.isChecked){
            if(category!=null){
                filterViewModel.getProducts10WithCat(category!!)
            }
            else{
                filterViewModel.getProducts10NoCat()
            }
        }
        else{
            filterViewModel.totalProducts.value = totalProducts.value
        }
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
        filterViewModel.totalProducts.value = null
    }
}