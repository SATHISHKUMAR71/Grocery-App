package com.example.shoppinggroceryapp.fragments.appfragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.example.shoppinggroceryapp.model.entities.products.Images
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
        var offerFilterCount:Int  = 0
    }
    private lateinit var productListViewModel:ProductListViewModel
    private lateinit var filterAndSortLayout:LinearLayout
    private lateinit var filterCount:TextView
    var products = listOf<Product>()
    lateinit var offerList:RecyclerView
    private lateinit var adapter: ProductListAdapter
    private lateinit var offerViewModel:OfferViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("On Offer Frag created")
        offerFilterCount = 0
    }

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("ON CREATE VIEW OFFER FRAGMENT")
        val view =  inflater.inflate(R.layout.fragment_offer, container, false)
        val noItemsFoundImage = view.findViewById<ImageView>(R.id.noItemFoundImageOfferFragment)
        val noItemsFoundImageText = view.findViewById<TextView>(R.id.noItemsFoundText)
        offerList = view.findViewById(R.id.offerList)
        filterCount = view.findViewById<TextView>(R.id.filterCountTextViewOffer)
        filterAndSortLayout = view.findViewById(R.id.linearLayout15)
        val sortButton = view.findViewById<MaterialButton>(R.id.sortButton)
        val filterButton = view.findViewById<MaterialButton>(R.id.filterButton)
        val fileDir = File(requireContext().filesDir,"AppImages")
        adapter = ProductListAdapter(this,fileDir,"O",false)
        offerViewModel = ViewModelProvider(this,OfferViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[OfferViewModel::class.java]
        productListViewModel = ViewModelProvider(this,ProductListViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[ProductListViewModel::class.java]
        if(FilterFragment.list!=null){
            if(FilterFragment.list!!.isEmpty()){
                noItemsFoundImageText.visibility = View.VISIBLE
                noItemsFoundImage.visibility =View.VISIBLE
            }
            else{
                noItemsFoundImageText.visibility = View.GONE
                noItemsFoundImage.visibility =View.GONE

            }
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
                if(offeredProductList.isEmpty()){
                    noItemsFoundImageText.visibility = View.VISIBLE
                    noItemsFoundImage.visibility =View.VISIBLE
                }
                else{
                    noItemsFoundImageText.visibility = View.GONE
                    noItemsFoundImage.visibility =View.GONE

                }
                adapter.setProducts(offeredProductList)
                offerList.adapter = adapter
                offerList.layoutManager = LinearLayoutManager(context)
                if((firstVisiblePosition!=null)&& offerList.layoutManager!=null){
                    println("First OFER LIST: ${offerList.layoutManager} $firstVisiblePosition")
                    (offerList.layoutManager as LinearLayoutManager).apply {
                        scrollToPosition(firstVisiblePosition?:0)
//                        stackFromEnd = true
//                        scrollToPositionWithOffset(firstVisiblePosition?:0,-100)
                    }
                }
            }
        }
        if(offerFilterCount!=0){
            filterCount.text = offerFilterCount.toString()
            filterCount.visibility = View.VISIBLE
        }
        else{
            filterCount.visibility = View.INVISIBLE
        }
        val filterBadge = BadgeDrawable.create(requireContext())
        filterBadge.number = 10
        filterBadge.badgeTextColor = Color.WHITE
        filterBadge.backgroundColor = Color.RED
        BadgeUtils.attachBadgeDrawable(filterBadge,filterButton)

        filterButton.setOnClickListener {
            offerFilterCount = 0
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
            offerList.layoutManager.let {layoutManager ->
                (layoutManager as LinearLayoutManager).scrollToPosition(0)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
//        adapter.setProducts(products)
        if(FilterFragment.list!=null){
            adapter.setProducts(FilterFragment.list!!)
            offerList.adapter = adapter
            offerList.layoutManager = LinearLayoutManager(context)
            if((firstVisiblePosition!=null)&& offerList.layoutManager!=null){
                println("First OFER LIST: ${offerList.layoutManager} $firstVisiblePosition")
                (offerList.layoutManager as LinearLayoutManager).scrollToPosition(firstVisiblePosition?:0)
            }
        }
        else{
            adapter.setProducts(products)
        }
        println("OFFER FRAGMENT ON Resume ${products.size}")
    }
    override fun onStop() {
        super.onStop()
        println("OFFER FRAGMENT ON STOP")
        offerList.adapter?.let {
            it.notifyDataSetChanged()
        }
//        firstVisiblePosition = (offerList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
//        println("First: $firstVisiblePosition")
//        offerList.adapter = null
        println("%%%%% FILTER IS NULL: ${FilterFragment.list}")
    }

    override fun onPause() {
        super.onPause()
        println("OFFER FRAGMENT ON PAUSE")
    }



    override fun onDestroy() {
        super.onDestroy()
        FilterFragment.list = null
        offerFilterCount = 0
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

