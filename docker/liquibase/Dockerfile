# Ephemeral migration image: applies db/changelog to the SQLite database on the shared data volume, then exits.
FROM liquibase/liquibase:5.0.4

ARG SQLITE_JDBC_VERSION=3.53.2.1

ADD --chown=liquibase:liquibase \
  https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/${SQLITE_JDBC_VERSION}/sqlite-jdbc-${SQLITE_JDBC_VERSION}.jar \
  /liquibase/lib/sqlite-jdbc.jar

COPY --chown=liquibase:liquibase db/changelog /liquibase/changelog

ENV LIQUIBASE_COMMAND_CHANGELOG_FILE=db.changelog-master.yaml \
    LIQUIBASE_COMMAND_URL=jdbc:sqlite:/data/tedee.db

CMD ["update"]
