Cloud-based Document Management System
====================

## What is that?

Document storage service backed by Azure Blob Storage that provides scalable management of document files (upload, download)

## Technology Stack

- `java`, `spring-boot`, `spring-web`
  [core]
- `git`, `maven`
  [code & build]
- `logback`, `filebeat`, `elastic-search`, `kibana`
  [log management]
- `swagger`, `http-client`, `junit`, `mockito`
  [testing] 
- `spring-actuator`, `prometheus`, `grafana`
  [monitoring]   
- `lombok`, `devtools`
  [development] 
- `docker`, `docker-compose`
  [containers]

## How to run

compile application:

```bash
mvn clean package
```

build docker image:

```bash
mvn com.google.cloud.tools:jib-maven-plugin:2.4.0:dockerBuild
```

run [application](http://localhost:8014/) or browse [swagger](http://localhost:8014/swagger-ui.html) documentation:

```bash
docker-compose -f app.yml up -d
```

check [kibana](http://localhost:5601) for logs:

```bash
docker-compose -f elk.yml up -d
```

view [grafana](http://localhost:3000) dashboard to monitor application:

```bash
docker-compose -f monitoring.yml up -d
```

run [sonar](http://localhost:9001) to check code quality and test coverage:

```bash
docker-compose -f sonar.yml up -d
mvn clean package -P dev,sonar
mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar -P dev,sonar
```

### application profiles

* **default**: localhost run
* **dev, qa, uat, prod**: dedicated profile for each environment you have
* **elk**: print logs to console in JSON format that can be efficiently shipped to elastic search
* **tls**: for testing secure connections using self-signed certificates

### build profiles

* **dev**: default profile, activates spring-boot-devtools for development environment
* **sonar**: activates jacoco for generating code coverage reports, and sonar for code feedback

