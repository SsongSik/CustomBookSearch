package com.example.booksearch.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class OrmConverter {
    //리스트가 들어오면 String 으로 인코딩
    @TypeConverter
    fun fromList(value : List<String>) = Json.encodeToString(value)

    //그 반대
    @TypeConverter
    fun toList(value : String) = Json.decodeFromString<List<String>>(value)
}