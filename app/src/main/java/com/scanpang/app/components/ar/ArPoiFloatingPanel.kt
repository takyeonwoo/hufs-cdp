package com.scanpang.app.components.ar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.LocalParking
import androidx.compose.material.icons.rounded.LocalPhone
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Stairs
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scanpang.app.data.ArFloorSectionUi
import com.scanpang.app.data.ArFloorStoreLine
import com.scanpang.app.data.DummyData
import com.scanpang.app.data.ExchangeRate
import com.scanpang.app.data.HalalCategory
import com.scanpang.app.data.StoreDetail
import com.scanpang.app.data.StoreMenuItem
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

// ─── Tab keys ───────────────────────────────────────────────────────────────

const val ArPoiTabBuilding = "building"
const val ArPoiTabFloors = "floors"
const val ArPoiTabAi = "ai"

// ─── Private color tokens ────────────────────────────────────────────────────

private val DetailPanelBg = Color.White.copy(alpha = 0.9f)
private val StorePanelBg = Color.White.copy(alpha = 0.93f)
private val DetailRowGray = Color(0xFFF4F5F8)
private val DetailChipBg = Color(0xFFF3F4F6)
private val DetailAiSummaryBg = Color(0xFFE8F1FF)
private val DetailAiTipBg = Color(0xFFFFF4E5)
private val DetailAiTipFg = Color(0xFFB45309)
private val HalalCertRed = Color(0xFF7B1E1E)
private val HalalBadgeBg = Color(0xFF7B1E1E).copy(alpha = 0.09f)
private val TrustBadgeBg = Color(0xFF10B981).copy(alpha = 0.07f)
private val TrustBadgeFg = Color(0xFF065F46)
private val HoursBg = Color(0xFF10B981).copy(alpha = 0.03f)
private val MenuItemBg = Color(0xFFF7F8FC)
private val ExchangeRowBg = Color(0xFFF9FAFB)
private val DividerColor = Color(0xFFE5E7EB)

// ─── Building detail overlay ─────────────────────────────────────────────────

@Composable
fun ArPoiFloatingDetailOverlay(
    poiName: String,
    activeDetailTab: String,
    onActiveDetailTabChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onFloorStoreClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSaved: Boolean = false,
    onSave: () -> Unit = {},
    distance: String = "–",
    category: String = "장소",
) {
    var expandedFloors by remember { mutableStateOf(setOf("B1")) }
    val floorData = DummyData.noonSquareFloorSections

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() },
        )
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = ScanPangDimens.detailArPanelTop)
                .width(ScanPangDimens.detailArPanelWidth)
                .height(ScanPangDimens.detailArPanelHeight)
                .clickable(enabled = false) { },
            shape = ScanPangShapes.radius16,
            color = DetailPanelBg,
            shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = ScanPangSpacing.md)
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "닫기",
                        tint = ScanPangColors.OnSurfaceMuted,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onDismiss() },
                    )
                    Text(
                        text = poiName,
                        style = ScanPangType.profileName18,
                        color = ScanPangColors.OnSurfaceStrong,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSaved) ScanPangColors.PrimarySoft else DetailRowGray)
                            .clickable { onSave() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = "저장",
                            tint = if (isSaved) ScanPangColors.Primary else ScanPangColors.OnSurfaceMuted,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                ArPoiStatusMetaRow(distance = distance, category = category)

                ArPoiDetailSegmentedTabs(active = activeDetailTab, onSelect = onActiveDetailTabChange)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    when (activeDetailTab) {
                        ArPoiTabBuilding -> ArPoiBuildingTabBody()
                        ArPoiTabFloors -> ArPoiFloorsTabBody(
                            floors = floorData,
                            expanded = expandedFloors,
                            onToggle = { id ->
                                expandedFloors =
                                    if (id in expandedFloors) expandedFloors - id else expandedFloors + id
                            },
                            onStoreClick = onFloorStoreClick,
                        )
                        ArPoiTabAi -> ArPoiAiGuideTabBody()
                    }
                }
            }
        }
    }
}

