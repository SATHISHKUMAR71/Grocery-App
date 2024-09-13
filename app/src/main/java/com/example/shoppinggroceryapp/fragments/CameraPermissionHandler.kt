package com.example.shoppinggroceryapp.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class CameraPermissionHandler(var fragment:Fragment,var imageHandler: ImageHandler):ImagePermissionHandler{

    private lateinit var requestLauncher: ActivityResultLauncher<String>
    override fun initPermissionResult(){
        requestLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                imageHandler.showAlertDialog()
            }
            else{
                imageHandler.launchOnlyImage()
                Toast.makeText(fragment.context,"Please Allow Camera Permission to take picture from device",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            imageHandler.showAlertDialog()
        }
        else{
            println("ON REQUEST Else")
            requestLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}