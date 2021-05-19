package com.android

import android.content.Intent
import android.content.SearchRecentSuggestionsProvider
import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.Fragments.HomeFragment
import com.android.Fragments.NotificationsFragment
import com.android.Fragments.ProfileFragment
import com.android.Fragments.SearchFragment
import com.android.instagram.R

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveToFragment(HomeFragment())

            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
            }
            R.id.nav_add_post -> {
                item.isChecked=false
                startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                moveToFragment(NotificationsFragment())

            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())

            }
        }

        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)
       // textView=findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        //To make home as default page,
        moveToFragment(HomeFragment())

    }
    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans=supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }
}