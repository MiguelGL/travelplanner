#!/usr/bin/env bash

. pg-functions.sh

log_info "Deleting DB '$DB_NAME' ..."
as_pg_user ${PG_PATH}/dropdb ${DB_NAME}
log_info "Done"
