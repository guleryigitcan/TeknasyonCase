package com.guleryigitcan.teknasyoncase.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
internal data object HomeDestination

internal const val EARTHQUAKE_ID_ARGUMENT = "earthquakeId"

@Serializable
internal data class EarthquakeDetailDestination(val earthquakeId: String)

@Serializable
internal data object AlertSettingsDestination