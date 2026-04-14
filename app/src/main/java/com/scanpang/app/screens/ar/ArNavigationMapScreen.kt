package com.scanpang.app.screens.ar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TurnSharpLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.scanpang.app.components.ar.ArCameraBackdrop
import com.scanpang.app.components.ar.ArFloorStoreGuideOverlay
import com.scanpang.app.components.ar.ArNavActionCardCluster
import com.scanpang.app.components.ar.ArNavAiGuideTabWithTextField
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
    var activeTab by remember { mutableStateOf(NAV_TAB_MAP) }
    var aiQuery by remember { mutableStateOf("") }
    var selectedPoi by remember { mutableStateOf<String?>(null) }
    var activePoiDetailTab by remember { mutableStateOf(ArPoiTabBuilding) }
    var selectedStore by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        ArCameraBackdrop(showFreezeTint = false, modifier = Modifier.fillMaxSize())

        ArNavActionCardCluster(
            showNextStep = true,
            nextDistance = "60m",
            currentManeuverIcon = Icons.Rounded.TurnSharpLeft,
            currentDistance = "80m",
            currentInstruction = "스타벅스에서 좌회전",
        )

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
            onHomeClick = { navController.popBackStack() },
            onSearchClick = {
                navController.navigate(AppRoutes.ArExplore) { launchSingleTop = true }
            },
            destinationPill = {
                ArNavDestinationPill(
                    text = "명동성당 안내 중",
                    containerColor = ScanPangColors.Primary,
                )
            },
        )

        ArNavSideVolumeCamera(
            onVolumeClick = { },
            onCameraClick = { },
        )

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
                    )
                },
            )
        }

        ArNavTurnBadge(
            icon = Icons.Rounded.TurnSharpLeft,
            iconSize = ScanPangDimens.arNavTurnBadgeIcon,
            badgeColor = ScanPangColors.ArNavPrimaryBadge90,
            iconTint = Color.White,
        )

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
    }
}
