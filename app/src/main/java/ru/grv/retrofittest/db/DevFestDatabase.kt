package ru.grv.retrofittest.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import ru.grv.retrofittest.Speaker
import ru.grv.retrofittest.Talk
import ru.grv.retrofittest.db.unitlocalized.ActivityDao
//import ru.grv.retrofittest.db.entity.SpeakerData
import ru.grv.retrofittest.db.unitlocalized.SpeakerDao

@Database(entities = [Talk::class, Speaker::class], version = 1, exportSchema = false)
abstract class DevFestDatabase: RoomDatabase() {

    companion object {
        @Volatile var database: DevFestDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "devfest.db"

        operator fun invoke(context: Context) = database?: synchronized(LOCK) {
            database?: buildDatabase(context).also { database = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, DevFestDatabase::class.java, DB_NAME).build()
    }

    abstract fun speakerDao(): SpeakerDao
    abstract fun activityDao(): ActivityDao

    fun destroyInstance() {
        database = null
    }

    /*companion object {
        private var INSTANCE: DevFestDatabase? = null

        fun getInstance(context: Context): DevFestDatabase? {
            if (INSTANCE == null) {
                synchronized(DevFestDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        DevFestDatabase::class.java, "devfest.db")
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }*/
}