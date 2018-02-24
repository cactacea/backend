#!/usr/bin/env bash
set -x
set -e
sudo service mysql stop || echo "mysql not stopped"
sudo stop mysql-5.7 || echo "mysql-5.7 not stopped"
sudo  mysqld_safe --skip-grant-tables &
sleep 4
sudo mysql -e "CREATE DATABASE IF NOT EXISTS cactacea;"
sudo mysql -e "use mysql; update user set authentication_string=PASSWORD('root') where User='root'; update user set plugin='mysql_native_password';FLUSH PRIVILEGES;"
sudo service mysql restart
sleep 4
