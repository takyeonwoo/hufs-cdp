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
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.AccessTime
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
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Stairs
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
private val DividerColor = Color(0xFFE5E7EB)

// ─── Floor data model ────────────────────────────────────────────────────────

private data class ArFloorStoreLine(
    val name: String,
    val category: String,
    val isHalal: Boolean,
    val hasDetail: Boolean = false,
)

private data class ArFloorSectionUi(
    val label: String,
    val storeCount: Int,
    val categoryLabel: String,
    val stores: List<ArFloorStoreLine>,
)

private fun noonSquareFloorSections(): List<ArFloorSectionUi> = listOf(
    ArFloorSectionUi("B2", 6, "식당", emptyList()),
    ArFloorSectionUi(
        "B1",
        15,
        "식당",
        listOf(
            ArFloorStoreLine("무궁화식당", "한식", false, hasDetail = true),
            ArFloorStoreLine("알리바바 케밥", "할랄", true, hasDetail = true),
            ArFloorStoreLine("올리브영", "뷰티", false, hasDetail = true),
        ),
    ),
    ArFloorSectionUi("1F", 12, "패션·잡화", emptyList()),
    ArFloorSectionUi("2F", 10, "뷰티·라이프", emptyList()),
    ArFloorSectionUi("3F", 9, "패션", emptyList()),
    ArFloorSectionUi("4F", 7, "잡화", emptyList()),
    ArFloorSectionUi("5F", 6, "F&B", emptyList()),
    ArFloorSectionUi("6F", 5, "문화", emptyList()),
    ArFloorSectionUi("7F", 4, "전망", emptyList()),
    ArFloorSectionUi("8F", 3, "루프탑", emptyList()),
)

// ─── Store detail model ───────────────────────────────────────────────────────

data class StoreMenuItem(
    val name: String,
    val nameEn: String,
    val price: String,
)

data class StoreDetail(
    val name: String,
    val nameEn: String,
    val cuisineLabel: String,
    val distance: String,
    val isOpen: Boolean,
    val openHours: String,
    val lastOrder: String? = null,
    val address: String,
    val phone: String? = null,
    val description: String,
    val isHalal: Boolean = false,
    val showTrustBadges: Boolean = false,
    val menus: List<StoreMenuItem> = emptyList(),
    val imageCount: Int = 3,
)

private val sampleStoreData: Map<String, StoreDetail> = mapOf(
    "알리바바 케밥" to StoreDetail(
        name = "알리바바 케밥",
        nameEn = "Ali Baba Kebab",
        cuisineLabel = "할랄 · 케밥",
        distance = "15m",
        isOpen = true,
        openHours = "오늘 11:00–22:00",
        lastOrder = "라스트오더 21:30",
        address = "서울 중구 명동8길 8-3",
        phone = "02-318-4221",
        description = "할랄 인증 케밥 전문점 — 엄격한 기준의 무슬림 친화 레스토랑\n터키식 정통 케밥을 할랄 재료로 즐길 수 있어요.",
        isHalal = true,
        showTrustBadges = true,
        menus = listOf(
            StoreMenuItem("치킨 케밥", "Chicken Kebab", "₩12,000"),
            StoreMenuItem("팔라펠 플레이트", "Falafel Plate", "₩10,000"),
        ),
    ),
    "무궁화식당" to StoreDetail(
        name = "무궁화식당",
        nameEn = "Mugungwha Restaurant",
        cuisineLabel = "한식 · 가정식",
        distance = "20m",
        isOpen = true,
        openHours = "오늘 10:00–21:00",
        lastOrder = "라스트오더 20:30",
        address = "서울 중구 명동8길 11-4",
        phone = "02-778-3456",
        description = "명동 한복판에서 즐기는 정통 한국 가정식.\n비빔밥, 된장찌개 등 다양한 메뉴를 합리적인 가격에 즐길 수 있어요.",
        isHalal = false,
        menus = listOf(
            StoreMenuItem("비빔밥", "Bibimbap", "₩13,000"),
            StoreMenuItem("된장찌개 정식", "Doenjang Jjigae Set", "₩15,000"),
        ),
    ),
    "올리브영" to StoreDetail(
        name = "올리브영",
        nameEn = "Olive Young",
        cuisineLabel = "뷰티 · 헬스",
        distance = "25m",
        isOpen = true,
        openHours = "오늘 10:00–22:00",
        address = "서울 중구 명동길 53",
        phone = "02-778-9900",
        description = "국내 최대 H&B 스토어.\n K-뷰티 제품부터 건강식품까지 다양한 상품을 만날 수 있어요.",
        isHalal = false,
        menus = emptyList(),
    ),
)

