package com.pwc.banking

import org.koin.core.qualifier.StringQualifier

/**
 * This class helps to bring the Transfer, Pay, Cash Advance options in ViewTransactions View through OOTB
 * When using this qualifier, A&T configuration must not be overrided in MainApplication
 */

internal object QuickActionQualifiers {
    val PAY = StringQualifier("pay")
    val CASH_ADVANCE = StringQualifier("cash_advance")
    val TRANSFER = StringQualifier("transfer")
}