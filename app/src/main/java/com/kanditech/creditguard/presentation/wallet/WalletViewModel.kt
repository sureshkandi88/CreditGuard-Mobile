package com.kanditech.creditguard.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanditech.creditguard.data.local.entities.CreditorEntity
import com.kanditech.creditguard.data.local.entities.WalletTransactionEntity
import com.kanditech.creditguard.domain.usecase.GetDashboardDataUseCase
import com.kanditech.creditguard.domain.usecase.GetWalletTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WalletUiState(
    val balance: Double = 0.0,
    val transactions: List<WalletTransactionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val addMoneyResult: Result<Unit>? = null
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val getWalletTransactionsUseCase: GetWalletTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Collect balance from dashboard data
            getDashboardDataUseCase().onEach { creditor ->
                _uiState.update { it.copy(balance = creditor?.walletBalance ?: 0.0) }
            }.launchIn(viewModelScope)

            // Collect transactions
            getWalletTransactionsUseCase().onEach { transactions ->
                _uiState.update { it.copy(transactions = transactions, isLoading = false) }
            }.catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }.collect()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val balanceResult = getDashboardDataUseCase.refresh()
            val walletResult = getWalletTransactionsUseCase.refresh()
            if (balanceResult.isFailure || walletResult.isFailure) {
                _uiState.update { it.copy(error = "Sync Failed", isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addMoney(amount: Double, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, addMoneyResult = null) }
            val result = getWalletTransactionsUseCase.addMoney(amount, reason)
            if (result.isSuccess) {
                refresh()
            }
            _uiState.update { it.copy(isLoading = false, addMoneyResult = result) }
        }
    }

    fun clearAddMoneyResult() {
        _uiState.update { it.copy(addMoneyResult = null) }
    }
}
