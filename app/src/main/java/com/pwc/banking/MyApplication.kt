// TODO: Move to package in line with bank-specific application ID
package com.pwc.banking

import android.app.Application
import android.os.Build
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.backbase.android.Backbase
import com.backbase.android.client.accesscontrolclient2.api.UserContextApi
import com.backbase.android.client.accesscontrolclient2.api.UsersApi
import com.backbase.android.client.accountstatementsclient2.api.AccountStatementApi
import com.backbase.android.client.actionclient2.api.ActionRecipesApi
import com.backbase.android.client.arrangementclient2.api.ArrangementsApi
import com.backbase.android.client.arrangementclient2.api.ProductSummaryApi
import com.backbase.android.client.cardsclient2.api.CardsApi
import com.backbase.android.client.contactmanagerclient2.api.ContactsApi
import com.backbase.android.client.messageclient4.api.MessagecenterApi
import com.backbase.android.client.notificationclient2.api.NotificationsApi
import com.backbase.android.client.paymentordera2aclient1.api.A2aClientApi
import com.backbase.android.client.paymentorderclient2.api.PaymentOrdersApi
import com.backbase.android.client.pocketsclient2.api.PocketTailorClientApi
import com.backbase.android.client.products.ProductsClient
import com.backbase.android.client.remotedepositcapturerclient2.api.RemoteDepositCapturerClientApi
import com.backbase.android.client.transactionclient2.api.TransactionClientApi
import com.backbase.android.client.usermanagerclient2.api.UserProfileManagementApi
import com.backbase.android.clients.common.*
import com.backbase.android.core.errorhandling.ErrorCodes
import com.backbase.android.core.security.environment.EnvironmentVerification
import com.backbase.android.core.utils.BBLogger
import com.backbase.android.dbs.DBSDataProvider
import com.backbase.android.identity.client.BBIdentityAuthClient
import com.backbase.android.identity.device.BBDeviceAuthenticator
import com.backbase.android.identity.journey.authentication.AuthenticationConfiguration
import com.backbase.android.identity.journey.authentication.AuthenticationJourneyScope
import com.backbase.android.identity.journey.authentication.identity_auth_client_1.IdentityAuthClient1AuthenticationUseCase
import com.backbase.android.identity.journey.authentication.login.LoginScreenConfiguration
import com.backbase.android.listeners.SecurityViolationListener
import com.backbase.android.retail.feature_filter.UserEntitlementFeatureFilterUseCase
import com.backbase.android.retail.feature_filter.entitlements.accesscontrol_client_2.AccessControlClient2EntitlementsUseCase
import com.backbase.android.retail.journey.accounts_and_transactions.AccountsAndTransactionsConfiguration
import com.backbase.android.retail.journey.accounts_and_transactions.AccountsAndTransactionsJourney
import com.backbase.android.retail.journey.accounts_and_transactions.AccountsAndTransactionsJourneyScope
import com.backbase.android.retail.journey.accounts_and_transactions.banner.AccountsBannerResponse
import com.backbase.android.retail.journey.accounts_and_transactions.banner.AccountsBannerUseCase
import com.backbase.android.retail.journey.accounts_and_transactions.gen_arrangements_client_2.ArrangementsUseCaseImpl
import com.backbase.android.retail.journey.accounts_and_transactions.gen_arrangements_client_2.ProductSummaryUseCaseImpl
import com.backbase.android.retail.journey.accounts_and_transactions.gen_transaction_client_2.TransactionsUseCaseImpl
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.TransactionsConfiguration
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.TransactionsUseCase
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.quick_actions.QuickActionButtonNavigationAction
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.quick_actions.QuickActionButtons
import com.backbase.android.retail.journey.accountstatements.AccountStatementsJourneyScope
import com.backbase.android.retail.journey.accountstatements.gen_accountstatements_client_2.AccountStatementsUseCaseImpl
import com.backbase.android.retail.journey.app.common.BackbaseSdkConfiguration
import com.backbase.android.retail.journey.app.common.COMMON_API_ROOT_QUALIFIER
import com.backbase.android.retail.journey.app.common.NetworkingConfiguration
import com.backbase.android.retail.journey.app.common.menu.BottomMenuConfiguration
import com.backbase.android.retail.journey.app.common.menu.BottomMenuItem
import com.backbase.android.retail.journey.app.common.splash.SplashScreenConfiguration
import com.backbase.android.retail.journey.app.us.*
import com.backbase.android.retail.journey.app.us.more.DefaultUsMoreConfiguration
import com.backbase.android.retail.journey.cardsmanagement.CardsManagementJourneyScope
import com.backbase.android.retail.journey.cardsmanagement.gen_cards_client_2_use_case.CardsUseCaseImpl
import com.backbase.android.retail.journey.cardsmanagement.gen_cards_client_2_use_case.ChangePinUseCaseImpl
import com.backbase.android.retail.journey.cardsmanagement.gen_usermanager_client_2_user_use_case.UserUseCaseImpl
import com.backbase.android.retail.journey.contacts.ContactsJourneyScope
import com.backbase.android.retail.journey.contacts.contactmanager_client_2.GenContactManagerClient2ContactsUseCase
import com.backbase.android.retail.journey.gen_accesscontrol_client_2_usercontext_use_case.UserContextSelectorAccessControlUseCaseImpl
import com.backbase.android.retail.journey.gen_arrangements_client_2_arrangements_use_case.PocketsArrangementsUseCaseImpl
import com.backbase.android.retail.journey.gen_paymentorder_client_2_payment_service_use_case.PocketsPaymentUseCaseImpl
import com.backbase.android.retail.journey.gen_pockets_client_2_use_case.PocketsUseCaseImpl
import com.backbase.android.retail.journey.payments.PaymentJourneyScope
import com.backbase.android.retail.journey.payments.PaymentJourneyType
import com.backbase.android.retail.journey.payments.filters.PaymentPartyListFilter
import com.backbase.android.retail.journey.payments.gen_arrangements_client_2.ArrangementsClient2PaymentAccountsUseCase
import com.backbase.android.retail.journey.payments.gen_contactmanager_client_2.ContactManagerClient2PaymentContactsUseCase
import com.backbase.android.retail.journey.payments.gen_paymentorder_client_2.PaymentOrderClient2PaymentServiceUseCase
import com.backbase.android.retail.journey.payments.gen_paymentordera2a_client_1.PaymentOrderA2AClient1ServiceUseCase
import com.backbase.android.retail.journey.payments.upcoming.UpcomingPaymentsJourneyScope
import com.backbase.android.retail.journey.payments.upcoming.gen_paymentorder_client_2.PaymentOrderClient2UpcomingPaymentsServiceUseCase
import com.backbase.android.retail.journey.pockets.PocketsJourneyScope
import com.backbase.android.retail.journey.rdc.RdcJourneyScope
import com.backbase.android.retail.journey.rdc.gen_remotedepositcapturer_client_2.Rdc2ServiceUseCase
import com.backbase.android.retail.journey.rdc.gen_remotedepositcapturer_client_2.model.DeviceInfo
import com.backbase.android.retail.journey.user_context_selector.UserContextSelectorJourneyScope
import com.backbase.android.utils.net.response.Response
import com.backbase.deferredresources.DeferredDrawable
import com.backbase.deferredresources.DeferredText
import com.backbase.engagementchannels.messages.MessagesJourneyScope
import com.backbase.engagementchannels.messages.gen_message_client_4.GenMessageClient4MessagesUseCase
import com.backbase.engagementchannels.notifications.NotificationsConfiguration
import com.backbase.engagementchannels.notifications.NotificationsJourneyScope
import com.backbase.engagementchannels.notifications.dto.AccountType
import com.backbase.engagementchannels.notifications.gen_action_client_2_gen_arrangement_client_2.GenActionClient2GenArrangementClient2NotificationSettingsUseCase
import com.backbase.engagementchannels.notifications.gen_notification_client_2.GenNotificationClient2NotificationsUseCase
import com.google.firebase.messaging.FirebaseMessaging
import com.pwc.banking.transactions.customapiclient.FetchScheduleTransfers
import com.pwc.banking.transactions.customapiclient.RoundUpClientApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module
import java.net.URI
import java.time.Duration
import java.time.OffsetDateTime

