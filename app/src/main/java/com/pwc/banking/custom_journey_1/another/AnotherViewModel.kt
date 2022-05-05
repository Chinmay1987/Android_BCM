package com.pwc.banking.custom_journey_1.another

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

internal class AnotherViewModel : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: ReceiveChannel<State>
        get() = _state.openSubscription()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _state = BroadcastChannel<State>(Channel.CONFLATED)

    val navigation: ReceiveChannel<Navigate>
        get() = _navigation

    private val _navigation = Channel<Navigate>(Channel.RENDEZVOUS)

    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    private var selectedDate: LocalDate? = null

    private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    private var selectedTime: LocalTime? = null

    private val zoneOptions = ZoneId.getAvailableZoneIds().toSortedSet()
    private var selectedZone: ZoneId? = null

    init {
        sendIdleState()
    }

    fun selectDate() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val nextWeek = today.plusDays(7)

            @OptIn(ExperimentalCoroutinesApi::class)
            _state.send(State.SelectDate(today, nextWeek))
        }
    }

    fun onDateSelected(date: LocalDate) {
        selectedDate = date
        sendIdleState()
    }

    fun selectTime() {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            _state.send(State.SelectTime)
        }
    }

    fun onTimeSelected(time: LocalTime) {
        selectedTime = time
        sendIdleState()
    }

    fun selectZone() {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            _state.send(State.SelectZone(zoneOptions, ZoneId.systemDefault().id))
        }
    }

    fun onZoneSelected(zone: String) {
        selectedZone = ZoneId.of(zone)
        sendIdleState()
    }

    fun onSelectionCancelled() = sendIdleState()

    private fun sendIdleState() {
        viewModelScope.launch {
            val formattedDate = selectedDate?.let { dateFormatter.format(it) }
            val formattedTime = selectedTime?.let { timeFormatter.format(it) }
            val formattedZone =
                selectedZone?.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())

            @OptIn(ExperimentalCoroutinesApi::class)
            _state.send(State.Idle(formattedDate, formattedTime, formattedZone))
        }
    }

    fun useSelectedDateTime() {
        viewModelScope.launch {
            _navigation.send(Navigate.OnDateTimeSelected(selectedDate, selectedTime, selectedZone))
        }
    }

    sealed class State {
        data class Idle(
            val selectedDate: CharSequence? = null,
            val selectedTime: CharSequence? = null,
            val selectedZone: CharSequence? = null
        ) : State()

        data class SelectDate(
            val start: LocalDate,
            val end: LocalDate
        ) : State()

        object SelectTime : State()

        data class SelectZone(
            val options: Set<String>,
            val defaultSelection: String
        ) : State()
    }

    sealed class Navigate {
        data class OnDateTimeSelected(
            val date: LocalDate?,
            val time: LocalTime?,
            val zone: ZoneId?
        ) : Navigate()
    }
}
