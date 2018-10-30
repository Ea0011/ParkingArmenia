package com.parkingarmenia.edvardasus.parkingarmenia

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet

@Suppress("DEPRECATION")
@CoordinatorLayout.DefaultBehavior(MoveUpwardBehaviour::class)
class MoveUpFABMenu : ConstraintLayout {
    constructor(ctx: Context) : super(ctx)

    constructor(ctx : Context, attrs : AttributeSet) : super(ctx, attrs)

    constructor(ctx:Context, attrs: AttributeSet, defStyleAttr : Int) : super(ctx, attrs, defStyleAttr)
}