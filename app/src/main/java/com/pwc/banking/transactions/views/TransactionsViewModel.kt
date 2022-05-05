package com.pwc.banking.transactions.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class TransactionsViewModel : ViewModel() {

    val navigation: ReceiveChannel<Navigate>
        get() = _navigation

    private val _navigation = Channel<Navigate>(Channel.RENDEZVOUS)

    fun onOneScreenAction() {
        viewModelScope.launch {
            _navigation.send(Navigate.ToOneScreen)
        }
    }

    fun onAnotherScreenAction() {
        viewModelScope.launch {
            _navigation.send(Navigate.ToAnotherScreen)
        }
    }

    sealed class Navigate {
        object ToOneScreen : Navigate()
        object ToAnotherScreen : Navigate()
    }
}