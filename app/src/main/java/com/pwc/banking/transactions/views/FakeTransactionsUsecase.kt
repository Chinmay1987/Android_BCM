package com.pwc.banking.transactions.views

import com.backbase.android.retail.journey.accounts_and_transactions.Amount
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.CallState
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.State
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.TransactionsUseCase
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.models.BillingStatus
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.models.CreditDebitIndicatorItem
import com.backbase.android.retail.journey.accounts_and_transactions.transactions.models.TransactionItem
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class FakeTransactionsUsecase(
    private val completedTransactionCount: Int = 15,
    private val pendingTransactionCount: Int = 3
) : TransactionsUseCase {

    override suspend fun getTransactions(
        from: Int,
        size: Int,
        arrangementId: String,
        query: String,
        state: State?
    ): CallState<out List<TransactionItem>> {

        val fromOverallIndex = from * size
        val resultBillingStatus = when (state) {
            State.COMPLETED -> BillingStatus.COMPLETED
            else -> BillingStatus.PENDING
        }
        val overallCount = when (resultBillingStatus) {
            BillingStatus.COMPLETED -> completedTransactionCount
            BillingStatus.PENDING -> pendingTransactionCount
        }
        val lastIndex = overallCount - 1

        val actualSize = when {
            fromOverallIndex >= lastIndex -> return CallState.Empty
            fromOverallIndex + size >= lastIndex -> overallCount - fromOverallIndex
            else -> size
        }

        val fakeList = List(actualSize) { index ->
            val overallIndex = fromOverallIndex + index
            TransactionItem {
                id = "fake-id-$overallIndex"
                type = "Cash"
                billingStatus = resultBillingStatus
                transactionAmountCurrency = Amount {
                    value = BigDecimal("3.50")
                    currency = Currency.getInstance("USD")
                }
                merchantName = "Merchant $overallIndex"
                bookingDate = LocalDate.of(2020, 12, 1)
                creditDebitIndicator = CreditDebitIndicatorItem.CREDIT
            }
        }
        return CallState.Success(fakeList)
    }


}