/**
 * The application class used by this app to define its various Journey configurations and other setup items.
 */
@Suppress("unused") // Used in AndroidManifest
class MyApplication : UsApplication() {

    override fun onCreate() {
        super.onCreate()

        Log.d("FirebaseToken ", FirebaseMessaging.getInstance().getToken().toString())
        if (!BuildConfig.DEBUG) {
            val environmentVerification = EnvironmentVerification.getInstance()

            val isEmulator = environmentVerification.getEmulatorDetector(this).isEmulator
            if (isEmulator) crashApp()

            val isRooted = environmentVerification.getRootVerification(this)
                .withRootManagementApps()
                .withPotentiallyDangerousApps()
                .withSuBinary()
                .isRooted
            if (isRooted) crashApp()

            environmentVerification.activityHijackDetector.start(this)
        }
    }

    override fun createSplashScreenConfiguration() = SplashScreenConfiguration {
        minimumDuration = if (BuildConfig.DEBUG)
            Duration.ZERO
        else
            Duration.ofSeconds(2)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun createApplicationConfiguration() = UsApplicationConfiguration {
        backbaseSdkConfiguration = BackbaseSdkConfiguration {
            logLevel = if (BuildConfig.DEBUG)
                BBLogger.LogLevel.DEBUG
            else
                BBLogger.LogLevel.WARN
        }
        networkingConfiguration = NetworkingConfiguration {
            authClientDefinition = {
                BBIdentityAuthClient(get<Application>(), "").apply {
                    addAuthenticator(BBDeviceAuthenticator())
                    //addAuthenticator(BBBiometricPromptAuthenticator<DefaultBBBiometricPromptAuthenticatorView>(DefaultBBBiometricPromptAuthenticatorView::class.java))
                }
            }
        }
        // TODO: Remove feature flag when user context selector journey is removed from apps.
        applicationFeatureFlags = listOf(
            UsApplicationFeatureFlag.WorkspacesJourneyFeatureFlag
        )
        journeyConfigurations = UsJourneyConfigurations {
            moreConfigurationDefinition = {
                DefaultUsMoreConfiguration(
                    userRepository = getOrNull(),
                    localeSelectorConfiguration = getOrNull()
                )
            }


             bottomMenuConfigurationDefinition = {
                BottomMenuConfiguration {
                    items = listOf(

                        //This below code helps to set navigated to a custom screen and then attached with OOTB features
//                        DefaultUsAccountsBottomMenuItem(){
//                              startDestinationArgs = bundleOf(
//                                  AccountsAndTransactionsJourney.NAVIGATION_GRAPH to R.navigation.custom_accounts_and_transactions_journey
//                              )
//                        },

                        /**
                         * below the commented code to access the default Accounts menu item. Since the custom landing screen is
                         * tried above, the below line is commented
                         */

                        /**
                         * below the commented code to access the default Accounts menu item. Since the custom landing screen is
                         * tried above, the below line is commented
                         */


                        //This code helps to add a custom journey to the existing bottom menu
                      DefaultUsAccountsBottomMenuItem{
                            startDestinationArgs = bundleOf(
                                  AccountsAndTransactionsJourney.NAVIGATION_GRAPH to R.navigation.custom_accounts_and_transactions_journey
                              )
                        },
                        /*BottomMenuItem {
                            title = DeferredText.Resource(R.string.customjourneyname)
                            icon = DeferredDrawable.Resource(R.drawable.ic_baseline_add_alert_24)
                            navigationResId = R.navigation.custom_1
                        },*/
                        //DefaultUsAccountsBottomMenuItem(),
                       DefaultUsPocketsBottomMenuItem(),
                        DefaultUsMoveMoneyBottomMenuItem(),
                        DefaultUsCardsBottomMenuItem(),
                        DefaultUsMoreBottomMenuItem()
                        )

                }


            }

//            accountsAndTransactionsConfigurationDefinition = {
//                AccountsAndTransactionsConfiguration {
//                    this.setAccountsScreen(AccountsScreenConfiguration {
//                        this.displayedAccounts = listOf(AccountType.CurrentAccount, AccountType.SavingsAccount, AccountType.Loan, AccountType.GeneralAccount, AccountType.CreditCard)
//                        //Below code helps to set the Account summany screen title change and Account type name changes
//                        this.screenTitle = DeferredText.Resource(R.string.account_transactions_title)
//                        this.savingsAccountTitle = DeferredText.Resource(R.string.customized_savings_account_title)
//                        this.generalAccountTitle = DeferredText.Resource(R.string.custom_general_account_title)
//                        //this.accountDetailsScreenTitle = DeferredText.Resource(R.string.account_details_title)
//                        //this.accountDetailsScreenBackNavigationIcon = DeferredDrawable.Resource(R.drawable.ic_extension_24)
//
//                        //Below code helps to modify the values for each type of account through provider
//                        savingsAccountRowItemProvider = SavingsAccountRowItemProvider { dto, _ ->
//                            AccountRowItem {
//                                accountId = dto.id
//                                title = dto.accountHolderNames
//                                subtitle = dto.BIC
//                                rightAccessoryText = dto.bookedBalance
//                                rightAccessorySubtitle = DeferredText.Constant(dto.currency.toString())
//                                icon = DeferredDrawable.Resource(R.drawable.ic_extension_24)
//
//                            }
//                        }
//
//                    })
//
////                    this.setTransactions(TransactionsConfiguration {
////                        this.setTransactionDetailsScreen(TransactionDetailsScreenConfiguration{
////                            this.typeTitle = DeferredText.Resource(R.string.custom_transaction_detail_typetile)
////                            this.descriptionTitle = DeferredText.Resource(R.string.custom_transaction_detail_Descriptiontile)
////                            this.instructedAmountTitle = DeferredText.Resource(R.string.custom_instructed_amount_title)
////                            this.runningBalanceTitle = DeferredText.Resource(R.string.custom_running_balance_title)
////
////                            this.sectionsProvider = TransactionDetailsSectionsProvider{dto,transactionconfiguration ->
////                                listOf(
////                                        TransactionDetailsSection{
////                                            this.title = dto.counterPartyAccountNumber
////                                        }
////
////                                )
////                            }
////
////                        })
////                    })
//
//
//                }
//            }


            authenticationConfigurationDefinition = {
                AuthenticationConfiguration{
                  //this.background = DeferredDrawable.Resource(R.color.design_dark_default_color_primary_variant)
                    this.setLoginScreenConfiguration(LoginScreenConfiguration{
                        this.titleText =  DeferredText.Resource(R.string.login_screen_title)
                    })
                    val instance = Backbase.requireInstance()
                    instance.getResolverByCode(-1003)?.let { it1 -> instance.registerErrorResponseResolver(-1003, it1) }
                }
            }

            notificationsConfigurationDefinition = {
                NotificationsConfiguration {
                    this.displayedAccounts = listOf(
                        AccountType.CURRENT_ACCOUNT,
                        AccountType.SAVINGS_ACCOUNT,
                        AccountType.LOAN,
                        AccountType.GENERAL_ACCOUNT)


                }
            }




           /* upcomingPaymentsAppConfigurationDefinition = {
                UpcomingPaymentsAppConfiguration{
                    title = DeferredText.Resource(R.string.alternative_title)
                    upcomingPaymentDetailScreen = UpcomingPaymentDetailScreenConfiguration {
                        description = DeferredText.Resource(R.string.details_alternative_description)
                        fromAccount = DeferredText.Resource(R.string.details_from_account)
                        recipientAccountNr = DeferredText.Resource(R.string.details_recipient)
                        schedule = DeferredText.Resource(R.string.details_schedule)
                        //headerScheduledFor = DeferredFormattedString.Resource(R.string.details_schedledfor)
                        //headerTo = DeferredFormattedString.Resource(R.string.details_header_to)

                        amountConfiguration = UpcomingPaymentsDetailsAmountConfiguration {
                            amountEnableIsoFormat = DeferredBoolean.Constant(false)
                            amountEnablePositiveSign = DeferredBoolean.Constant(true)
                        }

                        deletePaymentErrorMessage = DeferredText.Resource(R.string.delete_error_message_custom)
                    }
                    upcomingPaymentScreen = UpcomingPaymentScreenConfiguration {
                        noInternetTitle = DeferredText.Resource(R.string.no_internet_title)
                        amountConfiguration = UpcomingPaymentsAmountConfiguration {
                            amountEnableIsoFormat = DeferredBoolean.Constant(false)

                        }
                    }

                }
            }*/


//            setPaymentJourneyConfigurationDefinition {
//                PaymentJourneyConfiguration(java.util.Currency.getInstance("USD")){
//                    FromPaymentPartySelector {
//                        this.setFromPartySelector(FromPartySelection{
//                            this.filterSelectedAccount = false
//                        })
//                    }
//                }
//            }



        }
    }

    override fun createUseCaseDefinitions() = UsUseCaseDefinitions {
        accountStatementsUseCaseDefinition = { AccountStatementsUseCaseImpl(get()) }
        accountsUseCaseDefinition =
            { ProductSummaryUseCaseImpl(productSummaryClient = get<com.backbase.android.client.gen2.arrangementclient2.api.ProductSummaryApi>()) }
        accountsBannerUseCaseDefinition = {
            object : AccountsBannerUseCase {
                override suspend fun getBanner(): AccountsBannerResponse = AccountsBannerResponse {
                    result = AccountsBannerUseCase.AccountsBannerResult.FAILURE
                }
            }
        }
        arrangementsUseCaseDefinition = { ArrangementsUseCaseImpl(get<ArrangementsApi>()) }
        authenticationUseCaseDefinition = { IdentityAuthClient1AuthenticationUseCase(get(), get()) }
        messagesUseCaseDefinition = { GenMessageClient4MessagesUseCase(get()) }
        notificationSettingsUseCaseDefinition = {
            GenActionClient2GenArrangementClient2NotificationSettingsUseCase(get(), get())
        }
        notificationsUseCaseDefinition = { GenNotificationClient2NotificationsUseCase(get()) }
        paymentAccountsUseCaseDefinition =
            { ArrangementsClient2PaymentAccountsUseCase(get<com.backbase.android.client.gen2.arrangementclient2.api.ProductSummaryApi>()) }
        paymentContactsUseCaseDefinition = { ContactManagerClient2PaymentContactsUseCase(get()) }
        paymentUseCaseDefinition = { PaymentOrderClient2PaymentServiceUseCase(get()) }
        transactionsUseCaseDefinition = { TransactionsUseCaseImpl(get()) }
        upcomingPaymentsUseCaseDefinition = { PaymentOrderClient2UpcomingPaymentsServiceUseCase(get()) }
        cardsUseCaseDefinition = { CardsUseCaseImpl(get()) }
        cardsUserManagerUseCaseDefinition = { UserUseCaseImpl(get()) }
        contactsUseCaseDefinition = { GenContactManagerClient2ContactsUseCase(get()) }
        changePinUseCaseDefinition = { ChangePinUseCaseImpl(get()) }
        pocketsUseCaseDefinition = { PocketsUseCaseImpl(get()) }
        pocketsArrangementsUseCaseDefinition = { PocketsArrangementsUseCaseImpl(get()) }
        pocketsPaymentsUseCaseDefinition = { PocketsPaymentUseCaseImpl(get()) }
        userContextSelectorUseCaseDefinition = { UserContextSelectorAccessControlUseCaseImpl(get()) }
        entitlementsUseCaseDefinition = {
            AccessControlClient2EntitlementsUseCase(
                usersApi = get()
            )
        }
        featureFilterUserEntitleUseCaseDefinition = {
            UserEntitlementFeatureFilterUseCase(
                entitlementsUseCase = get()
            )
        }

        additionalUsUseCases()
    }

    override val additionalApplicationDependencies = module {

        //region Moshi
        single {
            Moshi.Builder()
                .add(bigDecimalAdapter)
                .add(base64Adapter)
                .add(dateAdapter)
                .add(
                    /**
                     * Some dates come in a format that is not an ISO standard from the backend. That format describes
                     * timezones as [+-]HHmm$.
                     *
                     * This workaround converts the string's timezone to [+-]HH:mm$. When the backend fixes this issue,
                     * the current workaround will not break the app, though it's advisable to remove it.
                     *
                     * The bug this code works around is known to be present only in the following spec: message-client-api-v4.0.7.yaml
                     */
                    object {

                        @FromJson
                        fun fromJson(string: String) =
                            OffsetDateTime.parse(string.replace(Regex("(?<=[+-])(\\d{2})(\\d{2})$"), "$1:$2"))

                        @ToJson
                        fun toJson(value: OffsetDateTime) = value.toString()
                    }
                )
                .build()
        }
        factory { MoshiResponseBodyParser(get()) } bind ResponseBodyParser::class
        //endregion

        single{ProductsClient(get<DBSDataProvider>(), get(COMMON_API_ROOT_QUALIFIER))}



        //region Shared clients
        factory {
            // TODO MANDATORY: Set assets/backbase/config.json serverURL and Identity attributes to ensure
            //  API root is a valid value
            ProductSummaryApi(
                context = get<Application>(),
                moshi = get(),
                parser = get(),
                serverUri = URI.create("${apiRoot()}/arrangement-manager"),
                provider = get(),
                backbase = get()
            )
        }
        factory {
            com.backbase.android.client.gen2.arrangementclient2.api.ProductSummaryApi(
                context = get<Application>(),
                moshi = get(),
                parser = get(),
                serverUri = URI.create("${apiRoot()}/arrangement-manager"),
                provider = get(),
                backbase = get()
            )
        }
        factory {
            PaymentOrdersApi(
                context = get<Application>(),
                moshi = get(),
                parser = get(),
                serverUri = URI.create("${apiRoot()}/payment-order-service"),
                provider = get(),
                backbase = get()
            )
        }
        //endregion





        scope<AccountsAndTransactionsJourneyScope> {
            scoped { getKoin().get<ProductSummaryApi>() }

            scoped {
                val serverUri = URI("${apiRoot()}/transaction-manager")
                TransactionClientApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = serverUri, provider = get(),
                    backbase = get()
                )
            }

            scoped {
                val serverUri = URI("${apiRoot()}/arrangement-manager")
                ArrangementsApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    backbase = get(), provider = get(),
                    serverUri = serverUri,
                )
            }


        }

