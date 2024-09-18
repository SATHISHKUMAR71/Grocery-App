package com.example.shoppinggroceryapp.fragments.appfragments.productfragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ext.SdkExtensions
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FindNumberOfCartItems
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.OfferFragment
import com.example.shoppinggroceryapp.fragments.appfragments.OfferFragment.Companion
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.fragments.filter.FilterFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.inventoryfragments.AddEditFragment
import com.example.shoppinggroceryapp.fragments.sort.BottomSheetDialog
import com.example.shoppinggroceryapp.fragments.sort.ProductSorter
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.Images
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.viewmodel.initialviewmodel.InitialViewModelFactory
import com.example.shoppinggroceryapp.viewmodel.initialviewmodel.SearchViewModel
import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductListViewModel
import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductListViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream


class ProductListFragment : Fragment() {
    companion object{
        var selectedProduct:MutableLiveData<Product> = MutableLiveData()
        var selectedPos:Int? = null
        var totalCost:MutableLiveData<Float> = MutableLiveData(0f)
        var position = 0
        var dis50Val: Boolean? = null
        var dis40Val: Boolean? = null
        var dis30Val: Boolean? = null
        var dis20Val: Boolean? = null
        var dis10Val: Boolean? = null
    }
    var category:String? = null
    private lateinit var productListViewModel:ProductListViewModel
    private lateinit var fileDir:File
    private lateinit var searchViewModel: SearchViewModel
    var searchViewOpened = false
    private lateinit var selectedProduct: Product
    private lateinit var toolbar:MaterialToolbar
    private var productList:MutableList<Product> = mutableListOf()

