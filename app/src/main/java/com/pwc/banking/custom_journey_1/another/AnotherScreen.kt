package com.pwc.banking.custom_journey_1.another

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.pwc.banking.custom_journey_1.CustomRouter1
import com.pwc.banking.databinding.AnotherScreenBinding
import java.time.LocalTime
import org.koin.android.ext.android.inject

internal class AnotherScreen : Fragment() {

    private val router by inject<CustomRouter1>()

    private val viewModel by viewModels<AnotherViewModel>()

    private val datePickerTag = "Date picker"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AnotherScreenBinding.inflate(inflater, container, false)
        observeState(binding)
        observeNavigation()

        with(binding) {
            dateSelector.setOnClickListener { viewModel.selectDate() }
            timeSelector.setOnClickListener { viewModel.selectTime() }
            zoneSelector.setOnClickListener { viewModel.selectZone() }
            viewDateTimeButton.setOnClickListener { viewModel.useSelectedDateTime() }
        }

        return binding.root
    }

    private fun observeState(binding: AnotherScreenBinding) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            for (state in viewModel.state) {
                when (state) {
                    is AnotherViewModel.State.Idle -> showIdleState(binding, state)
                    is AnotherViewModel.State.SelectDate -> showSelectDateState(state)
                    is AnotherViewModel.State.SelectTime -> showSelectTimeState(binding)
                    is AnotherViewModel.State.SelectZone -> showSelectZoneState(binding, state)
                }
            }
        }
    }

    private fun showIdleState(binding: AnotherScreenBinding, state: AnotherViewModel.State.Idle) {
        binding.dateSelector.text = state.selectedDate
        binding.timeSelector.text = state.selectedTime
        binding.zoneSelector.text = state.selectedZone
    }

    private fun showSelectDateState(state: AnotherViewModel.State.SelectDate) {
        val constraints = CalendarConstraints.Builder()
            .setOpenAt(state.start)
            .setStart(state.start)
            .setEnd(state.end)
            .setValidator(RangeDateValidator(state.start, state.end))
            .build()
        MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraints)
            .setSelection(state.start)
            .build()
            .apply {
                addOnDismissListener { viewModel.onSelectionCancelled() }
                addOnDateSelectedListener { viewModel.onDateSelected(it) }
            }
            .show()
    }

    private fun MaterialDatePicker<Long>.show() =
        show(this@AnotherScreen.childFragmentManager, datePickerTag)

    private fun showSelectTimeState(binding: ViewBinding) {
        val dialog = TimePickerDialog(binding.root.context, LocalTime.now()) {
            viewModel.onTimeSelected(it)
        }
        dialog.setOnDismissListener { viewModel.onSelectionCancelled() }
        dialog.show()
    }

    private fun showSelectZoneState(
        binding: ViewBinding,
        state: AnotherViewModel.State.SelectZone
    ) {
        val zones = state.options.toTypedArray()
        val defaultZone = state.defaultSelection

        var selectedZone = defaultZone
        AlertDialog.Builder(binding.root.context)
            .setSingleChoiceItems(zones, zones.indexOf(defaultZone)) { _, which ->
                selectedZone = zones[which]
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.onZoneSelected(selectedZone)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setOnCancelListener { viewModel.onSelectionCancelled() }
            .show()
    }

    private fun observeNavigation() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            for (navigation in viewModel.navigation) {
                when (navigation) {
                    is AnotherViewModel.Navigate.OnDateTimeSelected ->
                        router.viewDateTime(navigation.date, navigation.time, navigation.zone)
                }
            }
        }
    }
}
