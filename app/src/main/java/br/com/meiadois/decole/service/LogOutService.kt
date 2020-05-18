package br.com.meiadois.decole.service

import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.util.Coroutines

class LogOutService(
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) {
    fun perform() = Coroutines.io {
        db.clearAllTables()
        prefs.clear()
    }
}