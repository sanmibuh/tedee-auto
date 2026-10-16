# Ephemeral migration image: applies db/changelog to the SQLite database on the shared data volume, then exits.
FROM liquibase/liquibase:5.0.4

# Required: pass the version resolved from pom.xml (see .github/scripts/build-liquibase-image.sh).
ARG SQLITE_JDBC_VERSION

LABEL sqlite-jdbc.version=${SQLITE_JDBC_VERSION}

ADD --chown=liquibase:liquibase \
  https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/${SQLITE_JDBC_VERSION}/sqlite-jdbc-${SQLITE_JDBC_VERSION}.jar \
  /liquibase/lib/sqlite-jdbc.jar

COPY --chown=liquibase:liquibase db/changelog /liquibase/changelog

ENV LIQUIBASE_COMMAND_CHANGELOG_FILE=db.changelog-master.yaml \
    LIQUIBASE_COMMAND_URL=jdbc:sqlite:/data/tedee.db

CMD ["update"]
