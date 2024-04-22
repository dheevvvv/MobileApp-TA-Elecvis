package com.dheevvvv.taelecvis.database_room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notification_alerts_table")
data class AlertsData(
    @PrimaryKey(autoGenerate = true)
    var alertsId : Int,
    @ColumnInfo(name = "kwh")
    var kwh : String,
    @ColumnInfo(name = "userId")
    var userId : String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "statusActive")
    var statusActive:Boolean
)
