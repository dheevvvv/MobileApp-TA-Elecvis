package com.dheevvvv.taelecvis.database_room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [AlertsData::class], version = 1)
abstract class MainDatabase:RoomDatabase() {

    abstract fun alertsDao(): AlertsDAO

    companion object {
        private var INSTANCE: MainDatabase? = null

        fun getInstances(context: Context): MainDatabase? {

            if (INSTANCE == null){
                synchronized(MainDatabase::class){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext, MainDatabase::class.java,"Main.db"
                    ).build()
                }
            }
            return INSTANCE

        }
    }

}