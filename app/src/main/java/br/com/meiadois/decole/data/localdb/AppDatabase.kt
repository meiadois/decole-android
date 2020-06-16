package br.com.meiadois.decole.data.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.meiadois.decole.data.localdb.dao.*
import br.com.meiadois.decole.data.localdb.entity.*

@Database(
    entities = [User::class, Route::class, Lesson::class, Company::class, Segment::class, Account::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDAO
    abstract fun getRouteDao(): RouteDAO
    abstract fun getLessonDao(): LessonDAO
    abstract fun getCompanyDao(): CompanyDAO
    abstract fun getSegmentDao(): SegmentDAO
    abstract fun getAccountDao(): AccountDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "Decole.db"
            ).build()
    }
}