package com.constanthinium.exam

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class TestsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests)
        val pager = findViewById<ViewPager>(R.id.pager)
        pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                return 2
            }

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> TestsFragment()
                    1 -> Fragment()
                    else -> throw IllegalArgumentException("position")
                }
            }

            override fun getPageTitle(position: Int): CharSequence {
                return when (position) {
                    0 -> "Tests"
                    1 -> "Passed Tests"
                    else -> throw IllegalArgumentException("position")
                }
            }
        }
        findViewById<TabLayout>(R.id.tabs).setupWithViewPager(pager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.help -> {
                AlertDialog.Builder(this)
                    .setTitle(item.title)
                    .setMessage("Tap a test to take it, hold to edit, and swipe to delete.")
                    .setPositiveButton("OK", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}