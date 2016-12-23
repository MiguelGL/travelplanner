#!/bin/bash

set -e

function log_info {
    tput setaf 2; echo $@; tput sgr 0
}

function log_error {
    tput setaf 1; echo $@; tput sgr 0
}

. defaults.conf

[ -d ${WILDFLY_BASE_DIR} ] \
    && log_error "Directory ${WILDFLY_BASE_DIR} exists, please remote it first" \
    && exit 1

[ -d ${INSTALL_BASE_DIR} ] || mkdir -v -p ${INSTALL_BASE_DIR}

log_info "~~~ Downloading WildFly app. server distribution ..."
wget --timestamping \
     http://download.jboss.org/wildfly/10.1.0.Final/wildfly-${WILDFLY_VERSION}.zip

log_info "~~~ Extracting WildFly app. server distribution ..."
unzip -q wildfly-${WILDFLY_VERSION}.zip -d ${INSTALL_BASE_DIR}

log_info "~~~ Downloading PostgreSQL JDBC Driver ..."
wget --timestamping \
     http://central.maven.org/maven2/org/postgresql/postgresql/${PG_JDBC_DRIVER_VERSION}/postgresql-${PG_JDBC_DRIVER_VERSION}.jar

log_info "~~~ Starting WildFly app. server in the background ..."
$WILDFLY_BASE_DIR/bin/standalone.sh > /dev/null &

log_info "~~~ Waiting for WildFly app. server to start ..."
sleep 5

log_info "~~~ Setting up PostgreSQL JDBC Driver WildFly Module ..."
$WILDFLY_BASE_DIR/bin/jboss-cli.sh --connect controller=$WILDFLY_MANAGEMENT << EOF
module add --name=org.postgres \
           --resources=postgresql-${PG_JDBC_DRIVER_VERSION}.jar \
           --dependencies=javax.api,javax.transaction.api
EOF

log_info "~~~ Registering a PostgreSQL WildFly JDBC Driver ..."
$WILDFLY_BASE_DIR/bin/jboss-cli.sh --connect controller=$WILDFLY_MANAGEMENT << EOF
/subsystem=datasources/jdbc-driver=postgres:add( \
    driver-name="postgres", \
    driver-module-name="org.postgres", \
    driver-class-name="org.postgresql.Driver", \
    driver-xa-datasource-class-name="org.postgresql.xa.PGXADataSource")
EOF

log_info "~~~ Registering a PostgreSQL WildFly DataSource ..."
$WILDFLY_BASE_DIR/bin/jboss-cli.sh --connect controller=$WILDFLY_MANAGEMENT << EOF
xa-data-source add \
    --name=${PG_DS_NAME} \
    --driver-name=postgres \
    --jndi-name=java:jboss/datasources/${PG_DS_NAME} \
    --user-name=${DB_USER} \
    --password=${DB_PASSWORD} \
    --use-java-context=true \
    --use-ccm=true \
    --min-pool-size=10 \
    --max-pool-size=100 \
    --pool-prefill=true \
    --allocation-retry=1 \
    --prepared-statements-cache-size=32 \
    --share-prepared-statements=true \
    --xa-datasource-properties=ServerName=${DB_HOST},PortNumber=${DB_PORT},DatabaseName=${DB_NAME} \
    --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker \
    --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter
xa-data-source enable --name=${PG_DS_NAME}
reload
EOF

log_info "~~~ Setting up WildFly security ..."
$WILDFLY_BASE_DIR/bin/jboss-cli.sh --connect controller=$WILDFLY_MANAGEMENT << EOF
batch

/subsystem=security/security-domain=${SECURITY_DOMAIN}:add(cache-type=default)
/subsystem=security/security-domain=${SECURITY_DOMAIN}/authentication=classic:add()
/subsystem=security/security-domain=${SECURITY_DOMAIN}/authentication=classic/login-module=Database \
    :add(code=Database, flag=required, \
         module-options={"dsJndiName"=>"java:jboss/datasources/${PG_DS_NAME}", \
                         "principalsQuery"=>"${GET_PASSWORD_QUERY}", \
                         "rolesQuery"=>"${GET_ROLES_QUERY}", \
                         "hashAlgorithm"=>"${PASSWORD_HASH}", \
                         "hashEncoding"=>"${PASSWORD_ENCODING}"})

/core-service=management/security-realm=${SECURITY_REALM}:add
/core-service=management/security-realm=${SECURITY_REALM}/authentication=jaas:add(name=${SECURITY_DOMAIN})

run-batch
EOF


log_info "~~~ Stopping WildFly app. server ..."
$WILDFLY_BASE_DIR/bin/jboss-cli.sh --connect controller=$WILDFLY_MANAGEMENT << EOF
shutdown
EOF

log_info "~~~ Done!"

exit 0
