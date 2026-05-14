@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.scanpang.app.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.scanpang.app.data.Place
import com.scanpang.app.data.RecentlyViewedEntry
import com.scanpang.app.data.RecentlyViewedStore
import com.scanpang.app.data.SavedPlaceEntry
import com.scanpang.app.data.SavedPlacesStore
import com.scanpang.app.ui.ScanPangFigmaAssets
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangShapes
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

fun defaultPlaceDetailGallery(): List<String> = ScanPangFigmaAssets.RestaurantDetailGallery

data class DetailBookmarkController(
    val bookmarked: Boolean,
    val onToggle: () -> Unit,
)

fun Context.openPhoneDialer(rawPhone: String) {
    val digits = rawPhone.filter { it.isDigit() || it == '+' }
    if (digits.isEmpty()) return
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$digits"))
    startActivity(intent)
}

@Composable
fun rememberDetailBookmark(
    placeId: String,
    placeName: String,
    category: String,
    distanceLine: String,
    tags: List<String>,
    categoryKey: String,
): DetailBookmarkController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val store = remember { SavedPlacesStore(context) }
    val recentlyViewedStore = remember { RecentlyViewedStore(context) }
    var bookmarked by remember(placeId) { mutableStateOf(store.isSaved(placeId)) }

    LaunchedEffect(placeId) {
        recentlyViewedStore.record(
            RecentlyViewedEntry(
                id = placeId,
                name = placeName,
                category = category,
                distanceLine = distanceLine,
                categoryKey = categoryKey,
            ),
        )
    }

    DisposableEffect(lifecycleOwner, placeId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                bookmarked = store.isSaved(placeId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val onToggle: () -> Unit = {
        if (bookmarked) {
            store.remove(placeId)
            bookmarked = false
            Toast.makeText(context, "저장이 해제되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            store.save(
                SavedPlaceEntry(
                    id = placeId,
                    name = placeName,
                    category = category,
                    distanceLine = distanceLine,
                    tags = tags,
                    categoryKey = categoryKey,
                ),
            )
            bookmarked = true
            Toast.makeText(context, "저장되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

    return DetailBookmarkController(bookmarked, onToggle)
}

@Composable
fun DetailImageFullscreenDialog(
    gallery: List<Any>,
    pagerState: PagerState,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(gallery[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(ScanPangSpacing.md)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "닫기",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
fun DetailHeroPhotoPager(
    gallery: List<Any>,
    pagerState: PagerState,
    onBack: () -> Unit,
    onFullscreenClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ScanPangDimens.detailPhotoHeroHeight),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(gallery[page])
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        // Back button — black@25% circle with white icon
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 20.dp, top = 12.dp)
                .size(ScanPangDimens.arCircleBtn36)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.25f))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "뒤로",
                modifier = Modifier.size(24.dp),
                tint = Color.White,
            )
        }
        // Pager dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(gallery.size) { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = if (index == pagerState.currentPage) 1f else 0.38f)),
                )
            }
        }
        // Page count badge
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = ScanPangSpacing.lg, bottom = ScanPangSpacing.lg),
            shape = ScanPangShapes.badge6,
            color = Color.Black.copy(alpha = 0.45f),
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${gallery.size}",
                modifier = Modifier.padding(horizontal = ScanPangSpacing.sm, vertical = 3.dp),
                style = ScanPangType.detailImageCount9,
                color = Color.White,
            )
        }
    }
}

// Back-only header for screens without a hero photo
@Composable
fun DetailBackOnlyArea(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ScanPangColors.Surface)
            .statusBarsPadding()
            .height(56.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(ScanPangDimens.arCircleBtn36)
                .clip(CircleShape)
                .background(ScanPangColors.Background)
                .clickable(onClick = onBack)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "뒤로",
                modifier = Modifier.size(20.dp),
                tint = ScanPangColors.OnSurfaceStrong,
            )
        }
    }
}

@Composable
fun DetailTitleBookmarkRow(
    title: String,
    bookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = ScanPangType.detailRestaurantTitle24,
            color = ScanPangColors.OnSurfaceStrong,
            modifier = Modifier.weight(1f).padding(end = ScanPangSpacing.sm),
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ScanPangColors.Background)
                .clickable(onClick = onBookmarkClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (bookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                contentDescription = if (bookmarked) "저장됨" else "저장",
                modifier = Modifier.size(20.dp),
                tint = if (bookmarked) ScanPangColors.Primary else ScanPangColors.OnSurfaceMuted,
            )
        }
    }
}

