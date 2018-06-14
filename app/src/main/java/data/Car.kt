package data

import org.json.JSONObject
import java.io.Serializable

class Car(serial : String) : Serializable {

    var mSerial : String

    init {
        mSerial = serial
    }

    constructor(jo : JSONObject) : this(jo.getString(JSON_SERIAL)) {

        mSerial = jo.getString(JSON_SERIAL)

    }

    companion object {
         const val JSON_SERIAL : String = "serial"
    }

    fun convertToJSON() : JSONObject {
        val jo = JSONObject()
        jo.put(JSON_SERIAL, mSerial)

        return jo
    }

}