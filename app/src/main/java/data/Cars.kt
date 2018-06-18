package data

import android.content.Context

class Cars private constructor() {

    var mDb : NumbersDataManager? = null
        private set(value) { field = value }

    companion object {
        private var INSTANCE : Cars = Cars()

        fun getInstance(ctx : Context) : Cars {

            if (INSTANCE.mDb == null) {
                INSTANCE.mDb = NumbersDataManager(ctx)
            }

            return INSTANCE
        }
    }

}