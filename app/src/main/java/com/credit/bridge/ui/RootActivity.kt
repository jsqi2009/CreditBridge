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
import com.credit.bridge.remote.event.UpdateTabIndexEvent
import com.squareup.otto.Subscribe

class RootActivity : BaseActivity<ActivityRootBinding>(), View.OnClickListener {

    override fun getBinding() = ActivityRootBinding.inflate(layoutInflater)

    private val tabIcons = listOf(R.mipmap.ic_tab_home, R.mipmap.ic_tab_order, R.mipmap.ic_tab_account)
    private val tabIconsSelected = listOf(R.mipmap.ic_tab_home_select, R.mipmap.ic_tab_order_selected, R.mipmap.ic_tab_account_selected)
    private val tabTexts = listOf(R.string.tab_home, R.string.tab_order, R.string.tab_account)
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

        bindViews.tabHome.tabItem.setOnClickListener(this)
        bindViews.tabOrder.tabItem.setOnClickListener(this)
        bindViews.tabAccount.tabItem.setOnClickListener(this)

        initTabItems()
        initTabItemSelected()
        //bindNavClick()
        addPageChangeListener()
    }

    private fun initTabItems() {
        tabItem(bindViews.tabHome.tabItem, 0)
        tabItem(bindViews.tabOrder.tabItem, 1)
        tabItem(bindViews.tabAccount.tabItem, 2)
    }

    private fun initTabItemSelected() {
        tabItemSelected(bindViews.tabHome.tabItem, true, 0)
        tabItemSelected(bindViews.tabOrder.tabItem, false, 1)
        tabItemSelected(bindViews.tabAccount.tabItem, false, 2)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun tabItem(tabItem: LinearLayout, position: Int) {
        val tabIcon = tabItem.findViewById<ImageView>(R.id.item_icon)
        val tabText = tabItem.findViewById<TextView>(R.id.item_text)
        tabIcon.setImageResource(tabIcons[position])
        tabText.setText(tabTexts[position])
    }

    private fun tabItemSelected(tabItem: LinearLayout, isSelected: Boolean, index : Int) {
        tabItem.isSelected = isSelected
        val tabIcon = tabItem.findViewById<ImageView>(R.id.item_icon)
        val tabText = tabItem.findViewById<TextView>(R.id.item_text)
        if (isSelected) {
            tabIcon.setImageResource(tabIconsSelected[index])
            tabText.setTextColor(resources.getColor(R.color.text_gold, theme))
        } else {
            tabIcon.setImageResource(tabIcons[index])
            tabText.setTextColor(resources.getColor(R.color.text_unselected, theme))
        }
    }

    override fun onClick(v: View?) {
         when(v?.id) {
            R.id.tabHome -> {
                bindViews.viewPager.currentItem = 0
                tabItemSelected(bindViews.tabHome.tabItem, true, 0)
                tabItemSelected(bindViews.tabOrder.tabItem, false, 1)
                tabItemSelected(bindViews.tabAccount.tabItem, false,2)
            }
            R.id.tabOrder -> {
                bindViews.viewPager.currentItem = 1
                tabItemSelected(bindViews.tabHome.tabItem, false, 0)
                tabItemSelected(bindViews.tabOrder.tabItem, true, 1)
                tabItemSelected(bindViews.tabAccount.tabItem, false, 2)
            }
             R.id.tabAccount -> {
                 bindViews.viewPager.currentItem = 2
                 tabItemSelected(bindViews.tabHome.tabItem, false, 0)
                 tabItemSelected(bindViews.tabOrder.tabItem, false, 1)
                 tabItemSelected(bindViews.tabAccount.tabItem, true, 2)
             }
         }
    }

    /*private fun bindNavClick() {
        bindViews.itemHome.tabItem.setOnClickListener {
            bindViews.viewPager.currentItem = 0
            tabItemSelected(bindViews.itemHome.tabItem, true, 0)
            tabItemSelected(bindViews.tabOrder.tabItem, false, 1)
            tabItemSelected(bindViews.tabAccount.tabItem, false,2)
        }
        bindViews.tabOrder.tabItem.setOnClickListener {
            bindViews.viewPager.currentItem = 1
            tabItemSelected(bindViews.itemHome.tabItem, false, 0)
            tabItemSelected(bindViews.tabOrder.tabItem, true, 1)
            tabItemSelected(bindViews.tabAccount.tabItem, false, 2)
        }
        bindViews.tabAccount.tabItem.setOnClickListener {
            bindViews.viewPager.currentItem = 2
            tabItemSelected(bindViews.itemHome.tabItem, false, 0)
            tabItemSelected(bindViews.tabOrder.tabItem, false, 1)
            tabItemSelected(bindViews.tabAccount.tabItem, true, 2)
        }
    }*/

    private fun addPageChangeListener() {
        bindViews.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(index: Int) {
                super.onPageSelected(index)
                when (index) {
                    0 -> {
                        tabItemSelected(bindViews.tabHome.tabItem, true, 0)
                        tabItemSelected(bindViews.tabOrder.tabItem, false, 1)
                        tabItemSelected(bindViews.tabAccount.tabItem, false,2)
                    }
                    1 -> {
                        tabItemSelected(bindViews.tabHome.tabItem, false, 0)
                        tabItemSelected(bindViews.tabOrder.tabItem, true, 1)
                        tabItemSelected(bindViews.tabAccount.tabItem, false, 2)
                    }
                    2 -> {
                        tabItemSelected(bindViews.tabHome.tabItem, false, 0)
                        tabItemSelected(bindViews.tabOrder.tabItem, false, 1)
                        tabItemSelected(bindViews.tabAccount.tabItem, true, 2)
                    }
                }
            }
        })
    }

    @Subscribe
    fun onUpdateTabIndexEvent(event: UpdateTabIndexEvent) {
        if (event.pageIndex == 1) {
            bindViews.viewPager.currentItem = 1
            tabItemSelected(bindViews.tabHome.tabItem, false, 0)
            tabItemSelected(bindViews.tabOrder.tabItem, true, 1)
            tabItemSelected(bindViews.tabAccount.tabItem, false, 2)
        } else if (event.pageIndex == 0) {
            bindViews.viewPager.currentItem = 0
            tabItemSelected(bindViews.tabHome.tabItem, true, 0)
            tabItemSelected(bindViews.tabOrder.tabItem, false, 1)
            tabItemSelected(bindViews.tabAccount.tabItem, false, 2)
        }
    }

}