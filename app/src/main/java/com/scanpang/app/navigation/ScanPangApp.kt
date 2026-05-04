package com.scanpang.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scanpang.app.components.ScanPangMainTab
import com.scanpang.app.components.ScanPangTabBar

private val mainTabRoutes = setOf(
    AppRoutes.Home,
    AppRoutes.Search,
    AppRoutes.Saved,
    AppRoutes.Profile,
    AppRoutes.ArExplore,
    AppRoutes.ArNavMap,
)

@Composable
fun ScanPangApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showTabBar = currentRoute in mainTabRoutes

    Box(modifier = modifier.fillMaxSize()) {
        AppNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
        )
        if (showTabBar) {
            ScanPangTabBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedTab = when (currentRoute) {
                    AppRoutes.Home -> ScanPangMainTab.Home
                    AppRoutes.Search -> ScanPangMainTab.Search
                    AppRoutes.Saved -> ScanPangMainTab.Saved
                    AppRoutes.Profile -> ScanPangMainTab.Profile
                    AppRoutes.ArExplore, AppRoutes.ArNavMap -> ScanPangMainTab.Explore
                    else -> ScanPangMainTab.Home
                },
                onHomeClick = { navigateToHome(navController) },
                onSearchClick = { navigateMainTab(navController, AppRoutes.Search) },
                onSavedClick = { navigateMainTab(navController, AppRoutes.Saved) },
                onProfileClick = { navigateMainTab(navController, AppRoutes.Profile) },
                onExploreClick = { navigateToArExplore(navController) },
            )
        }
    }
}

/**
 * AR 탐색으로 이동. 길안내([AppRoutes.ArNavMap]) 위에 쌓인 경우 스택에 남은
 * [AppRoutes.ArExplore]까지 pop 해서 기본 탐색 화면으로 돌아간다.
 */
private fun navigateToArExplore(navController: androidx.navigation.NavController) {
    if (navController.currentBackStackEntry?.destination?.route == AppRoutes.ArExplore) {
        return
    }
    val popped = navController.popBackStack(AppRoutes.ArExplore, inclusive = false)
    if (!popped) {
        navController.navigate(AppRoutes.ArExplore) {
            popUpTo(AppRoutes.Home) {
                saveState = true
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun navigateToHome(navController: androidx.navigation.NavController) {
    navController.navigate(AppRoutes.Home) {
        popUpTo(AppRoutes.Home) { inclusive = true }
        launchSingleTop = true
    }
}

private fun navigateMainTab(navController: androidx.navigation.NavController, route: String) {
    navController.navigate(route) {
        popUpTo(AppRoutes.Home) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
}
