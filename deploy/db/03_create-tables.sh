#!/usr/bin/env bash

. pg-functions.sh

log_info "Creating DB '$DB_NAME' tables ..."
as_pg_user ${PG_PATH}/psql ${PSQL_ARGS} --file schema.sql
log_info "Done"
