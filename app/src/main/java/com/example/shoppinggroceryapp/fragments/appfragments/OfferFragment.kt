package com.example.shoppinggroceryapp.fragments.appfragments

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
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.ProductListAdapter
import com.example.shoppinggroceryapp.fragments.filter.FilterFragment
import com.example.shoppinggroceryapp.fragments.sort.BottomSheetDialog
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.viewmodel.offerviewmodel.OfferViewModel
import com.example.shoppinggroceryapp.model.viewmodel.offerviewmodel.OfferViewModelFactory
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_offer, container, false)
        val offerList = view.findViewById<RecyclerView>(R.id.offerList)
        val fileDir = File(requireContext().filesDir,"AppImages")
        val adapter = ProductListAdapter(this,fileDir,"O")
        val offerViewModel = ViewModelProvider(this,OfferViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[OfferViewModel::class.java]
        println("Filter Fragment ${FilterFragment.list}")
        if(FilterFragment.list!=null){
            adapter.setProducts(FilterFragment.list!!)
            offerList.adapter = adapter
            offerList.layoutManager = LinearLayoutManager(context)
        }
        else {
            offerViewModel.getOfferedProducts()
        }
        offerViewModel.offeredProductList.observe(viewLifecycleOwner){ offeredProductList ->
            if(FilterFragment.list==null){
                println("POSITION CALLED IN ELSE")
                FilterFragment.totalProducts.value = offeredProductList.size
                adapter.setProducts(offeredProductList)
                offerList.adapter = adapter
                offerList.layoutManager = LinearLayoutManager(context)
            }
        }
        val sortButton = view.findViewById<MaterialButton>(R.id.sortButton)
        val filterButton = view.findViewById<MaterialButton>(R.id.filterButton)



        filterButton.setOnClickListener {
//            FilterFragment.totalProducts.value = productList.size
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.fragmentMainLayout, FilterFragment(null))
                .addToBackStack("Filter")
                .commit()
        }

        sortButton.setOnClickListener {
            val bottomSheet = BottomSheetDialog()
            bottomSheet.show(parentFragmentManager,"Bottom Sort Sheet")
        }

        BottomSheetDialog.selectedOption.observe(viewLifecycleOwner){
            println("POSITION CALLED in Dialog")
            when(it){
                0 -> {
                    Thread{
                        val list = AppDatabase.getAppDatabase(requireContext()).getUserDao().getSortedManufacturedLowProductsNoCat().toMutableList()
                        MainActivity.handler.post {
                            offerList.adapter = adapter
                            offerList.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(list)
                        }
                    }.start()
                }
                1 -> {
                    Thread{
                        val list = AppDatabase.getAppDatabase(requireContext()).getUserDao().getSortedManufacturedHighProductsNoCat().toMutableList()
                        MainActivity.handler.post {
                            offerList.adapter = adapter
                            offerList.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(list)
                        }
                    }.start()
                }
                2 -> {
                    Thread{
                        val list = AppDatabase.getAppDatabase(requireContext()).getUserDao().getSortedExpiryLowProductsNoCat().toMutableList()
                        MainActivity.handler.post {
                            offerList.adapter = adapter
                            offerList.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(list)
                        }
                    }.start()
                }
                3 -> {
                    Thread{
                        val list = AppDatabase.getAppDatabase(requireContext()).getUserDao().getSortedExpiryHighProductsNoCat().toMutableList()
                        MainActivity.handler.post {
                            offerList.adapter = adapter
                            offerList.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(list)
                        }
                    }.start()
                }
                4 -> {
                    Thread{
                        val list = AppDatabase.getAppDatabase(requireContext()).getUserDao().getSortedPriceLowProductsNoCat().toMutableList()
                        MainActivity.handler.post {
                            offerList.adapter = adapter
                            offerList.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(list)
                        }
                    }.start()
                }
                5 -> {
                    Thread{
                        val list = AppDatabase.getAppDatabase(requireContext()).getUserDao().getSortedPriceHighProductsNoCat().toMutableList()
                        MainActivity.handler.post {
                            offerList.adapter = adapter
                            offerList.layoutManager = LinearLayoutManager(requireContext())
                            adapter.setProducts(list)
                        }
                    }.start()
                }
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        InitialFragment.hideSearchBar.value = true
    }

    override fun onPause() {
        super.onPause()
        InitialFragment.hideSearchBar.value = false
    }

    override fun onDestroy() {
        super.onDestroy()
        FilterFragment.list = null
        dis50Val = false
        dis40Val = false
        dis30Val = false
        dis20Val = false
        dis10Val = false
    }

}