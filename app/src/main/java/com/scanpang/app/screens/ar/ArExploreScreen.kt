package com.scanpang.app.screens.ar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropFree
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.scanpang.app.components.ar.ArCameraBackdrop
import com.scanpang.app.components.ar.ArChatBottomSection
import com.scanpang.app.components.ar.ArCircleIconButton
import com.scanpang.app.components.ar.ArExploreSideColumn
import com.scanpang.app.components.ar.ArFilterChipRow
import com.scanpang.app.components.ar.ArFilterChipRowMulti
import com.scanpang.app.components.ar.ArPoiPinsLayer
import com.scanpang.app.navigation.AppRoutes
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType
import kotlinx.coroutines.launch

private const val TAB_BUILDING = "building"
private const val TAB_FLOORS = "floors"
private const val TAB_AI = "ai"

private data class ArSearchHit(
    val title: String,
    val scoreLine: String,
    val distance: String,
)

/**
 * AR 탐색 단일 화면 — 필터·검색·고정·POI 시트·TTS 등 상태로 처리.
 */
@Composable
fun ArExploreScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isFilterOpen by remember { mutableStateOf(false) }
    var categorySelection by remember { mutableStateOf(setOf<String>()) }
    var sortOption by remember { mutableStateOf("거리순") }

    var isSearchOpen by remember { mutableStateOf(false) }
    var showArSearchResults by remember { mutableStateOf(false) }

    var isFrozen by remember { mutableStateOf(false) }
    var isTtsOn by remember { mutableStateOf(true) }

    var selectedPoi by remember { mutableStateOf<String?>(null) }
    var activeDetailTab by remember { mutableStateOf(TAB_BUILDING) }
    var selectedStore by remember { mutableStateOf<String?>(null) }

    val categories = remember {
        listOf("카페", "음식점", "쇼핑", "관광", "기도실", "환전")
    }
    val sorts = remember { listOf("거리순", "인기순", "할랄 인증") }
    val recentQueries = remember {
        listOf("할랄 식당", "명동성당", "근처 환전소")
    }
    val suggestionTags = remember {
        listOf("할랄", "카페", "기도실", "환전소")
    }
    val searchHits = remember {
        listOf(
            ArSearchHit("할랄가든 명동점", "일치도 98%", "120m"),
            ArSearchHit("명동성당", "일치도 92%", "350m"),
            ArSearchHit("우리은행 환전소", "일치도 88%", "80m"),
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            ArCameraBackdrop(
                showFreezeTint = isFrozen,
                modifier = Modifier.fillMaxSize(),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                ScanPangColors.ArExploreScrimGradientTop,
                                ScanPangColors.ArExploreScrimGradientBottom,
                            ),
                        ),
                    )
                    .statusBarsPadding()
                    .padding(horizontal = ScanPangDimens.arTopBarHorizontal)
                    .padding(bottom = ScanPangDimens.arTopBarBottomPadding),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            maxOf(
                                ScanPangDimens.arCircleBtn36,
                                ScanPangDimens.arStatusPillHeight,
                            ),
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ArCircleIconButton(
                        icon = Icons.Rounded.Home,
                        contentDescription = "홈",
                        onClick = { navController.popBackStack() },
                        modifier = Modifier,
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = ScanPangSpacing.sm),
                        contentAlignment = Alignment.Center,
                    ) {
                        ArExploreStatusPill(
                            isFrozen = isFrozen,
                            selectedFilters = categorySelection,
                            onClick = {
                                if (isFrozen) {
                                    isFrozen = false
                                } else {
                                    isFilterOpen = true
                                }
                            },
                        )
                    }
                    ArCircleIconButton(
                        icon = Icons.Rounded.Search,
                        contentDescription = "검색",
                        onClick = { isSearchOpen = true },
                        modifier = Modifier,
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                ArPoiPinsLayer(
                    onPoiOneClick = {
                        selectedPoi = "눈스퀘어"
                        activeDetailTab = TAB_BUILDING
                        selectedStore = null
                    },
                    onPoiTwoClick = {
                        selectedPoi = "명동빌딩"
                        activeDetailTab = TAB_BUILDING
                        selectedStore = null
                    },
                )
                ArExploreSideColumn(
                    onTtsClick = {
                        isTtsOn = !isTtsOn
                        val msg = if (isTtsOn) "음성 안내 켜짐" else "음성 안내 꺼짐"
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    },
                    onCameraClick = { isFrozen = !isFrozen },
                    isTtsOn = isTtsOn,
                    isFrozen = isFrozen,
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
            ) {
                ArChatBottomSection(
                    userMessage = "아미나님, 오늘은 어떤 할랄 맛집을 찾으세요?",
                    agentMessage = "안녕하세요! 스캔팡입니다. 주변 장소를 AR로 안내해 드릴게요.",
                    inputPlaceholder = "무엇이든 물어보세요",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            AnimatedVisibility(
                visible = isFilterOpen,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ScanPangColors.ArOverlayScrimDark)
                        .clickable { isFilterOpen = false },
                ) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(horizontal = ScanPangDimens.arFilterPanelHorizontal)
                            .padding(top = ScanPangSpacing.lg)
                            .clickable(enabled = false) { },
                        shape = ScanPangShapes.arFilterPanelTop,
                        color = ScanPangColors.Surface,
                        shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(ScanPangDimens.arTopBarHorizontal)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
                        ) {
                            Text(
                                text = "카테고리",
                                style = ScanPangType.arFilterTitle16,
                                color = ScanPangColors.OnSurfaceStrong,
                            )
                            ArFilterChipRowMulti(
                                labels = categories,
                                selected = categorySelection,
                                onToggle = { label ->
                                    categorySelection =
                                        if (label in categorySelection) {
                                            categorySelection - label
                                        } else {
                                            categorySelection + label
                                        }
                                },
                            )
                            Text(
                                text = "정렬",
                                style = ScanPangType.arFilterTitle16,
                                color = ScanPangColors.OnSurfaceStrong,
                            )
                            ArFilterChipRow(
                                labels = sorts,
                                selected = sortOption,
                                onSelect = { sortOption = it },
                            )
                            Spacer(modifier = Modifier.height(ScanPangDimens.arFilterSectionTitleTop))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
                            ) {
                                OutlinedButton(
                                    onClick = { categorySelection = emptySet() },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        text = "초기화",
                                        style = ScanPangType.body15Medium,
                                        color = ScanPangColors.OnSurfaceStrong,
                                    )
                                }
                                Button(
                                    onClick = { isFilterOpen = false },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(ScanPangDimens.searchBarHeightDefault),
                                    shape = ScanPangShapes.radius12,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ScanPangColors.Primary,
                                        contentColor = Color.White,
                                    ),
                                ) {
                                    Text("필터 적용", style = ScanPangType.body15Medium)
                                }
                            }
                            Spacer(modifier = Modifier.height(ScanPangDimens.arFilterApplyBottom))
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isSearchOpen,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ScanPangColors.ArOverlayScrimDark)
                        .clickable { isSearchOpen = false; showArSearchResults = false },
                ) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(horizontal = ScanPangDimens.arFilterPanelHorizontal)
                            .padding(top = ScanPangSpacing.lg)
                            .clickable(enabled = false) { },
                        shape = ScanPangShapes.arSearchPanel,
                        color = ScanPangColors.Surface,
                        shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(ScanPangDimens.arTopBarHorizontal)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null,
                                        tint = ScanPangColors.OnSurfaceMuted,
                                        modifier = Modifier.size(ScanPangDimens.icon20),
                                    )
                                    Text(
                                        text = "장소·메뉴 검색",
                                        style = ScanPangType.searchPlaceholderRegular,
                                        color = ScanPangColors.OnSurfacePlaceholder,
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        isSearchOpen = false
                                        showArSearchResults = false
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "닫기",
                                        tint = ScanPangColors.OnSurfaceStrong,
                                    )
                                }
                            }
                            Text(
                                text = "최근 검색",
                                style = ScanPangType.sectionTitle16,
                                color = ScanPangColors.OnSurfaceStrong,
                            )
                            recentQueries.forEach { q ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showArSearchResults = true
                                        }
                                        .padding(vertical = ScanPangSpacing.sm),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.History,
                                        contentDescription = null,
                                        tint = ScanPangColors.OnSurfaceMuted,
                                        modifier = Modifier.size(ScanPangDimens.icon18),
                                    )
                                    Text(
                                        text = q,
                                        style = ScanPangType.body14Regular,
                                        color = ScanPangColors.OnSurfaceStrong,
                                    )
                                }
                            }
                            Text(
                                text = "추천 검색어",
                                style = ScanPangType.sectionTitle16,
                                color = ScanPangColors.OnSurfaceStrong,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
                            ) {
                                suggestionTags.forEach { tag ->
                                    Surface(
                                        shape = ScanPangShapes.badge6,
                                        color = ScanPangColors.ArRecommendTagHalalBackground,
                                        modifier = Modifier.clickable { showArSearchResults = true },
                                    ) {
                                        Text(
                                            text = tag,
                                            modifier = Modifier.padding(
                                                horizontal = ScanPangDimens.arSearchTagHorizontalPad,
                                                vertical = ScanPangDimens.arSearchTagVerticalPad,
                                            ),
                                            style = ScanPangType.tag11Medium,
                                            color = ScanPangColors.Primary,
                                        )
                                    }
                                }
                            }
                            if (showArSearchResults) {
                                HorizontalDivider(color = ScanPangColors.OutlineSubtle)
                                Text(
                                    text = "정확도 · 거리순",
                                    style = ScanPangType.meta11SemiBold,
                                    color = ScanPangColors.OnSurfaceMuted,
                                )
                                searchHits.forEach { hit ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = ScanPangSpacing.sm),
                                    ) {
                                        Text(
                                            text = hit.title,
                                            style = ScanPangType.title14,
                                            color = ScanPangColors.OnSurfaceStrong,
                                        )
                                        Text(
                                            text = "${hit.scoreLine} · ${hit.distance}",
                                            style = ScanPangType.caption12Medium,
                                            color = ScanPangColors.OnSurfaceMuted,
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
                                            modifier = Modifier.padding(top = ScanPangSpacing.sm),
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    selectedPoi = hit.title
                                                    activeDetailTab = TAB_BUILDING
                                                    isSearchOpen = false
                                                    showArSearchResults = false
                                                },
                                            ) {
                                                Text(
                                                    text = "정보 보기",
                                                    color = ScanPangColors.Primary,
                                                    style = ScanPangType.body15Medium,
                                                )
                                            }
                                            TextButton(
                                                onClick = {
                                                    navController.navigate(AppRoutes.ArNavMap) {
                                                        launchSingleTop = true
                                                    }
                                                    isSearchOpen = false
                                                    showArSearchResults = false
                                                },
                                            ) {
                                                Text(
                                                    text = "길안내",
                                                    color = ScanPangColors.Primary,
                                                    style = ScanPangType.body15Medium,
                                                )
                                            }
                                        }
                                    }
                                    HorizontalDivider(color = ScanPangColors.OutlineSubtle)
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = selectedPoi != null,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                val poi = selectedPoi ?: return@AnimatedVisibility
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ScanPangColors.ArOverlayScrimDark)
                            .clickable {
                                selectedPoi = null
                                selectedStore = null
                                activeDetailTab = TAB_BUILDING
                            },
                    )
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(ScanPangDimens.arPoiSheetMaxHeight)
                            .clickable(enabled = false) { },
                        shape = ScanPangShapes.arFilterPanelTop,
                        color = ScanPangColors.DetailArPanelSurface,
                        shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(ScanPangSpacing.lg),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = poi,
                                    style = ScanPangType.detailPlaceTitle18,
                                    color = ScanPangColors.OnSurfaceStrong,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                IconButton(
                                    onClick = {
                                        selectedPoi = null
                                        selectedStore = null
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "닫기",
                                        tint = ScanPangColors.OnSurfaceStrong,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(ScanPangSpacing.sm))
                            PoiDetailTabRow(
                                active = activeDetailTab,
                                onSelect = { activeDetailTab = it },
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = ScanPangSpacing.sm),
                                color = ScanPangColors.OutlineSubtle,
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState()),
                            ) {
                                when (activeDetailTab) {
                                    TAB_BUILDING -> {
                                        Text(
                                            text = "$poi 은(는) 명동 일대 대표 쇼핑·문화 공간입니다. 아미나님께서 한눈에 동선을 파악하실 수 있도록 안내드릴게요.",
                                            style = ScanPangType.detailBody12Loose,
                                            color = ScanPangColors.OnSurfaceMuted,
                                        )
                                    }
                                    TAB_FLOORS -> {
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
                                                .clickable {
                                                    selectedStore = "할랄가든 명동점"
                                                }
                                                .padding(vertical = ScanPangSpacing.sm),
                                        )
                                    }
                                    TAB_AI -> {
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
            }

            AnimatedVisibility(
                visible = selectedStore != null,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                val store = selectedStore ?: return@AnimatedVisibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ScanPangColors.ArOverlayScrimDark)
                        .clickable { selectedStore = null },
                ) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(ScanPangSpacing.lg)
                            .clickable(enabled = false) { },
                        shape = ScanPangShapes.radius16,
                        color = ScanPangColors.Surface,
                        shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
                    ) {
                        Column(modifier = Modifier.padding(ScanPangSpacing.lg)) {
                            Text(
                                text = store,
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
                                onClick = {
                                    navController.navigate(AppRoutes.ArNavMap) {
                                        launchSingleTop = true
                                    }
                                },
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
        }
    }
}

