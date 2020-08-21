package br.com.meiadois.decole.service

import android.content.Context
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.util.Coroutines
import java.io.File

class LogOutService(
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) {
    fun perform(context: Context) = Coroutines.io {
        db.clearAllTables()

        prefs.clear()

        val dir = File(context.filesDir, CompanyRepository.IMAGES_FOLDER)
        if (dir.exists() && dir.isDirectory) {
            dir.list()?.let {
                for (child in it)
                    File(dir, child).delete()
            }
            dir.delete()
        }
    }
}