@Composable
fun DetailCategoryTagDistanceRow(
    categoryLabel: String,
    distanceText: String,
    modifier: Modifier = Modifier,
    isOpen: Boolean? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
    ) {
        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            color = ScanPangColors.PrimarySoft,
        ) {
            Text(
                text = categoryLabel,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = ScanPangType.trust10SemiBold,
                color = ScanPangColors.Primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = distanceText,
            style = ScanPangType.detailMetaSubtitle13,
            color = ScanPangColors.OnSurfaceMuted,
            maxLines = 1,
        )
        if (isOpen != null) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (isOpen) ScanPangColors.StatusOpen else ScanPangColors.Error),
            )
            Text(
                text = if (isOpen) "영업 중" else "영업 종료",
                style = ScanPangType.meta11SemiBold,
                color = if (isOpen) ScanPangColors.StatusOpen else ScanPangColors.Error,
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        trailing?.invoke()
    }
}

// CTA row: full-width nav button + phone square button
@Composable
fun DetailCtaRow(
    onNavigate: () -> Unit,
    onPhoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(ScanPangDimens.detailCtaHeight)
                .clip(ScanPangShapes.radius14)
                .background(ScanPangColors.Primary)
                .clickable(onClick = onNavigate),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.NearMe,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White,
                )
                Text(
                    text = "길안내 시작",
                    style = ScanPangType.detailSectionTitle15,
                    color = Color.White,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(ScanPangDimens.detailCtaSide)
                .clip(ScanPangShapes.radius14)
                .background(ScanPangColors.Background)
                .clickable(onClick = onPhoneClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Phone,
                contentDescription = "전화",
                modifier = Modifier.size(22.dp),
                tint = ScanPangColors.OnSurfaceMuted,
            )
        }
    }
}

@Composable
fun DetailSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier,
        style = ScanPangType.detailSectionTitle15,
        color = ScanPangColors.OnSurfaceStrong,
    )
}

@Composable
fun DetailIntroBody(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = ScanPangType.detailIntro13,
        color = ScanPangColors.OnSurfaceMuted,
    )
}

@Composable
fun DetailInfoLine(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ScanPangDimens.icon16),
            tint = ScanPangColors.OnSurfaceMuted,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = label,
                style = ScanPangType.quickLabel12,
                color = ScanPangColors.OnSurfaceStrong,
            )
            Text(
                text = value,
                style = ScanPangType.caption12,
                color = ScanPangColors.OnSurfaceMuted,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailFacilityTagRow(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    if (tags.isEmpty()) return
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm),
    ) {
        tags.forEach { tag -> DetailFacilityTagChip(text = tag) }
    }
}

@Composable
private fun DetailFacilityTagChip(text: String) {
    Surface(
        shape = ScanPangShapes.badge6,
        color = ScanPangColors.DetailFacilityTagBackground,
        border = BorderStroke(ScanPangDimens.borderHairline, ScanPangColors.OutlineSubtle),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = ScanPangSpacing.sm,
                vertical = ScanPangDimens.chipPadVertical,
            ),
            style = ScanPangType.tag11Medium,
            color = ScanPangColors.OnSurfaceStrong,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun DetailMenuPriceRow(
    name: String,
    price: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ScanPangShapes.detailMenuRow)
            .background(ScanPangColors.DetailMenuRowBackground)
            .padding(horizontal = ScanPangSpacing.md, vertical = ScanPangSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = ScanPangType.caption12Medium,
            color = ScanPangColors.OnSurfaceStrong,
        )
        Text(
            text = price,
            style = ScanPangType.detailMenuPrice14,
            color = ScanPangColors.OnSurfaceStrong,
        )
    }
}

@Composable
fun DetailScreenDivider() {
    HorizontalDivider(color = ScanPangColors.OutlineSubtle)
}

@Composable
fun DetailTodayVisitStatus(
    isOpen: Boolean,
    openHours: String,
    lastOrder: String = "",
    modifier: Modifier = Modifier,
) {
    val statusColor = if (isOpen) ScanPangColors.StatusOpen else ScanPangColors.Error
    val cardBg = if (isOpen) ScanPangColors.DetailVisitOpenSurface else ScanPangColors.DetailVisitClosedSurface
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.md),
    ) {
        DetailSectionHeader(title = "오늘 방문 가능 여부")
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = ScanPangShapes.radius12,
            color = cardBg,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor),
                    )
                    Text(
                        text = if (isOpen) "지금 영업 중" else "지금 영업 종료",
                        style = ScanPangType.caption12Medium,
                        color = statusColor,
                    )
                    Text(
                        text = "·",
                        style = ScanPangType.caption12Medium,
                        color = ScanPangColors.OnSurfaceStrong,
                    )
                    Text(
                        text = openHours,
                        style = ScanPangType.caption12Medium,
                        color = ScanPangColors.OnSurfaceStrong,
                    )
                }
                if (lastOrder.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ScanPangSpacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = ScanPangColors.OnSurfaceMuted,
                        )
                        Text(
                            text = "라스트오더 $lastOrder",
                            style = ScanPangType.caption12Medium,
                            color = ScanPangColors.OnSurfaceMuted,
                        )
                    }
                }
            }
        }
    }
}
