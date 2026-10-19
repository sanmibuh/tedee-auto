# Local development database directory (git-ignored); the database file is <DB_DIR>/tedee.mv.db.
DB_DIR ?= data

.PHONY: help build clean format test automation-test pitest image liquibase-image execute-migration create-migration db-drop

help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "  build   Compile source code"
	@echo "  clean   Remove build artifacts"
	@echo "  format  Apply Spotless code formatter"
	@echo "  test    Run tests and generate JaCoCo coverage report"
	@echo "  automation-test  Run GitHub automation script tests"
	@echo "  pitest  Run incremental mutation testing"
	@echo "  image   Build GraalVM native Docker image"
	@echo "  liquibase-image  Build the Liquibase migration image"
	@echo "  execute-migration  Apply db/changelog to $(DB_DIR)/tedee.mv.db with the Liquibase Maven plugin (DB_DIR=data by default)"
	@echo "  db-drop Delete the local database in $(DB_DIR)"
	@echo "  create-migration  Apply pending migrations, then generate a changeset from JPA entity changes (issue from the GH-<n> branch name)"

build:
	./mvnw compile -B

clean:
	./mvnw clean -B

format:
	./mvnw spotless:apply -B

test:
	./mvnw verify -B

automation-test:
	PYTHONDONTWRITEBYTECODE=1 python3 .github/tests/test_model_strategy.py

pitest:
	@mkdir -p .pit
	@if git fetch origin coverage-data 2>/dev/null \
	   && git show origin/coverage-data:pitest-history.bin > .pit/history-input.bin 2>/dev/null \
	   && [ -s .pit/history-input.bin ]; then \
	  echo ">> PITest: incremental run using history from origin/coverage-data"; \
	else \
	  rm -f .pit/history-input.bin; \
	  echo ">> PITest: no usable history in origin/coverage-data — running full baseline"; \
	fi
	./mvnw test-compile pitest:mutationCoverage -B
	@echo ">> PITest: updated history written to .pit/history-output.bin"

image:
	docker build -f docker/app/Dockerfile -t tedee-automation .

liquibase-image:
	.github/scripts/build-liquibase-image.sh tedee-liquibase

execute-migration:
	@mkdir -p $(DB_DIR)
	./mvnw -q -B -Pmigration liquibase:update -Ddb.path="$(abspath $(DB_DIR))/tedee"

db-drop:
	rm -f $(DB_DIR)/tedee.mv.db $(DB_DIR)/tedee.trace.db

create-migration: execute-migration
	DB_DIR="$(DB_DIR)" scripts/generate-migration.sh
