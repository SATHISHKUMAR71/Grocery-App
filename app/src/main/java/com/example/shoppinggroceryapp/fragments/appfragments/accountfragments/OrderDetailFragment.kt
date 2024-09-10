package com.example.shoppinggroceryapp.fragments.appfragments.accountfragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.fragments.SetProductImage
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.retailerfragments.RequestDetailFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.CartWithProductData
import com.example.shoppinggroceryapp.viewmodel.accountviewmodel.OrderDetailViewModel
import com.example.shoppinggroceryapp.viewmodel.accountviewmodel.OrderDetailViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.io.File

class OrderDetailFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    private var totalPrice = 0f

    var status:MutableLiveData<String> = MutableLiveData()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_detail, container, false)
        val productsContainer = view.findViewById<LinearLayout>(R.id.orderedProductViews)
        val orderDetailViewModel = ViewModelProvider(this,
            com.example.shoppinggroceryapp.viewmodel.accountviewmodel.OrderDetailViewModelFactory(
                AppDatabase.getAppDatabase(requireContext()).getRetailerDao()
            )
        )[com.example.shoppinggroceryapp.viewmodel.accountviewmodel.OrderDetailViewModel::class.java]

        view.findViewById<TextView>(R.id.productOrderedDate).text = DateGenerator.getDayAndMonth(OrderListFragment.selectedOrder?.orderedDate?:"")
        var deliveryDate = OrderListFragment.selectedOrder?.deliveryDate
        val deliveryText = view.findViewById<TextView>(R.id.productDeliveredDate)
        status.observe(viewLifecycleOwner){
            OrderListFragment.selectedOrder = OrderListFragment.selectedOrder?.copy(deliveryStatus = it)
            view.findViewById<TextView>(R.id.productDeliveredStatus).text = it
            if(OrderListFragment.selectedOrder!!.deliveryStatus=="Delivered"){
                val str = "Delivered on ${DateGenerator.getDayAndMonth(deliveryDate?:DateGenerator.getDeliveryDate())}"
                deliveryText.text = str
            }
            else if((it=="Pending")){
                val str = "Delivery Expected on:  ${DateGenerator.getDayAndMonth(deliveryDate?:DateGenerator.getDeliveryDate())}"
                deliveryText.text = str
            }
            else if(it == "Delayed"){
                val str = "Delivery Expected on:  ${DateGenerator.getDayAndMonth(DateGenerator.getDeliveryDate())}"
                deliveryText.text = str
            }
            else{
                val str = "Delivery Expected on:  ${DateGenerator.getDayAndMonth(DateGenerator.getDeliveryDate())}"
                deliveryText.text = str
            }
            orderDetailViewModel.updateOrderDetails(OrderListFragment.selectedOrder!!)
        }
        if(arguments?.getBoolean("hideToolBar")==true){
            view.findViewById<MaterialToolbar>(R.id.materialToolbarOrderDetail).visibility = View.GONE
        }
        if ((MainActivity.isRetailer)&&(!RequestDetailFragment.open)){
            view.findViewById<MaterialButton>(R.id.updateDeliveryStatus).visibility = View.VISIBLE
        }

        view.findViewById<MaterialButton>(R.id.updateDeliveryStatus).setOnClickListener {
            updateDeliveryStatus()
        }

        view.findViewById<MaterialToolbar>(R.id.materialToolbarOrderDetail).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        val status = DateGenerator.compareDeliveryStatus(DateGenerator.getCurrentDate(),OrderListFragment.selectedOrder?.deliveryDate?:DateGenerator.getCurrentDate())
