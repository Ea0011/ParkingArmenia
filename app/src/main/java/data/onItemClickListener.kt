package data

import android.view.View

interface onItemClickListener {
    fun onCardClicked(view : View, position : Int, serial : String)
}