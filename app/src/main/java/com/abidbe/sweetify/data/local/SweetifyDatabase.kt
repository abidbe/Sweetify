package com.abidbe.sweetify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Drink::class], version = 1, exportSchema = false)
abstract class SweetifyDatabase : RoomDatabase() {

    abstract fun sweetifyDao(): SweetifyDao

    companion object {
        @Volatile
        private var INSTANCE: SweetifyDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): SweetifyDatabase {
            if (INSTANCE == null) {
                synchronized(SweetifyDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        SweetifyDatabase::class.java, "sweetify_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE as SweetifyDatabase
        }

    }
}