package com.guleryigitcan.teknasyoncase.presentation.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.guleryigitcan.teknasyoncase.R
import com.guleryigitcan.teknasyoncase.presentation.components.GroupedSection
import com.guleryigitcan.teknasyoncase.presentation.components.TopBar
import com.guleryigitcan.teknasyoncase.domain.model.MAX_NOTIFICATION_THRESHOLD
import com.guleryigitcan.teknasyoncase.domain.model.MIN_NOTIFICATION_THRESHOLD
import com.guleryigitcan.teknasyoncase.domain.model.NOTIFICATION_THRESHOLD_STEP
import com.guleryigitcan.teknasyoncase.domain.model.normalizeNotificationThreshold
import com.guleryigitcan.teknasyoncase.ui.theme.TeknasyonCaseTheme
import com.guleryigitcan.teknasyoncase.ui.theme.Accent
import com.guleryigitcan.teknasyoncase.ui.theme.Background
import com.guleryigitcan.teknasyoncase.ui.theme.Divider
import com.guleryigitcan.teknasyoncase.ui.theme.MutedText
import com.guleryigitcan.teknasyoncase.ui.theme.Surface

@Composable
@SuppressLint("InlinedApi") // These constants are used only in the Android 13+ permission branch.
fun AlertSettingsRoute(
    onBackClick: () -> Unit,
    viewModel: AlertSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val requiresPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun hasPermission() = !requiresPermission || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS,) == PackageManager.PERMISSION_GRANTED

    var permissionGranted by remember { mutableStateOf(hasPermission()) }
    var requestDenied by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionGranted = granted
        requestDenied = !granted
        viewModel.onPermissionResult(granted)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) permissionGranted = hasPermission()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AlertSettingsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onThresholdChangeFinished = viewModel::saveThreshold,
        onSaveErrorConsumed = viewModel::consumeSaveError,
        showPermissionPrompt = requiresPermission && !permissionGranted,
        permissionDenied = requestDenied,
        onEnableNotificationsClick = {
            if (requestDenied) {
                context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                })
            } else {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                viewModel.onPermissionRequested()
            }
        }
    )
}

@Composable
fun AlertSettingsScreen(
    uiState: AlertSettingsUiState,
    onBackClick: () -> Unit,
    onThresholdChangeFinished: (Double) -> Unit,
    onSaveErrorConsumed: () -> Unit,
    showPermissionPrompt: Boolean = false,
    permissionDenied: Boolean = false,
    onEnableNotificationsClick: () -> Unit = {}
) {
    var sliderValue by rememberSaveable { mutableFloatStateOf(uiState.threshold.toFloat()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val saveErrorMessage = stringResource(R.string.alerts_save_error)

    LaunchedEffect(uiState.threshold) {
        sliderValue = uiState.threshold.toFloat()
    }

    LaunchedEffect(uiState.hasSaveError) {
        if (uiState.hasSaveError) {
            sliderValue = uiState.threshold.toFloat()
            snackbarHostState.showSnackbar(saveErrorMessage)
            onSaveErrorConsumed()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Background)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        TopBar(
            title = stringResource(R.string.alerts_title),
            onBackClick = onBackClick
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (uiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else {
                AlertSettingsContent(
                    threshold = sliderValue.toDouble(),
                    onThresholdChange = { sliderValue = normalizeNotificationThreshold(it).toFloat() },
                    onThresholdChangeFinished = { onThresholdChangeFinished(sliderValue.toDouble()) },
                    showPermissionPrompt = showPermissionPrompt,
                    permissionDenied = permissionDenied,
                    onEnableNotificationsClick = onEnableNotificationsClick,
                    modifier = Modifier.fillMaxSize()
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlertSettingsContent(
    threshold: Double,
    onThresholdChange: (Double) -> Unit,
    onThresholdChangeFinished: () -> Unit,
    showPermissionPrompt: Boolean,
    permissionDenied: Boolean,
    onEnableNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val thresholdText = stringResource(R.string.alerts_magnitude_format, threshold)

    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.alerts_intro),
            modifier = Modifier.padding(horizontal = 4.dp),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            color = Color.MutedText
        )

        GroupedSection(title = stringResource(R.string.alerts_threshold_section)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.alerts_threshold_label),
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )

                    Text(
                        text = stringResource(R.string.alerts_threshold_value, thresholdText),
                        fontSize = 26.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Accent
                    )
                }

                Slider(
                    value = threshold.toFloat(),
                    onValueChange = { onThresholdChange(it.toDouble()) },
                    onValueChangeFinished = onThresholdChangeFinished,
                    valueRange = MIN_NOTIFICATION_THRESHOLD.toFloat()..MAX_NOTIFICATION_THRESHOLD.toFloat(),
                    steps = ((MAX_NOTIFICATION_THRESHOLD - MIN_NOTIFICATION_THRESHOLD) / NOTIFICATION_THRESHOLD_STEP).toInt() - 1,
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.Accent,
                        inactiveTrackColor = Color.Divider,
                        inactiveTickColor = Color.MutedText,
                    ),
                    track = { state ->
                        SliderDefaults.Track(
                            sliderState = state,
                            thumbTrackGapSize = 0.dp,
                            colors = SliderDefaults.colors(
                                activeTrackColor = Color.Accent,
                                activeTickColor = Color.Surface,
                                inactiveTrackColor = Color.Divider,
                                inactiveTickColor = Color.MutedText,
                            ),
                            drawStopIndicator = { _ -> },
                            drawTick = { offset, color ->
                                val tickWidth = 1.dp.toPx()
                                val tickHeight = 6.dp.toPx()
                                drawLine(
                                    color = color,
                                    start = Offset(offset.x, offset.y - tickHeight / 2),
                                    end = Offset(offset.x, offset.y + tickHeight / 2),
                                    strokeWidth = tickWidth
                                )
                            },
                            modifier = Modifier
                                .height(height = 6.dp)
                        )
                    },
                    thumb = {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            colors = SliderDefaults.colors(
                                thumbColor = Color.Accent,
                            ),
                            enabled = true,
                            thumbSize = DpSize(16.dp, 16.dp)
                        )
                    },
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.alerts_range_min),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.MutedText
                    )

                    Text(
                        text = stringResource(R.string.alerts_range_max),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.MutedText
                    )
                }

                Text(
                    text = stringResource(R.string.alerts_rule, thresholdText),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.MutedText
                )
            }
        }
        if (showPermissionPrompt) {
            GroupedSection(title = stringResource(R.string.alerts_permission_title)) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(if (permissionDenied) R.string.alerts_permission_denied else R.string.alerts_permission_message,),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.MutedText
                    )
                    Button(onClick = onEnableNotificationsClick) {
                        Text(
                            text =  stringResource(if (permissionDenied) R.string.alerts_permission_settings else R.string.alerts_permission_enable,),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertSettingsPreview() {
    TeknasyonCaseTheme {
        AlertSettingsScreen(
            uiState = AlertSettingsUiState(isLoading = false, threshold = 4.0),
            onBackClick = {},
            onThresholdChangeFinished = {},
            onSaveErrorConsumed = {},
            showPermissionPrompt = true
        )
    }
}
