package com.pwc.banking



import com.backbase.android.retail.journey.app.us.UsAppActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pwc.banking.custom_journey_1.CustomRouter1
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import org.koin.dsl.ModuleDeclaration

class CustomActivity : UsAppActivity() {

    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)

    override val declareAdditionalActivityDependencies: ModuleDeclaration = {
        factory<CustomRouter1> {
            object : CustomRouter1 {
                override fun viewDateTime(date: LocalDate?, time: LocalTime?, zone: ZoneId?) {
                    val dateTime = ZonedDateTime.of(
                        date ?: LocalDate.now(),
                        time ?: LocalTime.now(),
                        zone ?: ZoneId.systemDefault(),
                    )
                    val formattedDateTime = formatter.format(dateTime)
                    MaterialAlertDialogBuilder(this@CustomActivity)
                        .setTitle("Date and time selected")
                        .setMessage(formattedDateTime)
                        .setPositiveButton(android.R.string.ok) { _, _ -> }
                        .show()
                }
            }
        }
    }
}
