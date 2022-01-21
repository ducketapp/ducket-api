START TRANSACTION;
USE `ducket-db`;
INSERT INTO `ducket-db`.`currency` (`id`, `area`, `name`, `symbol`, `iso_code`) VALUES (DEFAULT, 'Poland', 'Polish złoty', 'zł', 'PLN');
INSERT INTO `ducket-db`.`currency` (`id`, `area`, `name`, `symbol`, `iso_code`) VALUES (DEFAULT, 'EU', 'Euro', '€', 'EUR');
INSERT INTO `ducket-db`.`currency` (`id`, `area`, `name`, `symbol`, `iso_code`) VALUES (DEFAULT, 'United States', 'United States dollar', '$', 'USD');

COMMIT;


START TRANSACTION;
USE `ducket-db`;
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'HOUSING', 'HOUSING');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'HOUSING', 'BUILDING_AND_REPAIR');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'HOUSING', 'MORTGAGE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'HOUSING', 'RENT_PAYMENT');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'HOUSING', 'HOME_SECURITY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'HOUSING', 'UTILITIES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'HOUSING', 'HOME_MEDIA');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FOOD_AND_DRINKS', 'FOOD_AND_DRINKS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FOOD_AND_DRINKS', 'CAFE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FOOD_AND_DRINKS', 'RESTAURANTS_OR_BARS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FOOD_AND_DRINKS', 'FOOD_DELIVERY_OR_TAKEOUT');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FOOD_AND_DRINKS', 'FASTFOOD');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FOOD_AND_DRINKS', 'GROCERY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FOOD_AND_DRINKS', 'SPORT_NUTRITION');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'SHOPPING');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'GIFTS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'HOME_SUPPLIES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'ELECTRONICS_OR_APPLIANCES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'BOOKS_OR_STATIONERY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'PHARMACY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'CLOTHES_OR_SHOES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'ACCESSORIES_AND_EQUIPMENT');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'FURNITURE_AND_DECOR');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'DIGITAL_PRODUCTS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'SHOPPING', 'SERVICES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FAMILY', 'FAMILY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FAMILY', 'CHILD_SUPPLIES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FAMILY', 'CHILD_HEALTHCARE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FAMILY', 'CHILD_EDUCATION');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FAMILY', 'DAYCARE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FAMILY', 'BABYSITTER');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FAMILY', 'PETS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'LEISURE', 'LEISURE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'LEISURE', 'HOBBY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'LEISURE', 'HOLIDAYS_AND_VACATIONS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'LEISURE', 'PUBLIC_EVENTS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'LEISURE', 'CINEMA_AND_THEATER');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'LEISURE', 'GAMES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'PERSONAL_CARE', 'PERSONAL_CARE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'PERSONAL_CARE', 'WELLNESS_AND_BEAUTY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'PERSONAL_CARE', 'HEALTHCARE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'PERSONAL_CARE', 'EDUCATION');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'PERSONAL_CARE', 'SPORT_ACTIVITIES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'TRANSPORT', 'TRANSPORT');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'TRANSPORT', 'PUBLIC_TRANSPORT');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'TRANSPORT', 'INTERCITY_TRANSPORT');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'TRANSPORT', 'RIDE_SHARING');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'TRANSPORT', 'TAXI');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'TRANSPORT', 'BUSINESS_TRIP');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'TRANSPORT', 'PETROL_AND_CHARGING');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'TRANSPORT', 'PERSONAL_VEHICLE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'FINANCIAL_COSTS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'INSURANCE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'CHARGES_AND_FEES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'LOANS_AND_DEBTS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'TAXES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'CHARITY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'SUBSCRIPTIONS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'MOBILE_PHONE');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'INTERNET');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'SERVICES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'FAMILY_SUPPORT');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'FINANCIAL_COSTS', 'PARKING_LOT');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INVESTMENTS', 'INVESTMENTS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INVESTMENTS', 'STOCKS_AND_SHARES');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INVESTMENTS', 'CRYPTO');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INVESTMENTS', 'NFT');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INVESTMENTS', 'REALTY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INVESTMENTS', 'SAVINGS');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INCOME', 'INCOME');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INCOME', 'SALARY');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INCOME', 'CASHBACK_OR_REFUND');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'INCOME', 'SELLING');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'OTHER', 'OTHER');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'OTHER', 'WITHDRAWAL');
INSERT INTO `ducket-db`.`category` (`id`, `group`, `name`) VALUES (DEFAULT, 'OTHER', 'TRANSFER');

COMMIT;
