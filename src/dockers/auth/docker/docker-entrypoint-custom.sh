#!/bin/bash
################################################################################
# The John Operating System Project is the collection of software and configurations
# to generate IoT EcoSystem, like the John Operating System Platform one.
# Copyright (C) 2021 Roberto Pompermaier
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
################################################################################

set -eou pipefail


# usage: file_env VAR [DEFAULT]
#    ie: file_env 'XYZ_DB_PASSWORD' 'example'
# (will allow for "$XYZ_DB_PASSWORD_FILE" to fill in the value of
#  "$XYZ_DB_PASSWORD" from a file, especially for Docker's secrets feature)
file_env() {
    local var="$1"
    local fileVar="${var}_FILE"
    local def="${2:-}"
    if [[ ${!var:-} && ${!fileVar:-} ]]; then
        echo >&2 "error: both $var and $fileVar are set (but are exclusive)"
        exit 1
    fi
    local val="$def"
    if [[ ${!var:-} ]]; then
        val="${!var}"
    elif [[ ${!fileVar:-} ]]; then
        val="$(< "${!fileVar}")"
    fi

    if [[ -n $val ]]; then
        export "$var"="$val"
    fi

    unset "$fileVar"
}



echo ""
echo "========================================================================="
echo ""
echo "  Configure Keycloak startup's environment"
echo ""
echo "========================================================================="

SYS_PROPS=""
PRE_MAIN="# "
PRE_SUB="  "

echo "$PRE_MAIN Add admin user"
file_env 'KEYCLOAK_USER'
file_env 'KEYCLOAK_PASSWORD'

if [[ -n ${KEYCLOAK_USER:-} && -n ${KEYCLOAK_PASSWORD:-} ]]; then
    echo "$PRE_SUB Set Keycloak admin username and password"
    /opt/jboss/keycloak/bin/add-user-keycloak.sh --user "$KEYCLOAK_USER" --password "$KEYCLOAK_PASSWORD"
else
    echo "$PRE_SUB KEYCLOAK_USER and KEYCLOAK_PASSWORD NOT set, skipped."
fi


echo "$PRE_MAIN Hostname"
if [[ -n ${KEYCLOAK_FRONTEND_URL:-} ]]; then
    echo "$PRE_SUB Set property keycloak.frontendUrl: '$KEYCLOAK_FRONTEND_URL'"
    SYS_PROPS+="-Dkeycloak.frontendUrl=$KEYCLOAK_FRONTEND_URL"
else
    echo "$PRE_SUB KEYCLOAK_FRONTEND_URL NOT set, skipped."
fi

if [[ -n ${KEYCLOAK_HOSTNAME:-} ]]; then
    echo "$PRE_SUB Set property keycloak.hostname.provider: fixed"
    echo "$PRE_SUB Set property keycloak.hostname.fixed.hostname: $KEYCLOAK_FRONTEND_URL"
    SYS_PROPS+=" -Dkeycloak.hostname.provider=fixed -Dkeycloak.hostname.fixed.hostname=$KEYCLOAK_HOSTNAME"

    if [[ -n ${KEYCLOAK_HTTP_PORT:-} ]]; then
        echo "$PRE_SUB Set property keycloak.hostname.fixed.httpPort: $KEYCLOAK_HTTP_PORT"
        SYS_PROPS+=" -Dkeycloak.hostname.fixed.httpPort=$KEYCLOAK_HTTP_PORT"
    else
        echo "$PRE_SUB KEYCLOAK_HTTP_PORT NOT set, use default 8080."
    fi

    if [[ -n ${KEYCLOAK_HTTPS_PORT:-} ]]; then
        echo "$PRE_SUB Set property keycloak.hostname.fixed.httpsPort: $KEYCLOAK_HTTPS_PORT"
        SYS_PROPS+=" -Dkeycloak.hostname.fixed.httpsPort=$KEYCLOAK_HTTPS_PORT"
    else
        echo "$PRE_SUB KEYCLOAK_HTTPS_PORT NOT set, use default 8443."
    fi

    if [[ -n ${KEYCLOAK_ALWAYS_HTTPS:-} ]]; then
        echo "$PRE_SUB Set property keycloak.hostname.fixed.alwaysHttps: $KEYCLOAK_ALWAYS_HTTPS"
        SYS_PROPS+=" -Dkeycloak.hostname.fixed.alwaysHttps=$KEYCLOAK_ALWAYS_HTTPS"
    else
        echo "$PRE_SUB KEYCLOAK_ALWAYS_HTTPS NOT set, use default false."
    fi
else
    echo "$PRE_SUB KEYCLOAK_HOSTNAME NOT set, skipped."
fi


