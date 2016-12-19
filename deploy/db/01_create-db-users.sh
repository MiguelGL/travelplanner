#!/usr/bin/env bash

. pg-functions.sh

log_info "Creating DB user '$DB_USER' ..."
as_pg_user ${PG_PATH}/createuser --superuser --pwprompt ${DB_USER}
log_info "Done"
