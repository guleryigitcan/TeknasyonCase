package com.guleryigitcan.teknasyoncase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_monitor_state")
internal data class AlertMonitorStateEntity(@PrimaryKey val id: Int = 0)
