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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.fragments.filter.FilterFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.inventoryfragments.AddEditFragment
import com.example.shoppinggroceryapp.fragments.sort.BottomSheetDialog
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.model.viewmodel.productviewmodel.ProductListViewModel
import com.example.shoppinggroceryapp.model.viewmodel.productviewmodel.ProductListViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream


class ProductListFragment(var category:String?) : Fragment() {
    companion object{

        var selectedProduct:MutableLiveData<Product> = MutableLiveData()
        var selectedEditableProduct:MutableLiveData<Product> = MutableLiveData()
        var totalCost:MutableLiveData<Float> = MutableLiveData(0f)
        var position = 0
    }
    private lateinit var fileDir:File
    var searchViewOpened = false
    private lateinit var selectedProduct: Product
    private var productList:MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_product_list, container, false)
        val productRV = view.findViewById<RecyclerView>(R.id.productListRecyclerView)
        val notifyNoItems = view.findViewById<TextView>(R.id.notifyNoItemsAvailable)
        val handler = Handler(Looper.getMainLooper())
        val totalCostButton = view.findViewById<MaterialButton>(R.id.totalPriceWorthInCart)
        val exploreCategoryButton = view.findViewById<MaterialButton>(R.id.categoryButtonProductList)
        val productListToolbar =view.findViewById<MaterialToolbar>(R.id.productListToolBar)
        val sortButton = view.findViewById<MaterialButton>(R.id.sortButton)
        val filterButton = view.findViewById<MaterialButton>(R.id.filterButton)
        val productListViewModel = ViewModelProvider(this,ProductListViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[ProductListViewModel::class.java]
        searchViewOpened = (arguments?.getBoolean("searchViewOpened")==true)

        filterButton.setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,FilterFragment(category),"Filter")
        }

        productListToolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
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
        val userDb:UserDao = AppDatabase.getAppDatabase(requireContext()).getUserDao()
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
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(
                Build.VERSION_CODES.R) >= 2) {
            Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        }

        val adapter=ProductListAdapter(this,fileDir,"P",false)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        println("9999 PRODUCT LIST FRAGMENT CREATED $category ${productRV.verticalScrollbarPosition}")
        println(FilterFragment.list)
        if(FilterFragment.list!=null){
            if(productRV.adapter==null) {
                productRV.adapter = adapter
                productRV.layoutManager = LinearLayoutManager(requireContext())
            }
            adapter.setProducts(FilterFragment.list!!)
            if(FilterFragment.list!!.size==0){
                productRV.visibility = View.GONE
                notifyNoItems.visibility = View.VISIBLE
            }
            else{
                productRV.visibility = View.VISIBLE
                notifyNoItems.visibility = View.GONE
            }
            FilterFragment.list = null
        }
        else if(category==null){

            productListViewModel.getOnlyProducts()
            productListViewModel.productList.observe(viewLifecycleOwner){
                productList = it.toMutableList()
                if(productRV.adapter==null) {
                    productRV.adapter = adapter
                    productRV.layoutManager = LinearLayoutManager(requireContext())
                }
                adapter.setProducts(productList)
                if(productList.size==0){
                    productRV.visibility = View.GONE
                    notifyNoItems.visibility = View.VISIBLE
                }
                else{
                    productRV.visibility = View.VISIBLE
                    notifyNoItems.visibility = View.GONE
                }
            }
        }
        else{
            productListViewModel.getProductsByCategory(category!!)
            productListViewModel.productCategoryList.observe(viewLifecycleOwner){
                productList = it.toMutableList()
                if(productRV.adapter==null) {
                    productRV.adapter = adapter
                    productRV.layoutManager = LinearLayoutManager(requireContext())
                }
                    adapter.setProducts(productList)
                    if(productList.size==0){
                        productRV.visibility = View.GONE
                        notifyNoItems.visibility = View.VISIBLE
                    }
                    else{
                        productRV.visibility = View.VISIBLE
                        notifyNoItems.visibility = View.GONE
                    }
            }
        }
        sortButton.setOnClickListener {
            val bottomSheet = BottomSheetDialog()
            bottomSheet.show(parentFragmentManager,"Bottom Sort Sheet")
        }

        BottomSheetDialog.selectedOption.observe(viewLifecycleOwner){
            println("ON BOTTOM SHEET $it")
            when(it){
                0 -> {
                    if(category!=null){
                        productListViewModel.getSortedManufacturedLowProductsWithCat(category!!)
                    }
                    else{
                        productListViewModel.getSortedManufacturedLowProductsNoCat()
                    }
                }
                1 -> {
                    if(category!=null){
                        productListViewModel.getSortedManufacturedHighProductsWithCat(category!!)
                    }
                    else{
                        productListViewModel.getSortedManufacturedHighProductsNoCat()
                    }
                }
                2 -> {
                    if(category!=null){
                        productListViewModel.getSortedExpiryLowProductsWithCat(category!!)
                    }
                    else{
                        productListViewModel.getSortedExpiryLowProductsNoCat()
                    }
                }
                3 -> {
                    if(category!=null){
                        productListViewModel.getSortedExpiryHighProductsWithCat(category!!)
                    }
                    else{
                        productListViewModel.getSortedExpiryHighProductsNoCat()
                    }
                }
                4 -> {
                    if(category!=null){
                        productListViewModel.getSortedPriceLowProductsWithCat(category!!)
                    }
                    else{
                        productListViewModel.getSortedPriceLowProductsNoCat()
                    }
                }
                5 -> {
                    if(category!=null){
                        productListViewModel.getSortedPriceHighProductsWithCat(category!!)
                    }
                    else{
                        productListViewModel.getSortedPriceHighProductsNoCat()
                    }
                }

            }
        }
        productListViewModel.manufacturedSortedList.observe(viewLifecycleOwner){ newProductList ->
            println("List $newProductList")
            if(productRV.adapter==null) {
                productRV.adapter = adapter
                productRV.layoutManager = LinearLayoutManager(requireContext())
            }
            adapter.setProducts(newProductList)
            println("ON List: $newProductList")
            if(newProductList.isEmpty()){
                productRV.visibility = View.GONE
                notifyNoItems.visibility = View.VISIBLE
            }
            else{
                productRV.visibility = View.VISIBLE
                notifyNoItems.visibility = View.GONE
            }
        }
        return view
    }

