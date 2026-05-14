package com.scanpang.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.scanpang.app.screens.HomeScreen
import com.scanpang.app.screens.SplashScreen
import com.scanpang.app.screens.auth.LoginErrorScreen
import com.scanpang.app.screens.auth.LoginScreen
import com.scanpang.app.screens.auth.OAuthLoadingScreen
import com.scanpang.app.screens.auth.TermsAgreementScreen
import com.scanpang.app.screens.auth.WithdrawalScreen
import com.scanpang.app.screens.onboarding.OnboardingLanguageScreen
import com.scanpang.app.screens.onboarding.OnboardingNameScreen
import com.scanpang.app.screens.onboarding.OnboardingPreferenceScreen
import com.scanpang.app.screens.NearbyHalalRestaurantsScreen
import com.scanpang.app.screens.NearbyPrayerRoomsScreen
import com.scanpang.app.screens.PlaceDetailScreen
import com.scanpang.app.screens.ProfileScreen
import com.scanpang.app.screens.QiblaDirectionScreen
import com.scanpang.app.screens.RecentlyViewedListScreen
import com.scanpang.app.screens.SavedPlacesScreen
import com.scanpang.app.screens.SearchDefaultScreen
import com.scanpang.app.screens.ar.ArExploreScreen
import com.scanpang.app.screens.ar.ArNavigationMapScreen
import com.scanpang.app.screens.settings.LanguageSettingsScreen
import com.scanpang.app.screens.settings.NotificationSettingsScreen
import com.scanpang.app.screens.settings.ValueAddedSettingsScreen

object AppRoutes {
    const val Splash = "splash"
    const val OnboardingLanguage = "onboarding_language"
    const val OnboardingName = "onboarding_name"
    const val OnboardingPreference = "onboarding_preference"

    const val Home = "home"
    const val Qibla = "qibla"
    const val Search = "search"

    /**
     * Home 의 quick action 등에서 검색 탭을 띄우면서 입력칸에 미리 채워넣을 검색어를 전달할 때 쓰는
     * [androidx.navigation.NavBackStackEntry.savedStateHandle] 키.
     * 값이 비어있지 않으면 SearchDefaultScreen 진입 시 한 번 query 로 반영되고 null 로 리셋된다.
     */
    const val SearchSavedStatePendingQueryKey = "pending_search_query"

    const val Saved = "saved"
    const val Profile = "profile"
    const val RecentlyViewed = "recently_viewed"
    const val NearbyHalal = "nearby_halal"
    const val NearbyPrayer = "nearby_prayer"
    const val PlaceDetail = "place_detail/{categoryKey}/{placeId}"
    const val PlaceDetailCategoryKeyArg = "categoryKey"
    const val PlaceDetailPlaceIdArg = "placeId"
    fun placeDetailRoute(categoryKey: String, placeId: String) = "place_detail/$categoryKey/$placeId"
    const val ArExplore = "ar_explore"
    const val ArNavMap = "ar_nav_map?destination={destination}"
    const val ArNavMapDestinationArg = "destination"
    fun arNavMapRoute(destination: String) = "ar_nav_map?destination=${android.net.Uri.encode(destination)}"

    // 내 정보 → 상세 설정 페이지들
    const val SettingsLanguage = "settings_language"
    const val SettingsValueAdded = "settings_value_added"
    const val SettingsNotification = "settings_notification"

    const val Login = "login"
    const val TermsAgreement = "terms_agreement"
    /** 라우트 패턴 — 호출 시 [oauthLoadingRoute] 사용 */
    const val OAuthLoading = "oauth_loading?provider={provider}"
    const val OAuthLoadingArgProvider = "provider"
    const val LoginError = "login_error"
    const val Withdrawal = "withdrawal"

