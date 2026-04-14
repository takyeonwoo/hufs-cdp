package com.scanpang.app.components.ar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

const val ArPoiTabBuilding = "building"
const val ArPoiTabFloors = "floors"
const val ArPoiTabAi = "ai"

/**
 * 화면 중앙 플로팅 패널 + 딤 배경. Ar 탐색 / 길안내 공통.
 */
@Composable
fun ArPoiFloatingDetailOverlay(
    poiName: String,
    activeDetailTab: String,
    onActiveDetailTabChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onFloorStoreClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val panelWidth = maxWidth * 0.9f
        val panelMaxHeight = maxHeight * 0.7f
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ScanPangColors.ArOverlayScrimDark)
                .clickable { onDismiss() },
        )
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .width(panelWidth)
                .heightIn(max = panelMaxHeight),
            shape = ScanPangShapes.radius16,
            color = ScanPangColors.DetailArPanelSurface,
            shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = panelMaxHeight)
                    .verticalScroll(rememberScrollState())
                    .padding(ScanPangSpacing.lg),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = poiName,
                        style = ScanPangType.detailPlaceTitle18,
                        color = ScanPangColors.OnSurfaceStrong,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "닫기",
                            tint = ScanPangColors.OnSurfaceStrong,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(ScanPangSpacing.sm))
                ArPoiDetailTabRow(
                    active = activeDetailTab,
                    onSelect = onActiveDetailTabChange,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = ScanPangSpacing.sm),
                    color = ScanPangColors.OutlineSubtle,
                )
                when (activeDetailTab) {
                    ArPoiTabBuilding -> {
                        Text(
                            text = "$poiName 은(는) 명동 일대 대표 쇼핑·문화 공간입니다. 아미나님께서 한눈에 동선을 파악하실 수 있도록 안내드릴게요.",
                            style = ScanPangType.detailBody12Loose,
                            color = ScanPangColors.OnSurfaceMuted,
                        )
                    }
                    ArPoiTabFloors -> {
                        Text(
                            text = "층별 매장",
                            style = ScanPangType.title14,
                            color = ScanPangColors.OnSurfaceStrong,
                        )
                        Spacer(modifier = Modifier.height(ScanPangSpacing.sm))
                        Text(
                            text = "할랄가든 명동점 · 2층",
                            style = ScanPangType.body15Medium,
                            color = ScanPangColors.Primary,
                            modifier = Modifier
                                .clip(ScanPangShapes.radius12)
                                .clickable { onFloorStoreClick("할랄가든 명동점") }
                                .padding(vertical = ScanPangSpacing.sm),
                        )
                    }
                    ArPoiTabAi -> {
                        Text(
                            text = "AI 가이드",
                            style = ScanPangType.title14,
                            color = ScanPangColors.OnSurfaceStrong,
                        )
                        Spacer(modifier = Modifier.height(ScanPangSpacing.sm))
                        Text(
                            text = "이 건물은 보행 동선이 짧고 엘리베이터 위치가 명확해 이동이 편합니다.",
                            style = ScanPangType.detailBody12Loose,
                            color = ScanPangColors.OnSurfaceMuted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArPoiDetailTabRow(
    active: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
    ) {
        listOf(
            ArPoiTabBuilding to "건물정보",
            ArPoiTabFloors to "층별정보",
            ArPoiTabAi to "AI가이드",
        ).forEach { (key, label) ->
            val sel = active == key
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(ScanPangShapes.radius12)
                    .clickable { onSelect(key) },
                shape = ScanPangShapes.radius12,
                color = if (sel) ScanPangColors.PrimarySoft else ScanPangColors.Background,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = ScanPangSpacing.sm),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = if (sel) ScanPangType.meta11SemiBold else ScanPangType.meta11Medium,
                        color = if (sel) ScanPangColors.Primary else ScanPangColors.OnSurfaceMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun ArFloorStoreGuideOverlay(
    storeName: String,
    onDismiss: () -> Unit,
    onStartNavigation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScanPangColors.ArOverlayScrimDark)
            .clickable { onDismiss() },
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(ScanPangSpacing.lg),
            shape = ScanPangShapes.radius16,
            color = ScanPangColors.Surface,
            shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
        ) {
            Column(modifier = Modifier.padding(ScanPangSpacing.lg)) {
                Text(
                    text = storeName,
                    style = ScanPangType.title16SemiBold,
                    color = ScanPangColors.OnSurfaceStrong,
                )
                Spacer(modifier = Modifier.height(ScanPangSpacing.sm))
                Text(
                    text = "HALAL MEAT · 한식 · 영업 중",
                    style = ScanPangType.caption12Medium,
                    color = ScanPangColors.OnSurfaceMuted,
                )
                Spacer(modifier = Modifier.height(ScanPangSpacing.md))
                Button(
                    onClick = onStartNavigation,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ScanPangColors.Primary,
                        contentColor = Color.White,
                    ),
                    shape = ScanPangShapes.radius12,
                ) {
                    Text("길안내", style = ScanPangType.body15Medium)
                }
            }
        }
    }
}