echo "$PRE_MAIN Import/Export"
if [[ -n ${KEYCLOAK_IMPORT:-} ]]; then
    #echo "$PRE_SUB Set property keycloak.import: $KEYCLOAK_IMPORT"
    #SYS_PROPS+=" -Dkeycloak.import=$KEYCLOAK_IMPORT"
    echo "$PRE_SUB Set property keycloak.migration.action: import"
    echo "$PRE_SUB Set property keycloak.migration.provider: singleFile"
    echo "$PRE_SUB Set property keycloak.migration.file: $KEYCLOAK_IMPORT"
    SYS_PROPS+=" -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=$KEYCLOAK_IMPORT -Dkeycloak.migration.strategy=IGNORE_EXISTING"
else
    echo "$PRE_SUB KEYCLOAK_IMPORT NOT set, skipped."
fi

if [[ -n ${KEYCLOAK_EXPORT:-} ]]; then
    echo "$PRE_SUB Set property keycloak.migration.action: export"
    echo "$PRE_SUB Set property keycloak.migration.provider: singleFile"
    echo "$PRE_SUB Set property keycloak.migration.file: $KEYCLOAK_EXPORT"
    SYS_PROPS+=" -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=$KEYCLOAK_EXPORT"
else
    echo "$PRE_SUB KEYCLOAK_EXPORT NOT set, skipped."
fi


echo "$PRE_MAIN JGroups bind options"
if [[ -z ${BIND:-} ]]; then
    BIND=$(hostname --all-ip-addresses)
    echo "$PRE_SUB Get hostname as BIND var: $BIND"
else
    echo "$PRE_SUB Use BIND var: $BIND"
fi
if [[ -z ${BIND_OPTS:-} ]]; then
    for BIND_IP in $BIND
    do
        echo "$PRE_SUB Set property jboss.bind.address: $BIND_IP"
        echo "$PRE_SUB Set property jboss.bind.address.private: $BIND_IP"
        BIND_OPTS+=" -Djboss.bind.address=$BIND_IP -Djboss.bind.address.private=$BIND_IP "
    done
fi
echo "$PRE_SUB Use BIND var: $BIND_OPTS"
SYS_PROPS+=" $BIND_OPTS"


echo "$PRE_MAIN Expose management console for metrics"
if [[ -n ${KEYCLOAK_STATISTICS:-} ]] ; then
    echo "$PRE_SUB Set property jboss.bind.address.management: 0.0.0.0"
    SYS_PROPS+=" -Djboss.bind.address.management=0.0.0.0"
else
    echo "$PRE_SUB KEYCLOAK_STATISTICS NOT set, skipped."
fi


echo "$PRE_MAIN Configuration file"
# If the server configuration parameter is not present, append the HA profile.
#if echo "$@" | grep -E -v -- '-c |-c=|--server-config |--server-config='; then
    echo "$PRE_SUB Set default configs file standalone-ha.xml"
    SYS_PROPS+=" -c=standalone-ha.xml"
#else
#    CONFIG=echo "$@" | grep -E -v -- '-c |-c=|--server-config |--server-config='
#    echo "$PRE_SUB Use $CONFIG configuration file"
#fi


echo "$PRE_MAIN DB setup"
file_env 'DB_USER'
file_env 'DB_PASSWORD'
# Lower case DB_VENDOR
if [[ -n ${DB_VENDOR:-} ]]; then
  DB_VENDOR=$(echo "$DB_VENDOR" | tr "[:upper:]" "[:lower:]")
fi
# Detect DB vendor from default host names
if [[ -z ${DB_VENDOR:-} ]]; then
    if (getent hosts postgres &>/dev/null); then
        export DB_VENDOR="postgres"
    elif (getent hosts mysql &>/dev/null); then
        export DB_VENDOR="mysql"
    elif (getent hosts mariadb &>/dev/null); then
        export DB_VENDOR="mariadb"
    elif (getent hosts oracle &>/dev/null); then
        export DB_VENDOR="oracle"
    elif (getent hosts mssql &>/dev/null); then
        export DB_VENDOR="mssql"
    fi
fi
# Detect DB vendor from legacy `*_ADDR` environment variables
if [[ -z ${DB_VENDOR:-} ]]; then
    if (printenv | grep '^POSTGRES_ADDR=' &>/dev/null); then
        export DB_VENDOR="postgres"
    elif (printenv | grep '^MYSQL_ADDR=' &>/dev/null); then
        export DB_VENDOR="mysql"
    elif (printenv | grep '^MARIADB_ADDR=' &>/dev/null); then
        export DB_VENDOR="mariadb"
    elif (printenv | grep '^ORACLE_ADDR=' &>/dev/null); then
        export DB_VENDOR="oracle"
    elif (printenv | grep '^MSSQL_ADDR=' &>/dev/null); then
        export DB_VENDOR="mssql"
    fi
fi
# Default to H2 if DB type not detected
if [[ -z ${DB_VENDOR:-} ]]; then
    export DB_VENDOR="h2"
fi

