package com.example.shoppinggroceryapp.fragments.appfragments.productfragments

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
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
import androidx.viewpager2.widget.ViewPager2
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
import com.example.shoppinggroceryapp.model.entities.products.Images
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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File


class ProductDetailFragment : Fragment() {


    private var countOfOneProduct = 0
    private lateinit var imageLoader:ImageLoaderAndGetter
    private lateinit var cartViewModel:CartViewModel
    var once = 0
    companion object{
        var brandData:MutableLiveData<String> = MutableLiveData()
        var selectedProductList = mutableListOf<Product>()
        var productDetailCount:MutableLiveData<Int> = MutableLiveData(0)
    }
    private lateinit var productDetailViewModel:ProductDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        println("@@@@ ON CREATE OF PRODUCT DETAIL")
        super.onCreate(savedInstanceState)
        imageLoader = ImageLoaderAndGetter()
    }

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("@@@@ ON CREATE VIEW OF PRODUCT DETAIL")
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_product_detail, container, false)
        val viewPager = view.findViewById<ViewPager2>(R.id.productImageViewer)
        val productDetailToolBar = view.findViewById<MaterialToolbar>(R.id.productDetailToolbar)
        cartViewModel = CartViewModel(AppDatabase.getAppDatabase(requireContext()).getUserDao())
        val mrpTextView = view.findViewById<TextView>(R.id.productPriceProductDetail)
        val discountedPrice = view.findViewById<TextView>(R.id.discountedPrice)
        productDetailViewModel = ViewModelProvider(this,ProductDetailViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getRetailerDao()))[ProductDetailViewModel::class.java]
        productDetailViewModel.getImagesForProducts(ProductListFragment.selectedProduct.value?.productId?:0)

        productDetailViewModel.imageList.observe(viewLifecycleOwner){
            for(i in it){
                println(i)
            }

        }
        if(MainActivity.isRetailer){
            productDetailToolBar.menu.findItem(R.id.edit).setVisible(true)
            productDetailToolBar.menu.findItem(R.id.delete).setVisible(true)
            productDetailToolBar.menu.findItem(R.id.cart).setVisible(false)
//            view.findViewById<LinearLayout>(R.id.similarProductsLayout).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.exploreBottomLayout).visibility = View.GONE
        }

        else{
            productDetailToolBar.menu.findItem(R.id.edit).setVisible(false)
            productDetailToolBar.menu.findItem(R.id.cart).setVisible(true)
            productDetailToolBar.menu.findItem(R.id.delete).setVisible(false)
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
                R.id.delete -> {
                    if(MainActivity.isRetailer){
                        ProductListFragment.selectedProduct.value?.let {
                            productDetailViewModel.removeProduct(it)
                            parentFragmentManager.popBackStack()
                        }
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
//            productDetailViewModel.getImagesForProducts(it.productId)
        }


        ProductListFragment.selectedProduct.observe(viewLifecycleOwner) { selectedProduct ->
            if(once==0) {
                selectedProductList.add(selectedProduct)
            }
                for(i in selectedProductList){
                    println("#### ${i.productName}")
                }
                println("#### Observer Selected Product Called SELECTED PRODUCT LIST: ${selectedProductList.size} ${selectedProduct.offer} $selectedProduct ")
                productDetailViewModel.getImagesForProducts(selectedProduct.productId)
                val productNameWithQuantity =
                    "${ProductListFragment.selectedProduct.value?.productName} (${ProductListFragment.selectedProduct.value?.productQuantity})"
                view.findViewById<TextView>(R.id.productNameProductDetail).text =
                    productNameWithQuantity
                var price = ""

                if (ProductListFragment.selectedProduct.value?.offer != -1f) {
                    println("DISCOUNT CALCULATED: IN IF")
                    mrpTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    val discountedPriceStr = " MRP ₹${
                        calculateDiscountPrice(
                            ProductListFragment.selectedProduct.value!!.price,
                            ProductListFragment.selectedProduct.value!!.offer
                        )
                    }"
                    discountedPrice.visibility = View.VISIBLE
                    discountedPrice.text = discountedPriceStr
                } else {
                    mrpTextView.paintFlags = 0
                    println("DISCOUNT CALCULATED: IN ELSE")
                    discountedPrice.visibility = View.GONE
                }
                price = "MRP ₹${ProductListFragment.selectedProduct.value?.price}"

                ProductListFragment.selectedProduct.value?.brandId?.let {
                    productDetailViewModel.getBrandName(it)
                }

//            view.findViewById<ImageView>(R.id.productImage).setImageBitmap(
//                imageLoader.getImageInApp(
//                    requireContext(),
//                    ProductListFragment.selectedProduct.value?.mainImage ?: ""
//                )
//            )

                mrpTextView.text = price
                val offerView = view.findViewById<TextView>(R.id.productOffer)
                if (ProductListFragment.selectedProduct.value?.offer == -1.0f) {
                    offerView.visibility = View.GONE
                } else {
                    offerView.visibility = View.VISIBLE
                }
                var offerStr =
                    ProductListFragment.selectedProduct.value?.offer?.toInt().toString() + "% Off"
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
                    productDetailViewModel.getCartForSpecificProduct(
                        MainActivity.cartId,
                        ProductListFragment.selectedProduct.value!!.productId.toInt()
                    )

                    productDetailViewModel.isCartAvailable.observe(viewLifecycleOwner) {
                        if (it == null) {
                            addRemoveLayout.visibility = View.GONE
                            addProductButton.visibility = View.VISIBLE
                        } else {
                            addRemoveLayout.visibility = View.VISIBLE
                            addProductButton.visibility = View.GONE
                            countOfOneProduct = it.totalItems
                            totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                        }
                    }
                    addProductButton.setOnClickListener {
                        countOfOneProduct++
                        productDetailViewModel.addProductInCart(
                            Cart(
                                MainActivity.cartId,
                                ProductListFragment.selectedProduct.value!!.productId.toInt(),
                                countOfOneProduct,
                                calculateDiscountPrice(
                                    ProductListFragment.selectedProduct.value!!.price,
                                    ProductListFragment.selectedProduct.value!!.offer
                                )
                            )
                        )
                        totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                        addProductButton.visibility = View.GONE
                        productDetailCount.value = productDetailCount.value!! + 1
                        resetBadge(badgeDrawable, productDetailToolBar)
                        addRemoveLayout.visibility = View.VISIBLE
                    }
                    addProductImgButton.setOnClickListener {
                        countOfOneProduct++
                        productDetailViewModel.updateProductInCart(
                            Cart(
                                MainActivity.cartId,
                                ProductListFragment.selectedProduct.value!!.productId.toInt(),
                                countOfOneProduct,
                                calculateDiscountPrice(
                                    ProductListFragment.selectedProduct.value!!.price,
                                    ProductListFragment.selectedProduct.value!!.offer
                                )
                            )
                        )
                        totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                    }
                    removeProductImgButton.setOnClickListener {
                        if (countOfOneProduct > 1) {
                            countOfOneProduct--
                            totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                            productDetailViewModel.updateProductInCart(
                                Cart(
                                    MainActivity.cartId,
                                    ProductListFragment.selectedProduct.value!!.productId.toInt(),
                                    countOfOneProduct,
                                    calculateDiscountPrice(
                                        ProductListFragment.selectedProduct.value!!.price,
                                        ProductListFragment.selectedProduct.value!!.offer
                                    )
                                )
                            )
                        } else if (countOfOneProduct == 1) {
                            countOfOneProduct--
                            productDetailViewModel.removeProductInCart(
                                Cart(
                                    MainActivity.cartId,
                                    ProductListFragment.selectedProduct.value!!.productId.toInt(),
                                    countOfOneProduct,
                                    calculateDiscountPrice(
                                        ProductListFragment.selectedProduct.value!!.price,
                                        ProductListFragment.selectedProduct.value!!.offer
                                    )
                                )
                            )
                            productDetailCount.value = productDetailCount.value!! - 1
                            resetBadge(badgeDrawable, productDetailToolBar)
                            addRemoveLayout.visibility = View.GONE
                            addProductButton.visibility = View.VISIBLE
                        }
                    }
                }
                once =1
            productDetailViewModel.getSimilarProduct(selectedProduct.categoryName)
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.productListInProductDetailFragment)

        productDetailViewModel.similarProductsLiveData.observe(viewLifecycleOwner){
            val adapter = ProductListAdapter(this, File(requireContext().filesDir,"AppImages"),"P",true)
            recyclerView.adapter = adapter
            val tmpList = mutableListOf<Product>()
            println("@#@#@ SIMILAR PRODUCT LIST")
            for(i in it.toMutableList()){
                if(i.productId == ProductListFragment.selectedProduct.value?.productId){
                    println("@#@#@ SIMILAR PRODUCT LIST Removed $i")
                    continue
                }
                tmpList.add(i)
                println("@#@#@ SIMILAR PRODUCT LIST Added $i")
            }
            adapter.setProducts(tmpList)
            recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
        productDetailViewModel.imageList.observe(viewLifecycleOwner){
            var imageList:MutableList<String> = mutableListOf()
            println("IMAGE OBSERVER CALLED: $it")
            imageList.add(ProductListFragment.selectedProduct.value?.mainImage?:"")
            for(i in it){
                imageList.add(i.images)
            }
            viewPager.adapter  = ProductViewAdapter(this,imageList,imageLoader)
            TabLayoutMediator(view.findViewById(R.id.imageTabLayout),viewPager){tab,pos ->

            }.attach()
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

    override fun onPause() {
        super.onPause()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("@@@@ ON SAVE INSTANCE OF PRODUCT DETAIL")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

    }
    private fun calculateDiscountPrice(price:Float, offer:Float):Float{
        if(offer!=-1f) {
            return price - (price * (offer / 100))
        }
        else{
            return price
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("@#@#@ VALUEE IS CHANGED")
        var size =selectedProductList.size
        try{
            selectedProductList.removeAt(size-1)
            ProductListFragment.selectedProduct.value = selectedProductList[size-2]
        }
        catch (e:Exception){
            println(e)
        }
    }
}