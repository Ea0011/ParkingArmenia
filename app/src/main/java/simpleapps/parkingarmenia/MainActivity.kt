package simpleapps.parkingarmenia

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var tabView : TabLayout
    private lateinit var preferences : SharedPreferences

    companion object {
        private lateinit var viewPager: ViewPager
        private val secret: String = "logSec"
        fun getCurrentViewPager(): ViewPager? = viewPager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferences = getSharedPreferences("carid", Context.MODE_PRIVATE)
        tabView = findViewById(R.id.tabView) as TabLayout
        viewPager = findViewById(R.id.viewpager) as ViewPager
        viewPager.adapter = CustomAdapter(supportFragmentManager)
        tabView.setupWithViewPager(viewPager)
        /**
        * Tab Selected Event Handler
         */
        class OnTabSelectedListener : TabLayout.OnTabSelectedListener {
            private fun getFragment(index: Int): Fragment = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + index)

            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewPager.setCurrentItem(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                viewPager.setCurrentItem(tab!!.position)
                when(tab.position) {
                    1 -> {
                        val frag2 : Fragment2 = getFragment(1) as Fragment2
                        frag2.setNumber(preferences.getString("number", ""))
                        frag2.clearFocusForText()
                    }
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.setCurrentItem(tab!!.position)
            }

        }
        tabView.addOnTabSelectedListener(OnTabSelectedListener())
    }
    /**
     Custom Adapter Class
     */
    private class CustomAdapter(fmManager : FragmentManager) : FragmentPagerAdapter(fmManager) {
        private val fragments : List<String> = listOf("Home", "Settings")
        override fun getItem(position: Int): Fragment? = when (position) {
            0 -> Fragment1()
            1 -> Fragment2()
            else -> null
        }

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int): CharSequence = fragments[position]
    }

    override fun onPause() {
        super.onPause()
        Log.i(secret, "activity paused")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
//        TODO  restore timer state
    }

    override fun onStop() {
        super.onStop()
//        TODO save current timer state and current time to shared preferences
    }

    override fun onRestart() {
        super.onRestart()
//        TODO restore timer state using saved time and current time
    }
}