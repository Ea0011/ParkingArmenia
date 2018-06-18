package com.parkingarmenia.edvardasus.parkingarmenia

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import data.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), onItemClickListener, OnTopItemChangedListener, OnNewCarAddedListener, OnCarEditListener {

    private enum class PERMISSIONS {
        PERM_SEND_SMS
    }

    private class Receiver(private var CurrentContext: Context) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Toast.makeText(CurrentContext, R.string.sms_sent, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(CurrentContext, R.string.sms_sent_failed, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    class ScheduledTask(private var addressNum: String, private var lambda: (addressNum: String) -> Unit) : TimerTask() {
        override fun run() {
            lambda(addressNum)
        }
    }

    override fun onCarEdited(serial: String, position: Int, newSerial : String, delete : Boolean) {

        if (delete) {
            Cars.getInstance(this).mDb!!.delete(serial)

            //check if no elements left and change to appropriate fragment

            if (Cars.getInstance(this).mDb!!.isEmpty()) {
                mCurrentIndex = Int.MIN_VALUE
                val fm = fragmentManager
                val currentFg = fm.findFragmentById(R.id.carListFrameHolder)

                if (currentFg == null || currentFg is CarListFragment) {
                    fm.beginTransaction().replace(R.id.carListFrameHolder, EmptyCarsListFragment()).commit()
                }
            } else {
                (fragmentManager.findFragmentById(R.id.carListFrameHolder) as CarListFragment).dataChanged()
            }
        } else {
            numberDM.update(position, newSerial)
            (fragmentManager.findFragmentById(R.id.carListFrameHolder) as CarListFragment).dataChanged()
        }

    }

    override fun onNewCarAdded(serial: String) {
        val cars = Cars.getInstance(this).mDb!!
        if (cars.isEmpty()) {
            cars.insert(serial)
            val fm = fragmentManager
            val currentFm = fm.findFragmentById(R.id.carListFrameHolder)

            if(currentFm !is CarListFragment) {
                fm.beginTransaction().replace(R.id.carListFrameHolder, CarListFragment()).commit()
            } else {
                (currentFm).dataChanged()
            }
        } else {
                cars.insert(serial)
                val fm = fragmentManager
                val currentFm = fm.findFragmentById(R.id.carListFrameHolder)

                if(currentFm !is CarListFragment) {
                    fm.beginTransaction().replace(R.id.carListFrameHolder, CarListFragment()).commit()
                } else {
                    (currentFm).dataChanged()
                }
            }

    }

    override fun onTopItemChanged(position: Int, serial: String) {
        // here MainActivity receives the position of the top element
        if (!numberDM.isEmpty()) {
            val c = Cars.getInstance(this).mDb!!.searchPosition(serial)

            c.moveToFirst()

            mCurrentCar = c.getString(c.getColumnIndex(NumbersDataManager.TABLE_ROW_NUMBER))
        }
    }

    override fun onCardClicked(view : View, position: Int, serial: String) {
        val editDialog = EditCarDialog.newInstance(position, serial)
        editDialog.show(fragmentManager, "111")
     }

    private lateinit var mFloatingActionButtonAddCar : FloatingActionButton
    private lateinit var mFloatingActionButtonHour : FloatingActionButton
    private lateinit var mFloatingActionButtonDay : FloatingActionButton
    private lateinit var mFloatingActionMenu : FloatingActionsMenu
    private var mCurrentCar : String? = null
    private var mCurrentIndex: Int = Int.MIN_VALUE
    private val mDelay: Long = (Toast.LENGTH_LONG.toLong() + 2) * 1000
    private lateinit var mCurrentAddressNum: String
    private lateinit var numberDM : NumbersDataManager
    private lateinit var mPreferences: SharedPreferences
    private lateinit var mCurrentLanguage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferences = getSharedPreferences("PARKING_ARMENIA", Context.MODE_PRIVATE)

        mCurrentLanguage = mPreferences.getString(SettingsActivity.LANGUAGE_PREF, SettingsActivity.LOCALE_ARM)

        setLocale(mCurrentLanguage)
        setContentView(R.layout.activity_main)

        mFloatingActionMenu = expandableMenu
        mFloatingActionButtonHour = action_day
        mFloatingActionButtonDay = action_year
        mFloatingActionButtonAddCar = action_add_car
        Cars.getInstance(this) // instantiate Database Manager in Cars with activity context. DOESN'T WORK WITHOUT THIS
        numberDM = Cars.getInstance(this).mDb!!

        val fm = fragmentManager
        val fmTransaction = fm.beginTransaction()

        val currentFragment = fm.findFragmentById(R.id.carListFrameHolder)

        if (currentFragment == null) {
            if (numberDM.isEmpty()) {
                fmTransaction.add(R.id.carListFrameHolder, EmptyCarsListFragment())
            } else {
                fmTransaction.add(R.id.carListFrameHolder, CarListFragment())
            }
        }

        fmTransaction.commit()

        mFloatingActionButtonAddCar.setOnClickListener {
            val newCarDialog = NewCarDialog()
            newCarDialog.show(fragmentManager, "123")
            mFloatingActionMenu.collapse()
        }

        mFloatingActionButtonHour.setOnClickListener {
            val alertDialog : AlertDialog.Builder = AlertDialog.Builder(this)
            mFloatingActionMenu.collapse()

            alertDialog.setMessage(R.string.pay_hour)
                    .setPositiveButton(R.string.positive_button, { _, _ ->
                        if (numberDM.isEmpty()) {
                            val alert: AlertDialog.Builder = AlertDialog.Builder(this)
                            alert.setMessage(R.string.empty_list_warning)
                            alert.setPositiveButton(R.string.yes_button, { _, _ ->
                                val newCarDialog = NewCarDialog()
                                newCarDialog.show(fragmentManager, "123")
                                mFloatingActionMenu.collapse()
                            }).setNeutralButton(R.string.neutral_button, {d : DialogInterface, _ -> d.dismiss()}).show()
                        } else {
                            val snackBar: Snackbar = Snackbar.make(constraintLayout, R.string.sms_sending, Snackbar.LENGTH_LONG)
                            val timer = Timer()
                            timer.schedule(ScheduledTask("1045", this::sendSMS), mDelay)
                            snackBar.setAction(R.string.undo_button, { _ ->
                                timer.cancel()
                            })
                            snackBar.show()
                        }
                    })
                    .setNeutralButton(R.string.neutral_button, { d: DialogInterface, _ -> d.dismiss() })
                    .create()
                    .show()
        }

        mFloatingActionButtonDay.setOnClickListener {
            val alertDialog : AlertDialog.Builder = AlertDialog.Builder(this)
            mFloatingActionMenu.collapse()

            alertDialog.setMessage(R.string.pay_day)
                    .setPositiveButton(R.string.positive_button, { _, _ ->
                        if (numberDM.isEmpty()) {
                            val alert: AlertDialog.Builder = AlertDialog.Builder(this)
                            alert.setMessage(R.string.empty_list_warning)
                            alert.setPositiveButton(R.string.yes_button, { _, _ ->
                                val newCarDialog = NewCarDialog()
                                newCarDialog.show(fragmentManager, "123")
                                mFloatingActionMenu.collapse()
                            }).setNeutralButton(R.string.neutral_button, {d : DialogInterface, _ -> d.dismiss() }).show()
                        } else {
                            val snackBar: Snackbar = Snackbar.make(constraintLayout, R.string.sms_sending, Snackbar.LENGTH_LONG)
                            val timer = Timer()
                            timer.schedule(ScheduledTask("5045", this::sendSMS), mDelay)
                            snackBar.setAction(R.string.undo_button, { _ ->
                                timer.cancel()
                            })
                            snackBar.show()
                        }
                    })
                    .setNeutralButton(R.string.neutral_button, { d: DialogInterface, _ -> d.dismiss() })
                    .create()
                    .show()
            }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSIONS.PERM_SEND_SMS.ordinal -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS(mCurrentAddressNum)
                }
            }

            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun sendSMS(addressNum : String) {
        mCurrentAddressNum = addressNum
        when(numberDM.isEmpty()) {
            true -> {
                val alert: AlertDialog.Builder = AlertDialog.Builder(this)
                alert.setMessage(R.string.empty_list_warning)
                alert.setPositiveButton(R.string.positive_button, { _, _ ->
                    val newCarDialog = NewCarDialog()
                    newCarDialog.show(fragmentManager, "123")
                    mFloatingActionMenu.collapse()
                })
               this.runOnUiThread({alert.show()})
            }

            else -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
                        alert.setMessage(R.string.sms_permission_required)
                                .setPositiveButton(R.string.positive_button, { dialog: DialogInterface, _ ->
                                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSIONS.PERM_SEND_SMS.ordinal)
                                    dialog.dismiss()
                                })
                                .setNegativeButton(R.string.neutral_button, { dialog: DialogInterface, _ ->
                                    dialog.dismiss()
                                })
                                this.runOnUiThread({ alert.create().show() })
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSIONS.PERM_SEND_SMS.ordinal)
                    }
                } else {
                    val number = mCurrentCar
                    val telMgr: TelephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    when (telMgr.simState) {
                        TelephonyManager.SIM_STATE_READY -> {
                            try {
                                val sent = "SMS_SENT"
                                val sentIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, Intent(sent), 0)
                                this.registerReceiver(Receiver(this), IntentFilter(sent))
                                val smsManager: SmsManager = SmsManager.getDefault()
                                smsManager.sendTextMessage(addressNum, null, number, sentIntent, null)
                            } catch (e: Throwable) {
                                this.runOnUiThread({Toast.makeText(this, R.string.sms_sent_failed, Toast.LENGTH_LONG).show()})
                                e.printStackTrace()
                            }
                        }
                        else -> {
                            Toast.makeText(this, R.string.sim_card_error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

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

    override fun onResume() {
        super.onResume()

        val fm = fragmentManager
        val fmTransaction = fm.beginTransaction()

        val currentFragment = fm.findFragmentById(R.id.carListFrameHolder)

        if (currentFragment == null) {
            if (numberDM.isEmpty()) {
                fmTransaction.add(R.id.carListFrameHolder, EmptyCarsListFragment())
            } else {
                fmTransaction.add(R.id.carListFrameHolder, CarListFragment())
            }
        } else if(numberDM.isEmpty() && currentFragment !is EmptyCarsListFragment) {
            fmTransaction.replace(R.id.carListFrameHolder, EmptyCarsListFragment())
        } else if (!numberDM.isEmpty() && currentFragment !is CarListFragment){
            fmTransaction.replace(R.id.carListFrameHolder, CarListFragment())
        }

        fmTransaction.commit()

    }

    override fun onBackPressed() {
        if (mFloatingActionMenu.isExpanded) {
            mFloatingActionMenu.collapse()
        } else {
            super.onBackPressed()
        }
    }

    @Suppress("DEPRECATION")
    private fun setLocale(locale : String) {
        val myLocale = Locale(locale)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)
    }

}
