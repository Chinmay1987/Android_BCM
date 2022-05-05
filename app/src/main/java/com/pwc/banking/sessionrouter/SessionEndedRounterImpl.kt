package com.pwc.banking.sessionrouter

import android.content.Context
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.backbase.android.identity.journey.authentication.AuthenticationJourney
import com.backbase.android.retail.journey.app.common.session.SessionEndedRouter
import com.pwc.banking.R

class SessionEndedRounterImpl(private val context: Context,
                              private val navController: NavController
) : SessionEndedRouter {
    override fun onSessionEnded() {
        val currentDestinationId = navController.currentDestination?.id ?: 0
        if (currentDestinationId != 0 && currentDestinationId != R.id.authenticationJourney) {
            navController.navigate(
                R.id.action_global_authenticationJourney,
                bundleOf(
                    AuthenticationJourney.LAUNCH_ACTION_END_SESSION to true,

                            AuthenticationJourney.LAUNCH_ALERT_TITLE to
                            context.getText(R.string.universalApp_alerts_sessionExpired_title),
                    AuthenticationJourney.LAUNCH_ALERT_MESSAGE to
                            context.getText(R.string.universalApp_alerts_sessionExpired_message)
                )
            )
        }
    }

}