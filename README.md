# jbst
jbst — java17 bootstrapping (mostly spring) tools

### Build
The project ships with the Maven Wrapper (`./mvnw`, pinned to Maven 3.9.9) — no local Maven installation required

### Tests
`./mvnw test` executes only unit tests  
`./mvnw integration-test` executes all tests  
`./mvnw failsafe:integration-test` runs only integration tests  
`./mvnw clean verify` when you want to be sure, that whole project just works  

### Swagger
URL-dev: http://localhost:3002/api/swagger-ui/index.html  
URL-prod: http://{server:port}/api/swagger-ui/index.html

