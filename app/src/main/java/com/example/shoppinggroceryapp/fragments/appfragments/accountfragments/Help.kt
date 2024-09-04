package com.example.shoppinggroceryapp.fragments.appfragments.accountfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.shoppinggroceryapp.MainActivity
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.fragments.DateGenerator
import com.example.shoppinggroceryapp.fragments.appfragments.recyclerview.OrderListAdapter.Companion.cartWithProductList
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.help.CustomerRequest
import com.example.shoppinggroceryapp.model.entities.order.OrderDetails
import com.google.android.material.button.MaterialButton

class Help : Fragment() {

    companion object{
        var selectedOrder:OrderDetails? = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_help, container, false)
        val req = view.findViewById<TextView>(R.id.customerRequestHelpFrag)
        val orderGroup = view.findViewById<LinearLayout>(R.id.orderViewLayout)
        if(selectedOrder==null){
            val orderListFragment = OrderListFragment()
            orderListFragment.arguments = Bundle().apply {
                putBoolean("isClickable",true)
            }
            parentFragmentManager.beginTransaction()
                .addToBackStack("Select the Order")
                .replace(R.id.fragmentMainLayout,orderListFragment)
                .commit()
        }
        else{
            val selectedOrderView = LayoutInflater.from(context).inflate(R.layout.order_layout,
                container,false)
            if(selectedOrder!!.deliveryStatus=="Pending"){
                val screen = "Expected On: ${DateGenerator.getDayAndMonth(selectedOrder!!.deliveryDate)}"
                selectedOrderView.findViewById<TextView>(R.id.deliveryDate).text = screen
            }
            else{
                val screen = "Delivered On: ${DateGenerator.getDayAndMonth(selectedOrder!!.deliveryDate)}"
                selectedOrderView.findViewById<TextView>(R.id.deliveryDate).text = screen
            }
            var productsList = ""
            Thread {
                val cartItems = AppDatabase.getAppDatabase(requireContext()).getUserDao().getProductsWithCartId(
                    selectedOrder!!.cartId)
                for(i in cartItems){
                    productsList += i.productName+" (${i.totalItems}) "
                }
                MainActivity.handler.post {
                    selectedOrderView.findViewById<TextView>(R.id.orderedProductsList).text = productsList
                }
            }.start()
            selectedOrderView.findViewById<ImageView>(R.id.imageView).visibility = View.GONE
            orderGroup.addView(selectedOrderView)
            view.findViewById<MaterialButton>(R.id.sendReqBtn).setOnClickListener {
                if(req.text.toString().isNotEmpty()){
                    Toast.makeText(requireContext(),"Request Sent Successfully",Toast.LENGTH_SHORT).show()
                    println(selectedOrder)
                    var orderId = selectedOrder!!.orderId
                    Thread{
                        AppDatabase.getAppDatabase(requireContext()).getUserDao().addCustomerRequest(
                            CustomerRequest(0,MainActivity.userId.toInt(),DateGenerator.getCurrentDate(),
                                orderId,req.text.toString()))
                    }.start()
                    parentFragmentManager.popBackStack()
                }
                else{
                    Toast.makeText(requireContext(),"Please Write the Request",Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }

    override fun onPause() {
        super.onPause()
        selectedOrder = null
    }
}