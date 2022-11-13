package com.martinbg.androidlocalstorage.data

data class CountryModel(
    var name: String,
    var capital: String?,
    var flags: Flags,
    var region: String,
    var subregion: String,
    var population: String,
    var area: String?,
    var nativeName: String,
    var numericCode: String,
    var cioc: String?
)