    /** OAuth Loading 화면으로 이동할 때 provider 식별자를 함께 전달 */
    fun oauthLoadingRoute(provider: String): String = "oauth_loading?provider=$provider"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppRoutes.Splash,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(AppRoutes.Splash) {
            SplashScreen(navController = navController)
        }
        composable(AppRoutes.OnboardingLanguage) {
            OnboardingLanguageScreen(navController = navController)
        }
        composable(AppRoutes.OnboardingName) {
            OnboardingNameScreen(navController = navController)
        }
        composable(AppRoutes.OnboardingPreference) {
            OnboardingPreferenceScreen(navController = navController)
        }
        composable(AppRoutes.Home) {
            HomeScreen(navController = navController)
        }
        composable(AppRoutes.Qibla) {
            QiblaDirectionScreen(navController = navController)
        }
        // 검색 탭 — 검색 결과는 이 화면 안에서 query 상태에 따라 조건부로 렌더된다(별도 라우트 없음).
        // 다른 화면에서 사전입력 query 를 주고 싶으면 [navigateToSearchWithQuery] 헬퍼를 쓴다.
        composable(
            route = AppRoutes.Search,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            SearchDefaultScreen(navController = navController)
        }
        composable(AppRoutes.Saved) {
            SavedPlacesScreen(navController = navController)
        }
        composable(AppRoutes.Profile) {
            ProfileScreen(navController = navController)
        }
        composable(AppRoutes.RecentlyViewed) {
            RecentlyViewedListScreen(navController = navController)
        }
        composable(AppRoutes.NearbyHalal) {
            NearbyHalalRestaurantsScreen(navController = navController)
        }
        composable(AppRoutes.NearbyPrayer) {
            NearbyPrayerRoomsScreen(navController = navController)
        }
        composable(
            route = AppRoutes.PlaceDetail,
            arguments = listOf(
                navArgument(AppRoutes.PlaceDetailCategoryKeyArg) { type = NavType.StringType },
                navArgument(AppRoutes.PlaceDetailPlaceIdArg) { type = NavType.StringType },
            ),
        ) { entry ->
            val categoryKey = entry.arguments?.getString(AppRoutes.PlaceDetailCategoryKeyArg) ?: return@composable
            val placeId = entry.arguments?.getString(AppRoutes.PlaceDetailPlaceIdArg) ?: return@composable
            PlaceDetailScreen(navController = navController, categoryKey = categoryKey, placeId = placeId)
        }
        composable(AppRoutes.ArExplore) {
            ArExploreScreen(navController = navController)
        }
        composable(
            route = AppRoutes.ArNavMap,
            arguments = listOf(
                navArgument(AppRoutes.ArNavMapDestinationArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { entry ->
            val destination = entry.arguments?.getString(AppRoutes.ArNavMapDestinationArg)
            ArNavigationMapScreen(navController = navController, destinationName = destination)
        }
        composable(AppRoutes.SettingsLanguage) {
            LanguageSettingsScreen(navController = navController)
        }
        composable(AppRoutes.SettingsValueAdded) {
            ValueAddedSettingsScreen(navController = navController)
        }
        composable(AppRoutes.SettingsNotification) {
            NotificationSettingsScreen(navController = navController)
        }
        composable(AppRoutes.Login) {
            val context = androidx.compose.ui.platform.LocalContext.current
            LoginScreen(
                onKakaoClick = {
                    navController.navigate(
                        AppRoutes.oauthLoadingRoute(AuthProviderArg.Kakao),
                    )
                },
                onGoogleClick = {
                    navController.navigate(
                        AppRoutes.oauthLoadingRoute(AuthProviderArg.Google),
                    )
                },
                onTermsClick = {
                    // TODO: 약관 전문 화면 별도 구현 — 현재는 안내 토스트만 노출
                    android.widget.Toast
                        .makeText(context, "이용약관 (전문 화면은 준비 중)", android.widget.Toast.LENGTH_SHORT)
                        .show()
                },
                onPrivacyClick = {
                    // TODO: 개인정보 처리방침 전문 화면 별도 구현
                    android.widget.Toast
                        .makeText(context, "개인정보 처리방침 (전문 화면은 준비 중)", android.widget.Toast.LENGTH_SHORT)
                        .show()
                },
            )
        }
        composable(AppRoutes.TermsAgreement) {
            TermsAgreementScreen(
                onBack = { navController.popBackStack() },
                onAllAgreedAndContinue = {
                    navController.navigate(AppRoutes.OnboardingLanguage)
                },
                onTermDetailClick = { /* TODO: 약관 전문 보기 */ },
            )
        }
        composable(
            route = AppRoutes.OAuthLoading,
            arguments = listOf(
                navArgument(AppRoutes.OAuthLoadingArgProvider) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { entry ->
            val providerArg = entry.arguments?.getString(AppRoutes.OAuthLoadingArgProvider)
            val provider = when (providerArg) {
                AuthProviderArg.Kakao -> com.scanpang.app.data.AuthProvider.KAKAO
                AuthProviderArg.Google -> com.scanpang.app.data.AuthProvider.GOOGLE
                else -> null
            }
            OAuthLoadingScreen(
                provider = provider,
                onAuthSuccessExistingUser = {
                    navController.navigate(AppRoutes.Home) {
                        popUpTo(AppRoutes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onAuthSuccessNewUser = {
                    navController.navigate(AppRoutes.TermsAgreement) {
                        popUpTo(AppRoutes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppRoutes.LoginError) {
            LoginErrorScreen(
                onRetry = { /* TODO: 재시도 */ },
                onBackToLogin = {
                    navController.popBackStack(AppRoutes.Login, inclusive = false)
                },
            )
        }
        composable(AppRoutes.Withdrawal) {
            val context = androidx.compose.ui.platform.LocalContext.current
            WithdrawalScreen(
                onBack = { navController.popBackStack() },
                onWithdraw = {
                    // 회원탈퇴: 모든 prefs 삭제 + 백스택 비우고 Login 으로
                    com.scanpang.app.data.OnboardingPreferences(context).clearAll()
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}

/** OAuth provider 식별자(NavArg 직렬화용) */
private object AuthProviderArg {
    const val Kakao = "kakao"
    const val Google = "google"
}

/**
 * 검색 탭으로 이동하면서 입력칸에 [prefillQuery] 를 미리 채워둔다.
 * (예: 홈의 quick action 카드 → "비건 식당" 검색)
 *
 * 바텀 탭과 동일한 navigation 옵션(saveState/restoreState) 을 적용해 탭 전환 UX 가 깨지지 않게 한다.
 * 사전입력 값은 [AppRoutes.SearchSavedStatePendingQueryKey] 로 Search 라우트의 savedStateHandle 에
 * 전달되며, SearchDefaultScreen 의 LaunchedEffect 가 받아서 query 상태로 흘려준다.
 */
fun NavController.navigateToSearchWithQuery(prefillQuery: String) {
    navigate(AppRoutes.Search) {
        popUpTo(AppRoutes.Home) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
    runCatching {
        getBackStackEntry(AppRoutes.Search).savedStateHandle[AppRoutes.SearchSavedStatePendingQueryKey] =
            prefillQuery
    }
}
