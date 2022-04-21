package io.ducket.api.app.database.migration

import domain.model.category.CategoriesTable
import domain.model.currency.CurrenciesTable
import io.ducket.api.app.CategoryType
import io.ducket.api.app.CategoryTypeGroup
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
                CategoryTypeGroup.HOUSING to CategoryType.HOUSING.name,
                CategoryTypeGroup.HOUSING to CategoryType.BUILDING_AND_REPAIR.name,
                CategoryTypeGroup.HOUSING to CategoryType.MORTGAGE.name,
                CategoryTypeGroup.HOUSING to CategoryType.RENT_PAYMENT.name,
                CategoryTypeGroup.HOUSING to CategoryType.HOME_SECURITY.name,
                CategoryTypeGroup.HOUSING to CategoryType.UTILITIES.name,
                CategoryTypeGroup.HOUSING to CategoryType.HOME_MEDIA.name,
                CategoryTypeGroup.FOOD_AND_DRINKS to CategoryType.FOOD_AND_DRINKS.name,
                CategoryTypeGroup.FOOD_AND_DRINKS to CategoryType.CAFE.name,
                CategoryTypeGroup.FOOD_AND_DRINKS to CategoryType.RESTAURANTS_OR_BARS.name,
                CategoryTypeGroup.FOOD_AND_DRINKS to CategoryType.FOOD_DELIVERY_OR_TAKEOUT.name,
                CategoryTypeGroup.FOOD_AND_DRINKS to CategoryType.FASTFOOD.name,
                CategoryTypeGroup.FOOD_AND_DRINKS to CategoryType.GROCERY.name,
                CategoryTypeGroup.FOOD_AND_DRINKS to CategoryType.SPORT_NUTRITION.name,
                CategoryTypeGroup.SHOPPING to CategoryType.SHOPPING.name,
                CategoryTypeGroup.SHOPPING to CategoryType.GIFTS.name,
                CategoryTypeGroup.SHOPPING to CategoryType.HOME_SUPPLIES.name,
                CategoryTypeGroup.SHOPPING to CategoryType.ELECTRONICS_OR_APPLIANCES.name,
                CategoryTypeGroup.SHOPPING to CategoryType.BOOKS_OR_STATIONERY.name,
                CategoryTypeGroup.SHOPPING to CategoryType.PHARMACY.name,
                CategoryTypeGroup.SHOPPING to CategoryType.CLOTHES_OR_SHOES.name,
                CategoryTypeGroup.SHOPPING to CategoryType.ACCESSORIES_AND_EQUIPMENT.name,
                CategoryTypeGroup.SHOPPING to CategoryType.FURNITURE_AND_DECOR.name,
                CategoryTypeGroup.SHOPPING to CategoryType.DIGITAL_PRODUCTS.name,
                CategoryTypeGroup.FAMILY to CategoryType.FAMILY.name,
                CategoryTypeGroup.FAMILY to CategoryType.CHILD_SUPPLIES.name,
                CategoryTypeGroup.FAMILY to CategoryType.CHILD_HEALTHCARE.name,
                CategoryTypeGroup.FAMILY to CategoryType.CHILD_EDUCATION.name,
                CategoryTypeGroup.FAMILY to CategoryType.DAYCARE.name,
                CategoryTypeGroup.FAMILY to CategoryType.BABYSITTER.name,
                CategoryTypeGroup.FAMILY to CategoryType.PETS.name,
                CategoryTypeGroup.LEISURE to CategoryType.LEISURE.name,
                CategoryTypeGroup.LEISURE to CategoryType.HOBBY.name,
                CategoryTypeGroup.LEISURE to CategoryType.HOLIDAYS_AND_VACATIONS.name,
                CategoryTypeGroup.LEISURE to CategoryType.PUBLIC_EVENTS.name,
                CategoryTypeGroup.LEISURE to CategoryType.CINEMA_OR_THEATER.name,
                CategoryTypeGroup.LEISURE to CategoryType.GAMES.name,
                CategoryTypeGroup.PERSONAL_CARE to CategoryType.PERSONAL_CARE.name,
                CategoryTypeGroup.PERSONAL_CARE to CategoryType.WELLNESS_AND_BEAUTY.name,
                CategoryTypeGroup.PERSONAL_CARE to CategoryType.HEALTHCARE.name,
                CategoryTypeGroup.PERSONAL_CARE to CategoryType.EDUCATION.name,
                CategoryTypeGroup.PERSONAL_CARE to CategoryType.SPORT_ACTIVITIES.name,
                CategoryTypeGroup.TRANSPORT to CategoryType.TRANSPORT.name,
                CategoryTypeGroup.TRANSPORT to CategoryType.PUBLIC_TRANSPORT.name,
                CategoryTypeGroup.TRANSPORT to CategoryType.INTERCITY_TRANSPORT.name,
                CategoryTypeGroup.TRANSPORT to CategoryType.RIDE_SHARING.name,
                CategoryTypeGroup.TRANSPORT to CategoryType.TAXI.name,
                CategoryTypeGroup.TRANSPORT to CategoryType.BUSINESS_TRIP.name,
                CategoryTypeGroup.TRANSPORT to CategoryType.PETROL_AND_CHARGING.name,
                CategoryTypeGroup.TRANSPORT to CategoryType.PERSONAL_VEHICLE.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.FINANCIAL_COSTS.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.INSURANCE.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.CHARGES_AND_FEES.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.LOANS_AND_DEBTS.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.TAXES.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.CHARITY.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.SUBSCRIPTIONS.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.MOBILE_PHONE.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.INTERNET.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.SERVICES.name,
                CategoryTypeGroup.FINANCIAL_COSTS to CategoryType.PARKING_LOT.name,
                CategoryTypeGroup.INVESTMENTS to CategoryType.INVESTMENTS.name,
                CategoryTypeGroup.INVESTMENTS to CategoryType.STOCKS_AND_SHARES.name,
                CategoryTypeGroup.INVESTMENTS to CategoryType.CRYPTO.name,
                CategoryTypeGroup.INVESTMENTS to CategoryType.NFT.name,
                CategoryTypeGroup.INVESTMENTS to CategoryType.REALTY.name,
                CategoryTypeGroup.INVESTMENTS to CategoryType.SAVINGS.name,
                CategoryTypeGroup.INCOME to CategoryType.INCOME.name,
                CategoryTypeGroup.INCOME to CategoryType.SALARY.name,
                CategoryTypeGroup.INCOME to CategoryType.REIMBURSEMENT_OR_REFUND.name,
                CategoryTypeGroup.INCOME to CategoryType.SELLING.name,
                CategoryTypeGroup.OTHER to CategoryType.OTHER.name,
                CategoryTypeGroup.OTHER to CategoryType.WITHDRAWAL_OR_TRANSFER.name,
                CategoryTypeGroup.OTHER to CategoryType.UNCATEGORIZED.name,
            )

            CategoriesTable.batchInsert(data = categoryGroupsToCategory, ignore = true) { data ->
                this[CategoriesTable.name] = data.second
                this[CategoriesTable.group] = data.first
            }
        }
    }
}