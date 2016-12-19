#!/usr/bin/env bash

. pg-functions.sh

log_info "Deleting DB user '$DB_USER' ..."
as_pg_user ${PG_PATH}/dropuser ${DB_USER}
log_info "Done"
