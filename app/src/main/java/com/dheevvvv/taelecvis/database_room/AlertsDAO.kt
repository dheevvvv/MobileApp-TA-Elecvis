package com.dheevvvv.taelecvis.database_room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface AlertsDAO {

    @Insert
    fun insert(alertsData: AlertsData)

    @Query("SELECT * FROM notification_alerts_table WHERE userId = :userId")
    fun getAlertsByUser(userId: Int): List<AlertsData>

    @Query("DELETE FROM notification_alerts_table WHERE alertsId = :alertsId AND userId = :userId")
    fun deleteAlertsByIdAndUser(alertsId: Int, userId: Int)

    @Query("SELECT statusActive FROM notification_alerts_table WHERE alertsId = :alertsId AND userId = :userId")
    fun getStatusActive(alertsId: Int, userId: Int) : Boolean
}