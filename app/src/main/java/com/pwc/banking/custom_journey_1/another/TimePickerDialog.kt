package com.pwc.banking.custom_journey_1.another

import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.widget.TimePicker
import java.time.LocalTime

@Suppress("FunctionName") // Factory
internal fun TimePickerDialog(
    context: Context,
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) = TimePickerDialog(
    context,
    LocalTimeListener(onTimeSelected),
    initialTime.hour, initialTime.minute,
    DateFormat.is24HourFormat(context)
)

private class LocalTimeListener(
    private val onTimeSelected: (LocalTime) -> Unit
) : TimePickerDialog.OnTimeSetListener {
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) =
        onTimeSelected(LocalTime.of(hourOfDay, minute))
}
