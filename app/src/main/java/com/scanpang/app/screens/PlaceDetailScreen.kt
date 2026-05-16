@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.scanpang.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.automirrored.rounded.Accessible
import androidx.compose.material.icons.rounded.Healing
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalParking
import androidx.compose.material.icons.rounded.MiscellaneousServices
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.scanpang.app.data.DummyData
import com.scanpang.app.data.ExchangeRate
import com.scanpang.app.data.MenuItem
import com.scanpang.app.data.Place
import com.scanpang.app.data.RestaurantPlace
import com.scanpang.app.data.SubwayDetail
import com.scanpang.app.data.SubwayExit
import com.scanpang.app.data.SubwayScheduleDir
import com.scanpang.app.data.SubwayFastAlight
import com.scanpang.app.data.galleryModels
import com.scanpang.app.navigation.AppRoutes
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

private val INFO_ROW_SPACING = 14.dp
private val SECTION_INNER_SPACING = 12.dp

@Composable
fun PlaceDetailScreen(
    navController: NavController,
    categoryKey: String,
    placeId: String,
    modifier: Modifier = Modifier,
) {
    val place = remember(categoryKey, placeId) { DummyData.findPlaceById(categoryKey, placeId) }
    if (place == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val context = LocalContext.current

    val restaurantExtra = remember(categoryKey, placeId) {
        if (categoryKey == "restaurant") DummyData.halalRestaurants.firstOrNull { it.place.id == placeId } else null
    }
    val menuItems = remember(categoryKey, placeId) {
        when (categoryKey) {
            "restaurant" -> restaurantExtra?.menuItems.orEmpty()
            "cafe" -> DummyData.cafeRepresentativeMenus[placeId].orEmpty()
            else -> emptyList()
        }
    }
    val exchangeRates = remember(categoryKey) {
        if (categoryKey in setOf("exchange", "atm", "bank")) DummyData.exchangeRates else emptyList()
    }
    val subwayDetail = remember(categoryKey, placeId) {
        if (categoryKey == "subway") DummyData.subwayDetails[placeId] else null
    }

    val hasHeroPhoto = categoryKey !in setOf("atm", "subway", "restroom", "locker")
    val canFullscreen = categoryKey in setOf("restaurant", "tourist")

    val imageModels = remember(place.id, hasHeroPhoto) {
        if (hasHeroPhoto) place.galleryModels(defaultPlaceDetailGallery()) else emptyList()
    }
    val pagerState = if (hasHeroPhoto) rememberPagerState(pageCount = { imageModels.size.coerceAtLeast(1) }) else null
    var fullscreenOpen by remember { mutableStateOf(false) }

    val bookmark = rememberDetailBookmark(
        placeId = place.id,
        placeName = place.name,
        category = place.category,
        distanceLine = "${place.category} · ${place.distance}",
        tags = place.tags,
        categoryKey = categoryKey,
    )

    if (fullscreenOpen && pagerState != null) {
        DetailImageFullscreenDialog(
            gallery = imageModels,
            pagerState = pagerState,
            onDismiss = { fullscreenOpen = false },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScanPangColors.Surface)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        if (hasHeroPhoto && pagerState != null) {
            DetailHeroPhotoPager(
                gallery = imageModels,
                pagerState = pagerState,
                onBack = { navController.popBackStack() },
                onFullscreenClick = if (canFullscreen) ({ fullscreenOpen = true }) else null,
            )
        } else {
            DetailBackOnlyArea(onBack = { navController.popBackStack() })
        }

        Column(
            modifier = Modifier
                .padding(horizontal = ScanPangDimens.screenHorizontal)
                .padding(top = ScanPangSpacing.md, bottom = ScanPangDimens.detailContentBottomPad),
            verticalArrangement = Arrangement.spacedBy(ScanPangDimens.detailSectionSpacing),
        ) {
            // 제목 + (지하철 호선 배지) + 북마크
            val subwayLine = if (categoryKey == "subway")
                place.tags.firstOrNull { it.contains("호선") } else null
            val displayTitle = if (subwayLine != null) "${place.name} $subwayLine" else place.name
            DetailTitleBookmarkRow(
                title = displayTitle,
                bookmarked = bookmark.bookmarked,
                onBookmarkClick = bookmark.onToggle,
                trailingContent = subwayLine?.let { line ->
                    {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ScanPangColors.Primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = line.replace("호선", ""),
                                style = ScanPangType.detailSectionTitle15,
                                color = Color.White,
                            )
                        }
                    }
                },
            )

            // 메타행
            when (categoryKey) {
                "restaurant" -> RestaurantMetaRow(place)
                "atm" -> DetailCategoryTagDistanceRow(
                    categoryLabel = place.category,
                    distanceText = place.distance,
                    trailing = { AtmOperationBadge(place) },
                )
                "locker", "restroom" -> DetailCategoryTagDistanceRow(
                    categoryLabel = place.category,
                    distanceText = place.distance,
                    isOpen = null,
                )
                else -> DetailCategoryTagDistanceRow(
                    categoryLabel = place.category,
                    distanceText = place.distance,
                    isOpen = if (place.openHours.isNotBlank()) place.isOpen else null,
                )
            }

            // 할랄 신뢰 칩 (식당만)
            if (categoryKey == "restaurant" && restaurantExtra != null) {
                PlaceHalalChipsRow(restaurantExtra)
            }

            DetailScreenDivider()

            // CTA
            DetailCtaRow(
                onNavigate = { navController.navigate(AppRoutes.arNavMapRoute(place.name)) { launchSingleTop = true } },
                onPhoneClick = { context.openPhoneDialer(place.phone) },
                hasPhone = place.phone.isNotBlank(),
            )

            // 오늘 방문 가능 여부 (영업시간이 있는 카테고리만)
            val showVisitStatus = categoryKey in setOf(
                "restaurant", "cafe", "shopping", "convenience_store", "exchange",
                "bank", "hospital", "pharmacy", "tourist",
            )
            if (showVisitStatus && place.openHours.isNotBlank()) {
                DetailScreenDivider()
                DetailTodayVisitStatus(
                    isOpen = place.isOpen,
                    openHours = place.openHours,
                    lastOrder = restaurantExtra?.lastOrder ?: "",
                )
            }

            DetailScreenDivider()

            // 본문 — 데이터에 값이 있는 섹션만 자동으로 표시
            PlaceDetailContent(
                place = place,
                menuItems = menuItems,
                exchangeRates = exchangeRates,
                subwayDetail = subwayDetail,
            )
        }
    }
}

