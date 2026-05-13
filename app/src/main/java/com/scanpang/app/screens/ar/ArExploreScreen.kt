package com.scanpang.app.screens.ar

import android.Manifest
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropFree
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.scanpang.app.ar.ArExploreTtsController
import com.scanpang.app.ar.ArSpeechRecognizerHelper
import com.scanpang.app.ar.DummyAgentService
import com.scanpang.app.ar.sendVoiceMessage
import com.scanpang.app.components.ar.ArAgentChatMessage
import com.scanpang.app.components.ar.ArCameraBackdrop
import com.scanpang.app.components.ar.ArCircleIconButton
import com.scanpang.app.components.ar.ArExploreInteractiveChatSection
import com.scanpang.app.components.ar.ArFloorStoreGuideOverlay
import com.scanpang.app.components.ar.ArPoiFloatingDetailOverlay
import com.scanpang.app.components.ar.ArPoiTabBuilding
import com.scanpang.app.components.ar.ArExploreFilterPanelFigma
import com.scanpang.app.components.ar.ArExploreSearchHitUi
import com.scanpang.app.components.ar.ArExploreSearchPanelContent
import com.scanpang.app.components.ar.ArExploreSideColumn
import com.scanpang.app.components.ar.arExploreCategoryChipSpecs
import com.scanpang.app.components.ar.ArPoiPin
import com.scanpang.app.components.ar.arExploreBuildingSamples
import com.scanpang.app.components.ar.ArPoiPinsLayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.scanpang.app.data.AppSettingsPreferences
import com.scanpang.app.data.SearchHistoryPreferences
import com.scanpang.app.navigation.AppRoutes
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType
import kotlinx.coroutines.launch

/**
 * AR 탐색 단일 화면 — 필터·검색·고정·POI 시트·TTS 등 상태로 처리.
 */
