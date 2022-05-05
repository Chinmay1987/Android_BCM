package com.pwc.banking.custom_journey_1.another

import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// TODO: Check for open-source implementation or move these to a shared library

/**
 * Set the calendar to open at the month and year of the given [date].
 */
internal fun CalendarConstraints.Builder.setOpenAt(date: LocalDate): CalendarConstraints.Builder =
    setOpenAt(date.inUtcMilliseconds())

/**
 * Set the earliest month the calendar will page to the month and year of the given [date].
 */
internal fun CalendarConstraints.Builder.setStart(date: LocalDate): CalendarConstraints.Builder =
    setStart(date.inUtcMilliseconds())

/**
 * Set the earliest month the calendar will page to the month and year of the given [date].
 */
internal fun CalendarConstraints.Builder.setEnd(date: LocalDate): CalendarConstraints.Builder =
    setEnd(date.inUtcMilliseconds())

/**
 * Sets the given [selection] in as the initially selected date.
 */
internal fun MaterialDatePicker.Builder<Long>.setSelection(
    selection: LocalDate
): MaterialDatePicker.Builder<Long> = setSelection(selection.inUtcMilliseconds())

/**
 * Adds a positive button click listener that calls [onDateSelectedListener] with the selected date.
 *
 * Warning: This listener cannot be removed via
 * [MaterialDatePicker.removeOnPositiveButtonClickListener].
 */
internal fun MaterialDatePicker<Long>.addOnDateSelectedListener(
    onDateSelectedListener: MaterialPickerOnPositiveButtonClickListener<LocalDate>
) = addOnPositiveButtonClickListener { dateInUtcMillis ->
    val date = Instant.ofEpochMilli(dateInUtcMillis)
        .atOffset(ZoneOffset.UTC)
        .toLocalDate()
    onDateSelectedListener.onPositiveButtonClick(date)
}

@JvmSynthetic
internal fun MaterialDatePicker<Long>.addOnDateSelectedListener(
    onDateSelectedListener: (LocalDate) -> Unit
) = addOnDateSelectedListener(MaterialPickerOnPositiveButtonClickListener<LocalDate> { selection ->
    onDateSelectedListener(selection)
})

internal fun LocalDate.inUtcMilliseconds() =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
