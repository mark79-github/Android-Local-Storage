package com.martinbg.androidlocalstorage.db

import androidx.room.*
import com.martinbg.androidlocalstorage.data.Country
import com.martinbg.androidlocalstorage.data.Info


@Dao
interface CountryDao {

    @Query("SELECT * FROM country")
    fun getAll(): List<Country>

    @Query("SELECT * FROM country WHERE country.name = :name")
    fun getCountryByName(name: String): Country

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(country: Country)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(countries: List<Country>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(country: Country)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAll(countries: List<Country>)

    @Delete
    fun delete(country: Country)

    @Insert
    fun insertInfo(info: Info)

    @Query("select * from info order by id desc limit 1")
    fun getLastInfo(): Info?
}
