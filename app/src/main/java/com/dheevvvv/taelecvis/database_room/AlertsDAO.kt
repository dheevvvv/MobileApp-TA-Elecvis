package com.dheevvvv.taelecvis.database_room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface AlertsDAO {

    @Insert
    suspend fun insert(alertsData: AlertsData)

    @Update
    suspend fun update(alertsData: AlertsData)

    @Query("SELECT * FROM notification_alerts_table WHERE userId = :userId")
    suspend fun getAlertsByUser(userId: Int): List<AlertsData>

    @Query("DELETE FROM notification_alerts_table WHERE alertsId = :alertsId AND userId = :userId")
    suspend fun deleteAlertsByIdAndUser(alertsId: Int, userId: Int)

    @Query("SELECT statusActive FROM notification_alerts_table WHERE alertsId = :alertsId AND userId = :userId")
    suspend fun getStatusActive(alertsId: Int, userId: Int) : Boolean

    @Query("SELECT * FROM notification_alerts_table WHERE statusActive = 1 AND userId = :userId")
    suspend fun getActiveAlerts(userId: Int) : List<AlertsData>
}