    override fun onStart() {
        super.onStart()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString("category")
    }

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("PRODUCT LIST FRAG CREATED")
        if(!MainActivity.isRetailer) {
            InitialFragment.hideSearchBar.value = true
            InitialFragment.hideBottomNav.value = true
        }
        val view =  inflater.inflate(R.layout.fragment_product_list, container, false)
        val sortAndFilterLayout = view.findViewById<LinearLayout>(R.id.linearLayout15)
        toolbar = view.findViewById<MaterialToolbar>(R.id.productListToolBar)
        var badgeDrawableListFragment = BadgeDrawable.create(requireContext())
        toolbar.setTitle(category)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.searchProductInProductList -> {
                    InitialFragment.openSearchView.value = true
                }
                R.id.mic ->{
                    InitialFragment.openMicSearch.value = true
                }
                R.id.cart ->{
                    FragmentTransaction.navigateWithBackstack(parentFragmentManager,CartFragment(),"Going to cart")
                }
            }
            true
        }
        val productRV = view.findViewById<RecyclerView>(R.id.productListRecyclerView)
        val notifyNoItems = view.findViewById<TextView>(R.id.notifyNoItemsAvailable)
        val noItemsImage = view.findViewById<ImageView>(R.id.noItemsFound)
        searchViewModel = ViewModelProvider(this,InitialViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[SearchViewModel::class.java]
        val totalCostButton = view.findViewById<MaterialButton>(R.id.totalPriceWorthInCart)
        val exploreCategoryButton = view.findViewById<MaterialButton>(R.id.categoryButtonProductList)
        val sortButton = view.findViewById<MaterialButton>(R.id.sortButton)
        val filterButton = view.findViewById<MaterialButton>(R.id.filterButton)
        productListViewModel = ViewModelProvider(this,ProductListViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[ProductListViewModel::class.java]
        searchViewOpened = (arguments?.getBoolean("searchViewOpened")==true)
        productListViewModel.getCartItems(MainActivity.cartId.toInt())

        productListViewModel.cartList.observe(viewLifecycleOwner){
            FindNumberOfCartItems.productCount.value = it.size
        }

        FindNumberOfCartItems.productCount.observe(viewLifecycleOwner){
            badgeDrawableListFragment.text = it.toString()
            if(it!=0) {
                badgeDrawableListFragment.isVisible = true
                BadgeUtils.attachBadgeDrawable(badgeDrawableListFragment, toolbar, R.id.cart)
            }
            else{
                badgeDrawableListFragment.isVisible = false
            }
        }
        filterButton.setOnClickListener {
            println("ON FILTER FRAGMENT LIST ${productList.size} $productList ")
            var filterFragment = FilterFragment(productList).apply {
                arguments = Bundle().apply { putString("category",category) }
            }
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,filterFragment,"Filter")
        }
        totalCost.observe(viewLifecycleOwner){
            val str ="₹"+ (it?:0).toString()
            totalCostButton.text =str
        }
        totalCostButton.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,CartFragment(),"Going to cart")
        }
        exploreCategoryButton.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,CategoryFragment(),"Exploring Category")
        }
        fileDir = File(requireContext().filesDir,"AppImages")
        totalCost.value = 0f
        productListViewModel.getCartItems(cartId = MainActivity.cartId)
        productListViewModel.cartList.observe(viewLifecycleOwner){
            for(cart in it){
                val totalItems = cart.totalItems
                val price = cart.unitPrice
                totalCost.value =totalCost.value!!+(totalItems * price)
            }
        }

        val adapter=ProductListAdapter(this,fileDir,"P",false)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        if(FilterFragment.list!=null){
            println("ON FILTER FRAGMENT LIST at non null ${FilterFragment.list}")
            if(productRV.adapter==null) {
                productRV.adapter = adapter
                productRV.layoutManager = LinearLayoutManager(requireContext())
            }
            adapter.setProducts(FilterFragment.list!!)
            if(FilterFragment.list!!.size==0){
                productRV.visibility = View.GONE
                notifyNoItems.visibility = View.VISIBLE
                noItemsImage.visibility =View.VISIBLE
            }
            else{
                productRV.visibility = View.VISIBLE
                notifyNoItems.visibility = View.GONE
                noItemsImage.visibility = View.GONE
            }
        }
        else if(category==null){
            productListViewModel.getOnlyProducts()
        }
        else{
            productListViewModel.getProductsByCategory(category!!)
        }
        productListViewModel.productList.observe(viewLifecycleOwner){
            if(FilterFragment.list==null) {
                println("ON FILTER FRAGMENT LIST product list observer called")
                productList = it.toMutableList()
                if (productRV.adapter == null) {
                    productRV.adapter = adapter
                    productRV.layoutManager = LinearLayoutManager(requireContext())
                }
                adapter.setProducts(productList)
                if (productList.size == 0) {
                    productRV.visibility = View.GONE
                    notifyNoItems.visibility = View.VISIBLE
                    noItemsImage.visibility = View.VISIBLE
                } else {
                    productRV.visibility = View.VISIBLE
                    notifyNoItems.visibility = View.GONE
                    noItemsImage.visibility = View.GONE
                }
            }
        }
        productListViewModel.productCategoryList.observe(viewLifecycleOwner){
            if(FilterFragment.list==null) {
                println("ON FILTER FRAGMENT LIST product category list observer called")
                productList = it.toMutableList()
                if (productRV.adapter == null) {
                    productRV.adapter = adapter
                    productRV.layoutManager = LinearLayoutManager(requireContext())
                }
                adapter.setProducts(productList)
                if (productList.size == 0) {
                    productRV.visibility = View.GONE
                    notifyNoItems.visibility = View.VISIBLE
                    noItemsImage.visibility = View.VISIBLE
                } else {
                    productRV.visibility = View.VISIBLE
                    notifyNoItems.visibility = View.GONE
                    noItemsImage.visibility = View.GONE
                }
            }
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
                    newList = sorter.sortByDate(productList)
                }
                else{
                    newList = sorter.sortByDate(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
            else if(it == 1){
                if(FilterFragment.list==null) {
                    newList = sorter.sortByExpiryDate(productList)
                }
                else{
                    newList = sorter.sortByExpiryDate(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
            else if(it == 2){
                if(FilterFragment.list==null) {
                    newList = sorter.sortByDiscount(productList)
                }
                else{
                    newList = sorter.sortByDiscount(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
            else if(it == 3){
                if(FilterFragment.list==null) {
                    newList = sorter.sortByPriceLowToHigh(productList)
                }
                else{
                    newList = sorter.sortByPriceLowToHigh(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
            else if(it == 4){
                if(FilterFragment.list==null) {
                    newList = sorter.sortByPriceHighToLow(productList)
                }
                else{
                    newList = sorter.sortByPriceHighToLow(FilterFragment.list!!)
                }
                adapter.setProducts(newList)
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(!MainActivity.isRetailer) {
            InitialFragment.hideSearchBar.value = true
            InitialFragment.hideBottomNav.value = true
        }
        if(MainActivity.isRetailer){
            toolbar.visibility =View.GONE
            view?.findViewById<FloatingActionButton>(R.id.addProductsToInventory)?.visibility = View.VISIBLE
            view?.findViewById<FloatingActionButton>(R.id.addProductsToInventory)?.setOnClickListener {
                ProductListFragment.selectedProduct.value = null
                FragmentTransaction.navigateWithBackstack(parentFragmentManager,AddEditFragment(),"Edit in Product Fragment")
            }
            view?.findViewById<LinearLayout>(R.id.linearLayout8)?.visibility = View.GONE
        }
        else{
            view?.findViewById<FloatingActionButton>(R.id.addProductsToInventory)?.visibility = View.GONE
            view?.findViewById<LinearLayout>(R.id.linearLayout8)?.visibility = View.VISIBLE
        }
    }




    override fun onStop() {
        super.onStop()
        if(!MainActivity.isRetailer) {
            InitialFragment.hideSearchBar.value = false
            InitialFragment.hideBottomNav.value = false
        }
        productListViewModel.cartList.value = mutableListOf()
    }


    override fun onDestroy() {
        super.onDestroy()
        FilterFragment.list = null
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
        if(InitialFragment.searchQueryList.size <2){
            InitialFragment.searchHint.value = ""
            InitialFragment.searchQueryList = mutableListOf()
        }
        else{
            InitialFragment.searchHint.value = InitialFragment.searchQueryList[1]
            InitialFragment.searchQueryList.removeAt(0)
        }
        println("PRODUCT LIST FRAG DESTROYed")
    }
}
