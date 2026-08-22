package com.example.dbtest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [User::class],version = 1, exportSchema = false)
abstract class userdb:RoomDatabase() {
    abstract fun UserDao(): UserDao
    companion object{
        @Volatile
        private var INSTANCE: userdb?  = null
        fun getDatabase(context: Context): userdb{
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    userdb::class.java,
                    name = "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}