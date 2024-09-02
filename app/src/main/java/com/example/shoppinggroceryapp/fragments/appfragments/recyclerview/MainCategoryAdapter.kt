package com.example.shoppinggroceryapp.fragments.appfragments.recyclerview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.shoppinggroceryapp.R
import com.example.shoppinggroceryapp.model.database.AppDatabase
import com.example.shoppinggroceryapp.model.entities.products.ParentCategory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainCategoryAdapter(var fragment: Fragment, private var mainCategoryList: List<ParentCategory>,
                          private var searchBarTop:LinearLayout, private var bottomNav: BottomNavigationView):RecyclerView.Adapter<MainCategoryAdapter.MainCategoryHolder>() {

    private var expandedData = mutableSetOf<Int>()
    inner class MainCategoryHolder(mainCategoryView:View):RecyclerView.ViewHolder(mainCategoryView){
        val invisibleView = itemView.findViewById<RecyclerView>(R.id.subCategoryRecyclerView)
        val addSymbol = itemView.findViewById<ImageView>(R.id.addSymbol)
        val parentCategoryName = itemView.findViewById<TextView>(R.id.parentCategoryName)
        val parentCategoryDescription = itemView.findViewById<TextView>(R.id.parentCategoryDescription)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCategoryHolder {
        return MainCategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_category_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return mainCategoryList.size
    }

    override fun onBindViewHolder(holder: MainCategoryHolder, position: Int) {

        holder.parentCategoryName.text = mainCategoryList[position].parentCategoryName
        holder.parentCategoryDescription.text = mainCategoryList[position].parentCategoryDescription
        refreshViews(fragment.requireContext(),position,holder)

        holder.itemView.setOnClickListener {
            if (holder.adapterPosition == position) {
                if (holder.invisibleView.isVisible) {
//                    Animation for the Visibility GONE
//                    TransitionManager.beginDelayedTransition(holder.invisibleView,AutoTransition())
                    holder.invisibleView.animate()
                        .alpha(0f)
                        .scaleY(0f)
                        .setDuration(100)
                        .withEndAction {
                            expandedData.remove(position)
                            holder.invisibleView.visibility = View.GONE
                            holder.addSymbol.setImageDrawable(
                                ContextCompat.getDrawable(
                                    fragment.requireContext(),
                                    R.drawable.add_control
                                )
                            )
                        }
                } else {
                    val handler = Handler(Looper.getMainLooper())
//                    For The SubCategory Adapter List
                    Thread {
                        val categoryList = AppDatabase.getAppDatabase(fragment.requireContext()).getProductDao()
                            .getChildCategoryList(mainCategoryList[position].parentCategoryName)
                        handler.post {
                            holder.addSymbol.setImageDrawable(
                                ContextCompat.getDrawable(
                                    fragment.requireContext(),
                                    R.drawable.remove_control
                                )
                            )
                            holder.invisibleView.adapter = SubCategoryAdapter(fragment, categoryList,searchBarTop, bottomNav )
                            holder.invisibleView.layoutManager = LinearLayoutManager(fragment.requireContext())
//                            Animation for the View
//                    TransitionManager.beginDelayedTransition(holder.invisibleView,AutoTransition())
                            holder.invisibleView.visibility = View.VISIBLE
                            holder.invisibleView.alpha = 0f
                            holder.invisibleView.scaleY = 0f
                            holder.invisibleView.animate()
                                .alpha(1f)
                                .scaleY(1f)
                                .setDuration(100)
                            expandedData.add(position)
                        }
                    }.start()
                }
            }
        }

    }

    private fun refreshViews(context: Context, position: Int,holder: MainCategoryHolder) {
        if(expandedData.contains(position)){
            holder.invisibleView.visibility = View.VISIBLE
            holder.addSymbol.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.remove_control
                )
            )
            Thread {
                val handler = Handler(Looper.getMainLooper())
                val categoryList = AppDatabase.getAppDatabase(context).getProductDao()
                    .getChildCategoryList(mainCategoryList[position].parentCategoryName)
                handler.post {
                    holder.invisibleView.adapter = SubCategoryAdapter(fragment, categoryList,searchBarTop,bottomNav)
                    holder.invisibleView.layoutManager = LinearLayoutManager(context)
                }
            }.start()

        }
        else{
            holder.addSymbol.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.add_control
                )
            )
            holder.invisibleView.visibility = View.GONE
        }
    }

}