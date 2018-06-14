package com.parkingarmenia.edvardasus.parkingarmenia

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import data.Car
import data.Cars
import data.onNewCarAddedListener
import kotlinx.android.synthetic.main.activity_add_car.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class NewCarDialog : DialogFragment() {

    private var mOnNewCarAddedCallBack : onNewCarAddedListener? = null

    private fun formatText(str: String): String = str.replace("\\s".toRegex(), "").toLowerCase()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog.setTitle(R.string.add_car)

        val v = inflater!!.inflate(R.layout.activity_add_car, container, false)

        val btnSave : Button = v.findViewById(R.id.btnSave)
        val btnCancel : Button = v.findViewById(R.id.btnCancel)
        val txtSerial : TextInputEditText = v.findViewById(R.id.txtSerialEditText)

        btnSave.setOnClickListener {
            val text = formatText(txtSerial.text.toString())

            val pattern : Pattern = Pattern.compile("^(\\d{2}-?([a-z]{2})-?\\d{3})$")
            val matcher : Matcher = pattern.matcher(text)

            //if pattern is correct
            if (matcher.find()) {
                Log.e(MainActivity.MY_TAG, text)
                Log.e(MainActivity.MY_TAG, " " + Cars.getInstance(activity).mCars.size)
                mOnNewCarAddedCallBack = activity as onNewCarAddedListener
                mOnNewCarAddedCallBack!!.onNewCarAdded(text)

                mOnNewCarAddedCallBack = null
                dismiss()
            } else {
                val txtLayout : TextInputLayout = v.findViewById(R.id.txtSerialInput)
                txtLayout.error = "Incorrect format"
            }
        }

        btnCancel.setOnClickListener {
            mOnNewCarAddedCallBack = null
            dismiss()
        }

        txtSerial.setOnFocusChangeListener {_ : View, focus : Boolean ->
            if (!focus) {
                txtSerial.hint = getString(R.string.hint_serial)
            } else {
                txtSerial.hint = "77LL001"
            }
        }

        return v
    }

    override fun onDetach() {
        super.onDetach()
        mOnNewCarAddedCallBack = null
    }

}