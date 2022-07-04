# Realworld-v2

Re-imagining the [Realworld REST API](https://github.com/gothinkster/realworld/tree/master/api),
while splitting its functionality in microservices with [Quarkus](https://quarkus.io/).

## The build system

The build system is Maven and is configured by a set of properties and profiles, as follows:

### Build properties

The following properties are local to an environment; they can be specified as `-Dpropname=propvalue` command line arguments,
or placed in a local Maven profile in `~/.m2/settings.xml`.

- `database.user.url`, `database.article.url`, `database.comments.url`: The JDBC URL of the database for the respective microservice
- `database.user.username`, `database.article.username`, `database.comments.username`: The DB user name
- `database.user.password`, `database.article.password`, `database.comments.password`: The DB password
- `kafka.bootstrap.servers`: The Kafka bootstrap servers for that environment
- **(TODO)** `db.env` (default: `dev`): Needed only by Liquibase to indicate which environment-specific [contexts](https://www.liquibase.org/documentation/contexts.html)
  will it activate; e.g. `dev` will activate the `data-dev` context

Example:

```xml
<settings>
	<profile>
		<id>realworld-v2-local-postgres</id>
		<properties>
			<database.article.url>jdbc:postgresql://localhost/rwlv2</database.article.url>
			<database.article.username>rwlv2_article</database.article.username>
			<database.article.password>rwlv2_article</database.article.password>
			<database.user.url>jdbc:postgresql://localhost/rwlv2</database.user.url>
			<database.user.username>rwlv2_user</database.user.username>
			<database.user.password>rwlv2_user</database.user.password>
			<database.comments.url>jdbc:postgresql://localhost/rwlv2</database.comments.url>
			<database.comments.username>rwlv2_user</database.comments.username>
			<database.comments.password>rwlv2_user</database.comments.password>
			<kafka.bootstrap.servers>localhost:9094</kafka.bootstrap.servers>
		</properties>
	</profile>
	<profile>
		<id>realworld-v2-docker-postgres</id>
		<properties>
			<database.article.url>jdbc:postgresql://postgres/rwlv2</database.article.url>
			<database.article.username>rwlv2_article</database.article.username>
			<database.article.password>rwlv2_article</database.article.password>
			<database.user.url>jdbc:postgresql://postgres/rwlv2</database.user.url>
			<database.user.username>rwlv2_user</database.user.username>
			<database.user.password>rwlv2_user</database.user.password>
			<database.comments.url>jdbc:postgresql://postgres/rwlv2</database.comments.url>
			<database.comments.username>rwlv2_user</database.comments.username>
			<database.comments.password>rwlv2_user</database.comments.password>
			<kafka.bootstrap.servers>kafka:9092</kafka.bootstrap.servers>
		</properties>
	</profile>
</settings>
```

Both profiles use Postgresql. One is to run the entire application through `docker-compose`, in which case Kafka and
Postgresql are in the `kafka` and `postgres` hosts - see `realworld-v2-docker/docker-compose/docker-compose-postgres.yml`.
The other is to run only the peripherals in Docker - see `realworld-v2-docker/docker-compose/docker-compose-peripherals-postgres.yml`.

### Build profiles

- `article-dbupdate`, `user-dbupdate`, `comments-dbupdate`: Execute Liquibase to bring the respective database up-to-date
- `test-h2`: This will activate the DAO tests, using an in-memory H2 database (currently `h2` is the only DB option)
- `article-quarkus-dev`, `user-quarkus-dev`, `comments-quarkus-dev`: Activate `quarkus:dev` for the respective microservice; do not use together in the same command
  (naturally there is no problem running them in parallel, as long as they run from different shells)
- `docker`: Activating the Docker image build

All properties, except the db type (`quarkus.datasource.db-kind`) are set to dummy values in the configuration files
and overridden at runtime by command-line arguments. For the Quarkus dev mode, check out the `jvm.args` property
in each of the microservice `pom.xml` files. For running in Docker, check out the `env-<project>` files in `docker-compose/`.

### Updating dependencies

The versions of all dependencies are controlled by Maven properties in the form `version.<uniqueId>`,
where `<uniqueId>` is a unique identifier for the dependency, preferably the artifact id, but anything
unique and sufficiently descriptive will do. All version properties are defined in the parent pom.
As such, detecting updates is as simple as running (`-N` for non-recursive build, since all version properties are
in the parent pom):

```shell
mvn -N versions:display-property-updates
mvn -N versions:display-plugin-updates
```

If a dependency is left behind for a reason, please add a comment in the parent pom.

## Building

### Creating/updating the DB

Decide the database to use and make sure Maven picks up the corresponding properties.

Assuming that the properties are defined through a Maven profile, e.g. like the following in `~/.m2/settings.xml`:

```xml
	<profile>
		<id>realworld-v2-local</id>
		<properties>
			<database.user.url>jdbc:postgresql://localhost/rwlv2</database.user.url>
			<database.user.username>rwlv2_user</database.user.username>
			<database.user.password>rwlv2_user</database.user.password>
			<database.article.url>jdbc:postgresql://localhost/rwlv2</database.article.url>
			<database.article.username>rwlv2_article</database.article.username>
			<database.article.password>rwlv2_article</database.article.password>
			<database.comments.url>jdbc:postgresql://localhost/rwlv2</database.comments.url>
			<database.comments.username>rwlv2_comments</database.comments.username>
			<database.comments.password>rwlv2_comments</database.comments.password>
			<kafka.bootstrap.servers>localhost:9094</kafka.bootstrap.servers>
		</properties>
	</profile>
```

```shell
mvn process-resources -Particle-dbupdate,user-dbupdate,comments-dbupdate,realworld-v2-local
```

Otherwise, you have to specify the properties by command line:

```shell
mvn process-resources -Particle-dbupdate,user-dbupdate,comments-dbupdate -Ddatabase.article.url=... -Ddatabase.article.username=... -Ddatabase.article.password=... -D...
```

### Building the JAR artifacts

```shell
mvn clean package -Ptest-h2
```

You can omit `test-h2` to skip the DB tests.

### Building the native artifacts

**TODO**

### Building the Docker image

Building the Docker images occurs during the `package` phase.
It is not active by default; activate it with the `docker` profile.
I.e. the Maven command line should be amended as follows:

```shell
mvn ... package -Pdocker,...
```

E.g.

```shell
mvn clean package -Ptest-h2,docker
```

#### Docker compose

There is a `docker-compose` file under `realworld-v2-docker/docker-compose` that can be used to start the collaborating
applications: Zookeeper, Kafka and Keycloak with a predefined domain.
Make sure the images are built first!

Docker compose needs some environment properties files, one per microservice (`env-article`, `env-comments`, `env-user`).
Place these files under the `realworld-v2-docker/docker-compose` directory.
The corresponding template files (e.g. `env-article-template`) provide instructions on how to create the env files by hand.
For convenience, these files are created when building the docker images, under the `target/docker/` directory of the corresponding
microservice project (e.g. `cp realworld-v2-comments-module/realworld-v2-comments/target/docker/env-comments realworld-v2-docker/docker-compose/`).
For even more convenience, run the script `update-env-files.sh` from within the `realworld-v2-docker/docker-compose` directory,
after building the project and before executing `docker-compose up`.

There are several flavors of the docker-compose file:

- `docker-compose-h2.yml`: Start everything, using embedded H2 (DEPRECATED)
- `docker-compose-h2-peripherals.yml`: Start only the peripheral applications (e.g. Kafka, Keycloak), using embedded H2 (DEPRECATED)
- `docker-compose-postgres.yml`: Start everything, using a single separate Postgres instance as database
- `docker-compose-postgres-peripherals.yml`: Start only the peripheral applications (e.g. Kafka, Keycloak), using a single separate Postgres instance as database

To run e.g. the postgres/full flavor:

```shell
cd realworld-v2-docker/docker-compose
docker-compose -f docker-compose-postgres.yml -p rwl up -d    # the first time
docker-compose -f docker-compose-postgres.yml -p rwl start    # to start
docker-compose -f docker-compose-postgres.yml -p rwl stop     # to stop
docker-compose -f docker-compose-postgres.yml -p rwl down     # to remove the containers, without removing the persistent volumes
docker-compose -f docker-compose-postgres.yml -p rwl down -v  # to remove the containers, also removing the persistent volumes
```

## Launching

You have to build first! At least `mvn ... install` is required.

Development launch, assuming the profile `realworld-v2-local` is defined in `settings.xml` as above:

```shell
cd realworld-v2-article-module/realworld-v2-article
mvn quarkus:dev -Particle-quarkus-dev,realworld-v2-local
```

Likewise for user:

```shell
cd realworld-v2-user-module/realworld-v2-user
mvn quarkus:dev -Puser-quarkus-dev,realworld-v2-local
```

## Launching through IDE

When launching through IDE make sure to:

1. Activate the profiles as above
2. Let the IDE "Resolve Workspace Artifacts" (this is IntelliJ - what about Eclipse, Netbeans?) - this way you no longer need to `mvn install`, just `mvn package`

## Using

Assuming you have started the `docker-compose-postgres` variant:

### Register a user to Keycloak

Go to http://localhost:8580/realms/realworld/account/ and enter the data for a user.
