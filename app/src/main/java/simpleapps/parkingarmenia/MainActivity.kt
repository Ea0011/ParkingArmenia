package simpleapps.parkingarmenia

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private val secret : String = "logSec"
    private lateinit var tabView : TabLayout
    private lateinit var viewPager : ViewPager

    override fun onCreate(savedInstanceState : Bundle?) : Unit {
        super.onCreate(savedInstanceState)
        Log.i(secret, "activity created")
        setContentView(R.layout.activity_main)
        tabView = findViewById(R.id.tabView) as TabLayout
        viewPager = findViewById(R.id.viewpager) as ViewPager
        viewPager.adapter = CustomAdapter(supportFragmentManager)
        tabView.setupWithViewPager(viewPager)
        viewPager.setOnClickListener({_ : View ->
            Log.i(secret, "ViewPager is Clicked on But the Button is not")
        })
        /**
        * Tab Selected Event Handler
         */
        class OnTabSelectedListener : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.i(secret, tab!!.position.toString() + "Reselected")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.i(secret, tab!!.position.toString() + "Unselected")
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.setCurrentItem(tab!!.position)
                Log.i(secret, tab.position.toString() + "Selected")
            }

        }
        tabView.addOnTabSelectedListener(OnTabSelectedListener())
    }
    /**
     Custom Adapter Class
     */
    private class CustomAdapter(fmManager : FragmentManager) : FragmentPagerAdapter(fmManager) {
        private val fragments : List<String> = listOf("Home", "Settings")
        override fun getItem(position: Int): Fragment? {
            when(position) {
                0 -> return Fragment1()
                1 -> return Fragment2()
                else -> return null
            }
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragments[position]
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i(secret, "activity paused")
//        save activity state
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.i(secret, "instance saved")
//        saved state
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i(secret, "instance restored")
//        restored state
    }

    override fun onResume() {
        super.onResume()
        Log.i(secret, "activity paused")
//        activity resumed.. restore state
    }

    override fun onStop() {
        super.onStop()
        Log.i(secret, "activity paused")
//        save activity state
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(secret, "activity paused")
//        restore state
    }
}