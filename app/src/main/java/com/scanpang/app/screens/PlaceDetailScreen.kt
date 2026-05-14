@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.scanpang.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalParking
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material.icons.rounded.Verified
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
import com.scanpang.app.data.LockerTier
import com.scanpang.app.data.MenuItem
import com.scanpang.app.data.Place
import com.scanpang.app.data.RestaurantPlace
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
    val cafeMenus = remember(categoryKey, placeId) {
        if (categoryKey == "cafe") DummyData.cafeRepresentativeMenus[placeId].orEmpty() else emptyList()
    }
    val exchangeRates = remember(categoryKey) {
        if (categoryKey in setOf("exchange", "atm", "bank")) DummyData.exchangeRates else emptyList()
    }
    val lockerTiers = remember(categoryKey, placeId) {
        if (categoryKey == "locker") DummyData.lockerTiers[placeId].orEmpty() else emptyList()
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
            // Title + bookmark
            DetailTitleBookmarkRow(
                title = place.name,
                bookmarked = bookmark.bookmarked,
                onBookmarkClick = bookmark.onToggle,
            )

            // Meta row
            when (categoryKey) {
                "restaurant" -> RestaurantMetaRow(place)
                "subway" -> SubwayLineMetaRow(place)
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

            // Halal trust chips (restaurant only)
            if (categoryKey == "restaurant" && restaurantExtra != null) {
                PlaceHalalChipsRow(restaurantExtra)
            }

            DetailScreenDivider()

            // Navigation + phone CTA
            DetailCtaRow(
                onNavigate = { navController.navigate(AppRoutes.ArNavMap) { launchSingleTop = true } },
                onPhoneClick = { if (place.phone.isNotBlank()) context.openPhoneDialer(place.phone) },
            )

            // Visit status for applicable categories
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

            // Category-specific content
            PlaceCategoryContent(
                categoryKey = categoryKey,
                place = place,
                restaurantExtra = restaurantExtra,
                cafeMenus = cafeMenus,
                exchangeRates = exchangeRates,
                lockerTiers = lockerTiers,
            )
        }
    }
}

// ── Category-specific content ─────────────────────────────────────────────

@Composable
private fun PlaceCategoryContent(
    categoryKey: String,
    place: Place,
    restaurantExtra: RestaurantPlace?,
    cafeMenus: List<MenuItem>,
    exchangeRates: List<ExchangeRate>,
    lockerTiers: List<LockerTier>,
) {
    when (categoryKey) {
        "restaurant" -> RestaurantContent(place, restaurantExtra)
        "cafe" -> CafeContent(place, cafeMenus)
        "exchange" -> ExchangeContent(place, exchangeRates)
        "atm" -> AtmContent(place, exchangeRates)
        "subway" -> SubwayContent(place)
        "locker" -> LockerContent(place, lockerTiers)
        "restroom" -> RestroomContent(place)
        "prayer_room" -> PrayerRoomContent(place)
        "convenience_store" -> ConvenienceStoreContent(place)
        "shopping" -> ShoppingContent(place)
        "hospital" -> HospitalContent(place)
        "pharmacy" -> PharmacyContent(place)
        "bank" -> BankContent(place)
        else -> GenericContent(place)
    }
}

@Composable
private fun CommonDetailInfoSection(place: Place) {
    DetailSection(title = "상세 정보") {
        Column(verticalArrangement = Arrangement.spacedBy(INFO_ROW_SPACING)) {
            if (place.openHours.isNotBlank()) DetailInfoLine(Icons.Rounded.AccessTime, "영업시간", place.openHours)
            if (place.address.isNotBlank()) DetailInfoLine(Icons.Rounded.Place, "주소", place.address)
            if (place.phone.isNotBlank()) DetailInfoLine(Icons.Rounded.Phone, "전화", place.phone)
            if (place.floor.isNotBlank()) DetailInfoLine(Icons.Rounded.Store, "매장 층수", place.floor)
            if (place.parking.isNotBlank()) DetailInfoLine(Icons.Rounded.LocalParking, "주차 가능 여부", place.parking)
            if (place.website.isNotBlank()) DetailInfoLine(Icons.Rounded.Language, "웹사이트", place.website)
        }
    }
}

