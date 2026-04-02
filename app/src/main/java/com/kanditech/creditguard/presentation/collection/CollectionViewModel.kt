package com.kanditech.creditguard.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanditech.creditguard.data.local.entities.GroupEntity
import com.kanditech.creditguard.domain.usecase.CollectPaymentUseCase
import com.kanditech.creditguard.domain.usecase.GetGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionUiState(
    val groups: List<GroupEntity> = emptyList(),
    val filteredGroups: List<GroupEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val paymentResult: Result<Unit>? = null
)

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val collectPaymentUseCase: CollectPaymentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getGroupsUseCase().onEach { groups ->
                _uiState.update { 
                    it.copy(
                        groups = groups.filter { g -> g.isActive },
                        filteredGroups = filterGroups(groups, it.searchQuery),
                        isLoading = false 
                    ) 
                }
            }.catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }.collect()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { 
            it.copy(
                searchQuery = query,
                filteredGroups = filterGroups(it.groups, query)
            ) 
        }
    }

    private fun filterGroups(groups: List<GroupEntity>, query: String): List<GroupEntity> {
        if (query.isBlank()) return groups.filter { it.isActive }
        return groups.filter { group ->
            group.isActive && (group.name.contains(query, ignoreCase = true) || 
            group.leaderName.contains(query, ignoreCase = true))
        }
    }

    fun collectPayment(groupId: String, amount: Double, notes: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, paymentResult = null) }
            val result = collectPaymentUseCase(groupId, amount, notes)
            _uiState.update { it.copy(isLoading = false, paymentResult = result) }
        }
    }

    fun clearPaymentResult() {
        _uiState.update { it.copy(paymentResult = null) }
    }
}
