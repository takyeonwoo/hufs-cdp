package com.scanpang.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import com.scanpang.app.ui.theme.ScanPangColors
import kotlin.math.roundToInt
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

/** Switch 전체를 비율 유지(원형 썸)로 축소. scaleX≠scaleY 는 썸이 타원으로 찌그러지므로 금지. */
private const val SettingsSwitchScale = 0.72f

/** Switch 를 축소하면서 **레이아웃 높이·너비도 함께 줄인다**. (graphicsLayer 만 쓰면 행 높이가 기본 Switch 만큼 남음) */
private fun Modifier.settingsSwitchScale(scale: Float): Modifier = this.then(
    Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val outW = (placeable.width * scale).roundToInt().coerceAtLeast(1)
        val outH = (placeable.height * scale).roundToInt().coerceAtLeast(1)
        layout(outW, outH) {
            placeable.placeWithLayer(0, 0) {
                this.scaleX = scale
                this.scaleY = scale
                transformOrigin = TransformOrigin(1f, 0.5f)
            }
        }
    },
)

@Composable
fun ProfileSettingsSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = ScanPangType.sectionLabelSemiBold13,
        color = ScanPangColors.OnSurfaceMuted,
        modifier = modifier,
    )
}

@Composable
fun ProfileSettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ScanPangShapes.radius16)
            .background(ScanPangColors.Surface),
    ) {
        content()
    }
}

@Composable
fun ProfileSettingsRow(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    labelColor: Color = ScanPangColors.OnSurfaceStrong,
    showDividerBelow: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = ScanPangSpacing.lg, vertical = ScanPangSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(ScanPangDimens.settingsLeadingIcon),
                tint = iconTint,
            )
            Text(
                text = label,
                style = ScanPangType.body15Medium,
                color = labelColor,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(ScanPangDimens.icon18),
                tint = ScanPangColors.OnSurfacePlaceholder,
            )
        }
        if (showDividerBelow) {
            HorizontalDivider(
                thickness = ScanPangDimens.borderHairline,
                color = ScanPangColors.OutlineSubtle,
            )
        }
    }
}

/**
 * 토글 형태(우측 Switch)의 설정 행. 알림 설정 화면과 프로필의 "TTS 음성 안내" 행에서 사용한다.
 * - 행 전체 영역을 탭하면 토글이 바뀌므로 별도의 [Switch] hit slop 을 신경 쓰지 않아도 된다.
 * - [subtitle] 이 주어지면 라벨 아래에 회색으로 한 줄 더 노출된다.
 */
@Composable
fun ProfileSettingsToggleRow(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDividerBelow: Boolean,
    subtitle: String? = null,
    labelColor: Color = ScanPangColors.OnSurfaceStrong,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(horizontal = ScanPangSpacing.lg, vertical = ScanPangSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(ScanPangDimens.settingsLeadingIcon),
                tint = iconTint,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = ScanPangType.body15Medium,
                    color = labelColor,
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = ScanPangType.caption12Medium,
                        color = ScanPangColors.OnSurfaceMuted,
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.settingsSwitchScale(SettingsSwitchScale),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ScanPangColors.Primary,
                    checkedBorderColor = ScanPangColors.Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = ScanPangColors.OutlineSubtle,
                    uncheckedBorderColor = ScanPangColors.OnSurfacePlaceholder.copy(alpha = 0.55f),
                ),
            )
        }
        if (showDividerBelow) {
            HorizontalDivider(
                thickness = ScanPangDimens.borderHairline,
                color = ScanPangColors.OutlineSubtle,
            )
        }
    }
}
