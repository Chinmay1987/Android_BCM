package com.pwc.banking

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.backbase.android.Backbase
import com.backbase.android.core.networking.auth.BBOAuth2InvalidAccessTokenResolver
import com.backbase.android.core.networking.error.BBChainErrorResponseResolver
import com.backbase.android.identity.client.BBIdentityAuthClient
import com.backbase.android.identity.resolver.BBIdentityChallengesResolver
import com.backbase.android.retail.journey.accounts_and_transactions.AccountsAndTransactionsConfiguration
import com.backbase.android.retail.journey.accounts_and_transactions.AccountsAndTransactionsJourneyScope
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.TransactionsConfiguration
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.quick_actions.QuickActionButtonNavigationAction
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.quick_actions.QuickActionButtons
import com.backbase.android.retail.journey.app.common.session.SessionEndedRouter
import com.backbase.android.retail.journey.app.us.UsAppActivity
import com.pwc.banking.sessionrouter.SessionEndedRounterImpl
import org.koin.core.context.loadKoinModules
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module

class MyActivity : UsAppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        loadKoinModules(module{
            single<SessionEndedRouter>(override = true) { SessionEndedRounterImpl(this@MyActivity, findNavController() ) }
        })


        //This helps to refresh the access token on session ended
        var authClient = Backbase.requireInstance().authClient as BBIdentityAuthClient
        val accessTokenResolver = BBOAuth2InvalidAccessTokenResolver(
            authClient,
            mutableMapOf(),
            null
        )
        val resolversChain: BBChainErrorResponseResolver = BBIdentityChallengesResolver.buildChain(
            this,
            authClient,
            accessTokenResolver
        )
        authClient.registerAuthErrorResolver(resolversChain)
    }

    override val declareAdditionalActivityDependencies: ModuleDeclaration = {

        //This below code helps to bring the Transfer, Pay options in ViewTransactions view without customizing the A&T configuration
        scope<AccountsAndTransactionsJourneyScope> {
            scoped<AccountsAndTransactionsConfiguration>(override = true) {
                AccountsAndTransactionsConfiguration {
                    transactions = TransactionsConfiguration {
                        quickActionButtons = QuickActionButtons {
                            cashAdvanceButtonAction =
                                this@scoped.getOrNull<QuickActionButtonNavigationAction?>(qualifier = QuickActionQualifiers.CASH_ADVANCE)
                            payButtonAction =
                                this@scoped.getOrNull<QuickActionButtonNavigationAction?>(qualifier = QuickActionQualifiers.PAY)
                           transferButtonAction =
                                this@scoped.getOrNull<QuickActionButtonNavigationAction?>(qualifier = QuickActionQualifiers.TRANSFER)
                        }
                    }
                }
            }
        }
    }

}