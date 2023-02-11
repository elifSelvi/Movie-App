package com.example.obssmovieapp_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

class BaseFragment: Fragment() {

    private val defaultInt = -1
    private var layoutRes: Int = -1
    private var navHostId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // extract arguments from bundle
        arguments?.let {
            layoutRes = it.getInt(KEY_LAYOUT)
            navHostId = it.getInt(KEY_NAV_HOST)

        } ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return if (layoutRes == defaultInt) null
        else inflater.inflate(layoutRes, container, false)

    }

    override fun onStart() {
        super.onStart()
        // return early if no arguments were parsed
        if (navHostId == defaultInt) return
        // find navController using navHostFragment
        val navController = requireActivity().findNavController(navHostId)
        // setup navigation with root destinations and toolbar
        val navView = (activity as MainActivity).binding.bottomNavMenu
        NavigationUI.setupWithNavController(navView,navController)
    }


    companion object {

        private const val KEY_LAYOUT = "layout_key"
        private const val KEY_NAV_HOST = "nav_host_key"

        fun newInstance(layoutRes: Int, navHostId: Int) = BaseFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_LAYOUT, layoutRes)
                putInt(KEY_NAV_HOST, navHostId)
            }
        }
    }
}