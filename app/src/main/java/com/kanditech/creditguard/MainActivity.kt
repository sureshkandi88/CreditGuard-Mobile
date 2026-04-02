package com.kanditech.creditguard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kanditech.creditguard.presentation.auth.AuthViewModel
import com.kanditech.creditguard.presentation.auth.LoginScreen
import com.kanditech.creditguard.presentation.collection.CollectionScreen
import com.kanditech.creditguard.presentation.credit.AddCreditScreen
import com.kanditech.creditguard.presentation.dashboard.DashboardScreen
import com.kanditech.creditguard.presentation.groups.GroupsScreen
import com.kanditech.creditguard.presentation.navigation.Screen
import com.kanditech.creditguard.presentation.wallet.WalletScreen
import com.kanditech.creditguard.ui.theme.CreditGuardTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CreditGuardTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.uiState.collectAsState()

                val items = listOf(
                    Screen.Dashboard,
                    Screen.Groups,
                    Screen.Collection,
                    Screen.Wallet,
                    Screen.Profile
                )

                Scaffold(
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        val showBottomBar = items.any { it.route == currentDestination?.route } && authState.isLoggedIn

                        if (showBottomBar) {
                            NavigationBar {
                                items.forEach { screen ->
                                    NavigationBarItem(
                                        icon = {
                                            when (screen) {
                                                Screen.Dashboard -> Icon(Icons.Default.Dashboard, contentDescription = null)
                                                Screen.Groups -> Icon(Icons.Default.Group, contentDescription = null)
                                                Screen.Collection -> Icon(Icons.Default.Collections, contentDescription = null)
                                                Screen.Wallet -> Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
                                                Screen.Profile -> Icon(Icons.Default.Person, contentDescription = null)
                                                else -> Icon(Icons.Default.Dashboard, contentDescription = null)
                                            }
                                        },
                                        label = {
                                            Text(screen.route.replaceFirstChar {
                                                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                                            })
                                        },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (authState.isLoggedIn) Screen.Dashboard.route else Screen.Login.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                onNavigateToAddCredit = { navController.navigate(Screen.AddCredit.route) },
                                onNavigateToWallet = { navController.navigate(Screen.Wallet.route) }
                            )
                        }
                        composable(Screen.Groups.route) {
                            GroupsScreen(
                                onNavigateToDetails = { groupId ->
                                    navController.navigate(Screen.GroupDetails.createRoute(groupId))
                                }
                            )
                        }
                        composable(
                            route = Screen.GroupDetails.route,
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) {
                            Text("Group Details Screen (WIP)")
                        }
                        composable(Screen.Collection.route) {
                            CollectionScreen()
                        }
                        composable(Screen.Wallet.route) {
                            WalletScreen()
                        }
                        composable(Screen.AddCredit.route) {
                            AddCreditScreen(onSuccess = { navController.popBackStack() })
                        }
                        composable(Screen.Profile.route) {
                            Column {
                                Text("Profile Screen (WIP)")
                                Button(onClick = { 
                                    authViewModel.logout()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }) {
                                    Text("Logout")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
