package com.example.dbtest.data

import androidx.lifecycle.LiveData

class UserRepository(private val UserDao: UserDao) {
    val readAllData: LiveData<List<User>> = UserDao.readAllData()

    suspend fun addUser(user: User) {
        UserDao.addUser(user)
    }

    suspend fun updateUser(user: User) {
        UserDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        UserDao.deleteUser(user)
    }
}