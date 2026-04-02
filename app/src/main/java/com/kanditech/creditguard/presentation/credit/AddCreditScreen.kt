package com.kanditech.creditguard.presentation.credit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kanditech.creditguard.data.local.entities.CustomerEntity
import com.kanditech.creditguard.presentation.common.components.CustomerSearch
import com.kanditech.creditguard.presentation.groups.GroupsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCreditScreen(
    onSuccess: () -> Unit,
    viewModel: AddCreditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("New Credit Application (Step ${uiState.currentStep}/4)") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
            when (uiState.currentStep) {
                1 -> CustomerSelectionStep(viewModel)
                2 -> GroupInfoStep(viewModel)
                3 -> CreditAmountStep(viewModel)
                4 -> MemberRatiosStep(viewModel)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.currentStep > 1) {
                    OutlinedButton(
                        onClick = { viewModel.prevStep() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                }
                
                Button(
                    onClick = { 
                        if (uiState.currentStep < 4) viewModel.nextStep() else viewModel.submit()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading && isStepValid(uiState)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (uiState.currentStep < 4) "Next" else "Submit Application")
                    }
                }
            }
        }
    }
}

private fun isStepValid(state: AddCreditUiState): Boolean {
    return when (state.currentStep) {
        1 -> state.selectedCustomers.isNotEmpty()
        2 -> state.groupName.isNotBlank() && state.location.isNotBlank() && state.leaderId.isNotBlank()
        3 -> state.principalAmount > 0
        4 -> state.memberRatios.values.sum() == 100f
        else -> false
    }
}

@Composable
fun CustomerSelectionStep(viewModel: AddCreditViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    // Placeholder customers for now
    val customers = remember { 
        listOf(
            CustomerEntity("1", "John", "Doe", "1234567890", "AAD123", "Main St"),
            CustomerEntity("2", "Jane", "Smith", "0987654321", "AAD456", "Oak St")
        )
    }

    Text(text = "1. Select Members", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
    CustomerSearch(
        customers = customers,
        selectedCustomers = uiState.selectedCustomers,
        onToggleSelection = { viewModel.toggleCustomerSelection(it) }
    )
}

@Composable
fun GroupInfoStep(viewModel: AddCreditViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Text(text = "2. Group Details", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
    OutlinedTextField(
        value = uiState.groupName,
        onValueChange = { viewModel.updateGroupInfo(it, uiState.location, uiState.leaderId) },
        label = { Text("Group Name") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = uiState.location,
        onValueChange = { viewModel.updateGroupInfo(uiState.groupName, it, uiState.leaderId) },
        label = { Text("Location") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Select Leader", style = MaterialTheme.typography.titleSmall)
    uiState.selectedCustomers.forEach { customer ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = uiState.leaderId == customer.id,
                onClick = { viewModel.updateGroupInfo(uiState.groupName, uiState.location, customer.id) }
            )
            Text(text = "${customer.firstName} ${customer.lastName}")
        }
    }
}

@Composable
fun CreditAmountStep(viewModel: AddCreditViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var principalText by remember { mutableStateOf(uiState.principalAmount.toString()) }
    var interestText by remember { mutableStateOf(uiState.interestPercent.toString()) }

    Text(text = "3. Credit Details", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
    OutlinedTextField(
        value = principalText,
        onValueChange = { 
            principalText = it
            viewModel.updateCreditInfo(it.toDoubleOrNull() ?: 0.0, uiState.interestPercent)
        },
        label = { Text("Principal Amount (₹)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
        )
    )
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = interestText,
        onValueChange = { 
            interestText = it
            viewModel.updateCreditInfo(uiState.principalAmount, it.toDoubleOrNull() ?: 10.0)
        },
        label = { Text("Interest Percentage (%)") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
        )
    )
}

@Composable
fun MemberRatiosStep(viewModel: AddCreditViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Text(text = "4. Assign Member Ratios", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(uiState.selectedCustomers) { customer ->
            Column {
                Text(text = "${customer.firstName} ${customer.lastName}")
                Slider(
                    value = uiState.memberRatios[customer.id] ?: 0f,
                    onValueChange = { viewModel.updateMemberRatio(customer.id, it) },
                    valueRange = 0f..100f
                )
                Text(text = "${(uiState.memberRatios[customer.id] ?: 0f).toInt()}%")
            }
        }
        item {
            val total = uiState.memberRatios.values.sum()
            Text(
                text = "Total: ${total.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = if (total == 100f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
