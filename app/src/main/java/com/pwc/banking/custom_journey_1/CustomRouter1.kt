package com.pwc.banking.custom_journey_1

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Exit points for [CustomJourney1].
 */
interface CustomRouter1 {

    /**
     * View the selected [date], [time], and [zone]. Each parameter is nullable because the user is
     * allowed to view a date time even if they don't select any or all of the parameters.
     */
    fun viewDateTime(date: LocalDate?, time: LocalTime?, zone: ZoneId?)
}
