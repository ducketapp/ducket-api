package io.ducket.api.app.database.migration

import domain.model.category.CategoriesTable
import domain.model.currency.CurrenciesTable
import io.ducket.api.app.CategoryGroup
import io.ducket.api.config.AppConfig
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.java.KoinJavaComponent

@Suppress("unused", "ClassName")
class R__Insert_data : BaseJavaMigration() {

    override fun migrate(context: Context?) {
        transaction {
            CurrenciesTable.insertIgnore {
                it[this.area] = "Poland"
                it[this.name] = "Polish złoty"
                it[this.symbol] = "zł"
                it[this.isoCode] = "PLN"
            }
            CurrenciesTable.insertIgnore {
                it[this.area] = "EU"
                it[this.name] = "Euro"
                it[this.symbol] = "€"
                it[this.isoCode] = "EUR"
            }
            CurrenciesTable.insertIgnore {
                it[this.area] = "United States"
                it[this.name] = "United States dollar"
                it[this.symbol] = "$"
                it[this.isoCode] = "USD"
            }

            val categoryGroupsToCategory = listOf(
                CategoryGroup.HOUSING to "HOUSING",
                CategoryGroup.HOUSING to "BUILDING_AND_REPAIR",
                CategoryGroup.HOUSING to "MORTGAGE",
                CategoryGroup.HOUSING to "RENT_PAYMENT",
                CategoryGroup.HOUSING to "HOME_SECURITY",
                CategoryGroup.HOUSING to "UTILITIES",
                CategoryGroup.HOUSING to "HOME_MEDIA",
                CategoryGroup.FOOD_AND_DRINKS to "FOOD_AND_DRINKS",
                CategoryGroup.FOOD_AND_DRINKS to "CAFE",
                CategoryGroup.FOOD_AND_DRINKS to "RESTAURANTS_OR_BARS",
                CategoryGroup.FOOD_AND_DRINKS to "FOOD_DELIVERY_OR_TAKEOUT",
                CategoryGroup.FOOD_AND_DRINKS to "FASTFOOD",
                CategoryGroup.FOOD_AND_DRINKS to "GROCERY",
                CategoryGroup.FOOD_AND_DRINKS to "SPORT_NUTRITION",
                CategoryGroup.SHOPPING to "SHOPPING",
                CategoryGroup.SHOPPING to "GIFTS",
                CategoryGroup.SHOPPING to "HOME_SUPPLIES",
                CategoryGroup.SHOPPING to "ELECTRONICS_OR_APPLIANCES",
                CategoryGroup.SHOPPING to "BOOKS_OR_STATIONERY",
                CategoryGroup.SHOPPING to "PHARMACY",
                CategoryGroup.SHOPPING to "CLOTHES_OR_SHOES",
                CategoryGroup.SHOPPING to "ACCESSORIES_AND_EQUIPMENT",
                CategoryGroup.SHOPPING to "FURNITURE_AND_DECOR",
                CategoryGroup.SHOPPING to "DIGITAL_PRODUCTS",
                CategoryGroup.SHOPPING to "SERVICES",
                CategoryGroup.FAMILY to "FAMILY",
                CategoryGroup.FAMILY to "CHILD_SUPPLIES",
                CategoryGroup.FAMILY to "CHILD_HEALTHCARE",
                CategoryGroup.FAMILY to "CHILD_EDUCATION",
                CategoryGroup.FAMILY to "DAYCARE",
                CategoryGroup.FAMILY to "BABYSITTER",
                CategoryGroup.FAMILY to "PETS",
                CategoryGroup.LEISURE to "LEISURE",
                CategoryGroup.LEISURE to "HOBBY",
                CategoryGroup.LEISURE to "HOLIDAYS_AND_VACATIONS",
                CategoryGroup.LEISURE to "PUBLIC_EVENTS",
                CategoryGroup.LEISURE to "CINEMA_AND_THEATER",
                CategoryGroup.LEISURE to "GAMES",
                CategoryGroup.PERSONAL_CARE to "PERSONAL_CARE",
                CategoryGroup.PERSONAL_CARE to "WELLNESS_AND_BEAUTY",
                CategoryGroup.PERSONAL_CARE to "HEALTHCARE",
                CategoryGroup.PERSONAL_CARE to "EDUCATION",
                CategoryGroup.PERSONAL_CARE to "SPORT_ACTIVITIES",
                CategoryGroup.TRANSPORT to "TRANSPORT",
                CategoryGroup.TRANSPORT to "PUBLIC_TRANSPORT",
                CategoryGroup.TRANSPORT to "INTERCITY_TRANSPORT",
                CategoryGroup.TRANSPORT to "RIDE_SHARING",
                CategoryGroup.TRANSPORT to "TAXI",
                CategoryGroup.TRANSPORT to "BUSINESS_TRIP",
                CategoryGroup.TRANSPORT to "PETROL_AND_CHARGING",
                CategoryGroup.TRANSPORT to "PERSONAL_VEHICLE",
                CategoryGroup.FINANCIAL_COSTS to "FINANCIAL_COSTS",
                CategoryGroup.FINANCIAL_COSTS to "INSURANCE",
                CategoryGroup.FINANCIAL_COSTS to "CHARGES_AND_FEES",
                CategoryGroup.FINANCIAL_COSTS to "LOANS_AND_DEBTS",
                CategoryGroup.FINANCIAL_COSTS to "TAXES",
                CategoryGroup.FINANCIAL_COSTS to "CHARITY",
                CategoryGroup.FINANCIAL_COSTS to "SUBSCRIPTIONS",
                CategoryGroup.FINANCIAL_COSTS to "MOBILE_PHONE",
                CategoryGroup.FINANCIAL_COSTS to "INTERNET",
                CategoryGroup.FINANCIAL_COSTS to "SERVICES",
                CategoryGroup.FINANCIAL_COSTS to "PARKING_LOT",
                CategoryGroup.INVESTMENTS to "INVESTMENTS",
                CategoryGroup.INVESTMENTS to "STOCKS_AND_SHARES",
                CategoryGroup.INVESTMENTS to "CRYPTO",
                CategoryGroup.INVESTMENTS to "NFT",
                CategoryGroup.INVESTMENTS to "REALTY",
                CategoryGroup.INVESTMENTS to "SAVINGS",
                CategoryGroup.INCOME to "INCOME",
                CategoryGroup.INCOME to "SALARY",
                CategoryGroup.INCOME to "REIMBURSEMENT_OR_REFUND",
                CategoryGroup.INCOME to "SELLING",
                CategoryGroup.OTHER to "OTHER",
                CategoryGroup.OTHER to "WITHDRAWAL",
                CategoryGroup.OTHER to "TRANSFER",
                CategoryGroup.OTHER to "UNCATEGORIZED",
            )

            CategoriesTable.batchInsert(data = categoryGroupsToCategory, ignore = true) { data ->
                this[CategoriesTable.name] = data.second
                this[CategoriesTable.group] = data.first
            }
        }
    }
}