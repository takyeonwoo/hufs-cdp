package com.scanpang.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Atm
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.LocalConvenienceStore
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.scanpang.app.data.RecentlyViewedEntry
import com.scanpang.app.navigation.AppRoutes
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

/** categoryKey → 최근 본 장소 행에 쓰는 카테고리 아이콘. */
fun String.toRecentIcon(): ImageVector = when (this) {
    "restaurant" -> Icons.Rounded.Restaurant
    "prayer_room" -> Icons.Rounded.Mosque
    "tourist", "cultural" -> Icons.Rounded.Whatshot
    "shopping" -> Icons.Rounded.LocalMall
    "convenience_store" -> Icons.Rounded.LocalConvenienceStore
    "cafe" -> Icons.Rounded.Coffee
    "atm" -> Icons.Rounded.Atm
    "bank" -> Icons.Rounded.AccountBalance
    "exchange" -> Icons.Rounded.CurrencyExchange
    "subway" -> Icons.Rounded.Train
    "restroom" -> Icons.Rounded.Wc
    "locker" -> Icons.Rounded.Lock
    "hospital" -> Icons.Rounded.LocalHospital
    "pharmacy" -> Icons.Rounded.Medication
    else -> Icons.Rounded.Whatshot
}

/** categoryKey + placeId → 통합 상세 화면 라우트. */
fun recentDetailRoute(categoryKey: String, placeId: String): String =
    AppRoutes.placeDetailRoute(categoryKey, placeId)

/**
 * 최근 본 장소 카드 행 — Home 미리보기/RecentlyViewedListScreen 전체 리스트가 동일하게 사용한다.
 * 좌측 카테고리 아이콘(원형 PrimarySoft 배경) + 이름/부제 + 우측 chevron.
 */
@Composable
fun RecentlyViewedRow(
    entry: RecentlyViewedEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ScanPangShapes.radius14)
            .background(ScanPangColors.Background)
            .clickable(onClick = onClick)
            .padding(horizontal = ScanPangSpacing.md, vertical = ScanPangDimens.recentRowVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(ScanPangDimens.recentIconCircle)
                .clip(CircleShape)
                .background(ScanPangColors.PrimarySoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = entry.categoryKey.toRecentIcon(),
                contentDescription = null,
                modifier = Modifier.size(ScanPangDimens.icon20),
                tint = ScanPangColors.Primary,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.xs),
        ) {
            Text(
                text = entry.name,
                style = ScanPangType.title14,
                color = ScanPangColors.OnSurfaceStrong,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val subtitle = entry.distanceLine.ifBlank { entry.category }
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = ScanPangType.caption12Medium,
                    color = ScanPangColors.OnSurfaceMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(ScanPangDimens.icon20),
            tint = ScanPangColors.OnSurfacePlaceholder,
        )
    }
}
