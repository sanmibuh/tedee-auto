.PHONY: help build test pitest

help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "  build   Compile source code"
	@echo "  test    Run tests and generate JaCoCo coverage report"
	@echo "  pitest  Run incremental mutation testing"

build:
	./mvnw compile -B

test:
	./mvnw verify -B

pitest:
	@mkdir -p .pit
	@git fetch origin coverage-data 2>/dev/null || true
	@git show origin/coverage-data:pitest-history.bin > .pit/history-input.bin 2>/dev/null || true
	./mvnw pitest:mutationCoverage -B