/** 매장 이름으로 상세 정보 조회. 데이터가 없으면 null 반환. */
fun storeDetailFor(name: String): StoreDetail? = sampleStoreData[name]

// ─── Building detail overlay ─────────────────────────────────────────────────

@Composable
fun ArPoiFloatingDetailOverlay(
    poiName: String,
    activeDetailTab: String,
    onActiveDetailTabChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onFloorStoreClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
) {
    var expandedFloors by remember { mutableStateOf(setOf("B1")) }
    val floorData = remember { noonSquareFloorSections() }

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
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
                            .background(DetailRowGray)
                            .clickable { onSave() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BookmarkBorder,
                            contentDescription = "저장",
                            tint = ScanPangColors.OnSurfaceMuted,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                ArPoiStatusMetaRow()

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
private fun ArPoiStatusMetaRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Surface(shape = RoundedCornerShape(6.dp), color = ScanPangColors.PrimarySoft) {
            Text(
                text = "쇼핑몰",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                style = ScanPangType.meta11SemiBold,
                color = ScanPangColors.Primary,
            )
        }
        Text(text = "15m", style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceStrong)
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
            Triple(Icons.Rounded.Language, "홈페이지", true),
        )
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            gridItems.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    row.forEach { (icon, label, isLink) ->
                        ArPoiBuildingInfoChip(icon, label, Modifier.weight(1f), if (isLink) ScanPangColors.Primary else ScanPangColors.OnSurfaceStrong, if (isLink) ScanPangColors.Primary else ScanPangColors.OnSurfaceMuted)
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(10.dp))) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(pagerState, Orientation.Horizontal),
            ) { page -> Box(Modifier.fillMaxSize().background(buildingImageBg[page])) }
            Surface(shape = RoundedCornerShape(8.dp), color = Color.Black.copy(alpha = 0.27f), modifier = Modifier.align(Alignment.TopEnd).padding(top = 5.dp, end = 5.dp)) {
                Text("${currentPage + 1}/$buildingImageCount", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = ScanPangType.badge9SemiBold, color = Color.White)
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
                    Surface(shape = RoundedCornerShape(10.dp), color = ScanPangColors.PrimarySoft) {
                        Text(floor.categoryLabel, modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp), style = ScanPangType.badge9SemiBold, color = ScanPangColors.Primary)
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
                                if (store.hasDetail) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                        contentDescription = "상세보기",
                                        tint = ScanPangColors.OnSurfacePlaceholder,
                                        modifier = Modifier.size(10.dp),
                                    )
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

                    // Back button (top-left, ← 뒤로가기)
                    Box(
                        modifier = Modifier.align(Alignment.TopStart).padding(top = 6.dp, start = 6.dp).size(16.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.27f)).clickable { onDismiss() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White, modifier = Modifier.size(10.dp)) }

                    // Top-right: count badge + expand btn
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 6.dp, end = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color.Black.copy(alpha = 0.27f)) {
                            Text("${currentPage + 1}/$imageCount", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = ScanPangType.badge9SemiBold, color = Color.White)
                        }
                        Box(
                            modifier = Modifier.size(20.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.27f)).clickable { fullscreen = true },
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Rounded.OpenInFull, null, tint = Color.White, modifier = Modifier.size(12.dp)) }
                    }

                    // Dot indicators (bottom-center)
                    Row(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(imageCount) { i ->
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(if (i == currentPage) Color.White.copy(alpha = 0.93f) else Color.White.copy(alpha = 0.4f)))
                        }
                    }
                }

                // Info section (scrollable)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Header: name + halal cert + bookmark
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Text(detail.name, style = ScanPangType.sectionTitle, color = ScanPangColors.OnSurfaceStrong, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(detail.nameEn, style = ScanPangType.badge9SemiBold.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium), color = ScanPangColors.OnSurfaceMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (detail.isHalal) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(14.dp)).background(HalalCertRed),
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Rounded.CheckCircle, "할랄 인증", tint = Color.White, modifier = Modifier.size(16.dp)) }
                        }
                        Box(
                            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(14.dp)).background(DetailRowGray).clickable { onSave() },
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Rounded.BookmarkBorder, "저장", tint = ScanPangColors.OnSurfaceMuted, modifier = Modifier.size(20.dp)) }
                    }

                    // Meta row: halal badge + cuisine chip + distance + status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (detail.isHalal) {
                            Row(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(HalalBadgeBg).padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HalalCertRed))
                                Text("HALAL MEAT", style = ScanPangType.trust10SemiBold, color = HalalCertRed)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = ScanPangColors.PrimarySoft) {
                            Text(detail.cuisineLabel, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), style = ScanPangType.badge9SemiBold, color = ScanPangColors.Primary)
                        }
                        Text(detail.distance, style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceStrong)
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ScanPangColors.Success))
                        Text("영업 중", style = ScanPangType.meta11SemiBold, color = ScanPangColors.Success)
                    }

                    // Trust badges (halal only)
                    if (detail.showTrustBadges) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StoreDetailTrustChip("무슬림 조리사", Icons.Rounded.Restaurant)
                            StoreDetailTrustChip("주류 미판매", Icons.Rounded.Info)
                        }
                    }

                    // Description card
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DetailRowGray).padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(Icons.Rounded.Info, null, tint = ScanPangColors.Primary, modifier = Modifier.size(15.dp).padding(top = 1.dp))
                        Text(detail.description, style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceStrong, modifier = Modifier.weight(1f))
                    }

                    // Divider
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))

                    // Info grid: hours, address, phone
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Hours row
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(HoursBg).padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ScanPangColors.Success))
                            Icon(Icons.Rounded.AccessTime, null, tint = ScanPangColors.Success, modifier = Modifier.size(12.dp))
                            Text(detail.openHours, style = ScanPangType.quickLabel12, color = ScanPangColors.OnSurfaceStrong)
                            detail.lastOrder?.let { Text(it, style = ScanPangType.badge9SemiBold.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium), color = ScanPangColors.OnSurfaceMuted) }
                        }
                        // Address
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Rounded.Place, null, tint = ScanPangColors.OnSurfaceMuted, modifier = Modifier.size(12.dp))
                            Text(detail.address, style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceStrong)
                        }
                        // Phone
                        detail.phone?.let { phone ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Rounded.LocalPhone, null, tint = ScanPangColors.OnSurfaceMuted, modifier = Modifier.size(12.dp))
                                Text(phone, style = ScanPangType.meta11Medium, color = ScanPangColors.OnSurfaceStrong)
                            }
                        }
                    }

                    // Menu section (food stores only)
                    if (detail.menus.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
                        Text("대표 메뉴", style = ScanPangType.chip13SemiBold, color = ScanPangColors.OnSurfaceStrong)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            detail.menus.forEach { menu ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(MenuItemBg).padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                        Text(menu.name, style = ScanPangType.chip13SemiBold, color = ScanPangColors.OnSurfaceStrong)
                                        Text(menu.nameEn, style = ScanPangType.badge9SemiBold.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Normal), color = ScanPangColors.OnSurfaceMuted)
                                    }
                                    Text(menu.price, style = ScanPangType.chip13SemiBold, color = ScanPangColors.Primary)
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
