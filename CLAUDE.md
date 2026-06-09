# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

jMetal is a Java 21 Maven framework for multi-objective optimization with metaheuristics (version 7.4-SNAPSHOT). All code, comments, and documentation must be written in English.

## Build Commands

```bash
# Build entire project (skip tests)
mvn clean package -DskipTests

# Build with tests
mvn clean test

# Validate code style (Google Java Style via checkstyle)
mvn validate

# Run a single test class
mvn test -Dtest=MyTestClass

# Run a single test method
mvn test -Dtest=MyTestClass#myTestMethod

# Run tests in a specific module
mvn test -pl jmetal-core

# Run tests in specific module with dependencies
mvn test -pl jmetal-core -am

# Run integration tests (classes ending in IT)
mvn integration-test

# Generate coverage report
mvn test jacoco:report
```

## Architecture

### Module Dependency Hierarchy

```
jmetal-core          ← Foundation: interfaces, abstractions, utilities
jmetal-problem       ← Benchmark problems (depends on jmetal-core)
jmetal-algorithm     ← Classic algorithm implementations (depends on jmetal-core, jmetal-problem)
jmetal-component     ← Component-based algorithms (depends on jmetal-core, jmetal-problem)
jmetal-auto          ← Auto-configuration (depends on jmetal-component)
jmetal-lab           ← Experimentation/visualization (depends on all above)
jmetal-parallel      ← Parallel extensions (depends on jmetal-core, jmetal-component)
```

### Core Abstractions (`jmetal-core`)

- **`Algorithm<R>`** — top-level interface: `run()`, `result()`, `name()`, `description()`
- **`Problem<S>`** — defines `evaluate(S)`, `numberOfVariables()`, `numberOfObjectives()`, `numberOfConstraints()`
- **`Solution`** subtypes: `DoubleSolution`, `IntegerSolution`, `BinarySolution`, `PermutationSolution`, `CompositeSolution`
- **`Operator`** subtypes: crossover, mutation, selection, local search operators
- Abstract algorithm skeletons in `algorithm/impl/`: `AbstractEvolutionaryAlgorithm`, `AbstractGeneticAlgorithm`, `AbstractDifferentialEvolution`, `AbstractParticleSwarmOptimization`

### Two Algorithm Styles

**Classic style** (`jmetal-algorithm`): monolithic algorithm classes in `algorithm/multiobjective/` (nsgaii, moead, smpso, spea2, ibea, etc.). Each algorithm has its own concrete class. Use these to study existing behavior or add minor variants.

**Component-based style** (`jmetal-component`): preferred for new work. Algorithms are composed from interchangeable catalogue components:
- `catalogue/common/`: `Evaluation`, `Termination`, `SolutionsCreation`
- `catalogue/ea/`: `Selection`, `Variation`, `Replacement`
- `catalogue/pso/`: PSO-specific catalogue components
- `algorithm/multiobjective/`: `NSGAIIBuilder`, `MOEADBuilder`, `SMSEMOABuilder`, `RVEABuilder`, etc.

Each builder wires catalogue components into an `EvolutionaryAlgorithm` or `ParticleSwarmOptimizationAlgorithm` instance. To add a new component-based algorithm: create a Builder class that constructs an `EvolutionaryAlgorithm` with the appropriate catalogue components.

### Auto-Configuration (`jmetal-auto`)

`AutoConfigurableAlgorithm` implementations (e.g., `AutoNSGAII`, `AutoMOEAD`) expose algorithm parameters as a space that can be searched by an irace/SMAC-style configurator. They internally build component-based algorithms from parameter strings.

### Observer Pattern

Algorithms are observable via `Observable`/`Observer` in `util/observer/`. Observers (e.g., `RunTimeChartObserver`, `WriteSolutionsToFilesObserver`, `FitnessObserver`) register on an algorithm and receive notifications each generation. Examples in `jmetal-component/examples/` show how to attach observers.

### Quality Indicators

`QualityIndicator` implementations in `jmetal-core/qualityindicator/impl/`: Hypervolume, IGD, IGD+, GD, Epsilon, Spread, R2, SetCoverage, AverageHausdorffDistance. Used directly or via `QualityIndicatorUtils`.

### Experimentation (`jmetal-lab`)

Used for running experiments across multiple algorithms and problems, collecting results, and computing statistical significance. Entry point is typically an `Experiment` builder.

## Code Style

The project enforces **Google Java Style** via `maven-checkstyle-plugin` (run `mvn validate` to check):
- 2-space indentation, Egyptian braces, 100-char column limit
- No wildcard imports; static imports before regular imports

Key Java 21 idioms required throughout:
- **Records** for immutable DTOs/value objects
- **`Optional<T>`** instead of null returns
- **Pattern matching** `instanceof` (no manual casts after instanceof)
- **Switch expressions** returning values
- **`final` fields** and `List.of()` / `Set.of()` / `Map.of()` for immutability
- **Streams** for collection transformations
- **Guard clauses** at method top, single return point for main logic

## Testing Conventions

Uses JUnit 5 (Jupiter), AssertJ, and Mockito. Follow these patterns strictly:

```java
@DisplayName("Unit tests for class ClassName")
class ClassNameTest {
    private ClassUnderTest subject;

    @BeforeEach
    void setUp() {
        subject = new ClassUnderTest(...);
    }

    @Nested
    @DisplayName("When <scenario>")
    class WhenScenario {
        @Test
        @DisplayName("given ..., when ..., then ...")
        void givenContext_whenAction_thenOutcome() {
            // Arrange
            // Act
            // Assert
        }
    }
}
```

- Mocks created manually in `@BeforeEach` with `mock(Type.class)` — never use `@Mock`/`@InjectMocks`
- Integration test classes use the `IT` suffix and run only via `mvn integration-test`
- `@ParameterizedTest` with `@ValueSource` / `@CsvSource` to avoid duplicated test methods

## Commit Conventions

Conventional Commits format: `<type>: <short imperative description>`

Allowed types: `feat`, `fix`, `test`, `refactor`, `docs`, `chore`

Each commit must represent **one logical change** — never mix production code, tests, and docs in the same commit. The project must build and all tests must pass before committing.
