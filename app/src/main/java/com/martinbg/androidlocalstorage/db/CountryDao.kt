package com.martinbg.androidlocalstorage.db

import androidx.room.*
import com.martinbg.androidlocalstorage.data.CountryEntity
import com.martinbg.androidlocalstorage.data.Info


@Dao
interface CountryDao {

    @Query("SELECT * FROM country")
    suspend fun getAll(): List<CountryEntity>

    @Query("SELECT * FROM country WHERE country.name = :name")
    suspend fun getCountryByName(name: String): CountryEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(country: CountryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(country: CountryEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(countries: List<CountryEntity>)

    @Delete
    suspend fun delete(country: CountryEntity)

    @Query("DELETE FROM country")
    suspend fun deleteAll()

    @Insert
    suspend fun insertInfo(info: Info)

    @Query("select * from info order by id desc limit 1")
    suspend fun getLastInfo(): Info?
}
