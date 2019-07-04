package ru.grv.retrofittest

import android.nfc.Tag
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.view.View
import kotlinx.android.synthetic.main.activity_tab.*
import ru.grv.retrofittest.db.DevFestDatabase

//import kotlinx.android.synthetic.main.toolbar.*


class TabActivity: AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var fragmentAdapter: FragmentPagerAdapter? = null
    private var mDb: DevFestDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        val extras: Bundle = intent.extras

        mDb = DevFestDatabase.invoke(this)

        initToolbar()

        fragmentAdapter = PagerAdapter(supportFragmentManager, extras)
        viewpager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewpager)

        selectPage(0)
    }

    private fun initToolbar() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar?.setNavigationIcon(R.drawable.arrow_left)
        toolbar?.setTitle(R.string.app_name)
        //toolbar?.inflateMenu(R.menu.menu_toolbar)
        toolbar?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun selectPage(pageIndex: Int) {
        tabs?.setScrollPosition(pageIndex, 0f, true)
        viewpager.currentItem = pageIndex
    }
}