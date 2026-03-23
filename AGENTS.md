# AGENTS.md

Purpose
-------
This document provides guidance for automated contributors (human-in-the-loop or AI assistants) working on the jMetal repository. jMetal is a Java-based framework for multi-objective optimization with metaheuristics.

## Project Structure

```
jMetal/
├── jmetal-core/        # Core classes (Algorithm, Solution, Problem, Operator)
├── jmetal-algorithm/   # Algorithm implementations
├── jmetal-problem/     # Benchmark problems
├── jmetal-lab/         # Experimentation and visualization
├── jmetal-parallel/    # Parallel extensions
├── jmetal-auto/        # Auto-design and configuration
└── jmetal-component/   # Component-based algorithms
```

**Java Version:** 21 (requires JDK 21+)
**Build Tool:** Maven

---

## Build Commands

```bash
# Build entire project (skip tests)
mvn clean package -DskipTests

# Build with tests
mvn clean test

# Run a single test class
mvn test -Dtest=MyTestClass

# Run a single test method
mvn test -Dtest=MyTestClass#myTestMethod

# Run tests in a specific module
mvn test -pl jmetal-core

# Run tests in specific module with dependencies
mvn test -pl jmetal-core -am

# Run integration tests
mvn integration-test

# Generate coverage report
mvn test jacoco:report
```

---

## Code Style Guidelines

**ALWAYS follow the rules in `JAVA_CODING_GUIDELINES.md`.** Key points:

### Language
- Write all code, comments, and documentation in **English** (US spelling)

### Java 21+ Features
- Use **records** for immutable DTOs and value objects
- Use **sealed interfaces/classes** for controlled type hierarchies
- Use **pattern matching** with switch expressions
- Use **Optional<T>** instead of null returns

### Naming Conventions
- Classes: `UpperCamelCase` (e.g., `MyAlgorithm`)
- Methods/variables: `lowerCamelCase` (e.g., `myMethod`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_ITERATIONS`)
- Test classes: `*Test.java` (e.g., `NSGAIIITest`)

### Imports
- Group in order: `java.*`, `javax.*`, `org.*`, `com.*`, `static` imports
- Use **static imports** for test assertions and fluent APIs

### Testing Conventions
- Use **JUnit 5** (Jupiter)
- Use **AssertJ** for fluent assertions
- Use **Mockito** for mocking
- Test naming: descriptive, focus on behavior
- Follow **Given-When-Then** pattern in test names
- Use `@Nested` classes to group related tests
- Use `@DisplayName` for human-readable descriptions

### Error Handling
- Use **specific exceptions** (not generic RuntimeException)
- Provide descriptive error messages with context
- Use **guard clauses** for early validation returns

### Code Quality
- **Single Responsibility**: methods < 20 lines
- **Complete Javadoc** for all public APIs
- **Immutability** by default (final fields, immutable collections)
- Use **try-with-resources** for all Closeable resources
- **No magic numbers** - use named constants

---

## Commit Messages

Prefix format: `feat:`, `fix:`, `chore:`, `test:`, `docs:`, `refactor:`

Example:
```
feat: add RVEA algorithm implementation

- Added Reference Vector Guided Evolutionary Algorithm
- Includes adaptive scalarization approach
- Tests: mvn test -Dtest=RVEAIT
```

---

## Pull Request Checklist

- [ ] Build: `mvn -DskipTests=false clean package`
- [ ] Test: `mvn test -Dtest=MyTest test`
- [ ] Coverage: `mvn test jacoco:report` (verify coverage maintained)
- [ ] Code follows `JAVA_CODING_GUIDELINES.md`
- [ ] Documentation updated if public API changes
- [ ] Commit message follows convention

---

## Important Notes

- **DO NOT** commit secrets, API keys, or credentials
- **DO NOT** skip tests in pull requests without justification
- **DO ASK** for clarification when guidelines conflict with requirements
- Large refactors or dependency upgrades need approval

---

## References

- `JAVA_CODING_GUIDELINES.md` — Detailed Java style guide
- `README.rst` — Project overview
- pom.xml — Build configuration

---

*Last updated: March 2026*
