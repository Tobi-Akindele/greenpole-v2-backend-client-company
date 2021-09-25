#!/bin/bash

#Change OS time zone
mv -f /etc/localtime /etc/localtime.bak
ln -s /usr/share/zoneinfo/Africa/Lagos /etc/localtime

#download keystore from repo
set -e
cd /opt/greenpoleclientcompany/config/


cd /opt/greenpoleclientcompany/

java -jar -Dspring.profiles.active=docker greenpole-client-company-0.0.1.jar
