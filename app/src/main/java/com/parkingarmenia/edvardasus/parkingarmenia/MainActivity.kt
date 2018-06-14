package com.parkingarmenia.edvardasus.parkingarmenia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.getbase.floatingactionbutton.FloatingActionButton
import data.*

class MainActivity : AppCompatActivity(), onItemClickListener, onTopItemChangedListener, onNewCarAddedListener {
    override fun onNewCarAdded(serial: String) {

        Cars.getInstance(this).mCars.add(Car(serial))
        Cars.getInstance(this).save()

        val fm = fragmentManager
        val currentFm = fm.findFragmentById(R.id.carListFrameHolder)

        if(currentFm !is CarListFragment) {
            fm.beginTransaction().replace(R.id.carListFrameHolder, CarListFragment()).commit()
        } else {
            (currentFm).dataChanged()
        }
    }

    override fun onTopItemChanged(position: Int) {
        // here MainActivity receives the position of the top element
        if (position != -1 && Cars.getInstance(this).mCars.size != 0) {
            mCurrentCar = Cars.getInstance(this).mCars[position]
            mCurrentIndex = position
            Log.e(MainActivity.MY_TAG, mCurrentCar.mSerial)
        }
    }

    override fun onCardClicked(view : View, position: Int) {
        Cars.getInstance(this).mCars.removeAt(position)
        (fragmentManager.findFragmentById(R.id.carListFrameHolder) as CarListFragment).dataChanged()
    }

    private lateinit var mFloatingActionButtonAddCar : FloatingActionButton
    private lateinit var mFloatingActionButtonDay : FloatingActionButton
    private lateinit var mFloatingActionButtonYear : FloatingActionButton
    private lateinit var mFloatingActionMenu : FloatingActionsMenu
    private lateinit var mCurrentCar : Car
    private var mCurrentIndex: Int = Int.MIN_VALUE

    companion object {
        const val MY_TAG : String = "simple tag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFloatingActionMenu = findViewById(R.id.expandableMenu)
        mFloatingActionButtonDay = findViewById(R.id.action_day)
        mFloatingActionButtonYear = findViewById(R.id.action_year)
        mFloatingActionButtonAddCar = findViewById(R.id.action_add_car)
        Cars.getInstance(this) // instantiate JSONSerializer in Cars with activity context. DOESN'T WORK WITHOUT THIS

        val fm = fragmentManager
        val fmTransaction = fm.beginTransaction()

        val currentFragment = fm.findFragmentById(R.id.carListFrameHolder)

        if (currentFragment == null) {
            if (Cars.getInstance(this).mCars.size == 0) {
                fmTransaction.add(R.id.carListFrameHolder, EmptyCarsListFragment())
            } else {
                fmTransaction.add(R.id.carListFrameHolder, CarListFragment())
            }
        }

        fmTransaction.commit()

        mFloatingActionButtonAddCar.setOnClickListener {
            val newCarDialog : NewCarDialog = NewCarDialog()
            newCarDialog.show(fragmentManager, "123")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        //save all the cars before quitting
        super.onPause()
        Log.e(MY_TAG, "cars list saved")
        Cars.getInstance(this).save()
    }

    override fun onResume() {
        super.onResume()

        val fm = fragmentManager
        val fmTransaction = fm.beginTransaction()

        val currentFragment = fm.findFragmentById(R.id.carListFrameHolder)

        if (currentFragment == null) {
            if (Cars.getInstance(this).mCars.size == 0) {
                fmTransaction.add(R.id.carListFrameHolder, EmptyCarsListFragment())
            } else {
                fmTransaction.add(R.id.carListFrameHolder, CarListFragment())
            }
        } else if(Cars.getInstance(this).mCars.size == 0 && currentFragment !is EmptyCarsListFragment) {
            fmTransaction.replace(R.id.carListFrameHolder, EmptyCarsListFragment())
        } else if (Cars.getInstance(this).mCars.size != 0 && currentFragment !is CarListFragment){
            fmTransaction.replace(R.id.carListFrameHolder, CarListFragment())
        }
        fmTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        Cars.getInstance(this).save()
    }

    override fun onBackPressed() {
        if (mFloatingActionMenu.isExpanded) {
            mFloatingActionMenu.collapse()
        }
    }

}
