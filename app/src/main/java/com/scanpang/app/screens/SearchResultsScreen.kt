package com.scanpang.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NoDrinks
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.navigation.NavController
import com.scanpang.app.ui.theme.ScanPangType
import com.scanpang.app.components.ScanPangSearchFieldFilled
import com.scanpang.app.components.SearchResultBadgeKind
import com.scanpang.app.components.SearchResultPlaceCard
import com.scanpang.app.components.SearchResultTrustTag
import com.scanpang.app.navigation.AppRoutes
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangSpacing

private data class ResultItem(
    val title: String,
    val badgeKind: SearchResultBadgeKind,
    val badgeLabel: String,
    val cuisine: String,
    val distance: String,
    val trust: List<SearchResultTrustTag>,
)

@Composable
fun SearchResultsScreen(
    navController: NavController,
    searchQuery: String,
    modifier: Modifier = Modifier,
) {
    val displayQuery = searchQuery.trim().ifEmpty { "검색" }
    val resultItems = listOf(
        ResultItem(
            title = "명동부산집",
            badgeKind = SearchResultBadgeKind.HalalMeat,
            badgeLabel = "HALAL MEAT",
            cuisine = "한식 · 해산물",
            distance = "120m",
            trust = listOf(
                SearchResultTrustTag("무슬림 조리사", Icons.Rounded.Restaurant),
                SearchResultTrustTag("주류 미판매", Icons.Rounded.NoDrinks),
            ),
        ),
        ResultItem(
            title = "레팍라식당",
            badgeKind = SearchResultBadgeKind.HalalMeat,
            badgeLabel = "HALAL MEAT",
            cuisine = "말레이시아 · 한식",
            distance = "500m",
            trust = listOf(
                SearchResultTrustTag("무슬림 조리사", Icons.Rounded.Restaurant),
                SearchResultTrustTag("주류 미판매", Icons.Rounded.NoDrinks),
            ),
        ),
        ResultItem(
            title = "캄풍쿠",
            badgeKind = SearchResultBadgeKind.HalalMeat,
            badgeLabel = "HALAL MEAT",
            cuisine = "말레이시아 · 한식",
            distance = "780m",
            trust = listOf(
                SearchResultTrustTag("무슬림 조리사", Icons.Rounded.Restaurant),
                SearchResultTrustTag("주류 미판매", Icons.Rounded.NoDrinks),
            ),
        ),
        ResultItem(
            title = "희락갈치",
            badgeKind = SearchResultBadgeKind.Seafood,
            badgeLabel = "SEAFOOD",
            cuisine = "해산물",
            distance = "350m",
            trust = emptyList(),
        ),
    )
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ScanPangColors.Background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
    ) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScanPangColors.Background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(ScanPangDimens.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.lg),
        ) {
            item {
                ScanPangSearchFieldFilled(
                    query = displayQuery,
                    onClearClick = { navController.popBackStack() },
                )
            }
            item {
                Text(
                    text = "‘$displayQuery’ 검색 결과 ${resultItems.size}개",
                    style = ScanPangType.link13,
                    color = ScanPangColors.OnSurfaceMuted,
                )
            }
            items(resultItems) { row ->
                SearchResultPlaceCard(
                    title = row.title,
                    badgeKind = row.badgeKind,
                    badgeLabel = row.badgeLabel,
                    cuisineLabel = row.cuisine,
                    distance = row.distance,
                    isOpen = true,
                    trustTags = row.trust,
                    onClick = {
                        navController.navigate(AppRoutes.RestaurantDetail) { launchSingleTop = true }
                    },
                )
            }
        }
    }
}