@Composable
private fun ArPoiStatusMetaRow(
    distance: String,
    category: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Surface(shape = RoundedCornerShape(6.dp), color = ScanPangColors.PrimarySoft) {
            Text(
                text = category,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                style = ScanPangType.meta11SemiBold,
                color = ScanPangColors.Primary,
            )
        }
        Text(text = distance, style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceStrong)
        Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(ScanPangColors.Success))
        Text(text = "영업 중", style = ScanPangType.meta11SemiBold, color = ScanPangColors.Success)
    }
}

@Composable
private fun ArPoiDetailSegmentedTabs(active: String, onSelect: (String) -> Unit) {
    val tabs = listOf(ArPoiTabBuilding to "건물 정보", ArPoiTabFloors to "층별 정보")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DetailRowGray)
            .padding(2.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEach { (key, label) ->
                val selected = active == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selected) ScanPangColors.Primary else Color.Transparent)
                        .clickable { onSelect(key) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = if (selected) ScanPangType.meta11SemiBold else ScanPangType.meta11Medium,
                        color = if (selected) Color.White else ScanPangColors.OnSurfaceMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArPoiBuildingTabBody() {
    val buildingImageCount = 4
    val buildingImageBg = listOf(Color(0xFFE8E8E8), Color(0xFFD8D8D8), Color(0xFFC8C8C8), Color(0xFFB8B8B8))
    val pagerState = rememberPagerState(pageCount = { buildingImageCount })
    var fullscreen by remember { mutableStateOf(false) }
    val currentPage = pagerState.currentPage

    if (fullscreen) {
        Dialog(onDismissRequest = { fullscreen = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(pagerState, Orientation.Horizontal),
                ) { page -> Box(Modifier.fillMaxSize().background(buildingImageBg[page])) }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(ScanPangSpacing.md),
                ) {
                    Text("${currentPage + 1}/$buildingImageCount", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = ScanPangType.meta11Medium, color = Color.White)
                }
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(ScanPangSpacing.sm).size(32.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.35f)).clickable { fullscreen = false },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Rounded.Info, null, tint = ScanPangColors.Primary, modifier = Modifier.size(15.dp))
            Text("명동 중심 대형 복합 쇼핑몰. 지하2층~지상8층, 패션·뷰티·F&B 입점.", style = ScanPangType.caption12, color = ScanPangColors.OnSurfaceStrong, modifier = Modifier.weight(1f))
        }

        val gridItems = listOf(
            Triple(Icons.Rounded.AccessTime, "10:00–22:00", false),
            Triple(Icons.Rounded.Stairs, "B2~8F", false),
            Triple(Icons.Rounded.Place, "명동 중앙로 26", false),
            Triple(Icons.Rounded.LocalPhone, "02-778-1234", false),
            Triple(Icons.Rounded.LocalParking, "주차 가능", false),
            Triple(Icons.Rounded.ConfirmationNumber, "무료 입장", false),
        )
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            gridItems.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    row.forEach { (icon, label, _) ->
                        ArPoiBuildingInfoChip(icon, label, Modifier.weight(1f))
                    }
                }
            }
            // 홈페이지 — 전체 너비 단독 행
            ArPoiBuildingInfoChip(
                icon = Icons.Rounded.Language,
                text = "홈페이지",
                modifier = Modifier.fillMaxWidth(),
                textColor = ScanPangColors.Primary,
                iconTint = ScanPangColors.Primary,
            )
        }

        Box(modifier = Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(10.dp))) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(pagerState, Orientation.Horizontal),
            ) { page -> Box(Modifier.fillMaxSize().background(buildingImageBg[page])) }
            Surface(shape = RoundedCornerShape(8.dp), color = Color.Black.copy(alpha = 0.27f), modifier = Modifier.align(Alignment.TopEnd).padding(top = 5.dp, end = 5.dp)) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Photo, null, tint = Color.White, modifier = Modifier.size(10.dp))
                    Text("${currentPage + 1}/$buildingImageCount", style = ScanPangType.badge9SemiBold, color = Color.White)
                }
            }
            Box(modifier = Modifier.align(Alignment.TopStart).padding(top = 5.dp, start = 5.dp).size(20.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.27f)).clickable { fullscreen = true }, contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.OpenInFull, null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
            Row(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(buildingImageCount) { i ->
                    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(if (i == currentPage) Color.White else Color.White.copy(alpha = 0.4f)))
                }
            }
        }
    }
}

