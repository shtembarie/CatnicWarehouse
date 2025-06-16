package com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PackingTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentList = mutableListOf<Pair<Fragment, Bundle?>>()
    private val fragmentTitleList = mutableListOf<String>()

    fun addFragment(fragment: Fragment, title: String, args: Bundle? = null) {
        fragmentList.add(Pair(fragment, args))
        fragmentTitleList.add(title)
    }

    fun getFragmentTitle(position: Int): String = fragmentTitleList[position]

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        val (fragment, args) = fragmentList[position]
        fragment.arguments = args
        return fragment
    }
}

