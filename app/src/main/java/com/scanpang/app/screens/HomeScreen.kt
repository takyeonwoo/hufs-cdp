package com.scanpang.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.AltRoute
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.scanpang.app.components.RecentlyViewedRow
import com.scanpang.app.components.recentDetailRoute
import com.scanpang.app.data.DummyData
import com.scanpang.app.data.OnboardingPreferences
import com.scanpang.app.data.RecentlyViewedEntry
import com.scanpang.app.data.RecentlyViewedStore
import com.scanpang.app.data.ValueAdded
import com.scanpang.app.navigation.AppRoutes
import com.scanpang.app.navigation.navigateToSearchWithQuery
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

/**
 * 홈 빠른액션 카드 한 장의 스펙. 라벨/아이콘과, NavController 가 들어오면 어디로 보낼지 결정한다.
 * ValueAdded 분기별로 [quickActionsFor] 가 3장을 만들어 [HomeScreen] 으로 흘려준다.
 */
private data class HomeQuickAction(
    val title: String,
    val icon: ImageVector,
    val navigate: (NavController) -> Unit,
)

private fun quickActionsFor(value: ValueAdded): List<HomeQuickAction> = when (value) {
    ValueAdded.HALAL -> listOf(
        HomeQuickAction("할랄 식당", Icons.Rounded.Restaurant) { it.navigate(AppRoutes.NearbyHalal) },
        HomeQuickAction("기도실", Icons.Rounded.Mosque) { it.navigate(AppRoutes.NearbyPrayer) },
        HomeQuickAction("관광명소", Icons.Rounded.Whatshot) { it.navigateToSearchWithQuery("관광지") },
    )
    ValueAdded.VEGAN -> listOf(
        HomeQuickAction("비건 식당", Icons.Rounded.Restaurant) { it.navigateToSearchWithQuery("비건 식당") },
        HomeQuickAction("비건 카페", Icons.Rounded.Coffee) { it.navigateToSearchWithQuery("비건 카페") },
        HomeQuickAction("관광명소", Icons.Rounded.Whatshot) { it.navigateToSearchWithQuery("관광지") },
    )
    ValueAdded.GENERAL -> listOf(
        HomeQuickAction("식당", Icons.Rounded.Restaurant) { it.navigateToSearchWithQuery("식당") },
        HomeQuickAction("카페", Icons.Rounded.Coffee) { it.navigateToSearchWithQuery("카페") },
        HomeQuickAction("관광명소", Icons.Rounded.Whatshot) { it.navigateToSearchWithQuery("관광지") },
    )
}

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val onboardingPrefs = remember(context) { OnboardingPreferences(context) }
    val recentlyViewedStore = remember(context) { RecentlyViewedStore(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // 미설정 사용자(예: 신규 데모) 는 GENERAL 로 본다 — 키블라 카드 미노출 + 일반 quick actions.
    val valueAdded = remember(context) { onboardingPrefs.getValueAdded() ?: ValueAdded.GENERAL }
    val displayName = remember(context) { onboardingPrefs.getDisplayName() }
    val quickActions = remember(valueAdded) { quickActionsFor(valueAdded) }
    val showQiblaCard = valueAdded == ValueAdded.HALAL

    // Home 미리보기는 최신 2건만 노출. 나머지는 "더보기" 화면(AppRoutes.RecentlyViewed)에서 보여준다.
    var recentlyViewed by remember {
        mutableStateOf(recentlyViewedStore.getAll().take(MAX_RECENT_HOME))
    }
    // 사용자가 상세를 보고 Home 으로 돌아오는 순간 리스트가 즉시 갱신되도록.
    var hasMoreRecent by remember { mutableStateOf(recentlyViewedStore.getAll().size > MAX_RECENT_HOME) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val all = recentlyViewedStore.getAll()
                recentlyViewed = all.take(MAX_RECENT_HOME)
                hasMoreRecent = all.size > MAX_RECENT_HOME
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ScanPangColors.Surface,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScanPangColors.Surface)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = ScanPangDimens.mainTabContentBottomInset),
        ) {
            HomeTopSection(
                navController = navController,
                displayName = displayName,
                showQiblaCard = showQiblaCard,
                quickActions = quickActions,
                recentlyViewed = recentlyViewed,
                showMoreRecent = hasMoreRecent,
            )
        }
    }
}

