package data

interface OnCarEditListener {
    fun onCarEdited(serial : String, position : Int, newSerial : String, delete : Boolean)
}