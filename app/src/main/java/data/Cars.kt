package data

import android.content.Context

class Cars private constructor() {

    var mJSONSerializer: JSONSerializer? = null
        private set(value) { field = value }

    var mCars : ArrayList<Car> = ArrayList()
        private set(value) { field = value }

    companion object {
        private var INSTANCE : Cars = Cars()

        fun getInstance(ctx : Context) : Cars {
            if (INSTANCE.mJSONSerializer == null) {
                INSTANCE.mJSONSerializer = JSONSerializer("Parking_Armenia", ctx)
                INSTANCE.load()
                INSTANCE.save()
            }
            return INSTANCE
        }
    }

    fun save() {
        mJSONSerializer!!.save(mCars)
    }

    fun load() : ArrayList<Car> {
       mCars = mJSONSerializer!!.load()
       return mCars
    }

}