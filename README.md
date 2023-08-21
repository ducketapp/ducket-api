# expenny-service
[DEPRECATED] Backend service written in Kotlin, using [Ktor 2.0](https://ktor.io/docs/welcome.html), [Exposed ORM framework](https://github.com/JetBrains/Exposed) and [Flyway](https://flywaydb.org/). More about used libraries in gradle.properties 

Manual debug run:
* Precondition: MySQL DB server is running
* Main class: org.expenny.service.ApplicationKt
* Program arguments: -config=resources/application.dev.conf
* VM options: -Ddb.pullRates=false

Docker compose debug run:
* Create `debug.env` file in root which contains:
```
DB_TZ=UTC
DB_PORT=3306
DB_ROOT_USER=root
DB_ROOT_PASSWORD=toor

DB_MAIN_SCHEME=exp_main_prod_db
DB_SCHEDULER_SCHEME=exp_scheduler_prod_db
DB_RATES_SCHEME=exp_rates_prod_db

APP_PORT=8100
APP_SECRET=test

GR_USER=exp_admin
GR_PASSWORD=test
```
* Execute:
```
docker compose --env-file .\debug.env up
```
