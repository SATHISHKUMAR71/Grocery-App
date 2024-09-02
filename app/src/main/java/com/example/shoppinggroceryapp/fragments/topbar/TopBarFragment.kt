package com.example.shoppinggroceryapp.fragments.topbar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shoppinggroceryapp.R
import com.google.android.material.appbar.MaterialToolbar


class TopBarFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_top_bar, container, false)
        view.findViewById<MaterialToolbar>(R.id.appTopBar).setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
    }

}