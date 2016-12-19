#!/usr/bin/env bash

# Deliberately not: http://stackoverflow.com/questions/4381618/exit-a-script-on-error
# set -e

. pg-db.conf

function error_handler() {
    local readonly errcode=$? # save the exit code as the first thing done in the trap function
    echo "***> error $errorcode"
    # echo "***> the command executing at the time of the error was"
    # echo "***> $BASH_COMMAND"
    echo "***> on line ${BASH_LINENO[0]}"
    # do some error handling, cleanup, logging, notification
    # $BASH_COMMAND contains the command that was being executed at the time of the trap
    # ${BASH_LINENO[0]} contains the line number in the script of that command
    # exit the script or return to try again, etc.
    exit $errcode  # or use some other value or do return instead
}

trap error_handler ERR

function log_info() {
    local readonly msg="$@"
    echo "---> $msg"
}

function as_pg_user() {
    if [[ "$USER" == "$PG_USER" ]]
    then
        $@
    else
        sudo -u $PG_USER $@
    fi
}
