.PHONY: all
all: audit test build

.PHONY: audit
audit:
	pushd src/main/web && npm audit --audit-level=high && popd

.PHONY: build
build:
	npm install --prefix src/main/web --unsafe-perm
	mvn -Dmaven.test.skip clean package dependency:copy-dependencies

.PHONY: debug-web
debug-web:
	./run.sh

.PHONY: debug-publishing
debug-publishing:
	./run-publishing.sh

.PHONY: test
test:
	mvn test