        single {
            val serverUri = URI("${apiRoot()}/arrangement-manager")
            com.backbase.android.client.gen2.arrangementclient2.api.BalancesApi(
                context = get<Application>(),
                moshi = get(),
                parser = get(),
                serverUri = serverUri,
                provider = get(),
                backbase = get()
            )
        }
        single<TransactionsUseCase> { TransactionsUseCaseImpl(get()) }
        factory {
            val serverUri = URI("${apiRoot()}/transaction-manager-outbound")
            TransactionClientApi(
                context = get<Application>(),
                moshi = get(), parser = get(),
                serverUri = serverUri, provider = get(),
                backbase = get()
            )
        }

        //Custom Post call Implementation for Roundup
        single {
           //val serverUri = URI("http://api-csp.eastus.cloudapp.azure.com/")
            val serverUri = URI("${apiRoot()}")
            RoundUpClientApi(
                context = get<Application>(),
                moshi = get(),
                parser = get(),
                serverUri = serverUri,
                provider = get(),
                backbase = get()
            )
        }

        // Custom FetchTransfers by passing the arrangment number : Refer(FetchScheduleTransfers)
        single {
            //val serverUri = URI("http://api-csp.eastus.cloudapp.azure.com/")
            val serverUri = URI("${apiRoot()}")
            FetchScheduleTransfers(
                context = get<Application>(),
                moshi = get(),
                parser = get(),
                serverUri = serverUri,
                provider = get(),
                backbase = get()
            )
        }

