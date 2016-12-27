# TravelPlanner Demo Project

## Introduction

TODO: write.

## Requirements

TODO: review and ellaborate.
TODO: mention wildfly ports: 8080, 9990

### Runtime & Deployment

- Unix operating system (Linux, Mac OS).
- PostgreSQL server >= 9.3 installation.
- Oracle Java JDK >= 8.

### Deployment

- Shell command line utilities:
  - bash.
  - wget.
  - unzip.
- Apache Maven >= 3.3.0.
- Node >= 4, NPM >= 3.
- Yarn (https://yarnpkg.com/en/docs/install).

## Preparing, Compiling & Deploying

TODO: special mention to posgres auth setting.

TODO: write.

TODO: yarn global add angular-cli
TODO: yarn install

## Potential Future Improvements

A number of decisions have been taken when building the first version of this project, as well as other trade-offs have been chosen. These all may be revisited for a future version.

A non-exclusive list of subjects is included next.

- Put the system behind a reverse proxy such as NGINX for SSL support. Alternatively, configure the WildFly application server with a proper SSL certificate and have it do SSL.
- Validation messages for the web application could be more meaningful and provide more information.
- Some parameters are common to both server- and front-side code, such as validation constraints (min/max lengths, patterns, ...). They are however duplicated in both parts for this version and this is error prone as changes are to be made in both server code **and** frontend code to keep them in sync. A solution could be to try to use some kind of java-to-typescript tool for some files keeping the shared definitions.
- This version uses explicit method calls whenever access to a specific user's resources is to be checked. This could probably be better done using CDI interceptors annotating checked method calls.
- The REST API is heavily based on the client becoming aware of the database ID (primary key) for many entities. This is not probably a best solution and something based on UUIDs, slugs or similar could be done to not expose that low-level, sensitive, DB information.
- For configuration simplicity, the passwords are stored as the Base64 encoding of a SHA-256 hash. But for stronger security storing passwords using BCrypt is advisable. 
- There is no functionality for users to recover their passwords in case they forgot.
- There is no way to validate email addresses users enter are indeed valid (real). An additional step to email these addresses a confirmation link or similar would be a nice improvement.
- Angular router deactivation guards could be used to prevent user from navigating away from pages where values have been entered and could therefore be lost.
- Angular's AOT (Ahead Of Time) compiling feature could be used to speedup application client-side bootstrapping by precompiling templates.
- Because previously selected user destinations are stored server-side, autocompletion support could be added to the control used to enter a trip destination.
- A custom HTTP client wrapper could be built so that the application warns and redirects to login automatically upon receiving an authorization (401) error if security token expires. Alternatively, a periodic token refresh could be implemented to prevent this from happening.
