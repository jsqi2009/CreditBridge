package com.credit.bridge.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.credit.bridge.R
import com.credit.bridge.adapter.RootAdapter
import com.credit.bridge.base.BaseActivity
import com.credit.bridge.databinding.ActivityRootBinding
import com.squareup.otto.Subscribe

class RootActivity : BaseActivity<ActivityRootBinding>() {

    override fun getBinding() = ActivityRootBinding.inflate(layoutInflater)

    private val navIcons = listOf(R.drawable.ic_bill, R.drawable.ic_bill, R.drawable.ic_bill)
    private val navIconsSelected = listOf(R.drawable.ic_bill, R.drawable.ic_bill, R.drawable.ic_bill)
    private val navTexts = listOf(R.string.title_home, R.string.title_bills, R.string.title_profile)
    var exitTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    exitTime = System.currentTimeMillis()
                } else {
                    finish()
                }
            }
        })
    }

    override fun initRes() {
        super.initRes()
        bindViews.mainViewPager.adapter = RootAdapter(this)
        bindViews.mainViewPager.isUserInputEnabled = false

        initNavItem(bindViews.itemHome.navItemRoot, 0)
        initNavItem(bindViews.itemBill.navItemRoot, 1)
        initNavItem(bindViews.itemProfile.navItemRoot, 2)

        setNavItemSelected(bindViews.itemHome.navItemRoot, true, 0)
        setNavItemSelected(bindViews.itemBill.navItemRoot, false, 1)
        setNavItemSelected(bindViews.itemProfile.navItemRoot, false, 2)

        bindNavClick()
        bindViewPagerListener()
    }

    private fun initNavItem(item: LinearLayout, position: Int) {
        val icon = item.findViewById<ImageView>(R.id.nav_item_icon)
        val text = item.findViewById<TextView>(R.id.nav_item_text)
        icon.setImageResource(navIcons[position])
        text.setText(navTexts[position])
        item.background = resources.getDrawable(R.drawable.bottom_nav_item_bg, theme)
    }

    private fun setNavItemSelected(item: LinearLayout, isSelected: Boolean, index : Int) {
        item.isSelected = isSelected
        val icon = item.findViewById<ImageView>(R.id.nav_item_icon)
        val text = item.findViewById<TextView>(R.id.nav_item_text)
        if (isSelected) {
            icon.setImageResource(navIconsSelected[index])
            //icon.setColorFilter(resources.getColor(android.R.color.white, theme))
            text.setTextColor(resources.getColor(android.R.color.white, theme))
            text.visibility = View.VISIBLE
        } else {
            icon.setImageResource(navIcons[index])
            //icon.setColorFilter(resources.getColor(android.R.color.darker_gray, theme))
            text.setTextColor(resources.getColor(android.R.color.darker_gray, theme))
            text.visibility = View.GONE
        }
    }

    private fun bindNavClick() {
        bindViews.itemHome.navItemRoot.setOnClickListener {
            bindViews.mainViewPager.currentItem = 0
            setNavItemSelected(bindViews.itemHome.navItemRoot, true, 0)
            setNavItemSelected(bindViews.itemBill.navItemRoot, false, 1)
            setNavItemSelected(bindViews.itemProfile.navItemRoot, false,2)
        }
        bindViews.itemBill.navItemRoot.setOnClickListener {
            bindViews.mainViewPager.currentItem = 1
            setNavItemSelected(bindViews.itemHome.navItemRoot, false, 0)
            setNavItemSelected(bindViews.itemBill.navItemRoot, true, 1)
            setNavItemSelected(bindViews.itemProfile.navItemRoot, false, 2)
        }
        bindViews.itemProfile.navItemRoot.setOnClickListener {
            bindViews.mainViewPager.currentItem = 2
            setNavItemSelected(bindViews.itemHome.navItemRoot, false, 0)
            setNavItemSelected(bindViews.itemBill.navItemRoot, false, 1)
            setNavItemSelected(bindViews.itemProfile.navItemRoot, true, 2)
        }
    }

    private fun bindViewPagerListener() {
        bindViews.mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        setNavItemSelected(bindViews.itemHome.navItemRoot, true, 0)
                        setNavItemSelected(bindViews.itemBill.navItemRoot, false, 1)
                        setNavItemSelected(bindViews.itemProfile.navItemRoot, false,2)
                    }
                    1 -> {
                        setNavItemSelected(bindViews.itemHome.navItemRoot, false, 0)
                        setNavItemSelected(bindViews.itemBill.navItemRoot, true, 1)
                        setNavItemSelected(bindViews.itemProfile.navItemRoot, false, 2)
                    }
                    2 -> {
                        setNavItemSelected(bindViews.itemHome.navItemRoot, false, 0)
                        setNavItemSelected(bindViews.itemBill.navItemRoot, false, 1)
                        setNavItemSelected(bindViews.itemProfile.navItemRoot, true, 2)
                    }
                }
            }
        })
    }


    /*@Subscribe
    fun onSwitchPageEvent(event: SwitchPageEvent) {
        if (event.pageIndex == 1) {
            views.mainViewPager.currentItem = 1
            setNavItemSelected(views.itemHome.navItemRoot, false, 0)
            setNavItemSelected(views.itemBill.navItemRoot, true, 1)
            setNavItemSelected(views.itemProfile.navItemRoot, false, 2)
        }
    }*/

}