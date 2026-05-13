package com.scanpang.app.screens.ar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.UTurnLeft
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.TurnSharpLeft
import androidx.compose.material.icons.rounded.TurnSharpRight
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.scanpang.app.data.AppSettingsPreferences
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scanpang.app.components.ar.ArCameraBackdrop
import com.scanpang.app.components.ar.ArFloorStoreGuideOverlay
import com.scanpang.app.components.ar.ArNavActionCardCluster
import com.scanpang.app.components.ar.ArNavAiGuideTabWithTextField
import com.scanpang.app.components.ar.ArNavArrivedBadge
import com.scanpang.app.components.ar.ArNavBottomSheet
import com.scanpang.app.components.ar.ArNavDefaultPoiMarkers
import com.scanpang.app.components.ar.ArNavDestinationPill
import com.scanpang.app.components.ar.ArNavMapImageContent
import com.scanpang.app.components.ar.ArNavSideVolumeCamera
import com.scanpang.app.components.ar.ArNavTopHud
import com.scanpang.app.components.ar.ArNavTurnBadge
import com.scanpang.app.components.ar.ArPoiFloatingDetailOverlay
import com.scanpang.app.components.ar.ArPoiTabBuilding
import com.scanpang.app.navigation.AppRoutes
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

private const val NAV_TAB_MAP = "map"
private const val NAV_TAB_AI = "ai"

/**
 * AR 길안내 — 지도 / AI 가이드 탭을 한 화면 내 상태로 전환.
 */
