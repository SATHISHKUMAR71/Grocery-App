package com.example.shoppinggroceryapp.fragments.appfragments.accountfragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.TimeSlots
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

    var days = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    private var totalPrice = 0f

    var status:MutableLiveData<String> = MutableLiveData()
    @RequiresApi(Build.VERSION_CODES.O)
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
        val deliveryFrequency = view.findViewById<TextView>(R.id.productDeliveryFrequency)

        view.findViewById<TextView>(R.id.productOrderedDate).text = DateGenerator.getDayAndMonth(OrderListFragment.selectedOrder?.orderedDate?:DateGenerator.getCurrentDate())
        var deliveryDate = OrderListFragment.selectedOrder?.deliveryDate
        val deliveryText = view.findViewById<TextView>(R.id.productDeliveredDate)
        val deleteSubscription = view.findViewById<MaterialButton>(R.id.deleteSubscriptionOrder)
        val deliveryTimeSlot = view.findViewById<TextView>(R.id.productNextDeliveryTimeSlot)
        val nextDeliveryDate = view.findViewById<TextView>(R.id.productNextDeliveryDate)
        var hideCancelOrderButton = arguments?.getBoolean("hideCancelOrderButton")
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
//        if ((MainActivity.isRetailer)&&(!RequestDetailFragment.open)){
//            view.findViewById<MaterialButton>(R.id.updateDeliveryStatus).visibility = View.VISIBLE
//        }
//
//        view.findViewById<MaterialButton>(R.id.updateDeliveryStatus).setOnClickListener {
//            updateDeliveryStatus()
//        }

        view.findViewById<MaterialToolbar>(R.id.materialToolbarOrderDetail).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

