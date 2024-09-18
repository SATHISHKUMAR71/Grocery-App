package com.example.shoppinggroceryapp.fragments.appfragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
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
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
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
        var firstVisiblePosition: Int? = null
    }
    private lateinit var productListViewModel:ProductListViewModel
    private lateinit var filterAndSortLayout:LinearLayout
    var products = listOf<Product>()
    lateinit var offerList:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("On Offer Frag created")
    }

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_offer, container, false)
        offerList = view.findViewById(R.id.offerList)
        filterAndSortLayout = view.findViewById(R.id.linearLayout15)
        val sortButton = view.findViewById<MaterialButton>(R.id.sortButton)
        val filterButton = view.findViewById<MaterialButton>(R.id.filterButton)
        val fileDir = File(requireContext().filesDir,"AppImages")
        val adapter = ProductListAdapter(this,fileDir,"O",false)
        val offerViewModel = ViewModelProvider(this,OfferViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[OfferViewModel::class.java]
        productListViewModel = ViewModelProvider(this,ProductListViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[ProductListViewModel::class.java]
        if(FilterFragment.list!=null){
            adapter.setProducts(FilterFragment.list!!)
            offerList.adapter = adapter
            offerList.layoutManager = LinearLayoutManager(context)
            if((firstVisiblePosition!=null)&& offerList.layoutManager!=null){
                println("First OFER LIST: ${offerList.layoutManager} $firstVisiblePosition")
                (offerList.layoutManager as LinearLayoutManager).scrollToPosition(firstVisiblePosition?:0)
            }
        }
        offerViewModel.getOfferedProducts()


        offerViewModel.offeredProductList.observe(viewLifecycleOwner){ offeredProductList ->
            println("OOOOObserver Called")
            products = offeredProductList
            if(FilterFragment.list==null){
                println("OOOOObserver Called in IF")
                adapter.setProducts(offeredProductList)
                offerList.adapter = adapter
                offerList.layoutManager = LinearLayoutManager(context)
                if((firstVisiblePosition!=null)&& offerList.layoutManager!=null){
                    println("First OFER LIST: ${offerList.layoutManager} $firstVisiblePosition")
                    (offerList.layoutManager as LinearLayoutManager).scrollToPosition(firstVisiblePosition?:0)
                }
            }
        }

        val filterBadge = BadgeDrawable.create(requireContext())
        filterBadge.number = 10
        filterBadge.badgeTextColor = Color.WHITE
        filterBadge.backgroundColor = Color.RED
        BadgeUtils.attachBadgeDrawable(filterBadge,filterButton)

        filterButton.setOnClickListener {
            println("PRODUCTS LIST: ${products.size} $products")
            if(FilterFragment.list!=null) {
                FilterFragment.list = products.toMutableList()
                FragmentTransaction.navigateWithBackstack(
                    parentFragmentManager,
                    FilterFragment(FilterFragment.list!!),
                    "Filter"
                )
            }
            else{
                FragmentTransaction.navigateWithBackstack(
                    parentFragmentManager,
                    FilterFragment(products.toMutableList()),
                    "Filter"
                )
            }
        }

        sortButton.setOnClickListener {
            val bottomSheet = BottomSheetDialog()
            bottomSheet.show(parentFragmentManager,"Bottom Sort Sheet")
        }
        val sorter  = ProductSorter()
        BottomSheetDialog.selectedOption.observe(viewLifecycleOwner){
            val newList: List<Product>
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
            (offerList.layoutManager as LinearLayoutManager).scrollToPosition(0)
        }

        return view
    }

    override fun onStop() {
        super.onStop()
        println("%%%%% FILTER IS NULL: ${FilterFragment.list}")
    }

    override fun onPause() {
        super.onPause()

//        firstVisiblePosition = (offerList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        println("First: $firstVisiblePosition")
//        offerList.adapter = null
    }


    override fun onDestroy() {
        super.onDestroy()
        FilterFragment.list = null
        firstVisiblePosition = null
        OfferFragment.dis10Val = false
        OfferFragment.dis20Val = false
        OfferFragment.dis30Val = false
        OfferFragment.dis40Val = false
        OfferFragment.dis50Val =false
        ProductListFragment.dis10Val = false
        ProductListFragment.dis20Val = false
        ProductListFragment.dis30Val = false
        ProductListFragment.dis40Val = false
        ProductListFragment.dis50Val = false
        println("On Offer destroyed")
    }
}

