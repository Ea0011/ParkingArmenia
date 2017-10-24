package simpleapps.parkingarmenia

import android.net.Uri
import android.support.v4.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.telephony.SmsManager
import android.widget.Button
import android.widget.TextView
import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import android.widget.Toast
import java.util.*

/**
* Created by Edvard Avagyan on 9/11/2017.
*/
private enum class PERMISSION_CODES {
    PERM_SEND_SMS
}

class Fragment1 : Fragment() {
    private lateinit var hour: Button
    private lateinit var day: Button
    private lateinit var preferences: SharedPreferences
    private lateinit var number: String
    private val delay: Long = (Toast.LENGTH_LONG.toLong() + 2) * 1000

    companion object {
        private lateinit var currentNum: TextView
        fun setCurrentNum(num: String) {
            currentNum.setText(num)
        }
    }

    private class Receiver(private var CurrentContext: Context) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Toast.makeText(CurrentContext, "SMS Sent!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(CurrentContext, "Failed to send SMS", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("logSec", "Fragment 1 Paused")
    }

    override fun onResume() {
        super.onResume()
        Log.i("logSec", "Fragment 1 Resumed")
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mainView: View = inflater!!.inflate(R.layout.frag1, container, false)

        class ScheduledTask(private var addressNum: String, private var lambda: (addressNum: String) -> Unit) : TimerTask() {
            override fun run() {
                lambda(addressNum)
            }
        }
        currentNum = mainView.findViewById(R.id.currentpalette) as TextView
        hour = mainView.findViewById(R.id.hourbutton) as Button
        day = mainView.findViewById(R.id.daybutton) as Button
        preferences = activity.getSharedPreferences("carid", Context.MODE_PRIVATE)
        currentNum.setText(preferences.getString("number", "No Number"))
        hour.setOnClickListener({ _: View ->
            val Alert: AlertDialog.Builder = AlertDialog.Builder(activity)
            Alert.setMessage("Buy a ticket for 1 hour?")
                    .setPositiveButton(getString(R.string.positivebutton), { _, _ ->
                        val snackBar: Snackbar = Snackbar.make(mainView, "SMS is sending", Snackbar.LENGTH_LONG)
                        val timer: Timer = Timer()
                        timer.schedule(ScheduledTask("1045", this::sendSMS), delay)
                        snackBar.setAction("Undo", { _ ->
                            timer.cancel()
                        })
                        snackBar.show()
                    })
                    .setNeutralButton(getString(R.string.neutralbutton), { d: DialogInterface, _ -> d.dismiss() })
                    .create()
                    .show()
            // TODO make notification
        })
        day.setOnClickListener({ _: View ->
            val Alert: AlertDialog.Builder = AlertDialog.Builder(activity)
            Alert.setMessage("Buy a ticket for 1 day?")
                    .setPositiveButton(getString(R.string.positivebutton), { _, _ ->
                        val snackBar: Snackbar = Snackbar.make(mainView, "SMS is sending", Snackbar.LENGTH_LONG)
                        val timer: Timer = Timer()
                        timer.schedule(ScheduledTask("5045", this::sendSMS), delay)
                        snackBar.setAction("Undo", { _ ->
                            timer.cancel()
                        })
                        snackBar.show()
                    })
                    .setNeutralButton(getString(R.string.neutralbutton), { d: DialogInterface, _ -> d.dismiss() })
                    .create()
                    .show()
            // TODO make notification
        })
        return mainView
    }

    private fun sendSMS(addressNum: String) {
        number = preferences.getString("number", "")
        when (number) {
            "" -> {
                val Alert: AlertDialog.Builder = AlertDialog.Builder(activity)
                Alert.setTitle(getString(R.string.oops))
                Alert.setMessage("Looks like you did not specify your car palette number. \n Please head to settings and do so")
                Alert.setPositiveButton(getString(R.string.positivebutton), { _, _ ->
                    MainActivity.getCurrentViewPager()!!.setCurrentItem(1)
                })
                Alert.show()
            }
            else -> {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.SEND_SMS)) {
                        val Alert: AlertDialog.Builder = AlertDialog.Builder(activity)
                        Alert.setMessage("You need to allow access to sms services")
                                .setPositiveButton(getString(R.string.positivebutton), { dialog: DialogInterface, _ ->
                                    ActivityCompat.requestPermissions(activity, arrayOf<String>(Manifest.permission.SEND_SMS), PERMISSION_CODES.PERM_SEND_SMS.ordinal)
                                    dialog.dismiss()
                                })
                                .setNegativeButton(getString(R.string.neutralbutton), { dialog: DialogInterface, _ ->
                                    dialog.dismiss()
                                })
                                .create()
                                .show()
                    } else {
                        ActivityCompat.requestPermissions(activity, arrayOf<String>(Manifest.permission.SEND_SMS), PERMISSION_CODES.PERM_SEND_SMS.ordinal)
                    }
                } else {
                    val telMgr: TelephonyManager = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    when (telMgr.getSimState()) {
                        TelephonyManager.SIM_STATE_READY -> {
                            try {
                                val sent: String = "SMS_SENT"
                                val sentIntent: PendingIntent = PendingIntent.getBroadcast(activity, 0, Intent(sent), 0)
                                activity.registerReceiver(Receiver(activity), IntentFilter(sent))
                                val smsManager: SmsManager = SmsManager.getDefault()
                                smsManager.sendTextMessage(addressNum, null, number, sentIntent, null)
                            } catch (e: Throwable) {
                                Toast.makeText(activity, e.message + "!\n" + "Failed to send SMS", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            }
                        }
                        else -> {
                            Toast.makeText(activity, "Cannot send SMS", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    fun formatTime(text: String) {
        //TODO format text to time in format hh/mm/ss and return it
    }

    fun setTime(newTime: String) {
        //TODO set new time to textview
    }

    fun getCurrentTime() {
        //TODO get current time and return it
    }
}