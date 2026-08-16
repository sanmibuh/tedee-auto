.PHONY: help build clean format test pitest image

help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "  build   Compile source code"
	@echo "  clean   Remove build artifacts"
	@echo "  format  Apply Spotless code formatter"
	@echo "  test    Run tests and generate JaCoCo coverage report"
	@echo "  pitest  Run incremental mutation testing"
	@echo "  image   Build GraalVM native Docker image"

build:
	./mvnw compile -B

clean:
	./mvnw clean -B

format:
	./mvnw spotless:apply -B

test:
	./mvnw verify -B

pitest:
	@mkdir -p .pit
	@git fetch origin coverage-data 2>/dev/null || true
	@git show origin/coverage-data:pitest-history.bin > .pit/history-input.bin 2>/dev/null || true
	./mvnw pitest:mutationCoverage -B

image:
	docker build -t tedee-automation .
