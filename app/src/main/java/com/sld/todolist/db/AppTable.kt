package com.sld.todolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appdata_table")
data class AppTable (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "order")
    var order: Int?,

    @ColumnInfo(name = "title")
    var title: String?,

    @ColumnInfo(name = "description")
    var description: String?
) {
    constructor(): this(null, null, null, null)
}