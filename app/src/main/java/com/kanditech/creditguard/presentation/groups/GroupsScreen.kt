package com.kanditech.creditguard.presentation.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanditech.creditguard.data.local.entities.GroupEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onNavigateToDetails: (String) -> Unit,
    viewModel: GroupsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Inactive")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Customer Groups") })
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            val filteredGroups = if (selectedTab == 0) {
                uiState.groups.filter { it.isActive }
            } else {
                uiState.groups.filter { !it.isActive }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGroups) { group ->
                    GroupCard(group = group, onClick = { onNavigateToDetails(group.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCard(
    group: GroupEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (group.isActive) "Active" else "Inactive",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (group.isActive) Color.Green else Color.Gray
                )
            }
            Text(text = "Leader: ${group.leaderName}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Location: ${group.location}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Daily Due", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "₹${group.dailyInstallment}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(text = "Outstanding", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "₹${group.outstandingAmount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LinearProgressIndicator(
                progress = { group.totalRepaidPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = "${group.totalRepaidPercent}% Repaid",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(androidx.compose.ui.Alignment.End)
            )
        }
    }
}
