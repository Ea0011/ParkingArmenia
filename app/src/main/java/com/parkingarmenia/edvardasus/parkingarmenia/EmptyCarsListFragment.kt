package com.parkingarmenia.edvardasus.parkingarmenia

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import data.onNewCarAddedListener

class EmptyCarsListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v : View = inflater!!.inflate(R.layout.emptylist_fragment, container, false)

        val btnAddLicence: Button = v.findViewById(R.id.btnAddLicense)

        btnAddLicence.setOnClickListener {
            val newCarDialog = NewCarDialog()
            newCarDialog.show(activity.fragmentManager, "321")
        }

        return v
    }

}