package com.example.ui.simulator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PaperTradeEntity
import com.example.data.model.MockStock
import com.example.data.repository.AppRepository
import com.example.data.sample.BengaliStockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaperTradingViewModel(application: Application) : AndroidViewModel(application) {
    val repository = AppRepository(application)
    val currentUser = repository.currentUser

    val availableStocks: List<MockStock> = BengaliStockData.stocks

    private val _username = currentUser.value?.username ?: "investor"

    val holdings: StateFlow<List<PaperTradeEntity>> = repository.getPaperTrades(_username)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun buyStock(stock: MockStock, quantity: Int) {
        if (quantity <= 0) return
        val user = currentUser.value?.username ?: "investor"
        viewModelScope.launch {
            repository.executeBuyTrade(
                username = user,
                symbol = stock.symbol,
                nameBn = stock.nameBn,
                shares = quantity,
                price = stock.price
            )
            _message.value = "${stock.nameBn} এর $quantity টি শেয়ার ভার্চুয়াল পোর্টফোলিওতে যোগ হয়েছে!"
        }
    }

    fun sellStock(tradeId: Int) {
        viewModelScope.launch {
            repository.sellTrade(tradeId)
            _message.value = "শেয়ারটি সফলভাবে ভার্চুয়াল বাজারে বিক্রি হয়েছে!"
        }
    }

    fun resetPortfolio() {
        val user = currentUser.value?.username ?: "investor"
        viewModelScope.launch {
            repository.resetPaperTrading(user)
            _message.value = "ভার্চুয়াল পোর্টফোলিও রিসেট করা হয়েছে।"
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
