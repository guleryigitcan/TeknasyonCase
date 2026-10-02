package com.guleryigitcan.teknasyoncase.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guleryigitcan.teknasyoncase.ui.theme.MutedText
import com.guleryigitcan.teknasyoncase.ui.theme.Surface

@Composable
fun GroupedSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.MutedText,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 8.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Surface, shape = RoundedCornerShape(20.dp))
        ) {
            Column(content = content)
        }
    }
}