@Composable
private fun ArPoiBuildingInfoChip(icon: ImageVector, text: String, modifier: Modifier = Modifier, textColor: Color = ScanPangColors.OnSurfaceStrong, iconTint: Color = ScanPangColors.OnSurfaceMuted) {
    Surface(modifier = modifier, shape = RoundedCornerShape(8.dp), color = DetailRowGray) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(icon, null, modifier = Modifier.size(12.dp), tint = iconTint)
            Text(text, style = ScanPangType.tag10Medium, color = textColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ArPoiFloorsTabBody(
    floors: List<ArFloorSectionUi>,
    expanded: Set<String>,
    onToggle: (String) -> Unit,
    onStoreClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        floors.forEach { floor ->
            val isOpen = floor.label in expanded
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DetailRowGray)
                        .clickable { onToggle(floor.label) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(floor.label, style = ScanPangType.title14, color = ScanPangColors.OnSurfaceStrong)
                    Text("${floor.storeCount}개", style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceMuted)
                    floor.categories.forEach { cat ->
                        Surface(shape = RoundedCornerShape(10.dp), color = ScanPangColors.PrimarySoft) {
                            Text(cat, modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp), style = ScanPangType.badge9SemiBold, color = ScanPangColors.Primary)
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (isOpen) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = if (isOpen) ScanPangColors.Primary else ScanPangColors.OnSurfaceMuted,
                        modifier = Modifier.size(24.dp),
                    )
                }
                if (isOpen && floor.stores.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        floor.stores.forEach { store ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .then(if (store.hasDetail) Modifier.clickable { onStoreClick(store.name) } else Modifier)
                                    .padding(horizontal = 4.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier.size(4.dp).clip(CircleShape)
                                        .background(if (store.isHalal) HalalCertRed else Color(0xFF9CA4AF)),
                                )
                                Text(store.name, style = ScanPangType.quickLabel12, color = ScanPangColors.OnSurfaceStrong)
                                Text(store.category, style = ScanPangType.tag10Medium, color = ScanPangColors.OnSurfaceMuted)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = ScanPangColors.OnSurfacePlaceholder,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArPoiAiGuideTabBody() {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = DetailAiSummaryBg) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Rounded.SmartToy, null, tint = ScanPangColors.Primary, modifier = Modifier.size(22.dp))
            Text("명동의 랜드마크 쇼핑몰이에요. 혼자 여행하기 좋고, B1층에 할랄 인증 식당이 있어 식사도 편리합니다.", style = ScanPangType.body14Regular, color = ScanPangColors.OnSurfaceStrong)
        }
    }
    Spacer(Modifier.height(ScanPangSpacing.md))
    Text("추천 포인트", style = ScanPangType.title14, color = ScanPangColors.OnSurfaceStrong)
    Spacer(Modifier.height(8.dp))
    ArPoiAiPointCard(Icons.Rounded.Restaurant, "B1 할랄 식당", "알리바바 케밥 – 할랄 인증, 혼밥 가능")
    Spacer(Modifier.height(8.dp))
    ArPoiAiPointCard(Icons.Rounded.ShoppingBag, "1F 외국인 할인", "안내데스크에서 여권 제시 시 할인 쿠폰 제공")
    Spacer(Modifier.height(8.dp))
    ArPoiAiPointCard(Icons.Rounded.CameraAlt, "8F 루프탑 전망", "명동 거리가 한눈에 보이는 포토스팟")
    Spacer(Modifier.height(ScanPangSpacing.md))
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = DetailAiTipBg) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Rounded.Lightbulb, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
            Text("혼자 여행 팁: 2F~3F 뷰티 매장은 평일 오전이 한적해요", style = ScanPangType.caption12Medium, color = DetailAiTipFg)
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun ArPoiAiPointCard(icon: ImageVector, title: String, subtitle: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = DetailChipBg) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = ScanPangColors.PrimarySoft, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(icon, null, tint = ScanPangColors.Primary, modifier = Modifier.size(22.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = ScanPangType.title14, color = ScanPangColors.OnSurfaceStrong)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = ScanPangType.caption12Medium, color = ScanPangColors.OnSurfaceMuted)
            }
        }
    }
}

