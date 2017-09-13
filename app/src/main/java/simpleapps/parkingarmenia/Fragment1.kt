package simpleapps.parkingarmenia

import android.support.v4.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
* Created by Edvard Avagyan on 9/11/2017.
*/
class Fragment1 : Fragment() {
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
        return inflater!!.inflate(R.layout.frag1, container, false)
    }
}