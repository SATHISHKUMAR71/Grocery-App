package com.example.shoppinggroceryapp.fragments.appfragments.productfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.CategoryFragment
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.Cart
import com.example.shoppinggroceryapp.model.entities.products.Product
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.File


class ProductDetailFragment : Fragment() {


    private var countOfOneProduct = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_product_detail, container, false)
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
        Thread {
            val brandName = ProductListFragment.selectedProduct.value?.brandId?.let {
                AppDatabase.getAppDatabase(requireContext()).getRetailerDao().getBrandName(
                    it)
            }
            MainActivity.handler.post {
                view.findViewById<TextView>(R.id.brandNameProductDetail).text = brandName
            }
        }.start()
        view.findViewById<MaterialToolbar>(R.id.productDetailToolbar).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        val productNameWithQuantity = "${ProductListFragment.selectedProduct.value?.productName} (${ProductListFragment.selectedProduct.value?.productQuantity})"
        view.findViewById<TextView>(R.id.productNameProductDetail).text = productNameWithQuantity
        val price = "₹${ProductListFragment.selectedProduct.value?.price}"
        view.findViewById<TextView>(R.id.productPriceProductDetail).text =price
        view.findViewById<TextView>(R.id.productOffer).text = ProductListFragment.selectedProduct.value?.offer
        view.findViewById<TextView>(R.id.expiryDateProductDetail).text = ProductListFragment.selectedProduct.value?.expiryDate
        view.findViewById<TextView>(R.id.manufactureDateProductDetail).text = ProductListFragment.selectedProduct.value?.manufactureDate
        val totalItemsAddedProductDetail = view.findViewById<TextView>(R.id.totalItemsAddedProductDetail)
        val addProductButton = view.findViewById<MaterialButton>(R.id.addProductButtonProductDetail)
        val removeProductImgButton = view.findViewById<ImageButton>(R.id.productRemoveSymbolButtonProductDetail)
        val addProductImgButton = view.findViewById<ImageButton>(R.id.productAddSymbolButtonProductDetail)
        val addRemoveLayout = view.findViewById<LinearLayout>(R.id.productAddRemoveLayoutProductDetail)


        if(ProductListFragment.selectedProduct.value!=null) {
            Thread {
                val cartDataForSpecificProduct:Cart? =
                    AppDatabase.getAppDatabase(requireContext()).getUserDao().getSpecificCart(
                        MainActivity.cartId,
                        ProductListFragment.selectedProduct.value!!.productId.toInt()
                    )

                MainActivity.handler.post {
                    if (cartDataForSpecificProduct == null) {
                        addRemoveLayout.visibility = View.GONE
                        addProductButton.visibility = View.VISIBLE
                    } else {

                        addRemoveLayout.visibility = View.VISIBLE
                        addProductButton.visibility = View.GONE
                        countOfOneProduct = cartDataForSpecificProduct.totalItems
                        totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                    }
                }
            }.start()

            addProductButton.setOnClickListener {
                countOfOneProduct++
                Thread{
                    AppDatabase.getAppDatabase(requireContext()).getUserDao().addItemsToCart(Cart(MainActivity.cartId,
                        ProductListFragment.selectedProduct.value!!.productId.toInt(),
                        countOfOneProduct,ProductListFragment.selectedProduct.value!!.price))
                }.start()
                totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                addProductButton.visibility = View.GONE
                addRemoveLayout.visibility = View.VISIBLE
            }
            addProductImgButton.setOnClickListener {
                countOfOneProduct++
                Thread{
                    AppDatabase.getAppDatabase(requireContext()).getUserDao().updateCartItems(Cart(MainActivity.cartId,
                        ProductListFragment.selectedProduct.value!!.productId.toInt(),
                        countOfOneProduct,ProductListFragment.selectedProduct.value!!.price))
                }.start()
                totalItemsAddedProductDetail.text = countOfOneProduct.toString()
            }
            removeProductImgButton.setOnClickListener {
                if(countOfOneProduct>1){
                    countOfOneProduct--
                    totalItemsAddedProductDetail.text = countOfOneProduct.toString()
                    Thread{
                        AppDatabase.getAppDatabase(requireContext()).getUserDao().updateCartItems(Cart(MainActivity.cartId,
                            ProductListFragment.selectedProduct.value!!.productId.toInt(),
                            countOfOneProduct,ProductListFragment.selectedProduct.value!!.price))
                    }.start()
                }
                else if(countOfOneProduct==1){
                    countOfOneProduct--
                    Thread{
                        AppDatabase.getAppDatabase(requireContext()).getUserDao().removeProductInCart(Cart(MainActivity.cartId,
                            ProductListFragment.selectedProduct.value!!.productId.toInt(),
                            countOfOneProduct,ProductListFragment.selectedProduct.value!!.price))
                    }.start()
                    addRemoveLayout.visibility = View.GONE
                    addProductButton.visibility = View.VISIBLE
                }
            }
        }


        val recyclerView = view.findViewById<RecyclerView>(R.id.productListInProductDetailFragment)
        val adapter = ProductListAdapter(this, File(requireContext().filesDir,"AppImages"),"")
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        val list  = mutableListOf<Product>()
        for (i in 0..10){
            list.add(ProductListFragment.selectedProduct.value!!)
        }
        adapter.setProducts(list)
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