@Composable
private fun RestaurantContent(place: Place, rp: RestaurantPlace?) {
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    if (rp != null && rp.menuItems.isNotEmpty()) {
        DetailScreenDivider()
        DetailSection(title = "대표 메뉴") {
            Column(verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm)) {
                rp.menuItems.forEach { m -> DetailMenuPriceRow(name = m.name, price = m.price) }
            }
        }
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

@Composable
private fun CafeContent(place: Place, menus: List<MenuItem>) {
    if (menus.isNotEmpty()) {
        DetailSection(title = "대표 메뉴") {
            Column(verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm)) {
                menus.forEach { m -> DetailMenuPriceRow(name = m.name, price = m.price) }
            }
        }
        DetailScreenDivider()
    }
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

@Composable
private fun ExchangeContent(place: Place, rates: List<ExchangeRate>) {
    if (rates.isNotEmpty()) {
        DetailSection(title = "환율 정보") {
            Column(verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm)) {
                rates.forEach { row -> ExchangeRateRow(row) }
            }
        }
        DetailScreenDivider()
    }
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

@Composable
private fun AtmContent(place: Place, rates: List<ExchangeRate>) {
    if (place.description.isNotBlank()) {
        DetailSection(title = "소개") {
            DetailIntroBody(text = place.description)
        }
        DetailScreenDivider()
    }
    CommonDetailInfoSection(place)
    if (rates.isNotEmpty()) {
        DetailScreenDivider()
        DetailSection(title = "오늘의 환율") {
            Column(verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm)) {
                rates.forEach { row -> ExchangeRateRow(row) }
            }
        }
    }
}

@Composable
private fun SubwayContent(place: Place) {
    if (place.description.isNotBlank()) {
        DetailSection(title = "소개") {
            DetailIntroBody(text = place.description)
        }
        DetailScreenDivider()
    }
    CommonDetailInfoSection(place)
}

@Composable
private fun LockerContent(place: Place, tiers: List<LockerTier>) {
    if (place.description.isNotBlank()) {
        DetailSection(title = "소개") {
            DetailIntroBody(text = place.description)
        }
        DetailScreenDivider()
    }
    CommonDetailInfoSection(place)
}

@Composable
private fun RestroomContent(place: Place) {
    if (place.description.isNotBlank()) {
        DetailSection(title = "소개") {
            DetailIntroBody(text = place.description)
        }
        DetailScreenDivider()
    }
    CommonDetailInfoSection(place)
}

@Composable
private fun PrayerRoomContent(place: Place) {
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

@Composable
private fun ConvenienceStoreContent(place: Place) {
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

@Composable
private fun ShoppingContent(place: Place) {
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

@Composable
private fun HospitalContent(place: Place) {
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

@Composable
private fun PharmacyContent(place: Place) {
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

@Composable
private fun BankContent(place: Place) {
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

@Composable
private fun GenericContent(place: Place) {
    DetailSection(title = "소개") {
        DetailIntroBody(text = place.description)
    }
    DetailScreenDivider()
    CommonDetailInfoSection(place)
}

// Section = Header + body with consistent inner spacing
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

// ── Meta row helpers ──────────────────────────────────────────────────────

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
private fun SubwayLineMetaRow(place: Place) {
    val lineTags = place.tags.filter { it.contains("호선") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
    ) {
        FlowRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
        ) {
            lineTags.forEach { line ->
                Surface(shape = ScanPangShapes.badge6, color = ScanPangColors.PrimarySoft) {
                    Text(
                        text = line,
                        modifier = Modifier.padding(
                            horizontal = ScanPangSpacing.sm,
                            vertical = ScanPangDimens.chipPadVertical,
                        ),
                        style = ScanPangType.chip13SemiBold,
                        color = ScanPangColors.Primary,
                    )
                }
            }
        }
        Text(text = place.distance, style = ScanPangType.detailMetaSubtitle13, color = ScanPangColors.OnSurfaceMuted)
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

// ── Restaurant halal chips ─────────────────────────────────────────────────

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

// ── Locker tier card ──────────────────────────────────────────────────────

@Composable
private fun LockerTierCard(row: LockerTier) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ScanPangShapes.detailVisitCard,
        color = ScanPangColors.DetailMenuRowBackground,
        border = BorderStroke(ScanPangDimens.borderHairline, ScanPangColors.OutlineSubtle),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ScanPangSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(ScanPangDimens.icon5)) {
                Text(text = row.label, style = ScanPangType.title14, color = ScanPangColors.OnSurfaceStrong)
                Text(text = row.price, style = ScanPangType.detailIntro13, color = ScanPangColors.OnSurfaceMuted)
            }
            Text(
                text = if (row.available) "가용" else "만석",
                style = ScanPangType.chip13SemiBold,
                color = if (row.available) ScanPangColors.StatusOpen else ScanPangColors.Error,
            )
        }
    }
}

// ── Exchange rate row ─────────────────────────────────────────────────────

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
