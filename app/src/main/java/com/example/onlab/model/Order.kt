package com.example.onlab.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.*

@Entity(tableName = "order_tbl")
data class Order(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "order_date")
    var date: LocalDate,

    @ColumnInfo(name = "customer_id")
    val customerID: UUID,

    @ColumnInfo(name = "status")
    var status: Int,
)

class LocalDateConverter {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalDate(localDate: LocalDate?): Long? {
        return localDate?.toEpochDay()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? {
        return epochDay?.let { LocalDate.ofEpochDay(epochDay) }
    }
}