// ─── Store detail overlay ─────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArStoreDetailOverlay(
    detail: StoreDetail,
    isSaved: Boolean = false,
    onDismiss: () -> Unit,
    onStartNavigation: () -> Unit,
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
) {
    val imageCount = detail.imageCount
    val imageBg = remember {
        listOf(Color(0xFFE8E8E8), Color(0xFFD8D8D8), Color(0xFFC8C8C8), Color(0xFFB8B8B8))
            .take(imageCount)
    }
    val pagerState = rememberPagerState(pageCount = { imageCount })
    var fullscreen by remember { mutableStateOf(false) }
    val currentPage = pagerState.currentPage

    if (fullscreen) {
        Dialog(onDismissRequest = { fullscreen = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(pagerState, Orientation.Horizontal),
                ) { page -> Box(Modifier.fillMaxSize().background(imageBg.getOrElse(page) { Color(0xFFD8D8D8) })) }
                Surface(shape = RoundedCornerShape(6.dp), color = Color.Black.copy(alpha = 0.45f), modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(ScanPangSpacing.md)) {
                    Text("${currentPage + 1}/$imageCount", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = ScanPangType.meta11Medium, color = Color.White)
                }
                Box(modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(ScanPangSpacing.sm).size(32.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.35f)).clickable { fullscreen = false }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().clickable { onDismiss() })

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = ScanPangDimens.detailArPanelTop)
                .width(ScanPangDimens.detailArPanelWidth)
                .height(ScanPangDimens.detailArPanelHeight)
                .clickable(enabled = false) { },
            shape = RoundedCornerShape(20.dp),
            color = StorePanelBg,
            shadowElevation = ScanPangDimens.arPoiCardShadowElevation,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Photo carousel (h=90)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(Color(0xFFD9D9D9)),
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(pagerState, Orientation.Horizontal),
                    ) { page -> Box(Modifier.fillMaxSize().background(imageBg.getOrElse(page) { Color(0xFFD9D9D9) })) }

                    // Back button — top-left (20×20, 6dp inset)
                    Box(
                        modifier = Modifier.align(Alignment.TopStart).padding(top = 6.dp, start = 6.dp).size(20.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.27f)).clickable { onDismiss() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White, modifier = Modifier.size(10.dp)) }

                    // Expand button — top-right only (20×20)
                    Box(
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 6.dp, end = 10.dp).size(20.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.27f)).clickable { fullscreen = true },
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Rounded.OpenInFull, null, tint = Color.White, modifier = Modifier.size(12.dp)) }

                    // Photo count badge — bottom-right (Figma: x=308,y=64 → 10dp right, 9dp bottom)
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 9.dp, end = 10.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.27f),
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Photo, null, tint = Color.White, modifier = Modifier.size(10.dp))
                            Text("${currentPage + 1}/$imageCount", style = ScanPangType.badge9SemiBold, color = Color.White)
                        }
                    }

                    // Dot indicators — bottom-center (Figma: y=76, 9dp from bottom)
                    Row(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 9.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(imageCount) { i ->
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(if (i == currentPage) Color.White.copy(alpha = 0.93f) else Color.White.copy(alpha = 0.4f)))
                        }
                    }
                }

                // ── Info section (scrollable) ──────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // ── Header: name + [halal cert] + bookmark ─────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(detail.name, style = ScanPangType.sectionTitle, color = ScanPangColors.OnSurfaceStrong, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        if (detail.isHalal) {
                            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(14.dp)).background(HalalCertRed), contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.CheckCircle, "할랄 인증", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                        Box(
                            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(14.dp))
                                .background(if (isSaved) ScanPangColors.PrimarySoft else DetailRowGray)
                                .clickable { onSave() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                if (isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                "저장",
                                tint = if (isSaved) ScanPangColors.Primary else ScanPangColors.OnSurfaceMuted,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    // ── Meta row: [halal badge] + category chip + distance + [open status] ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        detail.halalCategory?.let { hc ->
                            Row(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(HalalBadgeBg).padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HalalCertRed))
                                Text(hc.label, style = ScanPangType.trust10SemiBold, color = HalalCertRed)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = ScanPangColors.PrimarySoft) {
                            Text(detail.cuisineLabel, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), style = ScanPangType.badge9SemiBold, color = ScanPangColors.Primary)
                        }
                        Text(detail.distance, style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceStrong)
                        detail.isOpen?.let { open ->
                            val statusColor = if (open) ScanPangColors.Success else ScanPangColors.OnSurfaceMuted
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(statusColor))
                            Text(if (open) "영업 중" else "영업 종료", style = ScanPangType.meta11SemiBold, color = statusColor)
                        }
                    }

                    // ── Trust badges (halal only) ─────────────────────────
                    if (detail.showTrustBadges) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StoreDetailTrustChip("무슬림 조리사", Icons.Rounded.Restaurant)
                            StoreDetailTrustChip("주류 미판매", Icons.Rounded.Info)
                        }
                    }

                    // ── Description card ──────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DetailRowGray).padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(Icons.Rounded.Info, null, tint = ScanPangColors.Primary, modifier = Modifier.size(15.dp).padding(top = 1.dp))
                        Text(detail.description, style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceStrong, modifier = Modifier.weight(1f))
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))

                    // ── Info grid (조건별 행 표시) ────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        detail.openHours?.let { hours ->
                            val open = detail.isOpen ?: true
                            val dotColor = if (open) ScanPangColors.Success else ScanPangColors.OnSurfaceMuted
                            Row(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(HoursBg).padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor))
                                Icon(Icons.Rounded.AccessTime, null, tint = dotColor, modifier = Modifier.size(12.dp))
                                Text(hours, style = ScanPangType.quickLabel12, color = ScanPangColors.OnSurfaceStrong)
                                detail.lastOrder?.let {
                                    Text(it, style = ScanPangType.badge9SemiBold.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium), color = ScanPangColors.OnSurfaceMuted)
                                }
                            }
                        }
                        detail.address?.let { StoreInfoRow(Icons.Rounded.Place, it) }
                        detail.phone?.let { StoreInfoRow(Icons.Rounded.LocalPhone, it) }
                        detail.floor?.let { StoreInfoRow(Icons.Rounded.Stairs, it) }
                        detail.website?.let { StoreInfoRow(Icons.Rounded.Language, it, textColor = ScanPangColors.Primary) }
                        detail.convenienceServices?.let { StoreInfoRow(Icons.Rounded.ShoppingBag, it) }
                        detail.departments?.let { StoreInfoRow(Icons.Rounded.Info, it) }
                    }

                    // ── 대표 메뉴 (식당·카페·할랄) ───────────────────────
                    if (detail.menus.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                        Text("대표 메뉴", style = ScanPangType.chip13SemiBold, color = ScanPangColors.OnSurfaceStrong)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            detail.menus.forEach { menu ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(MenuItemBg).padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(menu.name, style = ScanPangType.chip13SemiBold, color = ScanPangColors.OnSurfaceStrong, modifier = Modifier.weight(1f))
                                    Text(menu.price, style = ScanPangType.chip13SemiBold, color = ScanPangColors.Primary)
                                }
                            }
                        }
                    }

                    // ── 오늘의 환율 (환전소·은행·ATM) ────────────────────
                    if (detail.exchangeRates.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                        Text("오늘의 환율", style = ScanPangType.chip13SemiBold, color = ScanPangColors.OnSurfaceStrong)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            detail.exchangeRates.forEach { rate ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(ExchangeRowBg).padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(rate.flag, fontSize = 12.sp)
                                        Text(rate.currency, style = ScanPangType.quickLabel12, color = ScanPangColors.OnSurfaceStrong)
                                    }
                                    Text(rate.rate, style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceStrong)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreInfoRow(
    icon: ImageVector,
    text: String,
    textColor: Color = ScanPangColors.OnSurfaceStrong,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, null, tint = ScanPangColors.OnSurfaceMuted, modifier = Modifier.size(12.dp))
        Text(text, style = ScanPangType.meta11Medium, color = textColor)
    }
}

@Composable
private fun StoreDetailTrustChip(label: String, icon: ImageVector) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(TrustBadgeBg).padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, null, tint = TrustBadgeFg, modifier = Modifier.size(12.dp))
        Text(label, style = ScanPangType.trust10SemiBold, color = TrustBadgeFg)
    }
}
