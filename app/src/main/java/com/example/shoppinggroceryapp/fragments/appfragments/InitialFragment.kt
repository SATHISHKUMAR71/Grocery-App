package com.example.shoppinggroceryapp.fragments.appfragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.MainActivity.Companion.cartId
import com.example.shoppinggroceryapp.MainActivity.Companion.isRetailer
import com.example.shoppinggroceryapp.MainActivity.Companion.userEmail
import com.example.shoppinggroceryapp.MainActivity.Companion.userFirstName
import com.example.shoppinggroceryapp.MainActivity.Companion.userId
import com.example.shoppinggroceryapp.MainActivity.Companion.userLastName
import com.example.shoppinggroceryapp.MainActivity.Companion.userPhone
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.AccountFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.SearchViewAdapter
import com.example.shoppinggroceryapp.fragments.retailerfragments.CustomerRequestFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.DealsFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.FAQFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.OrderReceivedFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.ProductsFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.CartMapping
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView


class InitialFragment : Fragment() {

    private lateinit var bottomNav:BottomNavigationView
    private lateinit var searchView:SearchView
    private lateinit var searchBar:SearchBar
    private lateinit var homeFragment: Fragment
    companion object{
        private var searchString =""
        var hideSearchBar:MutableLiveData<Boolean> = MutableLiveData()
        var hideBottomNav:MutableLiveData<Boolean> = MutableLiveData()
        var closeSearchView:MutableLiveData<Boolean> = MutableLiveData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_initial, container, false)
        bottomNav = view.findViewById(R.id.bottomNav)
        searchBar = view.findViewById(R.id.searchBar)
        searchView = view.findViewById(R.id.searchView)
        searchView.setupWithSearchBar(searchBar)
        homeFragment = HomeFragment()
        val customerRequestFragment = CustomerRequestFragment()
        val pref = requireActivity().getSharedPreferences("freshCart", Context.MODE_PRIVATE)
        userFirstName = pref.getString("userFirstName","User").toString()
        userLastName = pref.getString("userLastName","User").toString()
        userId = pref.getString("userId","userId").toString()
        userEmail = pref.getString("userEmail","userEmail").toString()
        userPhone = pref.getString("userPhone","userPhone").toString()
        isRetailer = pref.getBoolean("isRetailer",false)

        if(isRetailer){
            bottomNav.menu.clear()
            bottomNav.inflateMenu(R.menu.admin_menu)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentMainLayout,customerRequestFragment)
                .commit()
            parentFragmentManager.registerFragmentLifecycleCallbacks(object :FragmentManager.FragmentLifecycleCallbacks(){
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    when (f){
                        is ProductsFragment -> bottomNav.menu.findItem(R.id.inventory).isChecked = true
                        is DealsFragment -> bottomNav.menu.findItem(R.id.deals).isChecked = true
                        is OrderReceivedFragment -> bottomNav.menu.findItem(R.id.ordersReceived).isChecked = true
                        is CustomerRequestFragment -> bottomNav.menu.findItem(R.id.customerRequest).isChecked = true
                        is FAQFragment -> bottomNav.menu.findItem(R.id.faq).isChecked = true
                    }
                }
            },true)
            bottomNav.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.inventory -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout, ProductsFragment())
                            .addToBackStack("Products Fragment")
                            .commit()
                    }
                    R.id.deals -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout,DealsFragment())
                            .addToBackStack("Deals Fragment")
                            .commit()
                    }
                    R.id.customerRequest -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout,customerRequestFragment)
                            .addToBackStack("Customer Request Fragment")
                            .commit()
                    }
                    R.id.faq-> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout,FAQFragment())
                            .addToBackStack("FAQ Fragment")
                            .commit()
                    }
                    R.id.ordersReceived -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout,OrderReceivedFragment())
                            .addToBackStack("COrders Received Fragment")
                            .commit()
                    }
                }
                true
            }
        }
        else{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentMainLayout,homeFragment)
                .commit()
            parentFragmentManager.registerFragmentLifecycleCallbacks(object :FragmentManager.FragmentLifecycleCallbacks(){
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    when (f){
                        is AccountFragment -> bottomNav.menu.findItem(R.id.account).isChecked = true
                        is CategoryFragment -> bottomNav.menu.findItem(R.id.category).isChecked = true
                        is HomeFragment -> bottomNav.menu.findItem(R.id.homeMenu).isChecked = true
                        is CartFragment -> bottomNav.menu.findItem(R.id.cart).isChecked = true
                        is OfferFragment -> bottomNav.menu.findItem(R.id.offer).isChecked = true
                    }
                }
            },true)

            bottomNav.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.account -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout, AccountFragment())
                            .addToBackStack("Account Fragment")
                            .commit()
                    }
                    R.id.cart -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout,CartFragment())
                            .addToBackStack("Cart Fragment")
                            .commit()
                    }
                    R.id.homeMenu -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout,homeFragment)
                            .addToBackStack("Initial Fragment")
                            .commit()
                    }
                    R.id.offer -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout,OfferFragment())
                            .addToBackStack("Offer Fragment")
                            .commit()
                    }
                    R.id.category -> {
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.fragmentMainLayout,CategoryFragment())
                            .addToBackStack("Category Fragment")
                            .commit()
                    }
                }
                true
            }
        }

        var searchRecyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
        if(!isRetailer) {
            searchView.editText.addTextChangedListener {
                Thread {
                    var searchList = mutableListOf<String>()
                    if (it?.isNotEmpty() == true) {
                        searchList = AppDatabase.getAppDatabase(requireContext()).getUserDao()
                            .getProductForQuery(it.toString()).toMutableList()
                    }
                    searchString = it.toString()
                    MainActivity.handler.post {
                        println(searchList)
                        SearchViewAdapter.searchList = searchList.toMutableList()
                        searchRecyclerView.adapter = SearchViewAdapter(this)
                        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    }
                }.start()
            }
        }
//        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),object : OnBackPressedCallback(true){
//            override fun handleOnBackPressed() {
//                println("Is Visible: ${this@InitialFragment.isVisible} ${homeFragment.isVisible}")
//                if(searchView.isShowing){
//                    searchView.hide()
//                }
//                else if(!homeFragment.isVisible){
//                    parentFragmentManager.popBackStack()
//                }
//                else{
//                    requireActivity().finish()
//                }
//            }
//        })

        val searchBarTop = view.findViewById<LinearLayout>(R.id.searchBarTop)



        closeSearchView.observe(viewLifecycleOwner){
            if(it){
                searchView.hide()
            }
        }
        hideSearchBar.observe(viewLifecycleOwner){
            if(it){
                searchBarTop.visibility = View.GONE
            }
            else{
                searchBarTop.visibility = View.VISIBLE
            }
        }
        hideBottomNav.observe(viewLifecycleOwner){
            if(it){
                bottomNav.visibility = View.GONE
            }
            else{
                bottomNav.visibility =View.VISIBLE
            }
        }

        bottomNav.setOnItemReselectedListener {
        }

        return view
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}