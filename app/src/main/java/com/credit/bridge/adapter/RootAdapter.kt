package com.credit.bridge.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.credit.bridge.ui.fragments.AccountFragment
import com.credit.bridge.ui.fragments.HomeFragment
import com.credit.bridge.ui.fragments.OrderFragment

class RootAdapter(
    fragment: FragmentActivity
) : FragmentStateAdapter(fragment) {

    companion object {
        const val PAGE_HOME = 0
        const val PAGE_BILL = 1
        const val PAGE_PROFILE = 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            PAGE_HOME -> HomeFragment.newInstance()
            PAGE_BILL -> OrderFragment.newInstance()
            PAGE_PROFILE -> AccountFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun getItemCount(): Int = 3
}
