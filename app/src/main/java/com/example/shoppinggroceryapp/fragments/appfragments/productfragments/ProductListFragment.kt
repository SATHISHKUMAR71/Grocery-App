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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.fragments.filter.FilterFragment
import com.example.shoppinggroceryapp.fragments.sort.BottomSheetDialog
import com.example.shoppinggroceryapp.model.dao.UserDao
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.Cart
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileOutputStream


class ProductListFragment(var category:String?) : Fragment() {
    companion object{
        var selectedProduct:MutableLiveData<Product> = MutableLiveData()
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
        println("PRODUCT LIST FRAGMENT CREATED $category")
        val view =  inflater.inflate(R.layout.fragment_product_list, container, false)
        val productRV = view.findViewById<RecyclerView>(R.id.productListRecyclerView)
        val notifyNoItems = view.findViewById<TextView>(R.id.notifyNoItemsAvailable)
        val handler = Handler(Looper.getMainLooper())
        val totalCostButton = view.findViewById<MaterialButton>(R.id.totalPriceWorthInCart)
        val exploreCategoryButton = view.findViewById<MaterialButton>(R.id.categoryButtonProductList)
        val productListToolbar =view.findViewById<MaterialToolbar>(R.id.productListToolBar)
        val sortButton = view.findViewById<MaterialButton>(R.id.sortButton)
        val filterButton = view.findViewById<MaterialButton>(R.id.filterButton)

        searchViewOpened = (arguments?.getBoolean("searchViewOpened")==true)

        filterButton.setOnClickListener {
//            FilterFragment.totalProducts.value = productList.size
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,FilterFragment(category))
                .addToBackStack("Filter")
                .commit()
        }


        productListToolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        totalCost.observe(viewLifecycleOwner){
            val str ="₹"+ (it?:0).toString()
            totalCostButton.text =str
        }

        totalCostButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,CartFragment())
                .addToBackStack("Going to Cart")
                .commit()
        }

        exploreCategoryButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,CategoryFragment())
                .addToBackStack("Exploring Category")
                .commit()
        }
        val userDb:UserDao = AppDatabase.getAppDatabase(requireContext()).getUserDao()
        fileDir = File(requireContext().filesDir,"AppImages")
        totalCost.value = 0f
        Thread{
            val list = userDb.getCartItems(MainActivity.cartId)
            for(cart in list){
                val totalItems = cart.totalItems
                val price = cart.unitPrice
                MainActivity.handler.post {
                    totalCost.value =totalCost.value!!+(totalItems * price)
                }
            }
        }.start()
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(
                Build.VERSION_CODES.R) >= 2) {
            Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        }

        val adapter=ProductListAdapter(this,fileDir,"P")
        println(FilterFragment.list)
        if(FilterFragment.list!=null){
            productRV.adapter = adapter
            productRV.layoutManager = LinearLayoutManager(requireContext())
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
            Thread{
                productList = AppDatabase.getAppDatabase(requireContext()).getUserDao().getOnlyProducts().toMutableList()
                handler.post {
                    productRV.adapter = adapter
                    productRV.layoutManager = LinearLayoutManager(requireContext())
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
            }.start()
        }
        else{
            Thread{
                productList = AppDatabase.getAppDatabase(requireContext()).getUserDao().getProductByCategory(category!!).toMutableList()
                handler.post {
                    productRV.adapter = adapter
                    productRV.layoutManager = LinearLayoutManager(requireContext())
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
            }.start()
        }
        sortButton.setOnClickListener {
            val bottomSheet = BottomSheetDialog()
            bottomSheet.show(parentFragmentManager,"Bottom Sort Sheet")
        }

        BottomSheetDialog.selectedOption.observe(viewLifecycleOwner){
            println("ON BOTTOM SHEET $it")
            when(it){
                0 -> {
                    Thread{
                        val newProductList: MutableList<Product> = if(category!=null) {
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedManufacturedLowProducts(category!!).toMutableList()
                        } else{
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedManufacturedLowProductsNoCat().toMutableList()
                        }
                        println(AppDatabase.getAppDatabase(requireContext()).getUserDao()
                            .getSortedManufacturedLowProductsNoCat().toMutableList())

                        handler.post {
                            println("List $newProductList")
                            productRV.adapter = adapter
                            productRV.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(newProductList)
                            println("ON List: $newProductList")
                            if(newProductList.size==0){
                                productRV.visibility = View.GONE
                                notifyNoItems.visibility = View.VISIBLE
                            }
                            else{
                                productRV.visibility = View.VISIBLE
                                notifyNoItems.visibility = View.GONE
                            }
                        }
                    }.start()
                }
                1 -> {
                    Thread{
                        val newProductList: MutableList<Product> = if(category!=null) {
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedManufacturedHighProducts(category!!).toMutableList()
                        } else{
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedManufacturedHighProductsNoCat().toMutableList()
                        }
                        println(AppDatabase.getAppDatabase(requireContext()).getUserDao()
                            .getSortedManufacturedHighProductsNoCat().toMutableList())
                        println(newProductList)
                        handler.post {
                            println("List $newProductList")
                            productRV.adapter = adapter
                            productRV.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(newProductList)
                            if(newProductList.size==0){
                                productRV.visibility = View.GONE
                                notifyNoItems.visibility = View.VISIBLE
                            }
                            else{
                                productRV.visibility = View.VISIBLE
                                notifyNoItems.visibility = View.GONE
                            }
                        }
                    }.start()
                }
                2 -> {
                    Thread{
                        val newProductList: MutableList<Product> = if(category!=null) {
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedExpiryLowProducts(category!!).toMutableList()
                        } else{
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedExpiryLowProductsNoCat().toMutableList()
                        }
                        println(AppDatabase.getAppDatabase(requireContext()).getUserDao()
                            .getSortedExpiryLowProductsNoCat().toMutableList())
                        println(newProductList)
                        handler.post {
                            println("List $newProductList")
                            productRV.adapter = adapter
                            productRV.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(newProductList)
                            if(newProductList.size==0){
                                productRV.visibility = View.GONE
                                notifyNoItems.visibility = View.VISIBLE
                            }
                            else{
                                productRV.visibility = View.VISIBLE
                                notifyNoItems.visibility = View.GONE
                            }
                        }
                    }.start()
                }
                3 -> {
                    Thread{
                        val newProductList = if(category!=null) {
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedExpiryHighProducts(category!!).toMutableList()
                        }
                        else{
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedExpiryHighProductsNoCat().toMutableList()
                        }
                        println(AppDatabase.getAppDatabase(requireContext()).getUserDao()
                            .getSortedExpiryHighProductsNoCat().toMutableList())
                        println(newProductList)
                        handler.post {
                            println("List $newProductList")
                            productRV.adapter = adapter
                            productRV.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(newProductList)
                            if(newProductList.size==0){
                                productRV.visibility = View.GONE
                                notifyNoItems.visibility = View.VISIBLE
                            }
                            else{
                                productRV.visibility = View.VISIBLE
                                notifyNoItems.visibility = View.GONE
                            }
                        }
                    }.start()
                }
                4 -> {
                    Thread{

                        val newProductList= if(category!=null) {
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedPriceLowProducts(category!!).toMutableList()
                        }
                        else{
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedPriceLowProductsNoCat().toMutableList()
                        }
                        handler.post {
                            println("List $newProductList")
                            productRV.adapter = adapter
                            productRV.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(newProductList)
                            if(newProductList.size==0){
                                productRV.visibility = View.GONE
                                notifyNoItems.visibility = View.VISIBLE
                            }
                            else{
                                productRV.visibility = View.VISIBLE
                                notifyNoItems.visibility = View.GONE
                            }
                        }
                    }.start()
                }
                5 -> {
                    Thread{
                        val newProductList = if(category!=null) {
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedPriceHighProducts(category!!).toMutableList()
                        }
                        else{
                            AppDatabase.getAppDatabase(requireContext()).getUserDao()
                                .getSortedPriceHighProductsNoCat().toMutableList()
                        }
                        println(AppDatabase.getAppDatabase(requireContext()).getUserDao()
                            .getSortedPriceHighProductsNoCat().toMutableList())
                        println(newProductList)
                        handler.post {
                            println("List $newProductList")
                            productRV.adapter = adapter
                            productRV.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(newProductList)
                            if(newProductList.size==0){
                                productRV.visibility = View.GONE
                                notifyNoItems.visibility = View.VISIBLE
                            }
                            else{
                                productRV.visibility = View.VISIBLE
                                notifyNoItems.visibility = View.GONE
                            }
                        }
                    }.start()
                }
            }
        }
        return view
    }

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
        if(MainActivity.isRetailer){
            view?.findViewById<MaterialToolbar>(R.id.productListToolBar)?.visibility = View.GONE
            view?.findViewById<LinearLayout>(R.id.linearLayout8)?.visibility = View.GONE
        }
        else{
            InitialFragment.hideSearchBar.value = true
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