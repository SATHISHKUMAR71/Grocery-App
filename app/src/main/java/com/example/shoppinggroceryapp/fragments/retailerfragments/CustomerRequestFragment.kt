package com.example.shoppinggroceryapp.fragments.retailerfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.FragmentTransaction
import com.example.shoppinggroceryapp.fragments.appfragments.accountfragments.OrderListFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.recyclerview.CustomerRequestAdapter
import com.example.shoppinggroceryapp.fragments.retailerfragments.recyclerview.CustomerRequestAdapter.Companion.requestList
import com.example.shoppinggroceryapp.viewmodel.retailerviewmodel.customerrequestviewmodel.CustomerRequestViewModel
import com.example.shoppinggroceryapp.viewmodel.retailerviewmodel.customerrequestviewmodel.CustomerRequestViewModelFactory
import com.example.shoppinggroceryapp.model.database.AppDatabase

class CustomerRequestFragment : Fragment() {

    companion object{
        var customerName:String = ""
        var requestedDate:String = ""
        var customerRequest:String = ""
    }

    private lateinit var customerViewModel: CustomerRequestViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer_request, container, false)
        val customerReqRV = view.findViewById<RecyclerView>(R.id.customerRequestRecyclerView)
        customerViewModel = ViewModelProvider(this,
            CustomerRequestViewModelFactory(AppDatabase.getAppDatabase(requireContext()).getUserDao())
        )[CustomerRequestViewModel::class.java]
        customerViewModel.getCustomerRequest()
        customerViewModel.customerRequestList.observe(viewLifecycleOwner){
            if(customerReqRV.adapter==null) {
                val adapter = CustomerRequestAdapter(customerViewModel, this)
                customerReqRV.adapter = adapter
                customerReqRV.layoutManager = LinearLayoutManager(context)
            }
            requestList = it.toMutableList()
        }

        customerViewModel.selectedOrderLiveData.observe(viewLifecycleOwner){
            if(it!=null) {
                customerViewModel.getCorrespondingCart(it.cartId)
            }
        }
        customerViewModel.correspondingCartLiveData.observe(viewLifecycleOwner){
            if(it!=null) {
                OrderListFragment.correspondingCartList = it
                OrderListFragment.selectedOrder = customerViewModel.selectedOrderLiveData.value
                FragmentTransaction.navigateWithBackstack(parentFragmentManager,RequestDetailFragment(),"Request Detail Fragment")
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        customerViewModel.selectedOrderLiveData.value = null
        customerViewModel.correspondingCartLiveData.value = null
        customerViewModel.correspondingCartLiveData.removeObservers(viewLifecycleOwner)
        customerViewModel.selectedOrderLiveData.removeObservers(viewLifecycleOwner)
        customerViewModel.customerRequestList.removeObservers(viewLifecycleOwner)
    }
}