//    fileDir = File(requireContext().filesDir,"AppImages")
    private fun storeImageInApp(context: Context,bitMap:Bitmap,fileName:String) {
        if(!fileDir.exists()){
            fileDir.mkdirs()
        }
        val bitmapFile = File(fileDir,fileName)
        val fileOutputStream = FileOutputStream(bitmapFile)
        bitMap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream)
    }


    override fun onPause() {
        super.onPause()
        println("PRODUCT LIST FRAGMENT ON PAUSE")
    }
    override fun onResume() {
        super.onResume()
//        ProductListFragment.selectedProduct.value = null
        if(MainActivity.isRetailer){
            view?.findViewById<FloatingActionButton>(R.id.addProductsToInventory)?.visibility = View.VISIBLE
            view?.findViewById<FloatingActionButton>(R.id.addProductsToInventory)?.setOnClickListener {
                ProductListFragment.selectedProduct.value = null
                FragmentTransaction.navigateWithBackstack(parentFragmentManager,AddEditFragment(),"Edit in Product Fragment")
            }
            view?.findViewById<MaterialToolbar>(R.id.productListToolBar)?.visibility = View.GONE
            view?.findViewById<LinearLayout>(R.id.linearLayout8)?.visibility = View.GONE
        }
        else{
            InitialFragment.hideSearchBar.value = true
            view?.findViewById<FloatingActionButton>(R.id.addProductsToInventory)?.visibility = View.GONE
            InitialFragment.hideBottomNav.value = true
            view?.findViewById<MaterialToolbar>(R.id.productListToolBar)?.visibility = View.VISIBLE
            view?.findViewById<LinearLayout>(R.id.linearLayout8)?.visibility = View.VISIBLE
        }

    }
    override fun onStop() {
        super.onStop()
        if(!MainActivity.isRetailer) {
            InitialFragment.hideSearchBar.value = false
            InitialFragment.hideBottomNav.value = false
        }
        if(searchViewOpened){
            InitialFragment.closeSearchView.value = false
        }
    }
}