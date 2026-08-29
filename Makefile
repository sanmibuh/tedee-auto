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
	docker build -t tedee-automation .
