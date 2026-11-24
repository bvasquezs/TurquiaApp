package cl.duoc.turquiaapp.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.turquiaapp.TurquiaApplication
import cl.duoc.turquiaapp.ui.screens.auth.LoginScreen
import cl.duoc.turquiaapp.ui.screens.auth.RegisterScreen
import cl.duoc.turquiaapp.ui.screens.home.HomeScreen
import cl.duoc.turquiaapp.ui.screens.profile.ProfileScreen
import cl.duoc.turquiaapp.ui.screens.translator.TranslatorScreen
import cl.duoc.turquiaapp.ui.screens.words.AddWordScreen
import cl.duoc.turquiaapp.ui.screens.words.WordsScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val TRANSLATOR = "translator"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val ADD_WORD = "add_word"
    const val CATEGORY_ARG = "category"
    const val WORDS = "words/{$CATEGORY_ARG}"
    fun words(categoryKey: String) = "words/$categoryKey"
}

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Inicio", Icons.Filled.Home),
    BottomItem(Routes.TRANSLATOR, "Traductor", Icons.Filled.Translate),
    BottomItem(Routes.FAVORITES, "Favoritas", Icons.Filled.Favorite),
    BottomItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val session = remember { (context.applicationContext as TurquiaApplication).container.session }
    val startDestination = if (session.isLoggedIn) Routes.HOME else Routes.LOGIN

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = { navController.navigateToTab(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
            enterTransition = { slideInHorizontally { it / 4 } + fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { slideOutHorizontally { it / 4 } + fadeOut() }
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onGoRegister = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onGoLogin = { navController.popBackStack() }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onCategoryClick = { navController.navigate(Routes.words(it.name)) },
                    onTranslatorClick = { navController.navigateToTab(Routes.TRANSLATOR) }
                )
            }
            composable(
                route = Routes.WORDS,
                arguments = listOf(navArgument(Routes.CATEGORY_ARG) { type = NavType.StringType })
            ) {
                WordsScreen(
                    onBack = { navController.popBackStack() },
                    onAddWord = { navController.navigate(Routes.ADD_WORD) }
                )
            }
            composable(Routes.FAVORITES) {
                WordsScreen(onBack = null, onAddWord = { navController.navigate(Routes.ADD_WORD) })
            }
            composable(Routes.ADD_WORD) {
                AddWordScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.TRANSLATOR) {
                TranslatorScreen(onAddWord = { navController.navigate(Routes.ADD_WORD) })
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                    }
                )
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
