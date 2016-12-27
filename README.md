# TravelPlanner Evaluation Project

## Introduction

This project implements a system designed to track and manage "trips"
users register. Users can create accounts and use the application.

Users with special permissions can manage user accounts.

The system is comprised of a Java EE server-side application backed by
a PostgreSQL database. An Angular 2 web interface is used to access
the application.

The Java EE provides a REST API consumed by the web application.

## Requirements

This section explains the requirements for building and deploying the
solution.

### Runtime & Deployment Requirements

- Unix operating system (Linux, Mac OS).
- PostgreSQL server >= 9.3 installation.
- Oracle Java JDK >= 8.
- Free TCP ports: 8080 and 9990.
- Internet connectivity.

### Deployment

- Shell command line utilities:
  - bash.
  - wget.
  - unzip.
- Apache Maven >= 3.3.0.
- Node >= 4, NPM >= 3.
- Yarn >= 0.18.1 (https://yarnpkg.com/en/docs/install).

## Preparing, Compiling & Deploying

From here on it will be assumed that the directory where the git
repository has been cloned into is `${SOURCES_DIR}`.

### Preparing the Data Base

A running installation of PostgreSQL is a requirement as explained
above.

We need to create the database, tables and user. There are a number of
utility scripts prepared under `${SOURCES_DIR}/deploy/db`.

The `pg-db.conf` file needs to be edited so that the `PG_USER` value
is set to a PostgreSQL user able to create databases in the
server. For example, for debian-based linux distributions this is the
`postgres` system user. Also, `PG_PATH` should be set to the base path
where PostgreSQL command-line utilities reside. This is `/usr/bin` for
debian-based linux distributions, again.

The changes above should be enough, now the set-up script should be
run:

    $ cd ${SOURCES_DIR}/deploy/db
	$ ./00_setup-db.sh

The commands above will create the database, user and tables required
to run the application.

### Preparing the WildFly Application Server

A utility script is provided so that the WildFly Java EE application
server is downloaded, installed and configured:
`${SOURCES_DIR}/deploy/provision-wildfly.sh`. 

The only configuration change to be done is setting up a base path for
the installation in the variable `INSTALL_BASE_DIR` from
`defaults.conf`. Then, the script can be used to prepare the
application server:

    $ cd ${SOURCES_DIR}/deploy
    $ ./provision-wildfly.sh

The directory selected via `INSTALL_BASE_DIR` will be referred from
now on as `${WILDFLY_DIR}`.

### Building the Angular 2 Web Application - Frontend

The following steps are required to build and prepare the Angular 2
application.

	$ cd ${SOURCES_DIR}/webapp
	$ yarn global add angular-cli
	$ yarn install
    $ ng build --target=development --base-href=/travelplanner/
	$ cp dist/* ../server/src/main/webapp/

The last command copies the output of the web application build into
the server-side project so that its resources get served from it.

### Building & Deploying the Java EE Application - Backend

Now, in order to build the server application:

    $ cd ${SOURCES_DIR}/server
	$ mvn clean install -DskipTests=true

When building for the first time this may take a long time while maven
downloads all dependencies.

To deploy the application you need to first start the server and then
copy the war file resulting from the previous compilation step:

	$ cd ${WILDFLY_DIR}/bin
	$ ./standalone.sh > /dev/null &
	$ cd ${SOURCES_DIR}/server
	$ cp target/travelplanner-server-1.0.0-SNAPSHOT.war ${WILDFLY_DIR}/standalone/deployments

You can confirm deployment worked by checking the existence of a
`travelplanner-server-1.0.0-SNAPSHOT.war.deployed` file under
`${WILDFLY_DIR}/standalone/deployments`.

### Integration Testing the Java EE Application

You can now run the (integration) tests:

    $ cd ${SOURCES_DIR}/server
    $ mvn test

A "BUILD SUCCESS" message should indicate everything went right.

### Using the Application

Just point your browser to
`http://localhost:8080/travelplanner/`.

There exist to default accounts with "administrator" and "manager"
roles assigned, respectively: `admin@email.com` with password
`adminpass`, and `manager@email.com` with password `managerpass`.

## Potential Future Improvements

A number of decisions have been taken when building the first version
of this project, as well as other trade-offs have been chosen. These
all may be revisited for a future version.

A non-exclusive list of subjects is included next.

- Put the system behind a reverse proxy such as NGINX for SSL
  support. Alternatively, configure the WildFly application server
  with a proper SSL certificate and have it do SSL.
- Validation messages for the web application could be more meaningful
  and provide more information.
- Some parameters are common to both server- and front-side code, such
  as validation constraints (min/max lengths, patterns, ...). They are
  however duplicated in both parts for this version and this is error
  prone as changes are to be made in both server code **and** frontend
  code to keep them in sync. A solution could be to try to use some
  kind of java-to-typescript tool for some files keeping the shared
  definitions.
- This version uses explicit method calls whenever access to a
  specific user's resources is to be checked. This could probably be
  better done using CDI interceptors annotating checked method calls.
- The REST API is heavily based on the client becoming aware of the
  database ID (primary key) for many entities. This is not probably a
  best solution and something based on UUIDs, slugs or similar could
  be done to not expose that low-level, sensitive, DB information.
- For configuration simplicity, the passwords are stored as the Base64
  encoding of a SHA-256 hash. But for stronger security storing
  passwords using BCrypt is advisable.
- There is no functionality for users to recover their passwords in
  case they forgot.
- There is no way to validate email addresses users enter are indeed
  valid (real). An additional step to email these addresses a
  confirmation link or similar would be a nice improvement.
- Angular router deactivation guards could be used to prevent user
  from navigating away from pages where values have been entered and
  could therefore be lost.
- Angular's AOT (Ahead Of Time) compiling feature could be used to
  speedup application client-side bootstrapping by precompiling
  templates.
- Because previously selected user destinations are stored
  server-side, autocompletion support could be added to the control
  used to enter a trip destination.
- A custom HTTP client wrapper could be built so that the application
  warns and redirects to login automatically upon receiving an
  authorization (401) error if security token expires. Alternatively,
  a periodic token refresh could be implemented to prevent this from
  happening.
- The web app trip edit and creation forms should be refactored to
  extract common features which are currently duplicated in both.
- Maven exec plugin could be used to include building the Angular web
  application as a part of the build process and saving a manual copy
  of files.