// ── 통합 본문 — 카테고리 분기 없이 데이터 유무로 섹션 표시 결정 ────────────

@Composable
private fun PlaceDetailContent(
    place: Place,
    menuItems: List<MenuItem>,
    exchangeRates: List<ExchangeRate>,
    subwayDetail: SubwayDetail? = null,
) {
    // 대표 메뉴 (식당·카페에만 데이터 있음)
    if (menuItems.isNotEmpty()) {
        DetailSection(title = "대표 메뉴") {
            Column(verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm)) {
                menuItems.forEach { m -> DetailMenuPriceRow(name = m.name, price = m.price) }
            }
        }
        DetailScreenDivider()
    }

    // 소개
    if (place.description.isNotBlank()) {
        DetailSection(title = "소개") {
            DetailIntroBody(text = place.description)
        }
        DetailScreenDivider()
    }

    // 상세 정보 — Place 필드가 비어있으면 해당 행 자동 생략
    DetailSection(title = "상세 정보") {
        Column(verticalArrangement = Arrangement.spacedBy(INFO_ROW_SPACING)) {
            if (place.openHours.isNotBlank()) DetailInfoLine(Icons.Rounded.AccessTime, "영업시간", place.openHours)
            if (place.address.isNotBlank()) DetailInfoLine(Icons.Rounded.Place, "주소", place.address)
            if (place.phone.isNotBlank()) DetailInfoLine(Icons.Rounded.Phone, "전화", place.phone)
            if (place.floor.isNotBlank()) DetailInfoLine(Icons.Rounded.Store, "매장 층수", place.floor)
            val toiletStr = buildList {
                if (place.toiletMale.isNotBlank()) add("남성 ${place.toiletMale}칸")
                if (place.toiletFemale.isNotBlank()) add("여성 ${place.toiletFemale}칸")
            }.joinToString(", ")
            if (toiletStr.isNotBlank()) DetailInfoLine(Icons.Rounded.Wc, "칸 수", toiletStr)
            if (place.facilityTags.isNotBlank()) DetailInfoLine(Icons.AutoMirrored.Rounded.Accessible, "편의시설", place.facilityTags)
            if (place.safetyTags.isNotBlank()) DetailInfoLine(Icons.Rounded.Security, "안전시설", place.safetyTags)
            if (place.parking.isNotBlank()) DetailInfoLine(Icons.Rounded.LocalParking, "주차 가능 여부", place.parking)
            if (place.website.isNotBlank()) DetailInfoLine(Icons.Rounded.Language, "웹사이트", place.website)
            if (place.convenienceServices.isNotBlank()) DetailInfoLine(Icons.Rounded.MiscellaneousServices, "편의시설", place.convenienceServices)
            if (place.departments.isNotBlank()) DetailInfoLine(Icons.Rounded.Healing, "진료과목", place.departments)
        }
    }

    // 환율 (환전소·ATM·은행에만 데이터 있음)
    if (exchangeRates.isNotEmpty()) {
        DetailScreenDivider()
        DetailSection(title = "오늘의 환율") {
            Column(verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm)) {
                exchangeRates.forEach { row -> ExchangeRateRow(row) }
            }
        }
    }

    // 지하철 전용 섹션
    if (subwayDetail != null) {
        if (subwayDetail.scheduleUp != null || subwayDetail.scheduleDown != null) {
            DetailScreenDivider()
            DetailSection(title = "열차 시간표") {
                SubwayScheduleSection(subwayDetail)
            }
        }
        if (subwayDetail.exits.isNotEmpty()) {
            DetailScreenDivider()
            DetailSection(title = "출구 정보") {
                SubwayExitsSection(subwayDetail.exits)
            }
        }
        if (subwayDetail.fastAlights.isNotEmpty()) {
            DetailScreenDivider()
            DetailSection(title = "빠른 하차") {
                SubwayFastAlightsSection(subwayDetail.fastAlights)
            }
        }
    }
}

