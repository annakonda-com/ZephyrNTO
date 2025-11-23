package ru.myitschool.work.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.myitschool.work.ui.nav.AuthScreenDestination
import ru.myitschool.work.ui.nav.BookScreenDestination
import ru.myitschool.work.ui.nav.MainScreenDestination
import ru.myitschool.work.ui.screen.auth.AuthScreen
import ru.myitschool.work.ui.screen.book.BookScreen
import ru.myitschool.work.ui.screen.main.MainScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        navController = navController,
        startDestination = AuthScreenDestination,
    ) {
        composable<AuthScreenDestination> {
            AuthScreen(navController = navController)
        }
        composable<MainScreenDestination> {
            MainScreen(navController = navController)
        }
        composable<BookScreenDestination> {
            BookScreen(navController = navController)
        }
    }
}