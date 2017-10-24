package simpleapps.parkingarmenia

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
* Created by Edvard Avagyan on 9/11/2017.
*/
class Fragment2 : Fragment() {
    private lateinit var setButton : Button
    private lateinit var setView : View
    private lateinit var setText : EditText
    private lateinit var reset : Button
    private lateinit var preferences : SharedPreferences
    private lateinit var currentNum: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        Log.i("logSec", "Paused activity 2")
    }

    override fun onResume() {
        super.onResume()
        Log.i("logSec", "Resumed activity 2")
    }

    override fun onStop() {
        super.onStop()
    }

    private fun formatText(str: String): String = str.replace("\\s".toRegex(), "").toLowerCase()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        preferences = activity.getSharedPreferences("carid", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = preferences.edit()
        val currentNumber = preferences.getString("number", "")
        setView = inflater!!.inflate(R.layout.frag2, container, false)
        setText = setView.findViewById(R.id.settext) as EditText
        setText.setText(currentNumber)
        setButton = setView.findViewById(R.id.setbutton) as Button
        reset = setView.findViewById(R.id.reset) as Button
        currentNum = setView.findViewById(R.id.currentnum) as TextView
        if (currentNumber != "")
            currentNum.setText(getString(R.string.currentnum) + currentNumber)
        reset.setOnClickListener({_ : View ->
            val AlertDialog : AlertDialog.Builder = AlertDialog.Builder(activity)
            AlertDialog.setTitle(getString(R.string.resetPalette))
                    .setMessage(getString(R.string.suretoreset))
                    .setPositiveButton(getString(R.string.yes), {_ : DialogInterface, _ : Int ->
                        editor.remove("number")
                        editor.apply()
                        setNumber("")
                    })
                    .setNegativeButton(getString(R.string.no), {_ : DialogInterface, _ : Int ->
                        setText.clearFocus()
                    })
                    .setNeutralButton(getString(R.string.neutralbutton), {_ : DialogInterface, _ : Int ->
                        setText.requestFocus()
                    })
                    .show()
        })
        setText.setOnFocusChangeListener { v : View, hasFocus : Boolean ->
            when(hasFocus) {
                false -> {
                    hideSoftKeyboard(v)
                }
            }
        }
        setButton.setOnClickListener({ _: View ->
            val currentText : String = setText.text.toString()
            when(currentText) {
                "" -> {
                    val alertDialog : AlertDialog.Builder = AlertDialog.Builder(activity)
                    alertDialog.setTitle(getString(R.string.oops))
                            .setMessage(getString(R.string.emptyinput))
                            .setPositiveButton(getString(R.string.positivebutton), { _, _ ->
                                setText.requestFocus()
                            })
                            .setNeutralButton(getString(R.string.neutralbutton), { _, _  ->
                                setText.clearFocus()
                            })
                            .show()
                }
                else -> {
                    val formatString : String = formatText(currentText)
                    val palletePatern1 : Pattern = Pattern.compile("^(\\d{2}-?([a-z]{2})-?\\d{3})$")
                    val palletePatern2 : Pattern = Pattern.compile("^(\\d{3}-?([a-z]{2})-?\\d{2})$")
                    val matcher1 : Matcher = palletePatern1.matcher(formatString)
                    val matcher2 : Matcher = palletePatern2.matcher(formatString)
                    if (matcher1.find()) {
                        val current : String = preferences.getString("number", "nonumber")
                        when(current) {
                            "nonumber" -> {
                                Log.i("logSec","Number not found")
                                editor.putString("number", formatString)
                                editor.apply()
                                setNumber(formatString)
                                Toast.makeText(activity, getString(R.string.palettesaved), Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                val AlertDialog : AlertDialog.Builder = AlertDialog.Builder(activity)
                                AlertDialog.setTitle("Change Number")
                                        .setMessage("Do you want to override your current number")
                                        .setPositiveButton(getString(R.string.yes), { _, _ ->
                                            editor.putString("number", formatString)
                                            editor.apply()
                                            setNumber(formatString)
                                            Toast.makeText(activity, getString(R.string.palettesaved), Toast.LENGTH_LONG).show()
                                        })
                                        .setNegativeButton(getString(R.string.no), { _, _ ->
                                            setText.setText(current)
                                            setText.clearFocus()
                                        })
                                        .setNeutralButton(getString(R.string.neutralbutton), {_, _ ->
                                            setText.requestFocus()
                                        })
                                        .show()
                            }
                        }
                    } else if (matcher2.find()) {
                        val alertDialog : AlertDialog.Builder = AlertDialog.Builder(activity)
                        alertDialog.setTitle(getString(R.string.oops))
                                .setMessage("Car palette numbers registered under state organizations аrе not supported")
                                .setPositiveButton(getString(R.string.positivebutton), { _, _ ->
                                    setText.setText("")
                                    setText.requestFocus()
                                })
                                .setNeutralButton(getString(R.string.neutralbutton), { _, _ ->
                                    setText.clearFocus()
                                })
                                .show()
                    } else {
                        val alertDialog : AlertDialog.Builder = AlertDialog.Builder(activity)
                        alertDialog.setTitle(getString(R.string.oops))
                                .setMessage("Looks like your palette number has wrong format.\nMake sure you enter Armenian car palette number format")
                                .setPositiveButton(getString(R.string.positivebutton), { _, _ ->
                                    setText.setText("")
                                    setText.requestFocus()
                                })
                                .setNeutralButton(getString(R.string.neutralbutton), { _, _ ->
                                    setText.clearFocus()
                                })
                                .show()
                    }
                }
            }
        })
        return setView
    }

    @SuppressLint("SetTextI18n")
    fun setNumber(newNum : String) {
        setText.setText(newNum)
        if (newNum != "") {
            currentNum.setText(getString(R.string.currentnum) + newNum)
            Fragment1.setCurrentNum(newNum)
        } else {
            currentNum.text = ""
            Fragment1.setCurrentNum("No Number")
        }
    }

    fun clearFocusForText() {
        setText.clearFocus()
    }

    fun hideSoftKeyboard(view : View) {
        val input : InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        input.hideSoftInputFromWindow(view.windowToken, 0)
    }
}