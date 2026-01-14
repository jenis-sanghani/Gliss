package com.gliss.motionui.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PremiumViewModel(private val billingManager: BillingManager = BillingManager()) : ViewModel() {
    val isPremium: StateFlow<Boolean> = billingManager.isPremium

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun upgradeToPremium() {
        viewModelScope.launch {
            _isProcessing.value = true
            billingManager.purchasePremium()
            _isProcessing.value = false
        }
    }
}