// ── 섹션 래퍼 ────────────────────────────────────────────────────────────────

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SECTION_INNER_SPACING)) {
        DetailSectionHeader(title = title)
        content()
    }
}

// ── 메타행 헬퍼 ──────────────────────────────────────────────────────────────

@Composable
private fun RestaurantMetaRow(place: Place) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${place.subCategory.ifBlank { "한식" }} · ${place.distance}",
            style = ScanPangType.detailMetaSubtitle13,
            color = ScanPangColors.OnSurfaceMuted,
        )
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(ScanPangDimens.icon5)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(if (place.isOpen) ScanPangColors.StatusOpen else ScanPangColors.Error),
        )
        Text(
            text = if (place.isOpen) "영업 중" else "영업 종료",
            style = ScanPangType.meta11SemiBold,
            color = if (place.isOpen) ScanPangColors.StatusOpen else ScanPangColors.Error,
        )
    }
}

@Composable
private fun AtmOperationBadge(place: Place) {
    val is24h = place.openHours.contains("24") || place.tags.any { it.contains("24") }
    Surface(
        shape = ScanPangShapes.badge6,
        color = if (is24h) ScanPangColors.DetailVisitOpenSurface else ScanPangColors.DetailFacilityTagBackground,
    ) {
        Text(
            text = if (is24h) "24시간" else "시간제",
            modifier = Modifier.padding(horizontal = ScanPangSpacing.sm, vertical = ScanPangDimens.chipPadVertical),
            style = ScanPangType.category11SemiBold,
            color = if (is24h) ScanPangColors.TrustPillText else ScanPangColors.OnSurfaceMuted,
        )
    }
}

// ── 할랄 신뢰 칩 ─────────────────────────────────────────────────────────────

@Composable
private fun PlaceHalalChipsRow(rp: RestaurantPlace) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ScanPangDimens.stackGap6),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HalalCategoryChip(label = rp.halalCategory)
        rp.place.tags.take(2).forEach { tag ->
            val icon: ImageVector = if (tag.contains("인증") || tag.contains("살람")) Icons.Rounded.Verified else Icons.Rounded.Star
            HalalTrustChip(text = tag, icon = icon)
        }
    }
}

