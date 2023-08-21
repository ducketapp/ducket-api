# expenny-service
Backend service

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
