package com.guleryigitcan.teknasyoncase.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.guleryigitcan.teknasyoncase.R
import com.guleryigitcan.teknasyoncase.presentation.components.GroupedSection
import com.guleryigitcan.teknasyoncase.presentation.components.TopBar
import com.guleryigitcan.teknasyoncase.presentation.components.severityColors
import com.guleryigitcan.teknasyoncase.presentation.components.severityLabel
import com.guleryigitcan.teknasyoncase.presentation.detail.model.EarthquakeDetailUiModel
import com.guleryigitcan.teknasyoncase.presentation.model.EarthquakeSeverity
import com.guleryigitcan.teknasyoncase.ui.theme.TeknasyonCaseTheme
import com.guleryigitcan.teknasyoncase.ui.theme.Background
import com.guleryigitcan.teknasyoncase.ui.theme.Divider
import com.guleryigitcan.teknasyoncase.ui.theme.MutedText
import com.guleryigitcan.teknasyoncase.ui.theme.PrimaryText

@Composable
fun EarthquakeDetailRoute(
    viewModel: EarthquakeDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EarthquakeDetailScreen(uiState = uiState, onBackClick = onBackClick)
}

@Composable
fun EarthquakeDetailScreen(
    uiState: EarthquakeDetailUiState,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Background)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        TopBar(
            title = stringResource(R.string.detail_title),
            onBackClick = onBackClick,
        )

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (uiState) {
                EarthquakeDetailUiState.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is EarthquakeDetailUiState.Content -> DetailContent(uiState.earthquake)
                EarthquakeDetailUiState.NotFound -> {
                    DetailMessage(
                        title = stringResource(R.string.detail_not_found),
                        message = stringResource(R.string.detail_not_found_message),
                        onBackClick = onBackClick
                    )
                }
                EarthquakeDetailUiState.Error -> {
                    DetailMessage(
                        title = stringResource(R.string.detail_load_error),
                        message = stringResource(R.string.detail_load_error_message),
                        onBackClick = onBackClick
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailContent(earthquake: EarthquakeDetailUiModel) {
    val colors = severityColors(earthquake.severity)

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 24.dp, end = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.detail_magnitude),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color.MutedText,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = earthquake.magnitudeText,
                    fontSize = 45.sp,
                    lineHeight = 52.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.PrimaryText,
                )
                Text(
                    text = severityLabel(earthquake.severity),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.content,
                )
            }
            Text(
                text = earthquake.place,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.PrimaryText,
            )
        }

        GroupedSection(title = stringResource(R.string.detail_details_section)) {
            DetailValue(stringResource(R.string.detail_occurred), earthquake.occurredText)
            DetailDivider()
            DetailValue(
                stringResource(R.string.detail_depth),
                earthquake.depthText ?: stringResource(R.string.detail_unknown),
            )
        }

        GroupedSection(title = stringResource(R.string.detail_location_section)) {
            DetailValue(stringResource(R.string.detail_latitude), earthquake.latitudeText)
            DetailDivider()
            DetailValue(stringResource(R.string.detail_longitude), earthquake.longitudeText)
        }

        androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 8.dp))
    }
}

@Composable
private fun DetailValue(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Normal,
            color = Color.MutedText
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Medium,
            color = Color.PrimaryText,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun DetailDivider() {
    HorizontalDivider(
        color = Color.Divider,
        modifier = Modifier.padding(start = 16.dp)
    )
}

@Composable
private fun DetailMessage(title: String, message: String, onBackClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = message,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        )

        Button(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.detail_back_to_list),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailContentPreview() {
    TeknasyonCaseTheme {
        EarthquakeDetailScreen(
            uiState = EarthquakeDetailUiState.Content(
                EarthquakeDetailUiModel(
                    magnitudeText = "4.7",
                    place = "12 km SW of Izmir",
                    occurredText = "30 Sep 2026, 14:42",
                    depthText = "8.2 km",
                    latitudeText = "38.423°",
                    longitudeText = "27.142°",
                    severity = EarthquakeSeverity.STRONG,
                ),
            ),
            onBackClick = {},
        )
    }
}
