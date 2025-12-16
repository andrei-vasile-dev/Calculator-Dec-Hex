package com.example.calculatorextins

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter (activity: FragmentActivity) : FragmentStateAdapter(activity){

    override fun getItemCount(): Int = 4 //sunt 4 tab-uri
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Calcul()
            1 -> Istoric()
            2 -> Email()
            3 -> Log()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}