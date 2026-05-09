package com.scanpang.app.components.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangTheme
import com.scanpang.app.ui.theme.ScanPangType

/**
 * 구글 로그인 버튼. 동작·시그니처는 [KakaoLoginButton] 과 동일하게 맞춘다.
 *
 * - bg: [ScanPangColors.Surface], 1dp [ScanPangColors.GoogleBorder] 보더
 * - pressed 시 8% 어두운 오버레이 (회색 0x14)
 */
@Composable
fun GoogleLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedOverlay = if (isPressed && enabled && !isLoading) Color(0x14000000) else Color.Transparent
    val canClick = enabled && !isLoading

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(12.dp))
            .background(ScanPangColors.Surface)
            .border(1.dp, ScanPangColors.GoogleBorder, RoundedCornerShape(12.dp))
            .background(pressedOverlay)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = canClick,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = ScanPangColors.GoogleBlue,
                strokeWidth = 2.dp,
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ScanPangColors.Surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "G",
                        style = ScanPangType.title16SemiBold,
                        color = ScanPangColors.GoogleBlue,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Google로 시작하기",
                    style = ScanPangType.title16SemiBold,
                    color = ScanPangColors.GoogleLabel,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, name = "Default")
@Composable
private fun GoogleLoginButtonPreviewDefault() {
    ScanPangTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            GoogleLoginButton(onClick = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, name = "Disabled")
@Composable
private fun GoogleLoginButtonPreviewDisabled() {
    ScanPangTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            GoogleLoginButton(onClick = {}, enabled = false)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, name = "Loading")
@Composable
private fun GoogleLoginButtonPreviewLoading() {
    ScanPangTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            GoogleLoginButton(onClick = {}, isLoading = true)
        }
    }
}
