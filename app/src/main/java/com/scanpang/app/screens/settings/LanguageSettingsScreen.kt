package com.scanpang.app.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.scanpang.app.data.OnboardingPreferences
import com.scanpang.app.screens.onboarding.OnboardingChoiceContent
import com.scanpang.app.screens.onboarding.OnboardingSelectableCard
import com.scanpang.app.ui.theme.ScanPangColors
import com.scanpang.app.ui.theme.ScanPangDimens
import com.scanpang.app.ui.theme.ScanPangSpacing
import com.scanpang.app.ui.theme.ScanPangType

private data class LanguageOption(
    val code: String,
    val flag: String,
    val label: String,
    val subLabel: String,
)

/**
 * 내 정보 → 언어 설정. 온보딩 1단계와 동일한 카드 UI 를 재사용하되,
 * "다음" 버튼 없이 선택 즉시 SharedPreferences 에 저장한다.
 */
@Composable
fun LanguageSettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val prefs = remember { OnboardingPreferences(context) }
    val options = remember {
        listOf(
            LanguageOption(OnboardingPreferences.LANG_KO, "🇰🇷", "한국어", "Korean"),
            LanguageOption(OnboardingPreferences.LANG_EN, "🇺🇸", "English", "영어"),
        )
    }
    var selectedCode by remember { mutableStateOf(prefs.getLanguageCode()) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = ScanPangDimens.screenHorizontal),
        ) {
            SettingsTitleBar(
                title = "언어 설정",
                onBack = { navController.popBackStack() },
            )
            Text(
                text = "앱에서 사용할 언어를 선택하세요. 선택한 언어로 음성 안내와 텍스트가 제공됩니다.",
                style = ScanPangType.body14Regular,
                color = ScanPangColors.OnSurfaceMuted,
            )
            Spacer(modifier = Modifier.height(ScanPangSpacing.lg))
            Column(verticalArrangement = Arrangement.spacedBy(ScanPangSpacing.sm)) {
                options.forEach { opt ->
                    OnboardingSelectableCard(
                        selected = selectedCode == opt.code,
                        onClick = {
                            selectedCode = opt.code
                            prefs.setLanguageCode(opt.code)
                        },
                    ) {
                        OnboardingChoiceContent(
                            leading = opt.flag,
                            title = opt.label,
                            subtitle = opt.subLabel,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SettingsTitleBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ScanPangSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "뒤로",
                tint = ScanPangColors.OnSurfaceStrong,
            )
        }
        Text(
            text = title,
            style = ScanPangType.detailScreenTitle22,
            color = ScanPangColors.OnSurfaceStrong,
            modifier = Modifier.weight(1f),
        )
    }
}
