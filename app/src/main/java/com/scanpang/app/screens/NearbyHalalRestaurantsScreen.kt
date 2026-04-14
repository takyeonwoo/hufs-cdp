package com.scanpang.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.scanpang.app.components.SearchResultBadgeKind
import com.scanpang.app.components.SearchResultPlaceCard
import com.scanpang.app.components.SearchResultTrustTag
import com.scanpang.app.navigation.AppRoutes
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

private val filterLabels = listOf(
    "전체",
    "HALAL MEAT",
    "SEAFOOD",
    "VEGGIE",
    "SALAM SEOUL",
)

private data class NearbyHalalPlace(
    val title: String,
    /** [filterLabels] 중 카테고리 칩과 동일한 문자열 (전체 제외) */
    val categoryFilter: String,
    val badgeKind: SearchResultBadgeKind,
    val badgeLabel: String,
    val cuisineLabel: String,
    val distance: String,
    val isOpen: Boolean,
    val trustTags: List<SearchResultTrustTag>,
)

/**
 * Figma: 주변 할랄 식당 (`290:2034`)
 */
@Composable
fun NearbyHalalRestaurantsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var filterIndex by remember { mutableIntStateOf(0) }

    val allPlaces = remember {
        listOf(
            NearbyHalalPlace(
                title = "할랄가든 명동점",
                categoryFilter = "HALAL MEAT",
                badgeKind = SearchResultBadgeKind.HalalMeat,
                badgeLabel = "HALAL MEAT",
                cuisineLabel = "한식",
                distance = "120m",
                isOpen = true,
                trustTags = listOf(
                    SearchResultTrustTag("할랄 인증", Icons.Rounded.Verified),
                    SearchResultTrustTag("방문자 추천", Icons.Rounded.Star),
                ),
            ),
            NearbyHalalPlace(
                title = "이스탄불 카페",
                categoryFilter = "HALAL MEAT",
                badgeKind = SearchResultBadgeKind.HalalMeat,
                badgeLabel = "HALAL MEAT",
                cuisineLabel = "터키",
                distance = "240m",
                isOpen = true,
                trustTags = listOf(
                    SearchResultTrustTag("할랄 인증", Icons.Rounded.Verified),
                ),
            ),
            NearbyHalalPlace(
                title = "레팍라 식당",
                categoryFilter = "HALAL MEAT",
                badgeKind = SearchResultBadgeKind.HalalMeat,
                badgeLabel = "HALAL MEAT",
                cuisineLabel = "말레이시아",
                distance = "310m",
                isOpen = true,
                trustTags = listOf(
                    SearchResultTrustTag("할랄 인증", Icons.Rounded.Verified),
                ),
            ),
            NearbyHalalPlace(
                title = "바다향 횟집",
                categoryFilter = "SEAFOOD",
                badgeKind = SearchResultBadgeKind.Seafood,
                badgeLabel = "SEAFOOD",
                cuisineLabel = "해산물",
                distance = "350m",
                isOpen = false,
                trustTags = emptyList(),
            ),
            NearbyHalalPlace(
                title = "제주 해물탕",
                categoryFilter = "SEAFOOD",
                badgeKind = SearchResultBadgeKind.Seafood,
                badgeLabel = "SEAFOOD",
                cuisineLabel = "한식 · 해산물",
                distance = "480m",
                isOpen = true,
                trustTags = listOf(
                    SearchResultTrustTag("할랄 인증", Icons.Rounded.Verified),
                ),
            ),
            NearbyHalalPlace(
                title = "그린샐러드 하우스",
                categoryFilter = "VEGGIE",
                badgeKind = SearchResultBadgeKind.Veggie,
                badgeLabel = "VEGGIE",
                cuisineLabel = "샐러드 · 비건",
                distance = "180m",
                isOpen = true,
                trustTags = listOf(
                    SearchResultTrustTag("방문자 추천", Icons.Rounded.Star),
                ),
            ),
            NearbyHalalPlace(
                title = "포레스트 키친",
                categoryFilter = "VEGGIE",
                badgeKind = SearchResultBadgeKind.Veggie,
                badgeLabel = "VEGGIE",
                cuisineLabel = "채식 뷔페",
                distance = "420m",
                isOpen = true,
                trustTags = emptyList(),
            ),
            NearbyHalalPlace(
                title = "살람서울 명동",
                categoryFilter = "SALAM SEOUL",
                badgeKind = SearchResultBadgeKind.SalamSeoul,
                badgeLabel = "SALAM SEOUL",
                cuisineLabel = "퓨전",
                distance = "95m",
                isOpen = true,
                trustTags = listOf(
                    SearchResultTrustTag("할랄 인증", Icons.Rounded.Verified),
                ),
            ),
            NearbyHalalPlace(
                title = "살람서울 카페",
                categoryFilter = "SALAM SEOUL",
                badgeKind = SearchResultBadgeKind.SalamSeoul,
                badgeLabel = "SALAM SEOUL",
                cuisineLabel = "디저트",
                distance = "260m",
                isOpen = false,
                trustTags = emptyList(),
            ),
        )
    }

    val visiblePlaces = remember(filterIndex, allPlaces) {
        if (filterIndex == 0) allPlaces
        else {
            val key = filterLabels[filterIndex]
            allPlaces.filter { it.categoryFilter == key }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ScanPangColors.Surface,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
    ) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScanPangColors.Surface)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(
                horizontal = ScanPangDimens.screenHorizontal,
                vertical = ScanPangSpacing.md,
            ),
            verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "뒤로",
                            tint = ScanPangColors.OnSurfaceStrong,
                        )
                    }
                    Text(
                        text = "주변 할랄 식당",
                        style = ScanPangType.detailScreenTitle22,
                        color = ScanPangColors.OnSurfaceStrong,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ScanPangShapes.radius14,
                    color = ScanPangColors.Background,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ScanPangDimens.searchBarHeightActive)
                            .padding(horizontal = ScanPangDimens.searchBarInnerHorizontal),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = ScanPangColors.OnSurfacePlaceholder,
                        )
                        Text(
                            text = "식당 이름 또는 메뉴 검색",
                            style = ScanPangType.caption12Medium,
                            color = ScanPangColors.OnSurfacePlaceholder,
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
                ) {
                    filterLabels.forEachIndexed { index, label ->
                        val selected = index == filterIndex
                        Surface(
                            modifier = Modifier
                                .clip(ScanPangShapes.filterChip)
                                .clickable { filterIndex = index },
                            shape = ScanPangShapes.filterChip,
                            color = if (selected) ScanPangColors.Primary else ScanPangColors.Surface,
                            border = BorderStroke(
                                ScanPangDimens.borderHairline,
                                ScanPangColors.OutlineSubtle,
                            ),
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(
                                    horizontal = ScanPangSpacing.md,
                                    vertical = ScanPangDimens.chipPadVertical,
                                ),
                                style = ScanPangType.caption12Medium,
                                color = if (selected) Color.White else ScanPangColors.OnSurfaceMuted,
                            )
                        }
                    }
                }
            }
            items(
                items = visiblePlaces,
                key = { it.title + it.distance },
            ) { place ->
                SearchResultPlaceCard(
                    title = place.title,
                    badgeKind = place.badgeKind,
                    badgeLabel = place.badgeLabel,
                    cuisineLabel = place.cuisineLabel,
                    distance = place.distance,
                    isOpen = place.isOpen,
                    trustTags = place.trustTags,
                    onClick = {
                        navController.navigate(AppRoutes.RestaurantDetail) { launchSingleTop = true }
                    },
                )
            }
        }
    }
}
