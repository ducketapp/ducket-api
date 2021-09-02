package io.budgery.api.domain.service

import io.budgery.api.domain.controller.budget.BudgetDto
import io.budgery.api.domain.controller.budget.BudgetProgressDto
import io.budgery.api.domain.controller.record.TransactionDto
import io.budgery.api.domain.repository.BudgetRepository
import io.budgery.api.domain.repository.TransactionRepository
import io.budgery.api.extension.sumByDecimal
import java.math.BigDecimal

class BudgetService(private val budgetRepository: BudgetRepository, private val transactionRepository: TransactionRepository) {

    fun getBudgets(userId: Int): List<BudgetDto> {
        return budgetRepository.findAll(userId).map { bg ->
            val transactions = transactionRepository.findAllByCategories(userId, bg.categories.map { it.id })
                .map { TransactionDto(it) }
//                .filter { tr ->
//
////                    return@filter trDateTime.isAfter(bg.startDate) && tr.date.isBefore(bg.endDate)
//                }

            val totalAmount: BigDecimal = transactions.sumByDecimal { it.amount }
            val absTotalAmount: BigDecimal = totalAmount.abs()
            val budgetPercentage: Double = if (totalAmount < BigDecimal.ZERO) (absTotalAmount * BigDecimal(100) / bg.amount).toDouble() else 0.0
            val roundedBudgetPercentage: Double = String.format("%.2", budgetPercentage).toDouble()

            BudgetDto(bg, BudgetProgressDto(roundedBudgetPercentage, transactions.size, absTotalAmount))
        }
    }
}