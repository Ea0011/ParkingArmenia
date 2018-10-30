package data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NumbersDataManager(ctx : Context) {

    inner class CustomSQLiteOpenHelper(ctx : Context) : SQLiteOpenHelper(ctx, DB_NAME, null ,DB_VERSION) {

        override fun onCreate(db: SQLiteDatabase?) {
            val newTableQueryString = "create table $TABLE_N ($TABLE_ROW_ID integer primary key autoincrement not null, $TABLE_ROW_NUMBER text not null);"

            db!!.execSQL(newTableQueryString)
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

        }

    }

    private var db : SQLiteDatabase

    init {
        val helper = CustomSQLiteOpenHelper(ctx)

        db = helper.writableDatabase
    }

    companion object {
        const val TABLE_ROW_ID = "_id"
        const val TABLE_ROW_NUMBER = "number"
        private const val DB_NAME = "numbers_list_db"
        private const val DB_VERSION = 1
        private const val TABLE_N = "numbers"
    }

    fun insert(number : String) {
        val query = "INSERT INTO $TABLE_N ($TABLE_ROW_NUMBER) VALUES ('$number');"

        db.execSQL(query)
    }

    fun delete(serial: String) {
        val query = "DELETE FROM $TABLE_N WHERE $TABLE_ROW_NUMBER = '$serial';"

        db.execSQL(query)
    }

    fun selectAll() : Cursor {
        return db.rawQuery("SELECT * from $TABLE_N", null)
    }

    fun searchPosition(serial : String) : Cursor {
        val query = "SELECT $TABLE_ROW_ID, $TABLE_ROW_NUMBER from $TABLE_N WHERE $TABLE_ROW_NUMBER = '$serial';"

        return db.rawQuery(query, null)
    }

    fun isEmpty() : Boolean {
        val query = "SELECT count(*) FROM $TABLE_N"

        val c = db.rawQuery(query, null)
        c.moveToFirst()
        val count = c.getInt(0)

        if (count == 0) {
            c.close()
            return true
        }
        c.close()
        return false
    }

    fun update(what : Int, to : String) {
        val query = "UPDATE $TABLE_N SET $TABLE_ROW_NUMBER = '$to' WHERE $TABLE_ROW_ID = $what"

        db.execSQL(query)
    }

    fun load() : ArrayList<Car> {
        val carsList = ArrayList<Car>()

        if (isEmpty()) {
            return carsList
        }
        val c = selectAll()
        if(c.moveToFirst()) {
            carsList.add(Car(c.getString(c.getColumnIndex(TABLE_ROW_NUMBER)), c.getInt(c.getColumnIndex(TABLE_ROW_ID))))
        }

        while (c.moveToNext()) {
            carsList.add(Car(c.getString(c.getColumnIndex(TABLE_ROW_NUMBER)), c.getInt(c.getColumnIndex(TABLE_ROW_ID))))
        }

        return carsList
    }

    fun destroy() {
        val query = "DELETE FROM $TABLE_N"

        db.execSQL(query)
    }

}