//        view.findViewById<TextView>(R.id.productDeliveredStatus).text = status


        println("STATUS OF ORDER: ${OrderListFragment.selectedOrder!!.deliveryStatus}")
        if(OrderListFragment.selectedOrder!!.deliveryStatus=="Delivered"){
            val str = "Delivered on ${DateGenerator.getDayAndMonth(deliveryDate?:DateGenerator.getDeliveryDate())}"
            deliveryText.text = str
        }
        else if((OrderListFragment.selectedOrder!!.deliveryStatus=="Pending")){
            val str = "Delivery Expected on:  ${DateGenerator.getDayAndMonth(deliveryDate?:DateGenerator.getDeliveryDate())}"
            deliveryText.text = str
        }
        else if(OrderListFragment.selectedOrder!!.deliveryStatus == "Delayed"){
            val str = "Delivery Expected on:  ${DateGenerator.getDayAndMonth(DateGenerator.getDeliveryDate())}"
            deliveryText.text = str
        }
        else if(OrderListFragment.selectedOrder!!.deliveryStatus== "Cancelled"){
            deliveryText.visibility = View.GONE
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
        deliveryFrequency.text = OrderListFragment.selectedOrder?.deliveryFrequency

        var alertTitle = ""
        var alertMessage = ""
        if(OrderListFragment.selectedOrder?.deliveryFrequency!="Once"){
            deleteSubscription.text = "Stop Subscription"
            alertTitle = "Stop Subscription!!"
            alertMessage = "Are you Sure to Stop the Subscription?"
            nextDeliveryDate.visibility = View.VISIBLE
            deliveryTimeSlot.visibility = View.VISIBLE
            deliveryText.visibility =View.GONE
            OrderListFragment.selectedOrder?.let {
                orderDetailViewModel.getTimeSlot(it.orderId)
            }
            if(OrderListFragment.selectedOrder?.deliveryFrequency=="Daily"){
                var text = "Next Delivery on ${DateGenerator.getDayAndMonth(DateGenerator.getDeliveryDate())}"
                nextDeliveryDate.text = text
            }
            when(OrderListFragment.selectedOrder?.deliveryFrequency){
                "Monthly Once" -> {
                    OrderListFragment.selectedOrder?.let {
                        orderDetailViewModel.getMonthlySubscriptionDate(it.orderId)
                    }
                }
                "Weekly Once" -> {
                    OrderListFragment.selectedOrder?.let {
                        orderDetailViewModel.getWeeklySubscriptionDate(it.orderId)
                    }
                }
            }
        }
        else{
            alertTitle = "Cancel Order!!"
            alertMessage = "Are you Sure to Cancel the Order?"
            deleteSubscription.text = "Cancel Order"
            nextDeliveryDate.visibility = View.GONE
            deliveryTimeSlot.visibility = View.GONE
//            deliveryText.visibility =View.VISIBLE
            OrderListFragment.selectedOrder?.let {
                if(it.deliveryFrequency=="Cancelled"){
                    deliveryText.visibility = View.GONE
                }
            }
        }
        orderDetailViewModel.timeSlot.observe(viewLifecycleOwner){
            var text = ""
            when(it){
                0 -> text = TimeSlots.EARLY_MORNING.timeDetails
                1 -> text = TimeSlots.MID_MORNING.timeDetails
                2 -> text = TimeSlots.AFTERNOON.timeDetails
                3 -> text = TimeSlots.EVENING.timeDetails
            }
            text = "Time Slot: $text"
            deliveryTimeSlot.text = text
        }
        orderDetailViewModel.date.observe(viewLifecycleOwner){
            var text = "Next Delivery on "
            if(OrderListFragment.selectedOrder?.deliveryFrequency=="Weekly Once"){
                text = "Next Delivery this "
                text += days[it]
            }
            else if(OrderListFragment.selectedOrder?.deliveryFrequency=="Monthly Once"){

                var currentDay = DateGenerator.getCurrentDayOfMonth()
                try{
                    if(currentDay.toInt()>=it){
                        text = "Next Delivery on ${DateGenerator.getDayAndMonth(DateGenerator.getNextMonth().substring(0,8)+it)}"
                    }
                    else{
                        text =  "Next Delivery on ${DateGenerator.getDayAndMonth(DateGenerator.getCurrentDate().substring(0,8)+it)}"
                    }
                }
                catch (e:Exception){
                    println("EXCEPTION IN CONVERTING INT Order Details")
                }
//                text += "$it"
            }
            nextDeliveryDate.text = text

        }

        var totalItems = 0
        val productView = (LayoutInflater.from(requireContext()).inflate(R.layout.ordered_product_layout,productsContainer,false))
        println("CORRESPONDING CART LIST FUll: ${OrderListFragment.correspondingCartList}")
        for(i in OrderListFragment.correspondingCartList!!){
            println("CORRESPONDING CART LIST: $i")
            addView(productsContainer,i)
            totalItems++
        }
        val totalItemsStr = "MRP ($totalItems Products)"
        view.findViewById<TextView>(R.id.priceDetailsMrpTotalItems).text = totalItemsStr
        val totalPriceStr = "₹${totalPrice}"
        val grandTotal = "₹${totalPrice+49}"
        view.findViewById<TextView>(R.id.priceDetailsMrpPrice).text = totalPriceStr
        view.findViewById<TextView>(R.id.priceDetailsTotalAmount).text = grandTotal
        deleteSubscription.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(alertTitle)
                .setMessage(alertMessage)
                .setPositiveButton("Yes"){dialog,_->
                    dialog.dismiss()
                    OrderListFragment.selectedOrder?.let {
                        orderDetailViewModel.updateOrderDetails(it.copy(deliveryFrequency = "Once", deliveryStatus = "Cancelled"))
                    }
                    parentFragmentManager.popBackStack()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        if((MainActivity.isRetailer) || (OrderListFragment.selectedOrder?.deliveryStatus=="Cancelled") || (hideCancelOrderButton==true)){
            deleteSubscription.visibility = View.GONE
        }
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
        val eachPriceText = newView.findViewById<TextView>(R.id.orderedEachProductPrice)
        newView.findViewById<TextView>(R.id.orderedProductFullName).text = productInfo.productName
        newView.findViewById<TextView>(R.id.orderedProductQuantity).text = productInfo.productQuantity
        newView.findViewById<TextView>(R.id.orderedProductBrandName).text = productInfo.brandName
        totalPrice += (productInfo.totalItems*productInfo.unitPrice)
        val totalPrice = "₹${(productInfo.totalItems*productInfo.unitPrice)}"
        newView.findViewById<TextView>(R.id.orderedProductTotalPrice).text = totalPrice
        val eachPrice = "₹${(productInfo.unitPrice)}"
        eachPriceText.text = eachPrice
        val str = "(${productInfo.totalItems})"
        newView.findViewById<TextView>(R.id.orderedNoOfProducts).text =str
        if(productInfo.totalItems==1){
            newView.findViewById<TextView>(R.id.eachTextViewOrderDetail).visibility = View.INVISIBLE
            eachPriceText.visibility = View.INVISIBLE
        }
        container.addView(newView)
    }

    override fun onResume() {
        super.onResume()
        InitialFragment.hideSearchBar.value = true
        InitialFragment.hideBottomNav.value = true
    }
    override fun onStop() {
        super.onStop()
        InitialFragment.hideSearchBar.value = false
        InitialFragment.hideBottomNav.value = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("!@#@! ON SAVE INSTANCE CALLED ON ORDER Details FRAGMENT")
    }
}