        scope<AccountStatementsJourneyScope> {
            scoped {
                val serverUri = URI("${apiRoot()}/account-statement")
                AccountStatementApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = serverUri, provider = get(),
                    backbase = get()
                )
            }
        }

        scope<MessagesJourneyScope> {
            scoped {
                MessagecenterApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = URI.create("${apiRoot()}/messages-service"),
                    provider = get(),
                    backbase = get(),
                )
            }
        }

        scope<NotificationsJourneyScope> {
            scoped {
                ActionRecipesApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = URI.create("${apiRoot()}/action"),
                    provider = get(),
                    backbase = get(),
                )
            }
            scoped {
                NotificationsApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = URI.create("${apiRoot()}/notifications-service"),
                    provider = get(),
                    backbase = get(),
                )
            }
            scoped { getKoin().get<ProductSummaryApi>() }
        }

        scope<PaymentJourneyScope> {
            scoped<PaymentPartyListFilter>(override = true, qualifier = PaymentJourneyType.A2A) {
                MyCustomFilter()
            }
            scoped { getKoin().get<PaymentOrdersApi>() }

            scoped {
                A2aClientApi(
                    context = get<Application>(),
                    moshi = get(),
                    parser = get(),
                    serverUri = URI.create("${apiRoot()}/payment-order-a2a"),
                    provider = get(),
                    backbase = get()
                )
            }

            scoped { getKoin().get<ProductSummaryApi>() }
            scoped {
                ContactsApi(
                    context = get<Application>(),
                    moshi = get(),
                    parser = get(),
                    serverUri = URI.create("${get<String>(COMMON_API_ROOT_QUALIFIER)}/contact-manager"),
                    provider = get(),
                    backbase = get(),
                )
            }


        }

        scope<UpcomingPaymentsJourneyScope> {
            scoped { getKoin().get<PaymentOrdersApi>() }
        }

        scope<CardsManagementJourneyScope> {
            scoped {
                CardsApi(
                    context = get<Application>(),
                    moshi = get(),
                    parser = get(),
                    serverUri = URI("${apiRoot()}/cards-presentation-service"),
                    provider = get(),
                    backbase = get()
                )
            }
            scoped {
                UserProfileManagementApi(
                    context = get<Application>(),
                    moshi = get(),
                    parser = get(),
                    serverUri = URI("${apiRoot()}/user-manager"),
                    provider = get(),
                    backbase = get(),
                )
            }
        }

        scope<ContactsJourneyScope> {
            scoped {
                ContactsApi(
                    context = get<Application>(),
                    moshi = get(),
                    parser = get(),
                    serverUri = URI.create("${apiRoot()}/contact-manager"),
                    provider = get(),
                    backbase = get(),
                )
            }
        }

        scope<PocketsJourneyScope> {
            scoped {
                val uri = URI("${apiRoot()}/pocket-tailor")
                PocketTailorClientApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = uri,
                    backbase = get()
                )
            }
            scoped {
                val uri = URI("${apiRoot()}/payment-order-service")
                PaymentOrdersApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = uri,
                    backbase = get()
                )
            }
            scoped {
                val uri = URI("${apiRoot()}/arrangement-manager")
                ProductSummaryApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = uri,
                    backbase = get()
                )
            }
        }

        scope<UserContextSelectorJourneyScope> {
            scoped {
                getKoin().get<UserContextApi>()
            }
        }

        single {
            val uri = URI("${apiRoot()}/access-control")
            UserContextApi(
                context = get<Application>(),
                moshi = get(),
                parser = get(),
                serverUri = uri,
                provider = get(),
                backbase = get()

            )
        }

        //region Entitlements
        single {
            val serverUri = URI("${apiRoot()}/access-control")
            UsersApi(
                context = get<Application>(),
                moshi = get(),
                parser = get(),
                serverUri = serverUri,
                provider = get(),
                backbase = get()
            )
        }
        //endregion

        //additionalUsDependencies()
        scope<AuthenticationJourneyScope> {
            AuthenticationConfiguration {

            }
        }
        additionalUsDependencies()
    }



    override suspend fun onInitialized() {
        if (!BuildConfig.DEBUG) {
            Backbase.requireInstance().setSecurityViolationListener(SecurityViolationHandler())
            EnvironmentVerification.getInstance().debuggerDetector.enableAntiNativeDebug()
        }
    }

    private fun Module.additionalUsDependencies() {

        scope<RdcJourneyScope> {
            scoped {
                val serverUri = URI("${apiRoot()}/remote-deposit-capturer")
                RemoteDepositCapturerClientApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = serverUri, provider = get(),
                    backbase = get()
                )
            }
            scoped {
                val serverUri = URI("${apiRoot()}/arrangement-manager")
                ProductSummaryApi(
                    context = get<Application>(),
                    moshi = get(), parser = get(),
                    serverUri = serverUri, provider = get(),
                    backbase = get()
                )
            }
        }
    }

    private fun UsUseCaseDefinitions.Builder.additionalUsUseCases() {
        externalPaymentAccountsServiceUseCaseDefinition = {
            PaymentOrderA2AClient1ServiceUseCase(
                a2aClientApi = get()
            )
        }

        rdcServiceUseCaseDefinition = {
            Rdc2ServiceUseCase(
                deviceInfo = DeviceInfo {
                    appVersion = BuildConfig.VERSION_NAME
                },
                rdcClientApi = get(),
                productSummaryApi = get()
            )
        }
    }

    private fun crashApp(code: Int = 1): Nothing = throw RuntimeException("$code")

    private fun Scope.apiRoot(): String = get(COMMON_API_ROOT_QUALIFIER)

    private inner class SecurityViolationHandler : SecurityViolationListener {

        override fun onSecurityViolation(violation: Response) {
            when (val responseCode = violation.responseCode) {
                ErrorCodes.SECURITY_BREACH_ACTIVITY_HIJACKING.code -> crashApp(responseCode)
            }
        }
    }

}
