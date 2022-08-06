package dev.ducketapp.service.app.database.migrations

import dev.ducketapp.service.domain.model.category.CategoriesTable
import dev.ducketapp.service.domain.model.currency.CurrenciesTable
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("unused", "ClassName")
class R__Insert_data : BaseJavaMigration() {

    override fun migrate(context: Context?) {
        transaction {
            CurrenciesTable.insertIgnore {
                it[area] = "Poland"
                it[name] = "Polish złoty"
                it[symbol] = "zł"
                it[isoCode] = "PLN"
            }
            CurrenciesTable.insertIgnore {
                it[area] = "Czech Republic"
                it[name] = "Czech koruna"
                it[symbol] = "Kč"
                it[isoCode] = "CZK"
            }
            CurrenciesTable.insertIgnore {
                it[area] = "European Union"
                it[name] = "Euro"
                it[symbol] = "€"
                it[isoCode] = "EUR"
            }
            CurrenciesTable.insertIgnore {
                it[area] = "United States"
                it[name] = "United States dollar"
                it[symbol] = "$"
                it[isoCode] = "USD"
            }
            CurrenciesTable.insertIgnore {
                it[area] = "Canada"
                it[name] = "Canadian dollar"
                it[symbol] = "$"
                it[isoCode] = "CAD"
            }
            CurrenciesTable.insertIgnore {
                it[area] = "Australia"
                it[name] = "Australian dollar"
                it[symbol] = "$"
                it[isoCode] = "AUD"
            }
            CurrenciesTable.insertIgnore {
                it[area] = "United Kingdom"
                it[name] = "UK pound sterling"
                it[symbol] = "£"
                it[isoCode] = "GBP"
            }

            val categoryGroupsToCategory = listOf(
                "Housing" to "Housing (other)",
                "Housing" to "Mortgage",
                "Housing" to "Rent payment",
                "Housing" to "Utilities",
                "Housing" to "Construction, repair",
                "Food, beverage" to "Food, beverage (other)",
                "Food, beverage" to "Cafe, Eating Out",
                "Food, beverage" to "Restaurants",
                "Food, beverage" to "Bars, alcohol",
                "Food, beverage" to "Delivery",
                "Food, beverage" to "Groceries",
                "Shopping" to "Shopping (other)",
                "Shopping" to "Gifts",
                "Shopping" to "Electronics, appliances",
                "Shopping" to "Books",
                "Shopping" to "Pharmacy, household",
                "Shopping" to "Clothes, shoes",
                "Shopping" to "Furniture, decor",
                "Shopping" to "Digital products",
                "Family" to "Family (other)",
                "Family" to "Childcare",
                "Family" to "Pets",
                "Leisure" to "Leisure (other)",
                "Leisure" to "Holidays & Vacations",
                "Leisure" to "Cinema, theater",
                "Leisure" to "Games",
                "Leisure" to "Hobby",
                "Personal care" to "Personal care (other)",
                "Personal care" to "Wellness & Beauty",
                "Personal care" to "Healthcare",
                "Personal care" to "Education",
                "Personal care" to "Sport activities",
                "Transport" to "Transport (other)",
                "Transport" to "Public transport",
                "Transport" to "Intercity transport",
                "Transport" to "Personal transport",
                "Transport" to "Taxi",
                "Transport" to "Ride-sharing",
                "Financial costs" to "Financial costs (other)",
                "Financial costs" to "Charges & Fees",
                "Financial costs" to "Insurance",
                "Financial costs" to "Charity",
                "Financial costs" to "Loans, debts",
                "Financial costs" to "Taxes",
                "Financial costs" to "Subscriptions",
                "Financial costs" to "Mobile phone",
                "Financial costs" to "Internet, media",
                "Financial costs" to "Parking lot",
                "Financial costs" to "Services",
                "Investments" to "Investments (other)",
                "Investments" to "Crypto",
                "Investments" to "Stocks, shares",
                "Investments" to "Savings",
                "Income" to "Income (other)",
                "Income" to "Bonus",
                "Income" to "Salary",
                "Income" to "Reimbursement, refund",
                "Other" to "Other",
                "Other" to "Withdrawal, transfer",
            )

            CategoriesTable.batchInsert(data = categoryGroupsToCategory, ignore = true) { data ->
                this[CategoriesTable.group] = data.first
                this[CategoriesTable.name] = data.second
            }
        }
    }
}