@Composable
private fun ArExploreStatusPill(
    isFrozen: Boolean,
    selectedFilters: Set<String>,
    onClick: () -> Unit,
) {
    when {
        isFrozen -> {
            Surface(
                modifier = Modifier
                    .height(ScanPangDimens.arStatusPillHeight)
                    .clip(CircleShape)
                    .clickable(onClick = onClick),
                shape = CircleShape,
                color = ScanPangColors.Primary,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = ScanPangDimens.arStatusPillHorizontalPad),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.xs),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = null,
                        modifier = Modifier.size(ScanPangDimens.icon18),
                        tint = Color.White,
                    )
                    Text(
                        text = "화면 고정 중",
                        style = ScanPangType.arStatusPill15,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(ScanPangDimens.arNavDestinationChevron),
                        tint = Color.White,
                    )
                }
            }
        }
        selectedFilters.isEmpty() -> {
            Surface(
                modifier = Modifier
                    .height(ScanPangDimens.arStatusPillHeight)
                    .clip(CircleShape)
                    .clickable(onClick = onClick),
                shape = CircleShape,
                color = ScanPangColors.ArOverlayWhite80,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = ScanPangDimens.arStatusPillHorizontalPad),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.xs),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CropFree,
                        contentDescription = null,
                        modifier = Modifier.size(ScanPangDimens.icon18),
                        tint = ScanPangColors.OnSurfaceStrong,
                    )
                    Text(
                        text = "AR 탐색 중",
                        style = ScanPangType.arStatusPill15,
                        color = ScanPangColors.OnSurfaceStrong,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(ScanPangDimens.arNavDestinationChevron),
                        tint = ScanPangColors.OnSurfacePlaceholder,
                    )
                }
            }
        }
        else -> {
            val label = buildFilterPillLabel(selectedFilters)
            Surface(
                modifier = Modifier
                    .height(ScanPangDimens.arStatusPillHeight)
                    .clip(CircleShape)
                    .clickable(onClick = onClick),
                shape = CircleShape,
                color = ScanPangColors.Primary,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = ScanPangDimens.arStatusPillHorizontalPad),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.xs),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(ScanPangDimens.icon18),
                        tint = Color.White,
                    )
                    Text(
                        text = label,
                        style = ScanPangType.arStatusPill15,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(ScanPangDimens.arNavDestinationChevron),
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

private fun buildFilterPillLabel(selected: Set<String>): String {
    val list = selected.toList()
    if (list.isEmpty()) return ""
    if (list.size == 1) return list[0]
    return "${list[0]} 외 ${list.size - 1}개"
}

@Composable
private fun PoiDetailTabRow(
    active: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
    ) {
        listOf(
            TAB_BUILDING to "건물정보",
            TAB_FLOORS to "층별정보",
            TAB_AI to "AI가이드",
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
