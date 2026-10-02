package com.guleryigitcan.teknasyoncase.data.remote.api

import com.guleryigitcan.teknasyoncase.data.remote.dto.EarthquakeResponseDto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import retrofit2.http.GET
import retrofit2.http.Query

internal interface UsgsEarthquakeApi {
    @GET("fdsnws/event/1/query")
    suspend fun getEarthquakes(
        @Query("format") format: String = "geojson",
        @Query("starttime") startTime: String = recentStartTimeUtc(),
        @Query("endtime") endTime: String? = null,
        @Query("minmagnitude") minMagnitude: Double = 2.5,
        @Query("limit") limit: Int = 100,
        @Query("orderby") orderBy: String = "time",
    ): EarthquakeResponseDto
}

private fun recentStartTimeUtc(): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(calendar.time)
}
