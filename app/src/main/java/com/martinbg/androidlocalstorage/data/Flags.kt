package com.martinbg.androidlocalstorage.data

import androidx.room.ColumnInfo

data class Flags(
    @ColumnInfo
    var svg: String,
    @ColumnInfo
    var png: String
)
