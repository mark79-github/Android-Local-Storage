package com.martinbg.androidlocalstorage.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info")
data class Info(
    @ColumnInfo(name = "last_modified_date")
    val date: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
