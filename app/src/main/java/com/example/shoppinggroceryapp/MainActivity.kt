package com.example.shoppinggroceryapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.shoppinggroceryapp.fragments.appfragments.InitialFragment
import com.example.shoppinggroceryapp.fragments.authentication.LoginFragment
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.order.CartMapping
import com.example.shoppinggroceryapp.model.entities.products.BrandData
import com.example.shoppinggroceryapp.model.entities.products.Category
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.example.shoppinggroceryapp.model.entities.products.Product


class MainActivity : AppCompatActivity() {

    private lateinit var fragmentTopBarView:FrameLayout
    private lateinit var fragmentBottomBarView:FrameLayout
    companion object{
        val handler = Handler(Looper.getMainLooper())
        private const val REQUEST_CAMERA_PERMISSION = 200
        private val permissions = arrayOf(Manifest.permission.CAMERA)
        var userFirstName = ""
        var userLastName = ""
        var userId = "-1"
        var userEmail = ""
        var userPhone = ""
        var cartId = 0
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERMISSION)

        val pref = getSharedPreferences("freshCart", Context.MODE_PRIVATE)
        val boo = pref.getBoolean("isSigned",false)
        userFirstName = pref.getString("userFirstName","User").toString()
        userLastName = pref.getString("userLastName","User").toString()
        userId = pref.getString("userId","userId").toString()
        userEmail = pref.getString("userEmail","userEmail").toString()
        userPhone = pref.getString("userPhone","userPhone").toString()
        if(boo){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentBody,InitialFragment())
                .commit()
        }
        else{
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentBody,LoginFragment())
                .commit()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100)
        }
        val db = AppDatabase.getAppDatabase(baseContext).getUserDao()
        if(boo) {

            Thread {
                val cart: CartMapping? = db.getCartForUser(userId.toInt())
                if (cart == null) {
                    db.addCartForUser(CartMapping(0, userId = userId.toInt(), "available"))
                    val newCart = db.getCartForUser(userId.toInt())
                    println("Cart is Al not available for user $newCart")
                    cartId = newCart.cartId
                } else {
                    println("Cart is Already available for the user $cart")
                    cartId = cart.cartId
                }
                println("Order Details: ${db.getOrdersForUser(userId.toInt())}")
            }.start()

        }
    }
}