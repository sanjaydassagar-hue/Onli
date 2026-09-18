package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.repository.AppRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.AuthViewModel
import com.example.ui.calculator.CalculatorScreen
import com.example.ui.glossary.GlossaryScreen
import com.example.ui.home.HomeScreen
import com.example.ui.home.LessonDetailScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.quiz.QuizScreen
import com.example.ui.quiz.QuizViewModel
import com.example.ui.simulator.PaperTradingScreen
import com.example.ui.simulator.PaperTradingViewModel

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Main : Screen("main")
    object LessonDetail : Screen("lesson_detail/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson_detail/$lessonId"
    }
    object Profile : Screen("profile")
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun AppNavigation(
    repository: AppRepository,
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val currentUser by repository.currentUser.collectAsState()

    // Determine initial route based on logged-in state
    val startDestination = if (currentUser != null) Screen.Main.route else Screen.Auth.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainContainer(
                repository = repository,
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.LessonDetail.createRoute(lessonId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(
            route = Screen.LessonDetail.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonDetailScreen(
                lessonId = lessonId,
                repository = repository,
                onBack = { navController.popBackStack() },
                onNavigateNext = { nextId ->
                    navController.navigate(Screen.LessonDetail.createRoute(nextId)) {
                        popUpTo(Screen.LessonDetail.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                repository = repository,
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.LessonDetail.createRoute(lessonId))
                },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainContainer(
    repository: AppRepository,
    onLessonClick: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var selectedBottomTab by rememberSaveable { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem("শিক্ষা", Icons.AutoMirrored.Filled.MenuBook, "nav_learn"),
        BottomNavItem("লাইভ ক্লাস", Icons.Default.VideoCall, "nav_live"),
        BottomNavItem("কুইজ", Icons.Default.Quiz, "nav_quiz"),
        BottomNavItem("ক্যালকুলেটর", Icons.Default.Calculate, "nav_calculator"),
        BottomNavItem("প্র্যাকটিস", Icons.Default.ShowChart, "nav_simulator"),
        BottomNavItem("প্রোফাইল", Icons.Default.Person, "nav_profile")
    )

    val quizViewModel: QuizViewModel = viewModel()
    val paperTradingViewModel: PaperTradingViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedBottomTab == index,
                        onClick = { selectedBottomTab = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedBottomTab) {
                0 -> HomeScreen(
                    repository = repository,
                    onLessonClick = onLessonClick,
                    onNavigateToLiveClass = { selectedBottomTab = 1 },
                    onNavigateToQuiz = { selectedBottomTab = 2 },
                    onNavigateToCalculator = { selectedBottomTab = 3 },
                    onNavigateToSimulator = { selectedBottomTab = 4 }
                )
                1 -> com.example.ui.live.LiveClassScreen(repository = repository)
                2 -> QuizScreen(
                    viewModel = quizViewModel,
                    onNavigateBackToHome = { selectedBottomTab = 0 }
                )
                3 -> CalculatorScreen()
                4 -> PaperTradingScreen(viewModel = paperTradingViewModel)
                5 -> ProfileScreen(
                    repository = repository,
                    onLessonClick = onLessonClick,
                    onNavigateToLiveClass = { selectedBottomTab = 1 },
                    onLogout = onNavigateToProfile
                )
            }
        }
    }
}