@Composable
fun ArExploreScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val chatListState = rememberLazyListState()
    var chatInput by remember { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                ArAgentChatMessage(
                    text = "안녕하세요! 스캔팡입니다. 주변 장소를 AR로 안내해 드릴게요.",
                    isUser = false,
                ),
                ArAgentChatMessage(
                    text = "아미나님, 오늘은 어떤 할랄 맛집을 찾으세요?",
                    isUser = true,
                ),
            ),
        )
    }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.scrollToItem(chatMessages.lastIndex)
        }
    }

    var isFilterOpen by remember { mutableStateOf(false) }
    var categorySelection by remember { mutableStateOf(setOf<String>()) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var showArSearchResults by remember { mutableStateOf(false) }
    var arSearchQuery by remember { mutableStateOf("") }
    var arSearchHistoryTick by remember { mutableIntStateOf(0) }
    val searchHistoryPrefs = remember(appContext) { SearchHistoryPreferences(appContext) }
    val appSettingsPrefs = remember(appContext) { AppSettingsPreferences(appContext) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var isFrozen by remember { mutableStateOf(false) }
    var isTtsOn by remember { mutableStateOf(appSettingsPrefs.isTtsEnabled()) }

    // 프로필 화면에서 TTS 토글 변경 후 돌아왔을 때 상태 동기화
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isTtsOn = appSettingsPrefs.isTtsEnabled()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var isSttListening by remember { mutableStateOf(false) }
    val ttsPlayingState = remember { mutableStateOf(false) }
    val isTtsPlaying by ttsPlayingState
    var speechHelperRef by remember { mutableStateOf<ArSpeechRecognizerHelper?>(null) }
    var pendingMicAfterPermission by remember { mutableStateOf(false) }

    val agentService = remember { DummyAgentService() }
    val ttsController = remember(appContext) {
        ArExploreTtsController(appContext) { playing -> ttsPlayingState.value = playing }
    }

    DisposableEffect(ttsController) {
        ttsController.start()
        onDispose { ttsController.shutdown() }
    }

    LaunchedEffect(isTtsOn) {
        if (!isTtsOn) ttsController.stop()
    }

    val onSttResult: (String) -> Unit = { text ->
        chatInput = text
        scope.launch {
            val reply = sendVoiceMessage(text, agentService)
            chatMessages = chatMessages +
                ArAgentChatMessage(text = text, isUser = true) +
                ArAgentChatMessage(text = reply, isUser = false)
            chatInput = ""
            ttsController.speakIfEnabled(reply, isTtsOn)
        }
    }
    val latestOnSttResult = rememberUpdatedState(onSttResult)

    val latestSnackbar = rememberUpdatedState(snackbarHostState)
    val latestScope = rememberUpdatedState(scope)

    DisposableEffect(appContext) {
        val h = ArSpeechRecognizerHelper(
            context = appContext,
            onListeningChange = { isSttListening = it },
            onResult = { text -> latestOnSttResult.value(text) },
            onErrorCode = { code ->
                if (code != SpeechRecognizer.ERROR_NO_MATCH &&
                    code != SpeechRecognizer.ERROR_SPEECH_TIMEOUT
                ) {
                    latestScope.value.launch {
                        latestSnackbar.value.showSnackbar("음성 인식 중 오류가 났어요")
                    }
                }
            },
        )
        speechHelperRef = h
        onDispose {
            h.destroy()
            speechHelperRef = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted && pendingMicAfterPermission) {
            speechHelperRef?.startListening()
        } else if (!granted) {
            scope.launch { snackbarHostState.showSnackbar("마이크 권한이 필요해요") }
        }
        pendingMicAfterPermission = false
    }

    var selectedPoi by remember { mutableStateOf<String?>(null) }
    var activeDetailTab by remember { mutableStateOf(ArPoiTabBuilding) }
    var selectedStore by remember { mutableStateOf<String?>(null) }

    val categoryChipSpecs = remember { arExploreCategoryChipSpecs() }
    val buildingSamples = remember { arExploreBuildingSamples() }
    // 필터 미선택: 건물 핀 / 필터 선택: 건물별 해당 카테고리 매장 1개 핀
    val visiblePins = remember(categorySelection) {
        if (categorySelection.isEmpty()) {
            buildingSamples.map { ArPoiPin.BuildingPin(it) }
        } else {
            buildingSamples.mapNotNull { building ->
                building.floorInfo
                    .flatMap { it.stores }
                    .firstOrNull { store -> store.category in categorySelection }
                    ?.let { store -> ArPoiPin.StorePin(store, building) }
            }
        }
    }
    val arExploreDemoHits = remember {
        listOf(
            ArExploreSearchHitUi("알리바바 케밥", "식당", "52m", "할랄 인증"),
            ArExploreSearchHitUi("할랄가든 명동점", "식당", "120m", "할랄 인증"),
            ArExploreSearchHitUi("명동성당", "관광지", "350m", null),
            ArExploreSearchHitUi("우리은행 환전소", "환전", "80m", null),
        )
    }
    val arRecentQueries = remember(arSearchHistoryTick, isSearchOpen) {
        if (isSearchOpen) searchHistoryPrefs.getRecent() else emptyList()
    }
    val displayedArHits = remember(arSearchQuery, showArSearchResults, arExploreDemoHits) {
        if (!showArSearchResults) {
            emptyList()
        } else {
            filterArExploreHits(arSearchQuery, arExploreDemoHits)
        }
    }

    LaunchedEffect(isSearchOpen) {
        if (isSearchOpen) {
            arSearchQuery = ""
            showArSearchResults = false
        }
    }

    val submitArSearch: () -> Unit = {
        val q = arSearchQuery.trim()
        if (q.isNotEmpty()) {
            searchHistoryPrefs.add(q)
            arSearchHistoryTick++
            showArSearchResults = true
        }
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
                                ScanPangDimens.arSideFab44,
                                ScanPangDimens.arStatusPillHeight,
                            ),
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ArCircleIconButton(
                        icon = Icons.Rounded.CameraAlt,
                        contentDescription = "화면 고정",
                        onClick = { isFrozen = !isFrozen },
                        surfaceColor = if (isFrozen) ScanPangColors.ArPrimaryTranslucent else ScanPangColors.ArOverlayWhite80,
                        iconTint = if (isFrozen) Color.White else ScanPangColors.OnSurfaceStrong,
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
                    pins = visiblePins,
                    onPinClick = { pin ->
                        selectedPoi = pin.buildingName
                        activeDetailTab = ArPoiTabBuilding
                        selectedStore = null
                    },
                )
                ArExploreSideColumn(
                    onTtsClick = {
                        val next = !isTtsOn
                        isTtsOn = next
                        appSettingsPrefs.setTtsEnabled(next)
                        val msg = if (next) "음성 안내 켜짐" else "음성 안내 꺼짐"
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    },
                    isTtsOn = isTtsOn,
                    isTtsPlaying = isTtsPlaying,
                )
            }

            // 하단 그라데이션 배경 — 탭바 뒤쪽 화면 맨 밑까지 확장 (Figma Frame 1: transparent → white 50%)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.5f)),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    // ScanPangTabBar가 NavHost 위에 오버레이되므로, 홈과 동일하게 탭 영역만큼 비움
                    .padding(bottom = ScanPangDimens.mainTabContentBottomInset)
                    .navigationBarsPadding(),
            ) {
                ArExploreInteractiveChatSection(
                    messages = chatMessages,
                    inputText = chatInput,
                    onInputChange = { chatInput = it },
                    onSend = send@{
                        val q = chatInput.trim()
                        if (q.isEmpty()) return@send
                        scope.launch {
                            val reply = agentService.sendMessage(q)
                            chatMessages = chatMessages +
                                ArAgentChatMessage(text = q, isUser = true) +
                                ArAgentChatMessage(text = reply, isUser = false)
                            chatInput = ""
                            ttsController.speakIfEnabled(reply, isTtsOn)
                        }
                    },
                    isSttListening = isSttListening,
                    onMicClick = mic@{
                        val h = speechHelperRef
                        if (isSttListening) {
                            h?.stopListening()
                            return@mic
                        }
                        if (h == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar("음성 입력을 준비하지 못했어요")
                            }
                            return@mic
                        }
                        if (!h.isRecognitionAvailable()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("이 기기에서 음성 인식을 쓸 수 없어요")
                            }
                            return@mic
                        }
                        val hasMic = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO,
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasMic) {
                            h.startListening()
                        } else {
                            pendingMicAfterPermission = true
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    listState = chatListState,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            AnimatedVisibility(
                visible = isFilterOpen,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .clickable { isFilterOpen = false },
                ) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(horizontal = ScanPangDimens.arFilterPanelHorizontal)
                            .padding(top = ScanPangDimens.arFilterPanelTopOffset)
                            .clickable(enabled = false) { },
                        shape = RoundedCornerShape(16.dp),
                        color = ScanPangColors.ArOverlayWhite93,
                        shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(ScanPangDimens.arTopBarHorizontal)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            ArExploreFilterPanelFigma(
                                categorySpecs = categoryChipSpecs,
                                categorySelection = categorySelection,
                                onCategoryToggle = { label ->
                                    categorySelection =
                                        if (label in categorySelection) {
                                            categorySelection - label
                                        } else {
                                            categorySelection + label
                                        }
                                },
                                onReset = { categorySelection = emptySet() },
                                onApply = { isFilterOpen = false },
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isSearchOpen,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .clickable { isSearchOpen = false; showArSearchResults = false },
                ) {
                    ArExploreSearchPanelContent(
                        query = arSearchQuery,
                        onQueryChange = { arSearchQuery = it },
                        onSubmitSearch = submitArSearch,
                        recentQueries = arRecentQueries,
                        onRecentQueryClick = { q ->
                            arSearchQuery = q
                            searchHistoryPrefs.add(q)
                            arSearchHistoryTick++
                            showArSearchResults = true
                        },
                        onRecentQueryRemove = { q ->
                            searchHistoryPrefs.remove(q)
                            arSearchHistoryTick++
                        },
                        onRecentClearAll = {
                            searchHistoryPrefs.clearAll()
                            arSearchHistoryTick++
                        },
                        showResultList = showArSearchResults,
                        searchHits = displayedArHits,
                        onHitViewInfo = { hit ->
                            selectedPoi = hit.title
                            activeDetailTab = ArPoiTabBuilding
                            isSearchOpen = false
                            showArSearchResults = false
                        },
                        onHitStartNav = {
                            navController.navigate(AppRoutes.ArNavMap) { launchSingleTop = true }
                            isSearchOpen = false
                            showArSearchResults = false
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(horizontal = ScanPangDimens.arFilterPanelHorizontal)
                            .padding(top = ScanPangDimens.arFilterPanelTopOffset)
                            .clickable(enabled = false) { },
                    )
                }
            }

            selectedPoi?.let { poi ->
                ArPoiFloatingDetailOverlay(
                    poiName = poi,
                    activeDetailTab = activeDetailTab,
                    onActiveDetailTabChange = { activeDetailTab = it },
                    onDismiss = {
                        selectedPoi = null
                        selectedStore = null
                        activeDetailTab = ArPoiTabBuilding
                    },
                    onFloorStoreClick = { selectedStore = it },
                    onSave = {
                        scope.launch { snackbarHostState.showSnackbar("저장되었습니다") }
                    },
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

private fun filterArExploreHits(query: String, all: List<ArExploreSearchHitUi>): List<ArExploreSearchHitUi> {
    val t = query.trim().lowercase()
    if (t.isEmpty()) return all
    val filtered = all.filter { hit ->
        hit.title.lowercase().contains(t) ||
            hit.category.lowercase().contains(t) ||
            hit.badgeLabel?.lowercase()?.contains(t) == true
    }
    return filtered.ifEmpty { all }
}

