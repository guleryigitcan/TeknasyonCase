package com.guleryigitcan.teknasyoncase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_feed")
internal data class HomeFeedEntity(@PrimaryKey val earthquakeId: String)
