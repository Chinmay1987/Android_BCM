package com.pwc.banking.custom_journey_1.another

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.time.LocalDate

internal class RangeDateValidator(
    private val start: LocalDate,
    private val end: LocalDate
) : CalendarConstraints.DateValidator {

    constructor(parcel: Parcel) : this(
        start = parcel.readSerializable() as LocalDate,
        end = parcel.readSerializable() as LocalDate
    )

    override fun isValid(date: Long) =
        date in start.inUtcMilliseconds()..end.endOfDayUtcMilliseconds()

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(start)
        dest.writeSerializable(end)
    }

    override fun describeContents() = 0

    private fun LocalDate.endOfDayUtcMilliseconds() = plusDays(1).inUtcMilliseconds() - 1

    companion object CREATOR : Parcelable.Creator<RangeDateValidator> {
        override fun createFromParcel(parcel: Parcel) = RangeDateValidator(parcel)
        override fun newArray(size: Int) = arrayOfNulls<RangeDateValidator>(size)
    }
}
