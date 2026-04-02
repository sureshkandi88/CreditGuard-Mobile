package com.kanditech.creditguard.presentation.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kanditech.creditguard.data.local.entities.CustomerEntity

@Composable
fun CustomerSearch(
    customers: List<CustomerEntity>,
    selectedCustomers: List<CustomerEntity>,
    onToggleSelection: (CustomerEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCustomers = customers.filter { 
        it.firstName.contains(searchQuery, ignoreCase = true) || 
        it.lastName.contains(searchQuery, ignoreCase = true) ||
        it.phone.contains(searchQuery)
    }

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            placeholder = { Text("Search Customers...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredCustomers) { customer ->
                val isSelected = selectedCustomers.any { it.id == customer.id }
                ListItem(
                    headlineContent = { Text("${customer.firstName} ${customer.lastName}") },
                    supportingContent = { Text(customer.phone) },
                    trailingContent = {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.clickable { onToggleSelection(customer) }
                )
            }
        }
    }
}
