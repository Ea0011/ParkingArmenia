package com.parkingarmenia.edvardasus.parkingarmenia

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.View

class MoveUpwardBehaviour : CoordinatorLayout.Behavior<View>() {
    companion object {
        private const val SNACKBAR_BEHAVIOUR_ENABLED : Boolean = true
    }

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
        return SNACKBAR_BEHAVIOUR_ENABLED && dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
        val translationY: Float = Math.min(0f, dependency!!.translationY - dependency.height)
        child!!.translationY = translationY
        return true
    }
}