//        view.findViewById<TextView>(R.id.productDeliveredStatus).text = status


        if(OrderListFragment.selectedOrder!!.deliveryStatus=="Delivered"){
            val str = "Delivered on ${DateGenerator.getDayAndMonth(deliveryDate?:DateGenerator.getDeliveryDate())}"
            deliveryText.text = str
        }
        else if((status=="Pending")){
            val str = "Delivery Expected on:  ${DateGenerator.getDayAndMonth(deliveryDate?:DateGenerator.getDeliveryDate())}"
            deliveryText.text = str
        }
        else if(status == "Delayed"){
            val str = "Delivery Expected on:  ${DateGenerator.getDayAndMonth(DateGenerator.getDeliveryDate())}"
            deliveryText.text = str
        }
        else{
            val str = "Delivery Expected on:  ${DateGenerator.getDayAndMonth(DateGenerator.getDeliveryDate())}"
            deliveryText.text = str
        }



        orderDetailViewModel.getSelectedAddress(OrderListFragment.selectedOrder!!.addressId)
        orderDetailViewModel.selectedAddress.observe(viewLifecycleOwner){ address ->
            view.findViewById<TextView>(R.id.addressOwnerName).text = address.addressContactName
            val addressText = with(address){
                "$buildingName, $streetName, $city, $state, $postalCode"
            }
            view.findViewById<TextView>(R.id.address).text = addressText
            view.findViewById<TextView>(R.id.addressPhone).text = address.addressContactNumber

        }

        view.findViewById<TextView>(R.id.orderIdValue).text = OrderListFragment.selectedOrder?.orderId.toString()
        view.findViewById<TextView>(R.id.productDeliveredStatus).text = OrderListFragment.selectedOrder?.deliveryStatus

        var totalItems = 0
        val productView = (LayoutInflater.from(requireContext()).inflate(R.layout.ordered_product_layout,productsContainer,false))
        for(i in OrderListFragment.correspondingCartList!!){
            addView(productsContainer,i)
            totalItems++
        }
        val totalItemsStr = "MRP ($totalItems Products)"
        view.findViewById<TextView>(R.id.priceDetailsMrpTotalItems).text = totalItemsStr
        val totalPriceStr = "₹$totalPrice"
        view.findViewById<TextView>(R.id.priceDetailsMrpPrice).text = totalPriceStr
        view.findViewById<TextView>(R.id.priceDetailsTotalAmount).text = totalPriceStr
        return view
    }

    private fun updateDeliveryStatus() {
        val status = arrayOf("Delayed","Cancelled","Delivered","Pending","Out For Delivery")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Delivery Status")
            .setSingleChoiceItems(status,-1,DialogInterface.OnClickListener { dialog, which ->
                this.status.value = status[which]
                dialog.dismiss()
            })
            .setNeutralButton("Cancel"){dialog,_ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun addView(container:LinearLayout,productInfo:CartWithProductData){
        val newView =LayoutInflater.from(requireContext()).inflate(R.layout.ordered_product_layout,container,false)
        newView.findViewById<ImageView>(R.id.orderedProductImage)
        SetProductImage.setImageView(newView.findViewById(R.id.orderedProductImage),productInfo.mainImage?:"",
            File(requireContext().filesDir,"AppImages")
        )
        newView.findViewById<TextView>(R.id.orderedProductFullName).text = productInfo.productName
        newView.findViewById<TextView>(R.id.orderedProductQuantity).text = productInfo.productQuantity
        totalPrice += (productInfo.totalItems*productInfo.unitPrice)
        val totalPrice = "₹${(productInfo.totalItems*productInfo.unitPrice)}"
        newView.findViewById<TextView>(R.id.orderedProductTotalPrice).text = totalPrice
        val eachPrice = "₹${(productInfo.unitPrice)}"
        newView.findViewById<TextView>(R.id.orderedEachProductPrice).text = eachPrice
        val str = "(${productInfo.totalItems})"
        newView.findViewById<TextView>(R.id.orderedNoOfProducts).text =str
        container.addView(newView)
    }

    override fun onResume() {
        super.onResume()
        InitialFragment.hideSearchBar.value = true
    }
    override fun onStop() {
        super.onStop()
        InitialFragment.hideSearchBar.value = false
    }
}