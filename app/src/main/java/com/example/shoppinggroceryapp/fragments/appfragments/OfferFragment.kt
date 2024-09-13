package com.example.shoppinggroceryapp.fragments.appfragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.fragments.filter.FilterFragment
import com.example.shoppinggroceryapp.fragments.sort.BottomSheetDialog
import com.example.shoppinggroceryapp.fragments.sort.ProductSorter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.viewmodel.offerviewmodel.OfferViewModel
import com.example.shoppinggroceryapp.viewmodel.offerviewmodel.OfferViewModelFactory
import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductListViewModel
import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductListViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.File


class OfferFragment : Fragment() {

    companion object {
        var dis50Val: Boolean? = null
        var dis40Val: Boolean? = null
        var dis30Val: Boolean? = null
        var dis20Val: Boolean? = null
        var dis10Val: Boolean? = null
    }
    private lateinit var productListViewModel:ProductListViewModel
    private lateinit var filterAndSortLayout:LinearLayout
    var products = listOf<Product>()
    lateinit var offerList:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("On Offer Frag created")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_offer, container, false)
        offerList = view.findViewById(R.id.offerList)
        filterAndSortLayout = view.findViewById(R.id.linearLayout15)

        val fileDir = File(requireContext().filesDir,"AppImages")
        val adapter = ProductListAdapter(this,fileDir,"O",false)
        val offerViewModel = ViewModelProvider(this,OfferViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[OfferViewModel::class.java]
        productListViewModel = ViewModelProvider(this,ProductListViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[ProductListViewModel::class.java]
        if(FilterFragment.list!=null){
            adapter.setProducts(FilterFragment.list!!)
            offerList.adapter = adapter
            offerList.layoutManager = LinearLayoutManager(context)
        }
        else {
            offerViewModel.getOfferedProducts()
        }

        offerViewModel.offeredProductList.observe(viewLifecycleOwner){ offeredProductList ->
            println("O00Observer Called")
            if(FilterFragment.list==null){
                products = offeredProductList
                adapter.setProducts(offeredProductList)
                offerList.adapter = adapter
                offerList.layoutManager = LinearLayoutManager(context)
            }
        }
        val sortButton = view.findViewById<MaterialButton>(R.id.sortButton)
        val filterButton = view.findViewById<MaterialButton>(R.id.filterButton)


        filterButton.setOnClickListener {
//            FilterFragment.totalProducts.value = productList.size
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,FilterFragment(products.toMutableList()),"Filter")
        }

        sortButton.setOnClickListener {
            val bottomSheet = BottomSheetDialog()
            bottomSheet.show(parentFragmentManager,"Bottom Sort Sheet")
        }
        val sorter  = ProductSorter()
        BottomSheetDialog.selectedOption.observe(viewLifecycleOwner){
            var newList = listOf<Product>()
            if(it==0){
                if(FilterFragment.list==null) {
                    newList = sorter.sortByDate(products)
                }
                else{
                    newList = sorter.sortByDate(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
            else if(it == 1){
                if(FilterFragment.list==null) {
                    newList = sorter.sortByExpiryDate(products)
                }
                else{
                    newList = sorter.sortByExpiryDate(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
            else if(it == 2){
                if(FilterFragment.list==null) {
                    newList = sorter.sortByDiscount(products)
                }
                else{
                    newList = sorter.sortByDiscount(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
            else if(it == 3){
                if(FilterFragment.list==null) {
                    newList = sorter.sortByPriceLowToHigh(products)
                }
                else{
                    newList = sorter.sortByPriceLowToHigh(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
            else if(it == 4){
                if(FilterFragment.list==null) {
                    newList = sorter.sortByPriceHighToLow(products)
                }
                else{
                    newList = sorter.sortByPriceHighToLow(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
        }
        return view
    }

    override fun onStop() {
        super.onStop()
        FilterFragment.list = null
        offerList.adapter = null
        println("%%%%% FILTER IS NULL: ${FilterFragment.list}")
    }

    override fun onPause() {
        super.onPause()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onDestroy() {
        super.onDestroy()
        dis50Val = false
        dis40Val = false
        dis30Val = false
        dis20Val = false
        dis10Val = false
        println("On Offer destroyed")
    }
}
//package com.example.shoppinggroceryapp.fragments.appfragments
//
//import android.content.Context
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.transition.TransitionInflater
//import com.example.shoppinggroceryapp.MainActivity
//import com.example.shoppinggroceryapp.R
//import com.example.shoppinggroceryapp.fragments.FragmentTransaction
//import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
//import com.example.shoppinggroceryapp.fragments.filter.FilterFragment
//import com.example.shoppinggroceryapp.fragments.sort.BottomSheetDialog
//import com.example.shoppinggroceryapp.fragments.sort.ProductSorter
//import com.example.shoppinggroceryapp.model.database.AppDatabase
//import com.example.shoppinggroceryapp.model.entities.products.Product
//import com.example.shoppinggroceryapp.viewmodel.offerviewmodel.OfferViewModel
//import com.example.shoppinggroceryapp.viewmodel.offerviewmodel.OfferViewModelFactory
//import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductListViewModel
//import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductListViewModelFactory
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.android.material.button.MaterialButton
//import java.io.File
//
//
//class OfferFragment : Fragment() {
//
//    private lateinit var productListViewModel:ProductListViewModel
//    private lateinit var filterAndSortLayout:LinearLayout
//    var products = listOf<Product>()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        println("On Offer Frag created")
//    }
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        // Inflate the layout for this fragment
//        val view =  inflater.inflate(R.layout.fragment_offer, container, false)
//        val offerList = view.findViewById<RecyclerView>(R.id.offerList)
//        filterAndSortLayout = view.findViewById(R.id.linearLayout15)
//
//        val fileDir = File(requireContext().filesDir,"AppImages")
//        val adapter = ProductListAdapter(this,fileDir,"O",false)
//        val offerViewModel = ViewModelProvider(this,OfferViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[OfferViewModel::class.java]
//        productListViewModel = ViewModelProvider(this,ProductListViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[ProductListViewModel::class.java]
//        offerViewModel.getOfferedProducts()
//
//        offerViewModel.offeredProductList.observe(viewLifecycleOwner){ offeredProductList ->
//            println("OObserver Called")
//            products = offeredProductList
//            adapter.setProducts(offeredProductList)
//            offerList.adapter = adapter
//            offerList.layoutManager = LinearLayoutManager(context)
//
//        }
//        val sortButton = view.findViewById<MaterialButton>(R.id.sortButton)
//        val filterButton = view.findViewById<MaterialButton>(R.id.filterButton)
//
//
//        filterButton.setOnClickListener {
////            FilterFragment.totalProducts.value = productList.size
//            var filterFragment = FilterFragment(products.toMutableList(),adapter)
//            filterFragment.arguments = Bundle().apply {
//                putString("type","offer")
//            }
//            FragmentTransaction.navigateWithBackstack(parentFragmentManager,filterFragment,"Filter")
//        }
//
//        sortButton.setOnClickListener {
//            val bottomSheet = BottomSheetDialog()
//            bottomSheet.show(parentFragmentManager,"Bottom Sort Sheet")
//        }
//        val sorter  = ProductSorter()
//        BottomSheetDialog.selectedOption.observe(viewLifecycleOwner){
//            var newList = listOf<Product>()
//            if(it==0){
//                newList = sorter.sortByDate(products)
//                adapter.setProducts(newList)
//            }
//            else if(it == 1){
//                newList = sorter.sortByExpiryDate(products)
//                adapter.setProducts(newList)
//            }
//            else if(it == 2){
//                newList = sorter.sortByDiscount(products)
//                adapter.setProducts(newList)
//            }
//            else if(it == 3){
//                newList = sorter.sortByPriceLowToHigh(products)
//                adapter.setProducts(newList)
//            }
//            else if(it == 4){
//                newList = sorter.sortByPriceHighToLow(products)
//                adapter.setProducts(newList)
//            }
//        }
//        return view
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        println("On Offer destroyed")
//    }
//}