package com.guleryigitcan.teknasyoncase.domain.model

import kotlin.math.roundToInt

const val DEFAULT_NOTIFICATION_THRESHOLD = 4.0
const val MIN_NOTIFICATION_THRESHOLD = 2.0
const val MAX_NOTIFICATION_THRESHOLD = 7.0
const val NOTIFICATION_THRESHOLD_STEP = 0.5

fun normalizeNotificationThreshold(value: Double): Double {
    if (!value.isFinite()) return DEFAULT_NOTIFICATION_THRESHOLD
    val clamped = value.coerceIn(MIN_NOTIFICATION_THRESHOLD, MAX_NOTIFICATION_THRESHOLD)
    return (clamped / NOTIFICATION_THRESHOLD_STEP).roundToInt() * NOTIFICATION_THRESHOLD_STEP
}
