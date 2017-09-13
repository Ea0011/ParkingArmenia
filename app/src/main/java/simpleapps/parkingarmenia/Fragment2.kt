package simpleapps.parkingarmenia

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
* Created by Edvard Avagyan on 9/11/2017.
*/
class Fragment2 : Fragment() {
    private lateinit var setButton : Button
    private lateinit var setView : View
    private lateinit var setText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun formatText(str : String) : String {
        return str.replace("\\s".toRegex(), "").toLowerCase()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setView = inflater!!.inflate(R.layout.frag2, container, false)
        setText = setView.findViewById(R.id.settext) as EditText
        setButton = setView.findViewById(R.id.setbutton) as Button
        setButton.setOnClickListener(View.OnClickListener { _ : View ->
            Log.i("logSec", "Workedd")
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
                    val palletePatern1 : Pattern = Pattern.compile("^(\\d{2}-?([a-z]{2})-?\\d{3})$")
                    val palletePatern2 : Pattern = Pattern.compile("^(\\d{3}-?([a-z]{2})-?\\d{2})$")
                    val matcher1 : Matcher = palletePatern1.matcher(formatText(currentText))
                    val matcher2 : Matcher = palletePatern2.matcher(formatText(currentText))
                    if (matcher1.find()) {
                        Log.i("logSec", "MAthsdhajsd for 1st")
                    } else if (matcher2.find()) {
                        Log.i("logSec", "MAtyaghafjsahjdh for 2ns")
                    }
                }
            }
        })
        return setView
    }
}