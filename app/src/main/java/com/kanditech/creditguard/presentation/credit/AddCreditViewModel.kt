package com.kanditech.creditguard.presentation.credit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanditech.creditguard.data.local.entities.CustomerEntity
import com.kanditech.creditguard.data.remote.dto.CreateGroupRequest
import com.kanditech.creditguard.data.remote.dto.GroupMemberRequest
import com.kanditech.creditguard.domain.repository.CreditGuardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddCreditUiState(
    val currentStep: Int = 1,
    val selectedCustomers: List<CustomerEntity> = emptyList(),
    val groupName: String = "",
    val location: String = "",
    val leaderId: String = "",
    val principalAmount: Double = 0.0,
    val interestPercent: Double = 10.0,
    val memberRatios: Map<String, Float> = emptyMap(), // customerId to ratio
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class AddCreditViewModel @Inject constructor(
    private val repository: CreditGuardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCreditUiState())
    val uiState: StateFlow<AddCreditUiState> = _uiState.asStateFlow()

    fun nextStep() {
        _uiState.update { it.copy(currentStep = it.currentStep + 1) }
    }

    fun prevStep() {
        _uiState.update { it.copy(currentStep = it.currentStep - 1) }
    }

    fun toggleCustomerSelection(customer: CustomerEntity) {
        _uiState.update { state ->
            val newList = if (state.selectedCustomers.contains(customer)) {
                state.selectedCustomers - customer
            } else {
                state.selectedCustomers + customer
            }
            state.copy(selectedCustomers = newList)
        }
    }

    fun updateGroupInfo(name: String, location: String, leaderId: String) {
        _uiState.update { it.copy(groupName = name, location = location, leaderId = leaderId) }
    }

    fun updateCreditInfo(principal: Double, interest: Double) {
        _uiState.update { it.copy(principalAmount = principal, interestPercent = interest) }
    }

    fun updateMemberRatio(customerId: String, ratio: Float) {
        _uiState.update { state ->
            val newRatios = state.memberRatios.toMutableMap()
            newRatios[customerId] = ratio
            state.copy(memberRatios = newRatios)
        }
    }

    fun submit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            val request = CreateGroupRequest(
                name = state.groupName,
                location = state.location,
                leaderId = state.leaderId,
                members = state.memberRatios.map { (id, ratio) ->
                    GroupMemberRequest(id, ratio)
                }
            )
            val result = repository.createGroupWithCredit(request)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, success = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
