package com.example.shoppinggroceryapp.fragments.appfragments.productfragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.fragments.ImageLoaderAndGetter
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.fragments.retailerfragments.inventoryfragments.AddEditFragment
import com.example.shoppinggroceryapp.model.dao.retailerviewmodel.inventoryviewmodel.AddEditViewModel
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.Cart
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.model.viewmodel.productviewmodel.ProductDetailViewModel
import com.example.shoppinggroceryapp.model.viewmodel.productviewmodel.ProductDetailViewModelFactory
import com.example.shoppinggroceryapp.model.viewmodel.productviewmodel.ProductListViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.File


class ProductDetailFragment : Fragment() {


    private var countOfOneProduct = 0
    private lateinit var imageLoader:ImageLoaderAndGetter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoader = ImageLoaderAndGetter()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_product_detail, container, false)
        val productDetailToolBar = view.findViewById<MaterialToolbar>(R.id.productDetailToolbar)
        val productDetailViewModel = ViewModelProvider(this,ProductDetailViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getRetailerDao()))[ProductDetailViewModel::class.java]
        if(MainActivity.isRetailer){
            productDetailToolBar.menu.findItem(R.id.edit).setVisible(true)
            productDetailToolBar.menu.findItem(R.id.cart).setVisible(false)
//            view.findViewById<LinearLayout>(R.id.similarProductsLayout).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.exploreBottomLayout).visibility = View.GONE
        }

        else{
            productDetailToolBar.menu.findItem(R.id.edit).setVisible(false)
            productDetailToolBar.menu.findItem(R.id.cart).setVisible(true)
//            view.findViewById<LinearLayout>(R.id.similarProductsLayout).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.exploreBottomLayout).visibility = View.VISIBLE
        }

        productDetailToolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.cart -> {
                    if (!MainActivity.isRetailer) {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentMainLayout,CartFragment())
                            .addToBackStack("Cart in Product Detail")
                            .commit()
                    }
                }
                R.id.edit -> {
                    if (MainActivity.isRetailer) {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentMainLayout,AddEditFragment())
                            .addToBackStack("Edit in Product Detail")
                            .commit()
                    }
                }
            }
            true
        }
        view.findViewById<MaterialButton>(R.id.categoryButton).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout,CategoryFragment())
                .addToBackStack("Category Opened from product Detail")
                .commit()
        }
        ProductListFragment.selectedProduct.value?.brandId?.let{
            productDetailViewModel.getBrandName(it)
        }

        productDetailViewModel.brandName.observe(viewLifecycleOwner){
            view.findViewById<TextView>(R.id.brandNameProductDetail).text = it
        }

        view.findViewById<MaterialToolbar>(R.id.productDetailToolbar).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val recentlyViewedItems =
            requireContext().getSharedPreferences("recentlyViewedItems", Context.MODE_PRIVATE)
        productDetailViewModel.addInRecentlyViewedItems(recentlyViewedItems)

        ProductListFragment.selectedProduct.observe(viewLifecycleOwner) {
            val productNameWithQuantity =
                "${ProductListFragment.selectedProduct.value?.productName} (${ProductListFragment.selectedProduct.value?.productQuantity})"
            view.findViewById<TextView>(R.id.productNameProductDetail).text =
                productNameWithQuantity
            val price = "₹${ProductListFragment.selectedProduct.value?.price}"
            view.findViewById<ImageView>(R.id.productImage).setImageBitmap(
                imageLoader.getImageInApp(
                    requireContext(),
                    ProductListFragment.selectedProduct.value?.mainImage ?: ""
                )
            )
            view.findViewById<TextView>(R.id.productPriceProductDetail).text = price
            val offerView = view.findViewById<TextView>(R.id.productOffer)
            if (ProductListFragment.selectedProduct.value?.offer == "-1") {
                offerView.visibility = View.GONE
            } else {
                offerView.visibility = View.VISIBLE
            }
            offerView.text = ProductListFragment.selectedProduct.value?.offer
            view.findViewById<TextView>(R.id.expiryDateProductDetail).text =
                DateGenerator.getDayAndMonth(ProductListFragment.selectedProduct.value?.expiryDate!!)
            view.findViewById<TextView>(R.id.manufactureDateProductDetail).text =
                DateGenerator.getDayAndMonth(ProductListFragment.selectedProduct.value?.manufactureDate!!)
            val totalItemsAddedProductDetail =
                view.findViewById<TextView>(R.id.totalItemsAddedProductDetail)
            val addProductButton =
                view.findViewById<MaterialButton>(R.id.addProductButtonProductDetail)
            val removeProductImgButton =
                view.findViewById<ImageButton>(R.id.productRemoveSymbolButtonProductDetail)
            val addProductImgButton =
                view.findViewById<ImageButton>(R.id.productAddSymbolButtonProductDetail)
            val addRemoveLayout =
                view.findViewById<LinearLayout>(R.id.productAddRemoveLayoutProductDetail)


            println("0000 ${ProductListFragment.selectedProduct.value}")
            if (ProductListFragment.selectedProduct.value != null) {
                productDetailViewModel.getCartForSpecificProduct(MainActivity.cartId,ProductListFragment.selectedProduct.value!!.productId.toInt())

                productDetailViewModel.isCartAvailable.observe(viewLifecycleOwner){
                    if(it==null){
                        addRemoveLayout.visibility = View.GONE
                        addProductButton.visibility = View.VISIBLE
                    }
                    else{
                        addRemoveLayout.visibility = View.VISIBLE
                        addProductButton.visibility = View.GONE
                        countOfOneProduct = it.totalItems
                        totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                    }
                }
                addProductButton.setOnClickListener {
                    countOfOneProduct++
                    productDetailViewModel.addProductInCart(Cart(
                                MainActivity.cartId,
                                ProductListFragment.selectedProduct.value!!.productId.toInt(),
                                countOfOneProduct, ProductListFragment.selectedProduct.value!!.price
                            ))
                    totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                    addProductButton.visibility = View.GONE
                    addRemoveLayout.visibility = View.VISIBLE
                }
                addProductImgButton.setOnClickListener {
                    countOfOneProduct++
                    productDetailViewModel.updateProductInCart(Cart(
                        MainActivity.cartId,
                        ProductListFragment.selectedProduct.value!!.productId.toInt(),
                        countOfOneProduct, ProductListFragment.selectedProduct.value!!.price
                    ))
                    totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                }
                removeProductImgButton.setOnClickListener {
                    if (countOfOneProduct > 1) {
                        countOfOneProduct--
                        totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                        productDetailViewModel.updateProductInCart(Cart(
                            MainActivity.cartId,
                            ProductListFragment.selectedProduct.value!!.productId.toInt(),
                            countOfOneProduct, ProductListFragment.selectedProduct.value!!.price
                        ))
                    } else if (countOfOneProduct == 1) {
                        countOfOneProduct--
                        productDetailViewModel.removeProductInCart(Cart(
                            MainActivity.cartId,
                            ProductListFragment.selectedProduct.value!!.productId.toInt(),
                            countOfOneProduct, ProductListFragment.selectedProduct.value!!.price
                        ))
                        addRemoveLayout.visibility = View.GONE
                        addProductButton.visibility = View.VISIBLE
                    }
                }
            }
        }
//        val recyclerView = view.findViewById<RecyclerView>(R.id.productListInProductDetailFragment)
//        val adapter = ProductListAdapter(this, File(requireContext().filesDir,"AppImages"),"")
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        return view
    }

    override fun onResume() {
        super.onResume()
        InitialFragment.hideBottomNav.value = true
        InitialFragment.hideSearchBar.value = true
    }
    override fun onStop() {
        super.onStop()
        InitialFragment.hideBottomNav.value = false
        InitialFragment.hideSearchBar.value = false
    }
}