@Composable
fun ArNavigationMapScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val appSettingsPrefs = remember(context) { AppSettingsPreferences(context.applicationContext) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var isTtsOn by remember { mutableStateOf(appSettingsPrefs.isTtsEnabled()) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isTtsOn = appSettingsPrefs.isTtsEnabled()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var activeTab by remember { mutableStateOf(NAV_TAB_MAP) }
    var navState by remember { mutableStateOf<NavigationUiState>(NavigationSamples.Cruising) }
    var aiQuery by remember { mutableStateOf("") }
    var navAiSttListening by remember { mutableStateOf(false) }
    var selectedPoi by remember { mutableStateOf<String?>(null) }
    var activePoiDetailTab by remember { mutableStateOf(ArPoiTabBuilding) }
    var selectedStore by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        ArCameraBackdrop(showFreezeTint = false, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            ArNavBottomSheet(
                mapTabSelected = activeTab == NAV_TAB_MAP,
                onSelectMap = { activeTab = NAV_TAB_MAP },
                onSelectAgent = { activeTab = NAV_TAB_AI },
                modifier = Modifier.fillMaxWidth(),
                mapContent = { ArNavMapImageContent(Modifier.fillMaxSize()) },
                agentContent = {
                    ArNavAiGuideTabWithTextField(
                        query = aiQuery,
                        onQueryChange = { aiQuery = it },
                        userMessage = "눈스퀘어가 뭐야?",
                        agentMessage = "거의 다 왔어요! 입구는 정면 오른쪽이에요.",
                        placeholder = "무엇이든 물어보세요",
                        isSttListening = navAiSttListening,
                        onMicClick = { navAiSttListening = !navAiSttListening },
                        onSend = {
                            if (aiQuery.isNotBlank()) {
                                aiQuery = ""
                            }
                        },
                    )
                },
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ScanPangDimens.arNavBottomPillZoneHeight)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                ScanPangColors.ArNavBottomPillGradientTop,
                                ScanPangColors.ArNavBottomPillGradientBottom,
                            ),
                        ),
                    ),
            )
        }

        if (navState.phase != NavigationPhase.Arrived && navState.currentInstruction != null) {
            val instr = navState.currentInstruction!!
            ArNavActionCardCluster(
                showNextStep = navState.phase == NavigationPhase.Cruising,
                nextDistance = "60m", // TODO: NavigationUiState에 nextInstruction 필드 추가 시 교체
                currentManeuverIcon = instr.direction.toIcon(),
                currentDistance = instr.distanceLabel,
                currentInstruction = instr.message,
            )
        }

        ArNavDefaultPoiMarkers(
            onShoppingPoiClick = {
                selectedPoi = "눈스퀘어"
                activePoiDetailTab = ArPoiTabBuilding
                selectedStore = null
            },
            onExchangePoiClick = {
                selectedPoi = "명동 환전소"
                activePoiDetailTab = ArPoiTabBuilding
                selectedStore = null
            },
        )

        ArNavTopHud(
            modifier = Modifier.align(Alignment.TopStart),
            onCameraClick = { },
            onSearchClick = {
                navController.navigate(AppRoutes.ArExplore) { launchSingleTop = true }
            },
            destinationPill = {
                val pillText = when (navState.phase) {
                    NavigationPhase.Arrived -> "${navState.destinationName} 도착"
                    else -> "${navState.destinationName} 안내 중"
                }
                val pillColor = when (navState.phase) {
                    NavigationPhase.Arrived -> ScanPangColors.ArNavSuccessBadge90
                    else -> ScanPangColors.Primary
                }
                ArNavDestinationPill(
                    text = pillText,
                    containerColor = pillColor,
                )
            },
        )

        ArNavSideVolumeCamera(
            isTtsOn = isTtsOn,
            onVolumeClick = {
                val next = !isTtsOn
                isTtsOn = next
                appSettingsPrefs.setTtsEnabled(next)
            },
        )

        when (navState.phase) {
            NavigationPhase.Arrived -> {
                ArNavArrivedBadge()
            }
            else -> {
                navState.currentInstruction?.let { instr ->
                    ArNavTurnBadge(
                        icon = instr.direction.toIcon(),
                        iconSize = ScanPangDimens.arNavTurnBadgeIcon,
                        badgeColor = ScanPangColors.ArNavPrimaryBadge90,
                        iconTint = Color.White,
                    )
                }
            }
        }

        selectedPoi?.let { poi ->
            ArPoiFloatingDetailOverlay(
                poiName = poi,
                activeDetailTab = activePoiDetailTab,
                onActiveDetailTabChange = { activePoiDetailTab = it },
                onDismiss = {
                    selectedPoi = null
                    selectedStore = null
                    activePoiDetailTab = ArPoiTabBuilding
                },
                onFloorStoreClick = { selectedStore = it },
                modifier = Modifier.fillMaxSize(),
            )
        }

        selectedStore?.let { store ->
            ArFloorStoreGuideOverlay(
                storeName = store,
                onDismiss = { selectedStore = null },
                onStartNavigation = {
                    navController.navigate(AppRoutes.ArNavMap) { launchSingleTop = true }
                    selectedStore = null
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        // TODO: 백엔드 연동 후 제거 — 디버그용 phase 토글
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(
                    end = ScanPangSpacing.md,
                    bottom = 80.dp, // TODO: 임시 — 바텀시트와 겹치지 않게 (디버그 토글 제거 시 함께 삭제)
                )
                .clickable {
                    navState = when (navState.phase) {
                        NavigationPhase.Cruising -> NavigationSamples.Approaching
                        NavigationPhase.Approaching -> NavigationSamples.Arrived
                        NavigationPhase.Arrived -> NavigationSamples.Cruising
                    }
                },
            shape = ScanPangShapes.filterChip,
            color = ScanPangColors.OnSurfaceStrong,
            shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
        ) {
            Text(
                text = "phase: ${navState.phase::class.simpleName}",
                modifier = Modifier.padding(
                    horizontal = ScanPangSpacing.md,
                    vertical = ScanPangSpacing.sm,
                ),
                style = ScanPangType.caption12Medium,
                color = Color.White,
            )
        }
    }
}

/**
 * [TurnDirection]을 화면에 노출할 [ImageVector]로 변환한다.
 *
 * [ArNavActionCardCluster]·[ArNavTurnBadge] 양쪽이 같은 매핑을 사용해
 * 좌상단 카드의 작은 파란 박스 안 아이콘과 가운데 큰 원형 배지 안 아이콘이
 * 항상 일치하도록 보장한다.
 */
private fun TurnDirection.toIcon(): ImageVector = when (this) {
    TurnDirection.Left -> Icons.Rounded.TurnSharpLeft
    TurnDirection.Right -> Icons.Rounded.TurnSharpRight
    TurnDirection.Straight -> Icons.Rounded.ArrowUpward
    TurnDirection.UTurn -> Icons.Rounded.UTurnLeft
}
