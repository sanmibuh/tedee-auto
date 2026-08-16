.PHONY: help build test pitest image

help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "  build   Compile source code"
	@echo "  test    Run tests and generate JaCoCo coverage report"
	@echo "  pitest  Run incremental mutation testing"
	@echo "  image   Build GraalVM native Docker image"

build:
	./mvnw compile -B

test:
	./mvnw verify -B

pitest:
	@mkdir -p .pit
	@git fetch origin coverage-data 2>/dev/null || true
	@git show origin/coverage-data:pitest-history.bin > .pit/history-input.bin 2>/dev/null || true
	./mvnw pitest:mutationCoverage -B

image:
	docker build -t tedee-automation .
