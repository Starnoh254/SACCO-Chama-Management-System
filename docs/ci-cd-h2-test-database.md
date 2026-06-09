# Fixing CI/CD Build Failures Caused by PostgreSQL Dependency During Tests

## Problem

The CI/CD pipeline was failing during the Maven build step:

```bash
mvn clean package
```

The build logs showed the following error:

```text
Connection to localhost:5432 refused.
Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.
```

This occurred while running the test class:

```text
SaccoManagementApplicationTests
```

Spring Boot attempted to start the entire application context using `@SpringBootTest`, which triggered the initialization of:

* DataSource
* Hibernate
* JPA EntityManager
* Repository Beans

As part of this startup process, Spring attempted to connect to PostgreSQL using the application's datasource configuration.

Because the CI/CD runner did not have a PostgreSQL server running on `localhost:5432`, the connection failed, causing the application context initialization to fail.

Additional errors generated as a result included:

```text
Unable to obtain isolated JDBC connection
```

and

```text
Unable to determine Dialect without JDBC metadata
```

These were secondary errors caused by the inability to connect to PostgreSQL.

---

## Root Cause

The application was configured to use PostgreSQL for all environments, including automated test execution.

During CI/CD execution:

1. Maven executed the test phase.
2. Spring Boot loaded the application context.
3. Hibernate attempted to connect to PostgreSQL.
4. PostgreSQL was unavailable in the CI environment.
5. Context initialization failed.
6. Maven build failed.

---

## Solution Implemented

Instead of requiring a PostgreSQL server during test execution, an in-memory H2 database was configured specifically for tests.

This allows:

* Fast test execution
* No external database dependency
* Reliable CI/CD builds
* Isolation between test and production environments

---

## Step 1: Add H2 Dependency

Add the following dependency to `pom.xml`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Step 2: Create Test Configuration

Create the file:

```text
src/test/resources/application-test.properties
```

with the following configuration:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### Configuration Explanation

| Property               | Purpose                                            |
| ---------------------- | -------------------------------------------------- |
| `jdbc:h2:mem:testdb`   | Creates an in-memory database                      |
| `driver-class-name`    | H2 JDBC Driver                                     |
| `ddl-auto=create-drop` | Creates schema at startup and drops it after tests |
| `H2Dialect`            | Hibernate dialect for H2                           |

---

## Step 3: Activate the Test Profile

Update the test class:

```java
@SpringBootTest
@ActiveProfiles("test")
class SaccoManagementApplicationTests {
}
```

### What This Does

The annotation:

```java
@ActiveProfiles("test")
```

instructs Spring Boot to load:

```text
application-test.properties
```

instead of the default datasource configuration.

As a result:

* Tests use H2
* Development uses PostgreSQL
* Production uses PostgreSQL

---

## Benefits of This Approach

### Faster Builds

H2 runs entirely in memory and starts instantly.

### No Infrastructure Dependencies

The CI/CD pipeline no longer requires a running PostgreSQL instance.

### Environment Isolation

Test environments are separated from development and production databases.

### Reliable Automated Testing

Builds become more predictable because they do not depend on external services.

---

## Alternative Solutions Considered

### Option 1: Skip Tests

```bash
mvn clean package -DskipTests
```

Not selected because it removes automated verification.

### Option 2: H2 In-Memory Database

✅ Selected Solution

Provides automated testing without external dependencies.

### Option 3: Start PostgreSQL in CI/CD

Using GitHub Actions services:

```yaml
services:
  postgres:
    image: postgres:16
```

Useful for integration testing but introduces additional complexity and longer build times.

### Option 4: Disable Context Loading Tests

```java
@Disabled
@SpringBootTest
class SaccoManagementApplicationTests {
}
```

Not selected because application startup validation remains valuable.

---

## Final Outcome

The CI/CD pipeline was failing because Spring Boot tests attempted to connect to PostgreSQL running on:

```text
localhost:5432
```

which was unavailable in the CI environment.

The issue was resolved by introducing an H2 in-memory database specifically for test execution and activating it through a dedicated Spring test profile.

This allows:

* Successful CI/CD builds
* Faster test execution
* No dependency on PostgreSQL during automated testing
* Clear separation between testing and production environments

```
```
