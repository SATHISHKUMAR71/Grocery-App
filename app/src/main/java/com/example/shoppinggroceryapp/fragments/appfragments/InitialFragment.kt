package com.example.shoppinggroceryapp.fragments.appfragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.MainActivity.Companion.isRetailer
import com.example.shoppinggroceryapp.MainActivity.Companion.userEmail
import com.example.shoppinggroceryapp.MainActivity.Companion.userFirstName
import com.example.shoppinggroceryapp.MainActivity.Companion.userId
import com.example.shoppinggroceryapp.MainActivity.Companion.userImage
import com.example.shoppinggroceryapp.MainActivity.Companion.userLastName
import com.example.shoppinggroceryapp.MainActivity.Companion.userPhone
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.AccountFragment
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderListFragment
import com.example.shoppinggroceryapp.fragments.appfragments.productfragments.ProductListFragment
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.SearchViewAdapter
import com.example.shoppinggroceryapp.fragments.retailerfragments.CustomerRequestFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.DealsFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.FAQFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.OrderReceivedFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.inventoryfragments.ProductsFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.help.CustomerRequest
import com.example.shoppinggroceryapp.model.entities.order.Cart
import com.example.shoppinggroceryapp.viewmodel.initialviewmodel.InitialViewModelFactory
import com.example.shoppinggroceryapp.viewmodel.initialviewmodel.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import java.util.Locale


class InitialFragment : Fragment() {

    private lateinit var bottomNav:BottomNavigationView
    private lateinit var searchView:SearchView
    private lateinit var searchBar:SearchBar
    private lateinit var homeFragment: Fragment
    private var clickedFrag = 0
    companion object{
        private var searchString =""
        var hideSearchBar:MutableLiveData<Boolean> = MutableLiveData()
        var hideBottomNav:MutableLiveData<Boolean> = MutableLiveData()
        var closeSearchView:MutableLiveData<Boolean> = MutableLiveData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState!=null){
            println("ON INIT CALLED $savedInstanceState")
        }
        else {
            println("INitial frag created")
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState!=null){
            parentFragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val option =savedInstanceState.getInt("clickedFragment")
            clickedFrag = option
            when(option){
                0 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        HomeFragment(),
                        "Home Retained"
                    )
                }
                1 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        CategoryFragment(),
                        "Category Retained"
                    )
                }
                2 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        OfferFragment(),
                        "Offer Retained"
                    )
                }
                3 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        AccountFragment(),
                        "Account Retained"
                    )
                }
                4 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        CartFragment(),
                        "Cart Retained"
                    )
                }
                5 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        CustomerRequestFragment(),
                        "Customer Request Retained"
                    )
                }
                6 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        ProductsFragment(),
                        "Products Fragment Retained"
                    )
                }
                7 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        DealsFragment(),
                        "Deals Frag Retained"
                    )
                }
                8 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        AccountFragment(),
                        "Account Retained"
                    )
                }
                9 -> {
                    FragmentTransaction.navigateWithBackstack(
                        parentFragmentManager,
                        OrderListFragment(),
                        "Orders Received Retained"
                    )
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_initial, container, false)
        var initialViewModel = ViewModelProvider(this,InitialViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao()))[SearchViewModel::class.java]
        bottomNav = view.findViewById(R.id.bottomNav)
        var launchMicResults = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val micResult = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val textOutput = micResult?.get(0).toString()
                    searchView.show()
                    searchView.editText.setText(textOutput)
                    searchView.editText.setSelection(textOutput.length)
                }
            }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Product Name")
        }
        searchBar = view.findViewById(R.id.searchBar)
        searchView = view.findViewById(R.id.searchView)
        searchView.setupWithSearchBar(searchBar)

        searchBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.searchBarMic ->{
                    launchMicResults.launch(intent)
                }
            }
            true
        }
        homeFragment = HomeFragment()
        val customerRequestFragment = CustomerRequestFragment()
        val pref = requireActivity().getSharedPreferences("freshCart", Context.MODE_PRIVATE)
        userFirstName = pref.getString("userFirstName","User").toString()
        userLastName = pref.getString("userLastName","User").toString()
        userId = pref.getString("userId","userId").toString()
        userEmail = pref.getString("userEmail","userEmail").toString()
        userPhone = pref.getString("userPhone","userPhone").toString()
        isRetailer = pref.getBoolean("isRetailer",false)
        userImage = pref.getString("userProfile","userProfile").toString()
        if(isRetailer){
            bottomNav.menu.clear()
            bottomNav.inflateMenu(R.menu.admin_menu)
            clickedFrag = 5
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentMainLayout,customerRequestFragment)
                .commit()
            parentFragmentManager.registerFragmentLifecycleCallbacks(object :FragmentManager.FragmentLifecycleCallbacks(){
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    when (f){
                        is ProductListFragment -> bottomNav.menu.findItem(R.id.inventory).isChecked = true
                        is DealsFragment -> bottomNav.menu.findItem(R.id.deals).isChecked = true
                        is OrderListFragment -> bottomNav.menu.findItem(R.id.ordersReceived).isChecked = true
                        is CustomerRequestFragment -> bottomNav.menu.findItem(R.id.customerRequest).isChecked = true
                        is AccountFragment -> bottomNav.menu.findItem(R.id.account).isChecked = true
                    }
                }
            },true)
            bottomNav.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.inventory -> {
                        clickedFrag = 6
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,ProductListFragment(),"Products Fragment")
                    }
                    R.id.deals -> {
                        clickedFrag = 7
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,DealsFragment(),"Deals Fragment")
                    }
                    R.id.customerRequest -> {
                        clickedFrag = 5
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,customerRequestFragment,"Customer Request Fragment")
                    }
                    R.id.account-> {
                        clickedFrag = 8
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,AccountFragment(),"Account Fragment")
                    }
                    R.id.ordersReceived -> {
                        clickedFrag = 9
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,OrderListFragment(),"Orders Received Fragment")
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
                        clickedFrag = 3
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,AccountFragment(),"Account Fragment")
                    }
                    R.id.cart -> {
                        clickedFrag = 4
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,CartFragment(),"Cart Fragment")
                    }
                    R.id.homeMenu -> {
                        clickedFrag = 0
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,homeFragment,"Initial Fragment")
                    }
                    R.id.offer -> {
                        clickedFrag = 2
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,OfferFragment(),"Offer Fragment")
                    }
                    R.id.category -> {
                        clickedFrag = 1
                        FragmentTransaction.navigateWithBackstack(parentFragmentManager,CategoryFragment(),"Category Fragment")
                    }
                }
                true
            }
        }

        var searchRecyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
        if(!isRetailer) {
            searchView.editText.addTextChangedListener {
                 if (it?.isNotEmpty() == true) {
                     initialViewModel.performSearch(it.toString())
                 }
                else{
                     initialViewModel.performSearch("-1")
                }
                searchString = it.toString()
            }
        }
        initialViewModel.searchedList.observe(viewLifecycleOwner){ searchList ->
            SearchViewAdapter.searchList = searchList.toMutableList()
            searchRecyclerView.adapter = SearchViewAdapter(this)
            searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("clickedFragment",clickedFrag)
    }
    override fun onDestroy() {
        super.onDestroy()
        println("Initial frag destroyed")
    }
}