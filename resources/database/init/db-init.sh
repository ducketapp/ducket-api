#!/bin/bash

echo "*** Creating default databases ***"

mysql --user=$MYSQL_ROOT_USER --password=$MYSQL_ROOT_PASSWORD --execute \
"CREATE DATABASE IF NOT EXISTS $DB_MAIN_SCHEME DEFAULT CHARACTER SET utf8;
 CREATE DATABASE IF NOT EXISTS $DB_RATES_SCHEME DEFAULT CHARACTER SET utf8;"

echo "*** Finished creating default databases ***"