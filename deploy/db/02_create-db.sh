#!/usr/bin/env bash

. pg-functions.sh

log_info "Creating DB '$DB_NAME' owned by '$DB_USER' ..."
as_pg_user ${PG_PATH}/createdb ${DB_NAME} -O ${DB_USER} --encoding=UTF-8 # --template=template0
log_info "Done"
