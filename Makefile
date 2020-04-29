.PHONY: all
all: audit test build

.PHONY: audit
audit : audit-java audit-js

.PHONY: audit-java
audit-java:
	mvn ossindex:audit

.PHONY: audit-js
audit-js:
	npm audit --prefix src/main/web --audit-level=high

.PHONY: build
build:
	npm install --prefix src/main/web --unsafe-perm
	mvn -Dmaven.test.skip -Dossindex.skip=true clean package dependency:copy-dependencies

.PHONY: debug-web
debug-web:
	./run.sh

.PHONY: debug-publishing
debug-publishing:
	./run-publishing.sh

.PHONY: test
test:
	mvn -Dossindex.skip=true test