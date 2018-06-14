package data

import android.content.Context
import android.util.Log
import com.parkingarmenia.edvardasus.parkingarmenia.MainActivity
import org.json.JSONArray
import org.json.JSONTokener
import java.io.*
import java.util.ArrayList

class JSONSerializer(private var mFileName : String, private var mCtx : Context) {

    fun save(cars : ArrayList<Car>) {

        val jsonArray = JSONArray()
        for (car in cars) {
           jsonArray.put(car.convertToJSON())
        }

        var writer: Writer? = null

        try {
            val out : OutputStream = mCtx.openFileOutput(mFileName, Context.MODE_PRIVATE)

            writer = OutputStreamWriter(out)
            writer.write(jsonArray.toString())
        } finally {
            if (writer != null) {
                writer.close()
            }
        }
    }

    fun load() : ArrayList<Car> {

        val cars : ArrayList<Car> = ArrayList()
        var bufferedReader : BufferedReader? = null

        try {

            val input : InputStream = mCtx.openFileInput(mFileName)
            bufferedReader = BufferedReader(InputStreamReader(input))
            val stringBuilder = StringBuilder()
            var line: String?

            do {
                line = bufferedReader.readLine()
                if (line == null)
                    break
                stringBuilder.append(line)
            } while (true)

            val jsonArray : JSONArray = JSONTokener(stringBuilder.toString()).nextValue() as JSONArray

            for (i in 0 until jsonArray.length()) {
                cars.add(Car(jsonArray.getJSONObject(i)))
            }

        } catch (e : FileNotFoundException) {
            Log.e(MainActivity.MY_TAG, "File not found! nothing to load")
        } finally {
            if (bufferedReader != null)
                bufferedReader.close()
        }

        return cars
    }

}