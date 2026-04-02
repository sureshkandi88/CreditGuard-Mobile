package com.kanditech.creditguard.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddCredit: () -> Unit,
    onNavigateToWallet: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Credit Guard Dashboard") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddCredit) {
                Icon(Icons.Default.Add, contentDescription = "Add Credit")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    KPICard(
                        title = "Wallet Balance",
                        value = "₹${uiState.creditor?.walletBalance ?: 0.0}",
                        color = MaterialTheme.colorScheme.primary,
                        onClick = onNavigateToWallet
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        KPICard(
                            modifier = Modifier.weight(1f),
                            title = "Today's Collection",
                            value = "₹${uiState.creditor?.todayCollection ?: 0.0}",
                            color = MaterialTheme.colorScheme.secondary
                        )
                        KPICard(
                            modifier = Modifier.weight(1f),
                            title = "Expected",
                            value = "₹${uiState.creditor?.expectedCollectionToday ?: 0.0}",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                item {
                    KPICard(
                        title = "Active Groups",
                        value = "${uiState.creditor?.activeGroupsCount ?: 0}",
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                item {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                
                // Recent activity items go here
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KPICard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick ?: {},
        enabled = onClick != null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = color
                )
            )
        }
    }
}
