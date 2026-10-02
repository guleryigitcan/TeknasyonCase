package com.guleryigitcan.teknasyoncase.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.guleryigitcan.teknasyoncase.R
import com.guleryigitcan.teknasyoncase.presentation.components.severityColors
import com.guleryigitcan.teknasyoncase.presentation.components.severityLabel
import com.guleryigitcan.teknasyoncase.presentation.home.model.EarthquakeUiModel
import com.guleryigitcan.teknasyoncase.presentation.model.EarthquakeSeverity
import com.guleryigitcan.teknasyoncase.ui.theme.TeknasyonCaseTheme
import com.guleryigitcan.teknasyoncase.ui.theme.Background
import com.guleryigitcan.teknasyoncase.ui.theme.Divider
import com.guleryigitcan.teknasyoncase.ui.theme.MutedText
import com.guleryigitcan.teknasyoncase.ui.theme.PrimaryText
import com.guleryigitcan.teknasyoncase.ui.theme.Surface
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import com.guleryigitcan.teknasyoncase.presentation.components.TopBar

@Composable
fun HomeRoute(
    onEarthquakeClick: (String) -> Unit,
    onAlertSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.onHomeVisible() }

    HomeScreen(
        uiState = uiState,
        onRefresh = viewModel::refreshFromPull,
        onRetry = viewModel::refresh,
        onEarthquakeClick = { id ->
            viewModel.onEarthquakeSelected(id)
            onEarthquakeClick(id)
        },
        onAlertSettingsClick = onAlertSettingsClick,
        onRefreshErrorConsumed = viewModel::consumeRefreshError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onEarthquakeClick: (String) -> Unit,
    onAlertSettingsClick: () -> Unit,
    onRefreshErrorConsumed: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshErrorMessage = stringResource(R.string.home_refresh_error)

    LaunchedEffect(uiState.hasRefreshError) {
        if (uiState.hasRefreshError) {
            snackbarHostState.showSnackbar(refreshErrorMessage)
            onRefreshErrorConsumed()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Background)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        TopBar(
            title = stringResource(R.string.home_title),
            onAlertSettingsClick = onAlertSettingsClick
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                uiState.earthquakes.isNotEmpty() -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = onRefresh,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        EarthquakeList(uiState.earthquakes, onEarthquakeClick)
                    }
                }

                uiState.isLoading -> CenteredState { CircularProgressIndicator() }
                uiState.hasBlockingError -> CenteredState {
                    Text(
                        stringResource(R.string.home_error_title),
                        fontSize = 20.sp,
                        lineHeight = 27.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        stringResource(R.string.home_error_message),
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Button(onClick = onRetry) {
                        Text(
                            text = stringResource(R.string.home_retry),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                else -> PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CenteredState(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = stringResource(R.string.home_empty_title),
                            fontSize = 20.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.home_empty_message),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun EarthquakeList(
    earthquakes: List<EarthquakeUiModel>,
    onEarthquakeClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                text = stringResource(R.string.home_summary, pluralStringResource(R.plurals.home_event_count, earthquakes.size, earthquakes.size), stringResource(R.string.home_source)),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.MutedText,
                modifier = Modifier
                    .padding(start = 4.dp, bottom = 16.dp)
            )
        }
        itemsIndexed(items = earthquakes, key = { _, item -> item.id }) { index, earthquake ->
            EarthquakeListItem(
                earthquake = earthquake,
                isFirst = index == 0,
                isLast = index == earthquakes.lastIndex,
                onClick = { onEarthquakeClick(earthquake.id) }
            )
        }
    }
}

@Composable
private fun EarthquakeListItem(
    earthquake: EarthquakeUiModel,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val badgeColors = severityColors(earthquake.severity)
    val severityLabel = severityLabel(earthquake.severity)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Surface, shape = RoundedCornerShape(
                topStart = if (isFirst) 20.dp else 0.dp,
                topEnd = if (isFirst) 20.dp else 0.dp,
                bottomStart = if (isLast) 20.dp else 0.dp,
                bottomEnd = if (isLast) 20.dp else 0.dp
                )
            )
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .clickable(
                        onClickLabel = stringResource(R.string.home_open_earthquake, earthquake.place),
                        onClick = onClick
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .width(64.dp)
                ) {
                    Text(
                        text = earthquake.magnitudeText,
                        fontSize = 26.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = badgeColors.content
                    )

                    Text(
                        text = severityLabel,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = badgeColors.content
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = earthquake.place,
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.PrimaryText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = earthquake.depthText?.let { stringResource(R.string.home_row_metadata, earthquake.timeText, it) } ?: earthquake.timeText,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.MutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (!isLast)
                HorizontalDivider(
                    color = Color.Divider,
                    modifier = Modifier.padding(start = 96.dp),
                )
        }
    }
}

@Composable
private fun CenteredState(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .padding(24.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    TeknasyonCaseTheme {
        HomeScreen(
            uiState = HomeUiState(
                earthquakes = listOf(
                    EarthquakeUiModel("us1", "4.7", "12 km SW of Izmir", "30 Sep, 14:42", "8.2", EarthquakeSeverity.STRONG),
                    EarthquakeUiModel("us2", "3.2", "Near Denizli", "30 Sep, 12:16", null, EarthquakeSeverity.MODERATE),
                ),
                isLoading = false,
            ),
            onRefresh = {},
            onRetry = {},
            onEarthquakeClick = {},
            onAlertSettingsClick = {},
            onRefreshErrorConsumed = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeErrorPreview() {
    TeknasyonCaseTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = false, hasBlockingError = true),
            onRefresh = {},
            onRetry = {},
            onEarthquakeClick = {},
            onAlertSettingsClick = {},
            onRefreshErrorConsumed = {},
        )
    }
}
