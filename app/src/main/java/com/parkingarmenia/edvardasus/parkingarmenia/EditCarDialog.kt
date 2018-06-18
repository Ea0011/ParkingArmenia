package com.parkingarmenia.edvardasus.parkingarmenia

import android.app.DialogFragment
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import data.Cars
import data.NumbersDataManager
import data.OnCarEditListener
import java.util.regex.Matcher
import java.util.regex.Pattern

class EditCarDialog : DialogFragment() {

    private fun formatText(str: String): String = str.replace("\\s".toRegex(), "").toLowerCase()

    private var mOnCarEditCallBack : OnCarEditListener? = null

    companion object {
        fun newInstance(editPosition : Int, editSerial : String) : EditCarDialog {
            val params = Bundle()
            params.putInt("position", editPosition)
            params.putString("serial", editSerial)

            val fg = EditCarDialog()
            fg.arguments = params

            return fg
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v: View = inflater!!.inflate(R.layout.edit_car, container, false)

        val txtInput: TextInputEditText = v.findViewById(R.id.txtSerialEditText)
        val c = Cars.getInstance(activity).mDb!!.searchPosition(arguments.getString("serial"))
        c.moveToFirst()
        txtInput.setText(c.getString(c.getColumnIndex(NumbersDataManager.TABLE_ROW_NUMBER)).toUpperCase())
        val currentText = txtInput.text.toString()
        val btnDelete: Button = v.findViewById(R.id.btnDeleteEdit)
        val btnCancel: Button = v.findViewById(R.id.btnCancelEdit)
        val btnSave: Button = v.findViewById(R.id.btnSaveEdit)

        btnSave.setOnClickListener {
            val text = formatText(txtInput.text.toString())

            val pattern: Pattern = Pattern.compile("^(\\d{2}-?([a-z]{2})-?\\d{3})$")
            val matcher: Matcher = pattern.matcher(text)

            //if pattern is correct
            if (matcher.find()) {
                val cursor = Cars.getInstance(activity).mDb!!.searchPosition(text)

                if(cursor.moveToFirst() && text.toUpperCase() != currentText.toUpperCase()) {
                    val txtLayout: TextInputLayout = v.findViewById(R.id.txtSerialEdit)
                    txtLayout.error = getString(R.string.already_exists)
                } else {

                    mOnCarEditCallBack = activity as OnCarEditListener
                    mOnCarEditCallBack!!.onCarEdited(arguments.getString("serial"), arguments.getInt("position"), text, false)

                    mOnCarEditCallBack = null
                    dismiss()
                }
            } else {
                val txtLayout: TextInputLayout = v.findViewById(R.id.txtSerialEdit)
                txtLayout.error = getString(R.string.incorrect_format)
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnDelete.setOnClickListener {
            mOnCarEditCallBack = activity as OnCarEditListener
            mOnCarEditCallBack!!.onCarEdited(arguments.getString("serial"), arguments.getInt("position"), "", true)

            mOnCarEditCallBack = null
            dismiss()
        }

        txtInput.setOnFocusChangeListener {_ : View, focus : Boolean ->
            if (!focus) {
                txtInput.hint = getString(R.string.hint_serial)
            } else {
                txtInput.hint = getString(R.string.example_hint)
            }
        }

        return v
    }

    override fun onDetach() {
        super.onDetach()
        mOnCarEditCallBack = null
    }

}