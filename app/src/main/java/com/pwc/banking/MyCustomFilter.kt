package com.pwc.banking

import com.backbase.android.retail.journey.payments.filters.PaymentPartyListFilter
import com.backbase.android.retail.journey.payments.model.PaymentParty
import com.backbase.android.retail.journey.payments.model.PaymentPartyType

/**
 * This class helps to filter the PaymentPartyTypes through MakeTransfer flow to display the accounts
 * Under From & To list. If required to filter more General accounts, the code has to be modified by adding like
 *  PaymentPartyType.GeneralAccount("Multiposition Account"),
 *  And it is required to register the MyCustomFilter() with Koin scope in MainApplication class under AdditionalDependencies
 */
class MyCustomFilter : PaymentPartyListFilter {

    private val allowedPartyTypes = listOf(
        PaymentPartyType.CreditCard,
        PaymentPartyType.SavingsAccount,
        PaymentPartyType.Loan,
        PaymentPartyType.CurrentAccount,
        PaymentPartyType.GeneralAccount("Multiposition Account"),
        PaymentPartyType.GeneralAccount("Multiposition Account 2"),
        PaymentPartyType.ExternalSavingsAccount,
        PaymentPartyType.ExternalCheckingAccount
    )

    override fun filterFromPaymentParty(
        fromPaymentParty: PaymentParty,
        toPaymentParty: PaymentParty?
    ): Boolean {
        return allowedPartyTypes.contains(fromPaymentParty.paymentPartyType)
    }
    override fun filterToPaymentParty(
        fromPaymentParty: PaymentParty?,
        toPaymentParty: PaymentParty
    ): Boolean {
        return allowedPartyTypes.contains(toPaymentParty.paymentPartyType)
    }
}