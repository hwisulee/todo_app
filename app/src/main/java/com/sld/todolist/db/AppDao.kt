package com.sld.todolist.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppDao {
    @Insert
    fun insert(data: AppTable)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(data: AppTable)

    @Delete
    fun delete(data: AppTable)

    @Query("DELETE FROM appdata_table")
    fun deleteAll()

    @Query("SELECT * FROM appdata_table ORDER BY `order` DESC")
    fun getAll(): LiveData<List<AppTable>>
}