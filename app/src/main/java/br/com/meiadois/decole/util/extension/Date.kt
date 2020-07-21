package br.com.meiadois.decole.util.extension

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Date.isFetchNeeded(): Boolean {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    val now = Date()
    val nowWithZeroTime = formatter.parse(formatter.format(now))
    val lastFetchWithZeroTime = formatter.parse(formatter.format(this))

    return lastFetchWithZeroTime!!.before(nowWithZeroTime)
}
