package com.example.shoppinggroceryapp.fragments

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object :Migration(1,2){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE recentlyViewedItems (recentlyViewedId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,productId INTEGER NOT NULL)")
    }
}