# if the DB_VENDOR is postgres then append port to the DB_ADDR
function append_port_db_addr() {
  local db_host_regex='^[a-zA-Z0-9]([a-zA-Z0-9]|-|.)*:[0-9]{4,5}$'
  IFS=',' read -ra addresses <<< "$DB_ADDR"
  DB_ADDR=""
  for i in "${addresses[@]}"; do
    if [[ $i =~ $db_host_regex ]]; then
        DB_ADDR+=$i;
     else
        DB_ADDR+="${i}:${DB_PORT}";
     fi
        DB_ADDR+=","
  done
  DB_ADDR=$(echo $DB_ADDR | sed 's/.$//') # remove the last comma
}
# Set DB name
case "$DB_VENDOR" in
    postgres)
        DB_NAME="PostgreSQL"
        if [[ -z ${DB_PORT:-} ]] ; then
          DB_PORT="5432"
        fi
        append_port_db_addr
        ;;
    mysql)
        DB_NAME="MySQL";;
    mariadb)
        DB_NAME="MariaDB";;
    oracle)
        DB_NAME="Oracle";;
    h2)
        DB_NAME="Embedded H2";;
    mssql)
        DB_NAME="Microsoft SQL Server";;
    *)
        echo "Unknown DB vendor $DB_VENDOR"
        exit 1
esac

# Append '?' in the beggining of the string if JDBC_PARAMS value isn't empty
JDBC_PARAMS=$(echo "${JDBC_PARAMS:-}" | sed '/^$/! s/^/?/')
export JDBC_PARAMS

# Convert deprecated DB specific variables
function set_legacy_vars() {
  local suffixes=(ADDR DATABASE USER PASSWORD PORT)
  for suffix in "${suffixes[@]}"; do
    local varname="$1_$suffix"
    if [[ -n ${!varname:-} ]]; then
      echo WARNING: "$varname" variable name is DEPRECATED replace with DB_"$suffix"
      export DB_"$suffix=${!varname}"
    fi
  done
}
set_legacy_vars "$(echo "$DB_VENDOR" | tr "[:upper:]" "[:lower:]")"

echo "$PRE_SUB Use '$DB_VENDOR' as database vendor"
echo "$PRE_SUB Use '$DB_DATABASE' as database name"
echo "$PRE_SUB Use '$DB_ADDR' as database address"
echo "$PRE_SUB Use '$DB_PORT' as database port"
echo "$PRE_SUB Use '$JDBC_PARAMS' as jdbc params"


echo ""
echo "========================================================================="
echo ""
echo "  Configure Keycloak database"
echo ""
echo "========================================================================="

#sleep 300
DB_URL="http://${DB_ADDR}:${DB_PORT}"
echo "$PRE_MAIN Wait for DB $DB_URL"
if [[ ${WAIT_FOR_DB} = "true" ]]; then
    MAX=30
    TRIES=0
    WAIT_SEC=5

    if curl -m 1 http://$DB_ADDR:$DB_PORT &> /dev/null; then
        DB_UP=0
    else
        DB_UP=1
    fi

    while [[ ${TRIES} -lt ${MAX} ]] && [[ ${DB_UP} -ne 0 ]]; do
        echo "$PRE_SUB Not reachable, waiting $WAIT_SEC seconds and retry ($TRIES/$MAX)"
        sleep $WAIT_SEC
        if curl -m 1 http://$DB_ADDR:$DB_PORT &> /dev/null; then
            DB_UP=0
        else
            DB_UP=1
        fi
        let TRIES=${TRIES}+1;
    done

    if [[ ${DB_UP} -ne 0 ]]; then
        echo "Database not reachable after $MAX x $WAIT_SEC seconds, exit!"
        exit
    fi
else
    echo "$PRE_SUB WAIT_FOR_DB NOT set, skipped."
fi

if [ "$DB_VENDOR" != "h2" ]; then
    echo "$PRE_MAIN Change DB"
    /bin/sh /opt/jboss/tools/databases/change-database.sh $DB_VENDOR
fi

echo "$PRE_MAIN Exec pre init scripts"
/opt/jboss/tools/x509.sh
/opt/jboss/tools/jgroups.sh
/opt/jboss/tools/infinispan.sh
/opt/jboss/tools/statistics.sh
/opt/jboss/tools/autorun.sh
/opt/jboss/tools/vault.sh



echo ""
echo "========================================================================="
echo ""
echo "  Start Keycloak"
echo ""
echo "========================================================================="

echo "$PRE_MAIN Execute Keycloak"
echo "$PRE_SUB /opt/jboss/keycloak/bin/standalone.sh"
echo "$PRE_SUB                     $SYS_PROPS"
echo "$PRE_SUB                     $@"
exec /opt/jboss/keycloak/bin/standalone.sh $SYS_PROPS $@
#exec /opt/jboss/keycloak/bin/standalone.sh $SYS_PROPS $@ -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=/tmp/export.json
exit $?
