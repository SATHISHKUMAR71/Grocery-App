package com.example.shoppinggroceryapp.fragments.appfragments.productfragments

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.ImageLoaderAndGetter
import com.example.shoppinggroceryapp.fragments.appfragments.CartFragment
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.fragments.retailerfragments.inventoryfragments.AddEditFragment
import com.example.shoppinggroceryapp.viewmodel.retailerviewmodel.inventoryviewmodel.AddEditViewModel
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.Cart
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.example.shoppinggroceryapp.viewmodel.cartviewmodel.CartViewModel
import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductDetailViewModel
import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductDetailViewModelFactory
import com.example.shoppinggroceryapp.viewmodel.productviewmodel.ProductListViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.File


class ProductDetailFragment : Fragment() {


    private var countOfOneProduct = 0
    private lateinit var imageLoader:ImageLoaderAndGetter
    private lateinit var cartViewModel:CartViewModel
    companion object{
        var brandData:MutableLiveData<String> = MutableLiveData()
        var productDetailCount:MutableLiveData<Int> = MutableLiveData(0)
    }
    private lateinit var productDetailViewModel:ProductDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoader = ImageLoaderAndGetter()
    }

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_product_detail, container, false)
        val productDetailToolBar = view.findViewById<MaterialToolbar>(R.id.productDetailToolbar)
        cartViewModel = CartViewModel(AppDatabase.getAppDatabase(requireContext()).getUserDao())
        val mrpTextView = view.findViewById<TextView>(R.id.productPriceProductDetail)
        val discountedPrice = view.findViewById<TextView>(R.id.discountedPrice)
        productDetailViewModel = ViewModelProvider(this,ProductDetailViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getRetailerDao()))[ProductDetailViewModel::class.java]
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
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,CartFragment(),"Cart In Product Detail")
                    }
                }
                R.id.edit -> {
                    if (MainActivity.isRetailer) {
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,AddEditFragment(),"Edit in Product Detail")
                    }
                }
            }
            true
        }
        var badgeDrawable = BadgeDrawable.create(requireContext())



        view.findViewById<MaterialButton>(R.id.categoryButton).setOnClickListener {
            FragmentTransaction.navigateWithBackstack(parentFragmentManager,CategoryFragment(),"Category Opened From Product Detail")
        }

        cartViewModel.getProductsByCartId(MainActivity.cartId)
        cartViewModel.cartProducts.observe(viewLifecycleOwner){
            productDetailCount.value = it.size
            var noOfItemsInt = productDetailCount.value
            if(noOfItemsInt==0){
                badgeDrawable.isVisible = false
            }
            else{
                badgeDrawable.isVisible = true
                badgeDrawable.text = noOfItemsInt.toString()
            }
            BadgeUtils.attachBadgeDrawable(badgeDrawable,productDetailToolBar,R.id.cart)
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

        ProductListFragment.selectedProduct.value?.let {
            productDetailViewModel.addInRecentlyViewedItems(it.productId)
        }

        ProductListFragment.selectedProduct.observe(viewLifecycleOwner) {
            val productNameWithQuantity =
                "${ProductListFragment.selectedProduct.value?.productName} (${ProductListFragment.selectedProduct.value?.productQuantity})"
            view.findViewById<TextView>(R.id.productNameProductDetail).text =
                productNameWithQuantity
            var price =""
            if(ProductListFragment.selectedProduct.value?.offer!=-1f){
                mrpTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                val discountedPriceStr = " MRP ₹${calculateDiscountPrice(ProductListFragment.selectedProduct.value!!.price,ProductListFragment.selectedProduct.value!!.offer)}"
                discountedPrice.text = discountedPriceStr
            }
            else{
                discountedPrice.visibility = View.GONE
            }
            price = "MRP ₹${ProductListFragment.selectedProduct.value?.price}"

            ProductListFragment.selectedProduct.value?.brandId?.let{
                productDetailViewModel.getBrandName(it)
            }
            view.findViewById<ImageView>(R.id.productImage).setImageBitmap(
                imageLoader.getImageInApp(
                    requireContext(),
                    ProductListFragment.selectedProduct.value?.mainImage ?: ""
                )
            )

            mrpTextView.text = price
            val offerView = view.findViewById<TextView>(R.id.productOffer)
            if (ProductListFragment.selectedProduct.value?.offer == -1.0f) {
                offerView.visibility = View.GONE
            } else {
                offerView.visibility = View.VISIBLE
            }
            var offerStr = ProductListFragment.selectedProduct.value?.offer?.toInt().toString() + "% Off"
            offerView.text = offerStr
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
                                countOfOneProduct, calculateDiscountPrice(ProductListFragment.selectedProduct.value!!.price,ProductListFragment.selectedProduct.value!!.offer)
                            ))
                    totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                    addProductButton.visibility = View.GONE
                    productDetailCount.value = productDetailCount.value!! + 1
                    resetBadge(badgeDrawable, productDetailToolBar)
                    addRemoveLayout.visibility = View.VISIBLE
                }
                addProductImgButton.setOnClickListener {
                    countOfOneProduct++
                    productDetailViewModel.updateProductInCart(Cart(
                        MainActivity.cartId,
                        ProductListFragment.selectedProduct.value!!.productId.toInt(),
                        countOfOneProduct, calculateDiscountPrice(ProductListFragment.selectedProduct.value!!.price,ProductListFragment.selectedProduct.value!!.offer)
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
                            countOfOneProduct, calculateDiscountPrice(ProductListFragment.selectedProduct.value!!.price,ProductListFragment.selectedProduct.value!!.offer)
                        ))
                    } else if (countOfOneProduct == 1) {
                        countOfOneProduct--
                        productDetailViewModel.removeProductInCart(Cart(
                            MainActivity.cartId,
                            ProductListFragment.selectedProduct.value!!.productId.toInt(),
                            countOfOneProduct, calculateDiscountPrice(ProductListFragment.selectedProduct.value!!.price,ProductListFragment.selectedProduct.value!!.offer)
                        ))
                        productDetailCount.value = productDetailCount.value!! - 1
                        resetBadge(badgeDrawable, productDetailToolBar)
                        addRemoveLayout.visibility = View.GONE
                        addProductButton.visibility = View.VISIBLE
                    }
                }
            }
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.productListInProductDetailFragment)
        productDetailViewModel.getSimilarProduct(ProductListFragment.selectedProduct.value?.categoryName?:"")
        productDetailViewModel.similarProductsLiveData.observe(viewLifecycleOwner){
            val adapter = ProductListAdapter(this, File(requireContext().filesDir,"AppImages"),"P",true)
            recyclerView.adapter = adapter
            var tmpList = it.toMutableList()
            for(i in tmpList){
                if(i.productId == ProductListFragment.selectedProduct.value?.productId){
                    tmpList.remove(i)
                    break
                }
            }
            adapter.setProducts(tmpList)
            recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
        productDetailCount.observe(viewLifecycleOwner){
            if(productDetailCount.value==0){
                badgeDrawable.isVisible = false
            }
            else{
                badgeDrawable.isVisible = true
                badgeDrawable.text = productDetailCount.value.toString()
            }
            BadgeUtils.attachBadgeDrawable(badgeDrawable,productDetailToolBar,R.id.cart)
        }
        return view
    }

    @OptIn(ExperimentalBadgeUtils::class)
    private fun resetBadge(badgeDrawable: BadgeDrawable,productDetailToolBar:MaterialToolbar) {

        if(productDetailCount.value==0){
            badgeDrawable.isVisible = false
        }
        else{
            badgeDrawable.isVisible = true
            badgeDrawable.text = productDetailCount.value.toString()
        }
        BadgeUtils.attachBadgeDrawable(badgeDrawable,productDetailToolBar,R.id.cart)
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

    override fun onDestroyView() {
        super.onDestroyView()
        productDetailViewModel.brandName.value = null
        productDetailViewModel.brandName.removeObservers(viewLifecycleOwner)
        productDetailViewModel.isCartAvailable.removeObservers(viewLifecycleOwner)
    }

    private fun calculateDiscountPrice(price:Float, offer:Float):Float{
        if(offer!=-1f) {
            return price - (price * (offer / 100))
        }
        else{
            return price
        }
    }
}