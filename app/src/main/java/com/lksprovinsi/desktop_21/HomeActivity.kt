package com.lksprovinsi.desktop_21

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.lksprovinsi.desktop_21.adapters.CustomFragmentAdapter
import com.lksprovinsi.desktop_21.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    lateinit var binding: ActivityHomeBinding
    private var fragments: List<Fragment> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        binding.homeTl.setOnTabSelectedListener(this)
        binding.homeTl.selectTab(binding.homeTl.getTabAt(0))

        fragments = listOf(HomeFragment(), CartFragment(), HomeFragment(), ItemDetailFragment())
        binding.homeFl.adapter = CustomFragmentAdapter(supportFragmentManager, lifecycle, fragments)

        initTab()
    }

    private fun initTab(){
        val menus = listOf<String>("Home", "Cart(0)", "History")
        binding.homeTl.removeAllTabs()

        for(menu in menus){
            val tab = binding.homeTl.newTab()
            tab.text = menu
            tab.icon = null
            binding.homeTl.addTab(tab)
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {

        when (tab!!.text){
            "Home" -> binding.homeFl.currentItem = 0
            "History" -> binding.homeFl.currentItem = 2
            else -> binding.homeFl.currentItem = 1
        }
    }

    fun openDetail(id: Int){
        (fragments[3] as ItemDetailFragment).itemID = id
        binding.homeFl.currentItem = 3
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }
}