package com.example.shoppinggroceryapp.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.TimeZone
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ImageLoaderAndGetter {


    fun storeImageInApp(context: Context, bitMap: Bitmap, fileName:String) {
        val fileDir = File(context.filesDir,"AppImages")
        if(!fileDir.exists()){
            fileDir.mkdirs()
        }
        val bitmapFile = File(fileDir,fileName)
        println(bitmapFile.absolutePath)
        val fileOutputStream = FileOutputStream(bitmapFile)
        bitMap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream)
    }

    fun getImageInApp(context: Context,fileName: String):Bitmap?{
        val fileDir = File(context.filesDir,"AppImages")
        val bitmapFilePath = File(fileDir,fileName)
        try {
            println(bitmapFilePath)
            val fileInputStream = FileInputStream(bitmapFilePath)
            return BitmapFactory.decodeStream(fileInputStream)
        }
        catch (e:Exception){
            println(e)
            return null
        }
    }
}