@Composable
private fun HomeTopSection(
    navController: NavController,
    displayName: String?,
    showQiblaCard: Boolean,
    quickActions: List<HomeQuickAction>,
    recentlyViewed: List<RecentlyViewedEntry>,
    showMoreRecent: Boolean,
) {
    val greetingLine = if (!displayName.isNullOrBlank()) {
        "안녕하세요, ${displayName}님!"
    } else {
        "안녕하세요!"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScanPangSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(ScanPangDimens.homeSectionGap),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = ScanPangDimens.homeHeaderInset,
                    end = ScanPangDimens.homeHeaderInset,
                    top = ScanPangDimens.homeHeaderTop,
                    bottom = ScanPangDimens.homeHeaderInset,
                ),
            verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
        ) {
            Text(
                text = "$greetingLine\n오늘 ${DummyData.homeAreaName}을 탐험해볼까요?",
                style = ScanPangType.homeGreeting,
                color = ScanPangColors.OnSurfaceStrong,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.AltRoute,
                    contentDescription = null,
                    modifier = Modifier.size(ScanPangDimens.icon20),
                    tint = ScanPangColors.OnSurfaceMuted,
                )
                Text(
                    text = "현재 위치: ${DummyData.currentLocationLabel}",
                    style = ScanPangType.meta13,
                    color = ScanPangColors.OnSurfaceMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = ScanPangDimens.homeSectionGap),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ScanPangDimens.homeSearchBarHeight)
                    .clip(ScanPangShapes.radius14)
                    .background(ScanPangColors.Background)
                    .clickable { navController.navigate(AppRoutes.Search) }
                    .padding(horizontal = ScanPangSpacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
            ) {
                Icon(
                    imageVector = Icons.Rounded.NearMe,
                    contentDescription = null,
                    modifier = Modifier.size(ScanPangDimens.tabIcon),
                    tint = ScanPangColors.OnSurfacePlaceholder,
                )
                Text(
                    text = "목적지 검색",
                    style = ScanPangType.searchPlaceholder,
                    color = ScanPangColors.OnSurfacePlaceholder,
                )
            }
        }

        if (showQiblaCard) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = ScanPangDimens.homeSectionGap),
            ) {
                QiblaSummaryCard(onClick = { navController.navigate(AppRoutes.Qibla) })
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = ScanPangDimens.homeSectionGap),
            horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
        ) {
            quickActions.forEach { action ->
                QuickActionChip(
                    title = action.title,
                    icon = action.icon,
                    modifier = Modifier.weight(1f),
                    onClick = { action.navigate(navController) },
                )
            }
        }

        RecentlyViewedSection(
            recentlyViewed = recentlyViewed,
            showMore = showMoreRecent,
            onMoreClick = { navController.navigate(AppRoutes.RecentlyViewed) },
            onItemClick = { entry ->
                navController.navigate(recentDetailRoute(entry.categoryKey, entry.id))
            },
        )
    }
}

@Composable
private fun QiblaSummaryCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ScanPangShapes.radius14)
            .background(ScanPangColors.PrimarySoft)
            .clickable(onClick = onClick)
            .padding(horizontal = ScanPangSpacing.lg, vertical = ScanPangDimens.homeQiblaRowVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.xs),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Explore,
                    contentDescription = null,
                    modifier = Modifier.size(ScanPangDimens.tabIcon),
                    tint = ScanPangColors.Primary,
                )
                Text(
                    text = DummyData.qiblaDirectionLabel,
                    style = ScanPangType.title14,
                    color = ScanPangColors.OnSurfaceStrong,
                )
            }
            Text(
                text = DummyData.nextPrayerLabel,
                style = ScanPangType.caption12Medium,
                color = ScanPangColors.OnSurfaceMuted,
            )
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(ScanPangDimens.icon20),
            tint = ScanPangColors.Primary,
        )
    }
}

@Composable
private fun QuickActionChip(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(ScanPangShapes.radius14)
            .background(ScanPangColors.Background)
            .clickable(onClick = onClick)
            .padding(horizontal = ScanPangDimens.homeQuickChipHorizontal, vertical = ScanPangSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ScanPangDimens.tabIcon),
            tint = ScanPangColors.Primary,
        )
        Text(
            text = title,
            style = ScanPangType.quickLabel12,
            color = ScanPangColors.OnSurfaceStrong,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RecentlyViewedSection(
    recentlyViewed: List<RecentlyViewedEntry>,
    showMore: Boolean,
    onMoreClick: () -> Unit,
    onItemClick: (RecentlyViewedEntry) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = ScanPangSpacing.md),
        verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "최근 본 장소",
                style = ScanPangType.sectionTitle16,
                color = ScanPangColors.OnSurfaceStrong,
            )
            // "더보기" 는 누적 기록이 미리보기(2건)보다 많을 때만 의미가 있어 그때만 노출.
            if (showMore) {
                Text(
                    text = "더보기",
                    style = ScanPangType.caption12Medium,
                    color = ScanPangColors.Primary,
                    modifier = Modifier.clickable(onClick = onMoreClick),
                )
            }
        }
        if (recentlyViewed.isEmpty()) {
            RecentlyViewedEmpty()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm)) {
                recentlyViewed.forEach { entry ->
                    RecentlyViewedRow(entry = entry, onClick = { onItemClick(entry) })
                }
            }
        }
    }
}

@Composable
private fun RecentlyViewedEmpty() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ScanPangShapes.radius14)
            .background(ScanPangColors.Background)
            .padding(horizontal = ScanPangSpacing.lg, vertical = ScanPangSpacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "아직 본 장소가 없어요",
            style = ScanPangType.body14Regular,
            color = ScanPangColors.OnSurfaceMuted,
        )
    }
}

private const val MAX_RECENT_HOME = 2
