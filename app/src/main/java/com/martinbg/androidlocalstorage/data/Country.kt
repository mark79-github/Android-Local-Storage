package com.martinbg.androidlocalstorage.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country")
data class Country(
    @PrimaryKey
    var name: String,
    @ColumnInfo
    var capital: String?,
    @Embedded
    var flags: Flags,
    @ColumnInfo
    var region: String,
    @ColumnInfo
    var subregion: String,
    @ColumnInfo
    var population: String,
    @ColumnInfo
    var area: String?,
    @ColumnInfo
    var nativeName: String,
    @ColumnInfo
    var numericCode: String,
    @ColumnInfo
    var cioc: String?
)
