package br.com.meiadois.decole.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

private const val KEY_ROUTE_LAST_FETCH = "ROUTE_LAST_FETCH"
private const val KEY_LESSON_LAST_FETCH = "LESSON_LAST_FETCH"
private const val LESSON_DIVIDER = ": "

class PreferenceProvider(
    context: Context
) {
    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveLastRouteFetch(fetchedAt: Long) =
        preference.edit()
            .putLong(KEY_ROUTE_LAST_FETCH, fetchedAt)
            .apply()

    fun getLastRouteFetch(): Long = preference.getLong(KEY_ROUTE_LAST_FETCH, 0L)

    fun saveLastLessonFetch(routeId: Long, fetchedAt: Long) {
        val saved: MutableSet<String> =
            preference.getStringSet(KEY_LESSON_LAST_FETCH, mutableSetOf())!!
        val newData =
            StringBuilder(routeId.toString()).append(LESSON_DIVIDER).append(fetchedAt.toString())

        saved.add(newData.toString())

        preference.edit()
            .putStringSet(KEY_LESSON_LAST_FETCH, saved)
            .apply()
    }

    //TODO corrigir problemas com timezone
    fun getLastLessonFetch(routeId: Long): Long {
        var res = "0"
        val all = preference.getStringSet(KEY_LESSON_LAST_FETCH, mutableSetOf())!!
        val match = all.firstOrNull { item ->
            item.split(LESSON_DIVIDER)[0] == routeId.toString()
        }
        match?.let {
            res = it.split(LESSON_DIVIDER)[1]
        }

        return res.toLong()
    }
}