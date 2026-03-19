package com.credit.bridge.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.credit.bridge.ui.fragments.AccountFragment
import com.credit.bridge.ui.fragments.HomeFragment
import com.credit.bridge.ui.fragments.OrderFragment
import kotlin.collections.set
import kotlin.let

/**
 * author : Jason
 * desc   :
 */
class RootAdapter(
    fragment: FragmentActivity
) : FragmentStateAdapter(fragment) {

    companion object {
        const val PAGE_HOME = 0
        const val PAGE_BILL = 1
        const val PAGE_PROFILE = 2
    }

    private val fragmentCache = mutableMapOf<Int, Fragment>()

    override fun createFragment(position: Int): Fragment {
        fragmentCache[position]?.let { return it }
        val fragment = when (position) {
            PAGE_HOME -> HomeFragment.newInstance()
            PAGE_BILL -> OrderFragment.newInstance()
            PAGE_PROFILE -> AccountFragment.newInstance()
            else -> throw kotlin.IllegalArgumentException("Invalid position $position")
        }
        fragmentCache[position] = fragment
        return fragment
    }

    override fun getItemCount(): Int = 3

    /**
     * get Fragment
     */
    fun getFragment(position: Int): Fragment? {
        return fragmentCache[position]
    }
}