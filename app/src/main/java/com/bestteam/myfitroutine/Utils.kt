package com.bestteam.myfitroutine

import android.content.Context
import com.bestteam.myfitroutine.Contain.PREF
import com.bestteam.myfitroutine.Contain.PREF_KEY
import retrofit2.http.Query
import java.sql.Date
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat

object Utils {
    fun getdata(
        timestamp: String?,
        fromFormat: String?,
        toFormat: String?
    ):String {
        var date : Date? = null
        var res = ""
        try{
            val format = SimpleDateFormat(fromFormat)
            date = format.parse(timestamp) as Date?
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val df = SimpleDateFormat(toFormat)
        res = df.format(date)
        return res
    }

    fun saveRecentSearch(context: Context, query: String){
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_KEY, query).apply()
    }

    fun getRecentSearch(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return prefs.getString(PREF_KEY, null)
    }
}