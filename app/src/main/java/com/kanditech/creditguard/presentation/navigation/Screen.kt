package com.kanditech.creditguard.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Groups : Screen("groups")
    object Collection : Screen("collection")
    object Wallet : Screen("wallet")
    object Profile : Screen("profile")
    object AddCredit : Screen("add_credit")
    object GroupDetails : Screen("group_details/{groupId}") {
        fun createRoute(groupId: String) = "group_details/$groupId"
    }
}
