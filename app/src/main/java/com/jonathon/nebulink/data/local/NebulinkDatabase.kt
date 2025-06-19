package com.jonathon.nebulink.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.jonathon.nebulink.data.local.dao.PuzzleDao
import com.jonathon.nebulink.data.local.entity.PlayerStatsEntity
import com.jonathon.nebulink.data.local.entity.PuzzleEntity
import com.jonathon.nebulink.data.local.entity.PuzzleProgressEntity
import java.time.LocalDate

@Database(    entities = [
        PuzzleEntity::class,
        PuzzleProgressEntity::class,
        PlayerStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NebulinkDatabase : RoomDatabase() {
    abstract val puzzleDao: PuzzleDao
}

class Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun fromCharListList(value: List<List<Char>>): String {
        return value.joinToString(";") { row ->
            row.joinToString(",")
        }
    }

    @TypeConverter
    fun toCharListList(value: String): List<List<Char>> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            value.split(";").map { row ->
                row.split(",").map { it[0] }
            }
        }
    }
}
