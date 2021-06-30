package edu.kylegilmartin.shopapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.kylegilmartin.shopapp.R




class SoldProductsFragment : BaseFragment() {


    override fun onCreateView
            (inflater: LayoutInflater, container: ViewGroup?,
             savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sold_products, container, false)
    }


}