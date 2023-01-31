package com.sld.todolist.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class AppViewModel(application: Application): AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val data = repository.getAll()

    fun insert(data: AppTable) = repository.insert(data)

    fun update(data: AppTable) = repository.update(data)

    fun delete(data: AppTable) = repository.delete(data)

    fun deleteAll() = repository.deleteAll()

    fun getAll(): LiveData<List<AppTable>> = this.data
}