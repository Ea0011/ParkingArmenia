package data

interface onCarEditListener {
    fun onCarEdited(position : Int, newSerial : String, delete : Boolean)
}