package com.credit.bridge.ui

import android.annotation.SuppressLint
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

class RootActivity : BaseActivity<ActivityRootBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityRootBinding.inflate(layoutInflater)

    private val tabIcons = listOf(R.drawable.ic_bill, R.drawable.ic_bill, R.drawable.ic_bill)
    private val tabIconsSelected = listOf(R.drawable.ic_bill, R.drawable.ic_bill, R.drawable.ic_bill)
    private val tabTexts = listOf(R.string.title_home, R.string.title_bills, R.string.title_profile)
    private var exitAppTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - exitAppTime > 2000) {
                    exitAppTime = System.currentTimeMillis()
                } else {
                    finish()
                }
            }
        })
    }

    override fun initRes() {
        super.initRes()
        bindViews.viewPager.adapter = RootAdapter(this)
        bindViews.viewPager.isUserInputEnabled = false

        bindViews.tabHome.navItemRoot.setOnClickListener(this)
        bindViews.tabOrder.navItemRoot.setOnClickListener(this)
        bindViews.tabAccount.navItemRoot.setOnClickListener(this)

        initTabItems()
        initTabItemSelected()
        //bindNavClick()
        addPageChangeListener()
    }

    private fun initTabItems() {
        tabItem(bindViews.tabHome.navItemRoot, 0)
        tabItem(bindViews.tabOrder.navItemRoot, 1)
        tabItem(bindViews.tabAccount.navItemRoot, 2)
    }

    private fun initTabItemSelected() {
        tabItemSelected(bindViews.tabHome.navItemRoot, true, 0)
        tabItemSelected(bindViews.tabOrder.navItemRoot, false, 1)
        tabItemSelected(bindViews.tabAccount.navItemRoot, false, 2)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun tabItem(tabItem: LinearLayout, position: Int) {
        val tabIcon = tabItem.findViewById<ImageView>(R.id.nav_item_icon)
        val tabText = tabItem.findViewById<TextView>(R.id.nav_item_text)
        tabIcon.setImageResource(tabIcons[position])
        tabText.setText(tabTexts[position])
        tabItem.background = resources.getDrawable(R.drawable.bottom_nav_item_bg, theme)
    }

    private fun tabItemSelected(tabItem: LinearLayout, isSelected: Boolean, index : Int) {
        tabItem.isSelected = isSelected
        val tabIcon = tabItem.findViewById<ImageView>(R.id.nav_item_icon)
        val tabText = tabItem.findViewById<TextView>(R.id.nav_item_text)
        if (isSelected) {
            tabIcon.setImageResource(tabIconsSelected[index])
            tabText.setTextColor(resources.getColor(android.R.color.white, theme))
            tabText.visibility = View.VISIBLE
        } else {
            tabIcon.setImageResource(tabIcons[index])
            tabText.setTextColor(resources.getColor(android.R.color.darker_gray, theme))
            tabText.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
         when(v?.id) {
            R.id.tabHome -> {
                bindViews.viewPager.currentItem = 0
                tabItemSelected(bindViews.tabHome.navItemRoot, true, 0)
                tabItemSelected(bindViews.tabOrder.navItemRoot, false, 1)
                tabItemSelected(bindViews.tabAccount.navItemRoot, false,2)
            }
            R.id.tabOrder -> {
                bindViews.viewPager.currentItem = 1
                tabItemSelected(bindViews.tabHome.navItemRoot, false, 0)
                tabItemSelected(bindViews.tabOrder.navItemRoot, true, 1)
                tabItemSelected(bindViews.tabAccount.navItemRoot, false, 2)
            }
             R.id.tabAccount -> {
                 bindViews.viewPager.currentItem = 2
                 tabItemSelected(bindViews.tabHome.navItemRoot, false, 0)
                 tabItemSelected(bindViews.tabOrder.navItemRoot, false, 1)
                 tabItemSelected(bindViews.tabAccount.navItemRoot, true, 2)
             }
         }
    }

    /*private fun bindNavClick() {
        bindViews.itemHome.navItemRoot.setOnClickListener {
            bindViews.viewPager.currentItem = 0
            tabItemSelected(bindViews.itemHome.navItemRoot, true, 0)
            tabItemSelected(bindViews.tabOrder.navItemRoot, false, 1)
            tabItemSelected(bindViews.tabAccount.navItemRoot, false,2)
        }
        bindViews.tabOrder.navItemRoot.setOnClickListener {
            bindViews.viewPager.currentItem = 1
            tabItemSelected(bindViews.itemHome.navItemRoot, false, 0)
            tabItemSelected(bindViews.tabOrder.navItemRoot, true, 1)
            tabItemSelected(bindViews.tabAccount.navItemRoot, false, 2)
        }
        bindViews.tabAccount.navItemRoot.setOnClickListener {
            bindViews.viewPager.currentItem = 2
            tabItemSelected(bindViews.itemHome.navItemRoot, false, 0)
            tabItemSelected(bindViews.tabOrder.navItemRoot, false, 1)
            tabItemSelected(bindViews.tabAccount.navItemRoot, true, 2)
        }
    }*/

    private fun addPageChangeListener() {
        bindViews.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(index: Int) {
                super.onPageSelected(index)
                when (index) {
                    0 -> {
                        tabItemSelected(bindViews.tabHome.navItemRoot, true, 0)
                        tabItemSelected(bindViews.tabOrder.navItemRoot, false, 1)
                        tabItemSelected(bindViews.tabAccount.navItemRoot, false,2)
                    }
                    1 -> {
                        tabItemSelected(bindViews.tabHome.navItemRoot, false, 0)
                        tabItemSelected(bindViews.tabOrder.navItemRoot, true, 1)
                        tabItemSelected(bindViews.tabAccount.navItemRoot, false, 2)
                    }
                    2 -> {
                        tabItemSelected(bindViews.tabHome.navItemRoot, false, 0)
                        tabItemSelected(bindViews.tabOrder.navItemRoot, false, 1)
                        tabItemSelected(bindViews.tabAccount.navItemRoot, true, 2)
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
            setNavItemSelected(views.tabOrder.navItemRoot, true, 1)
            setNavItemSelected(views.tabAccount.navItemRoot, false, 2)
        }
    }*/

}