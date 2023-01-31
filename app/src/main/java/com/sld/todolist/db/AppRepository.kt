package com.sld.todolist.db

import android.app.Application
import androidx.lifecycle.LiveData

class AppRepository(application: Application) {
    private val appDatabase: AppDatabase = AppDatabase.getInstance(application)!!
    private val appDao: AppDao = appDatabase.appDao()
    private val allData: LiveData<List<AppTable>> = appDao.getAll()

    fun insert(data: AppTable) = threadMainController("insert", data)

    fun update(data: AppTable) = threadMainController("update", data)

    fun delete(data: AppTable) = threadMainController("delete", data)

    fun deleteAll() = threadMainController("deleteAll", null)

    fun getAll(): LiveData<List<AppTable>> = allData

    private fun threadMainController(code: String, data: AppTable?) {
        try {
            val thread = Thread {
                when(code) {
                    "insert" -> appDao.insert(data!!)
                    "update" -> appDao.update(data!!)
                    "delete" -> appDao.delete(data!!)
                    "deleteAll" -> appDao.deleteAll()
                }
            }
            thread.start()
        } catch (_: Exception) { }
    }
}