@Composable
private fun HalalCategoryChip(label: String) {
    val (bg, fg) = when (label) {
        "HALAL MEAT" -> ScanPangColors.HalalMeatBadgeBackground to ScanPangColors.HalalMeatBadgeText
        "SEAFOOD" -> ScanPangColors.SeafoodBadgeBackground to ScanPangColors.Primary
        "VEGGIE" -> ScanPangColors.VeggieBadgeBackground to ScanPangColors.VeggieBadgeText
        "SALAM SEOUL" -> ScanPangColors.SalamSeoulBadgeBackground to ScanPangColors.SalamSeoulBadgeText
        else -> ScanPangColors.HalalMeatBadgeBackground to ScanPangColors.HalalMeatBadgeText
    }
    Surface(
        shape = ScanPangShapes.badge6,
        color = bg,
        border = BorderStroke(ScanPangDimens.borderHairline, ScanPangColors.OutlineSubtle),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = ScanPangDimens.trustChipHorizontal,
                vertical = ScanPangDimens.trustChipVertical,
            ),
            style = ScanPangType.badge9SemiBold,
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HalalTrustChip(text: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .clip(ScanPangShapes.badge6)
            .background(ScanPangColors.TrustPillBackground)
            .padding(
                horizontal = ScanPangDimens.trustChipHorizontal,
                vertical = ScanPangDimens.trustChipVertical,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ScanPangDimens.trustIconGap),
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ScanPangDimens.icon10),
            tint = ScanPangColors.TrustPillText,
        )
        Text(
            text = text,
            style = ScanPangType.badge9SemiBold,
            color = ScanPangColors.TrustPillText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ── 환율 행 ───────────────────────────────────────────────────────────────────

@Composable
private fun ExchangeRateRow(row: ExchangeRate) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ScanPangShapes.detailMenuRow)
            .background(ScanPangColors.DetailMenuRowBackground)
            .padding(horizontal = ScanPangSpacing.md, vertical = ScanPangSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${row.flag} ${row.currency} → KRW",
            style = ScanPangType.caption12Medium,
            color = ScanPangColors.OnSurfaceStrong,
        )
        Text(
            text = row.rate,
            style = ScanPangType.detailMenuPrice14,
            color = ScanPangColors.OnSurfaceStrong,
        )
    }
}

// ── 지하철 전용 섹션 ──────────────────────────────────────────────────────────

@Composable
private fun SubwayScheduleSection(detail: SubwayDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        detail.scheduleUp?.let { SubwayScheduleRow("상행", it) }
        detail.scheduleDown?.let { SubwayScheduleRow("하행", it) }
    }
}

@Composable
private fun SubwayScheduleRow(label: String, dir: SubwayScheduleDir) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ScanPangShapes.radius12,
        color = ScanPangColors.Background,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = RoundedCornerShape(4.dp), color = ScanPangColors.PrimarySoft) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    style = ScanPangType.badge9SemiBold,
                    color = ScanPangColors.Primary,
                )
            }
            Text(
                text = "  ${dir.toward} 방면",
                modifier = Modifier.weight(1f),
                style = ScanPangType.caption12,
                color = ScanPangColors.OnSurfaceMuted,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text("첫차", style = ScanPangType.tag11Medium, color = ScanPangColors.OnSurfaceMuted)
                    Text(dir.first, style = ScanPangType.detailSectionTitle15, color = ScanPangColors.OnSurfaceStrong)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text("막차", style = ScanPangType.tag11Medium, color = ScanPangColors.OnSurfaceMuted)
                    Text(dir.last, style = ScanPangType.detailSectionTitle15, color = ScanPangColors.OnSurfaceStrong)
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun SubwayExitsSection(exits: List<SubwayExit>) {
    var selectedExitNo by remember { mutableStateOf(exits.firstOrNull()?.exitNo ?: "") }
    val selectedExit = exits.firstOrNull { it.exitNo == selectedExitNo }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(exits) { exit ->
                val selected = exit.exitNo == selectedExitNo
                Surface(
                    modifier = Modifier.clickable { selectedExitNo = exit.exitNo },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selected) ScanPangColors.Primary else ScanPangColors.Background,
                ) {
                    Text(
                        text = "${exit.exitNo}번",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = ScanPangType.quickLabel12,
                        color = if (selected) Color.White else ScanPangColors.OnSurfaceMuted,
                    )
                }
            }
        }
        selectedExit?.let { exit ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = ScanPangShapes.radius12,
                color = ScanPangColors.Background,
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "${exit.exitNo}번 출구 주변",
                        style = ScanPangType.quickLabel12,
                        color = ScanPangColors.OnSurfaceStrong,
                    )
                    exit.facilities.forEach { fac ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(ScanPangColors.OnSurfaceMuted),
                            )
                            Text(fac, style = ScanPangType.caption12, color = ScanPangColors.OnSurfaceMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubwayFastAlightsSection(fastAlights: List<SubwayFastAlight>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ScanPangShapes.radius12,
        color = ScanPangColors.Background,
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            fastAlights.take(2).forEachIndexed { index, item ->
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "${item.direction} 방면",
                        style = ScanPangType.caption12,
                        color = ScanPangColors.OnSurfaceMuted,
                    )
                    Text(
                        text = item.door,
                        style = ScanPangType.detailSectionTitle15,
                        color = ScanPangColors.OnSurfaceStrong,
                    )
                }
                if (index == 0 && fastAlights.size >= 2) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(width = 1.dp, height = 40.dp)
                            .background(ScanPangColors.OutlineSubtle),
                    )
                }
            }
        }
    }
}
