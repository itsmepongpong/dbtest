package com.example.dbtest.data

import androidx.lifecycle.LiveData

class UserRepository(private val UserDao: UserDao) {
    val readAllData: LiveData<List<User>> = UserDao.readAllData()
   suspend fun addUser(user: User){
        UserDao.addUser(user)
    }
}