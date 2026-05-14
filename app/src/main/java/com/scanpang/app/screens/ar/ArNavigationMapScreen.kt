package com.scanpang.app.screens.ar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.UTurnLeft
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.TurnLeft
import androidx.compose.material.icons.rounded.TurnRight
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
import com.scanpang.app.data.SavedPlaceEntry
import com.scanpang.app.data.SavedPlacesStore
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scanpang.app.components.ar.ArCameraBackdrop
import com.scanpang.app.components.ar.ArStoreDetailOverlay
import com.scanpang.app.components.ar.ArNavActionCardCluster
import com.scanpang.app.data.DummyData
import com.scanpang.app.components.ar.ArNavAiGuideTabWithTextField
import com.scanpang.app.components.ar.ArNavArrivedBadge
import com.scanpang.app.components.ar.ArNavBottomSheet
import com.scanpang.app.components.ar.ArNavDefaultPoiMarkers
import com.scanpang.app.components.ar.ArNavDestinationPill
import com.scanpang.app.components.ar.ArNavMapImageContent
import com.scanpang.app.components.ar.ArNavSideVolumeCamera
import com.scanpang.app.components.ar.ArNavTopHud
import com.scanpang.app.components.ar.ArNavTurnBadge
import com.scanpang.app.components.ar.ArNavStopNavigationSheet
import com.scanpang.app.components.ar.ArNavStopConfirmDialog
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
    destinationName: String? = null,
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

    val savedStore = remember { SavedPlacesStore(context) }
    var savedPoiIds by remember { mutableStateOf(savedStore.getAll().map { it.id }.toSet()) }

    val effectiveDestination = destinationName ?: NavigationSamples.Cruising.destinationName

    var activeTab by remember { mutableStateOf(NAV_TAB_MAP) }
    var navState by remember {
        mutableStateOf<NavigationUiState>(
            NavigationSamples.Cruising.copy(destinationName = effectiveDestination)
        )
    }
    var aiQuery by remember { mutableStateOf("") }
    var navAiSttListening by remember { mutableStateOf(false) }
    var selectedPoi by remember { mutableStateOf<String?>(null) }
    var activePoiDetailTab by remember { mutableStateOf(ArPoiTabBuilding) }
    var selectedStore by remember { mutableStateOf<String?>(null) }
    var showStopNavSheet by remember { mutableStateOf(false) }
    var showStopConfirmDialog by remember { mutableStateOf(false) }

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
                        userMessage = DummyData.arNavDemoUserMessage,
                        agentMessage = DummyData.arNavDemoAgentMessage,
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
                    onClick = { showStopNavSheet = true },
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
            val poiSaved = poi in savedPoiIds
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
                isSaved = poiSaved,
                onSave = {
                    if (poiSaved) {
                        savedStore.remove(poi)
                    } else {
                        savedStore.save(navPoiSavedEntry(poi))
                    }
                    savedPoiIds = savedStore.getAll().map { it.id }.toSet()
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        selectedStore?.let { name ->
            DummyData.storeDetailFor(name)?.let { detail ->
                ArStoreDetailOverlay(
                    detail = detail,
                    onDismiss = { selectedStore = null },
                    onStartNavigation = {
                        navController.navigate(AppRoutes.arNavMapRoute(name)) { launchSingleTop = true }
                        selectedStore = null
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        if (showStopNavSheet) {
            ArNavStopNavigationSheet(
                destinationName = navState.destinationName,
                onDismiss = { showStopNavSheet = false },
                onStopNavigation = {
                    showStopNavSheet = false
                    showStopConfirmDialog = true
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (showStopConfirmDialog) {
            ArNavStopConfirmDialog(
                onNavigateToExplore = {
                    navController.navigate(AppRoutes.ArExplore) {
                        popUpTo(AppRoutes.ArExplore) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToHome = {
                    navController.navigate(AppRoutes.Home) {
                        popUpTo(AppRoutes.Home) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onDismiss = { showStopConfirmDialog = false },
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
                    val dest = navState.destinationName
                    navState = when (navState.phase) {
                        NavigationPhase.Cruising -> NavigationSamples.Approaching.copy(destinationName = dest)
                        NavigationPhase.Approaching -> NavigationSamples.Arrived.copy(destinationName = dest)
                        NavigationPhase.Arrived -> NavigationSamples.Cruising.copy(destinationName = dest)
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

private fun navPoiSavedEntry(poiName: String): SavedPlaceEntry {
    val (category, categoryKey) = when {
        poiName.contains("환전") -> "환전소" to "exchange"
        poiName.contains("쇼핑") || poiName.contains("스퀘어") || poiName.contains("몰") -> "쇼핑" to "shopping"
        poiName.contains("카페") || poiName.contains("커피") -> "카페" to "cafe"
        poiName.contains("식당") || poiName.contains("레스토랑") || poiName.contains("찜닭") -> "식당" to "restaurant"
        poiName.contains("기도") -> "기도실" to "prayer_room"
        poiName.contains("편의점") -> "편의점" to "convenience_store"
        poiName.contains("ATM") || poiName.contains("atm") -> "ATM" to "atm"
        poiName.contains("은행") -> "은행" to "bank"
        poiName.contains("지하철") || poiName.contains("역") -> "지하철" to "subway"
        poiName.contains("화장실") -> "화장실" to "restroom"
        poiName.contains("보관") || poiName.contains("락커") -> "물품보관함" to "locker"
        poiName.contains("병원") || poiName.contains("의원") -> "병원" to "hospital"
        poiName.contains("약국") -> "약국" to "pharmacy"
        else -> "관광지" to "tourist"
    }
    return SavedPlaceEntry(
        id = poiName,
        name = poiName,
        category = category,
        distanceLine = "",
        tags = emptyList(),
        categoryKey = categoryKey,
        savedOrder = System.currentTimeMillis(),
    )
}

/**
 * [TurnDirection]을 화면에 노출할 [ImageVector]로 변환한다.
 *
 * [ArNavActionCardCluster]·[ArNavTurnBadge] 양쪽이 같은 매핑을 사용해
 * 좌상단 카드의 작은 파란 박스 안 아이콘과 가운데 큰 원형 배지 안 아이콘이
 * 항상 일치하도록 보장한다.
 */
private fun TurnDirection.toIcon(): ImageVector = when (this) {
    TurnDirection.Left -> Icons.Rounded.TurnLeft
    TurnDirection.Right -> Icons.Rounded.TurnRight
    TurnDirection.Straight -> Icons.Rounded.ArrowUpward
    TurnDirection.UTurn -> Icons.Rounded.UTurnLeft
}
