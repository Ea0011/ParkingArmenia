package com.parkingarmenia.edvardasus.parkingarmenia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.RadioGroup
import data.Cars
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var mPreferences: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor
    private lateinit var mCurrentLocale: String

    companion object {
        const val LOCALE_ARM = "hy"
        const val LOCALE_ENG = "en"
        const val LOCALE_RUS = "ru"

        const val LANGUAGE_PREF = "LANGUAGE"
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mPreferences = getSharedPreferences("PARKING_ARMENIA", Context.MODE_PRIVATE)
        mEditor = mPreferences.edit()

        val btnDelete = findViewById<Button>(R.id.btnDeleteAll)

        btnDelete.setOnClickListener {
            val alertDialog : AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setMessage("Ցանականում ե՞ք ջնջել համարների ողջ ցուցակը")
                    .setPositiveButton(R.string.positive_button, { d : DialogInterface, _ ->
                        Cars.getInstance(this).mDb!!.destroy()
                        d.dismiss()
                    })
                    .setNeutralButton(R.string.neutral_button, { d : DialogInterface, _ -> d.dismiss()})
                    .create()
                    .show()
        }

        val rgLanguage = findViewById<RadioGroup>(R.id.rgLanguage)

        rgLanguage.clearCheck()

        mCurrentLocale = mPreferences.getString(LANGUAGE_PREF, LOCALE_ARM)

        when(mCurrentLocale) {
            LOCALE_ARM -> { rgLanguage.check(R.id.rbArm) }
            LOCALE_RUS -> { rgLanguage.check(R.id.rbRus) }
            LOCALE_ENG -> { rgLanguage.check(R.id.rbEng) }
            else -> {  }
        }

        rgLanguage.setOnCheckedChangeListener { radioGroup , i ->
            if (radioGroup != null && i > -1) {
                when (i) {
                    R.id.rbArm -> {
                        mEditor.putString(LANGUAGE_PREF, LOCALE_ARM)
                        setLocale(LOCALE_ARM)
                    }
                    R.id.rbRus -> {
                        mEditor.putString(LANGUAGE_PREF, LOCALE_RUS)
                        setLocale(LOCALE_RUS)
                    }
                    R.id.rbEng -> {
                        mEditor.putString(LANGUAGE_PREF, LOCALE_ENG)
                        setLocale(LOCALE_ENG)
                    }
                    else -> { setLocale(LOCALE_ARM) }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mEditor.commit()
    }

    @Suppress("DEPRECATION")
    private fun setLocale(locale : String) {
        val myLocale = Locale(locale)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)
        val refresh = Intent(this, SettingsActivity::class.java)
        startActivity(refresh)
        finish()
    }
}
