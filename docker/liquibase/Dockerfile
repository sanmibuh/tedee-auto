# Ephemeral migration image: applies db/changelog to the H2 database on the shared data volume, then exits.
FROM liquibase/liquibase:5.0.4

# Required: pass the version resolved from pom.xml (see .github/scripts/build-liquibase-image.sh).
ARG H2_VERSION

LABEL h2.version=${H2_VERSION}

ADD --chown=liquibase:liquibase --chmod=644 \
  https://repo1.maven.org/maven2/com/h2database/h2/${H2_VERSION}/h2-${H2_VERSION}.jar \
  /liquibase/lib/h2.jar

COPY --chown=liquibase:liquibase db/changelog /liquibase/changelog

# Make the installation readable so the container can run as the application's UID (65532) and share the data volume.
USER root
RUN chmod 755 /liquibase
USER liquibase

ENV LIQUIBASE_COMMAND_CHANGELOG_FILE=db.changelog-master.yaml \
    LIQUIBASE_COMMAND_URL=jdbc:h2:file:/data/tedee

CMD ["update"]
