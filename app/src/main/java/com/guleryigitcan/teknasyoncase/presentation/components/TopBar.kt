package com.guleryigitcan.teknasyoncase.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guleryigitcan.teknasyoncase.R
import com.guleryigitcan.teknasyoncase.ui.theme.Background
import com.guleryigitcan.teknasyoncase.ui.theme.PrimaryText

@Composable
fun TopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onAlertSettingsClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .heightIn(min = 64.dp)
            .padding(start = if (onBackClick == null) 16.dp else 8.dp, end = 16.dp)
    ) {
        onBackClick?.let { action ->
            IconButton(onClick = action) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.detail_back),
                    tint = Color.PrimaryText,
                )
            }
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = if (onBackClick == null) 26.sp else 20.sp,
            lineHeight = if (onBackClick == null) 32.sp else 27.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        onAlertSettingsClick?.let { action ->
            IconButton(onClick = action) {
                Icon(
                    painter = painterResource(R.drawable.ic_alerts_bell),
                    contentDescription = stringResource(R.string.alerts_action),
                    tint = Color.PrimaryText,
                )
            }
        }
    }
}
