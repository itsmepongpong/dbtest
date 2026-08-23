package com.example.dbtest.data
import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName = "users")
    data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val username: String,
    val password: String
)
