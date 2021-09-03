package com.harjot.newssprint.db

import androidx.room.TypeConverter
import com.harjot.newssprint.models.Source


// required because Room cannot parse source class directly from the NewsResponse
class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String) = Source(name, name)
}