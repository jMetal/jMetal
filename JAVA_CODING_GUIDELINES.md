# JAVA_CODING_GUIDELINES.md

## Purpose
This document defines Java coding standards for projects using Java 21+ and Maven. These guidelines work in conjunction with `AGENTS.md` and should be followed by all contributors and AI assistants.

---

## 0. Code Formatting: Google Java Style

This project enforces the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) via `maven-checkstyle-plugin` with `google_checks.xml`. Formatting violations are reported during the `validate` phase (`mvn validate`).

### Key rules

| Rule | Value |
|---|---|
| Indentation | 2 spaces (no tabs) |
| Continuation indent | 4 spaces |
| Column limit | 100 characters |
| Braces | Egyptian style — opening brace on same line, always present even for single-statement blocks |
| Blank lines | 1 between members, 2 between top-level type declarations |
| Imports | No wildcard imports; static imports before regular imports; grouped and separated by a blank line |

### Naming conventions

| Element | Style | Example |
|---|---|---|
| Package | lowercase, no underscores | `org.uma.evolver.meta` |
| Class / Interface / Enum | UpperCamelCase | `MetaOptimizationProblem` |
| Method / Variable | lowerCamelCase | `evaluateSolution` |
| Constant (`static final`) | UPPER_SNAKE_CASE | `MAX_EVALUATIONS` |
| Type parameter | Single uppercase letter or UpperCamelCase + T | `T`, `SolutionT` |

### ✅ DO

```java
// 2-space indent, opening brace on same line, space after keyword
if (solution == null) {
  throw new IllegalArgumentException("Solution cannot be null");
}

// Continuation: 4-space indent
String result = someObject
    .methodA()
    .methodB();

// Constant naming
private static final int MAX_POPULATION_SIZE = 100;

// Grouped imports (static first, then regular, no wildcards)
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
```

### ❌ DON'T

```java
// Tabs or 4-space indent
if (solution == null)
{                          // Brace on new line — not allowed
    throw new ...;
}

// Wildcard imports
import org.uma.jmetal.solution.*;

// Constant in lowerCamelCase
private static final int maxPopulationSize = 100;
```

---

## 1. Records for DTOs and Value Objects

### ✅ DO
- Use `record` for immutable data classes
- Add validations in compact constructor when necessary
- Leverage automatic generation of constructor, getters, `equals()`, `hashCode()`, and `toString()`

```java
public record UserDTO(String name, String email) {
    public UserDTO {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
    }
}
```

### ❌ DON'T
- Create verbose classes with manual getters/setters/equals/hashCode
- Use mutable classes for simple data transfer objects

---

## 2. Pattern Matching and Switch Expressions

### ✅ DO
- Use pattern matching with `instanceof` to avoid manual casting
- Use switch expressions that return values instead of switch statements
- Use switch over enums for exhaustiveness

```java
// Pattern matching instanceof — no explicit cast needed
if (solution instanceof DoubleSolution ds) {
    double value = ds.variables().get(0);
}

// Switch expression returning a value
String label = switch (status) {
    case PENDING  -> "Waiting";
    case APPROVED -> "Done";
    case REJECTED -> "Failed";
};
```

### ❌ DON'T
- Use if-else chains with `instanceof` and manual casting
- Use traditional switch statements when a switch expression is cleaner

```java
// Bad: manual cast after instanceof
if (solution instanceof DoubleSolution) {
    DoubleSolution ds = (DoubleSolution) solution; // unnecessary cast
}
```

---

## 3. Optional Instead of null

### ✅ DO
- Return `Optional<T>` when a value may be absent
- Use fluent API: `.map()`, `.filter()`, `.flatMap()`, `.orElse()`, `.orElseThrow()`
- Make nullability explicit in method signatures

```java
Optional<User> findUserById(String id) {
    return userRepository.findById(id);
}

// Usage
String userName = findUserById("123")
    .map(User::name)
    .orElse("Unknown");
```

### ❌ DON'T
- Return `null` from methods
- Use manual null checks with `if (x == null)`
- Use `Optional` for fields or method parameters

---

## 4. Streams API

### ✅ DO
- Use streams for collection operations
- Prefer declarative over imperative style
- Use method references when possible
- Keep stream pipelines readable (max 3-4 operations)

```java
List<String> activeUserNames = users.stream()
    .filter(User::isActive)
    .map(User::name)
    .toList();
```

### ❌ DON'T
- Use for/while loops for simple transformations
- Create overly complex stream chains that harm readability
- Use streams for simple iterations (prefer enhanced for-loop)

---

## 5. Try-with-Resources

### ✅ DO
- Use try-with-resources for ALL closeable resources
- Use `var` to reduce verbosity when type is obvious
- Stack multiple resources in one try statement

```java
try (var connection = dataSource.getConnection();
     var statement = connection.prepareStatement(sql);
     var resultSet = statement.executeQuery()) {
    // Process results
}
```

### ❌ DON'T
- Close resources manually with `finally` blocks
- Forget to close any `AutoCloseable` resource

---

## 6. Single Return Point with Guard Clauses

### ✅ DO
- Use guard clauses at the top of the method to validate preconditions and fail fast
- After the guard clauses, the method body has a single return point at the end
- Declare a result variable at the start of the main logic when it aids clarity

```java
public String processOrder(Order order) {
    // Guard clauses: validate preconditions at the top
    if (order == null) {
        throw new IllegalArgumentException("Order cannot be null");
    }
    if (!order.isValid()) {
        return "Order validation failed";
    }

    // Main logic: single return point
    String result;
    if (order.isEmpty()) {
        result = "Empty order";
    } else {
        result = fulfillOrder(order);
    }
    return result;
}
```

### ❌ DON'T
- Return from the middle of the method body (after the guard clause section)
- Create nested if-else pyramids (pyramid of doom)
- Mix validation logic with business logic

---

## 7. Single Responsibility Principle

### ✅ DO
- Each method does ONE thing
- Keep cognitive complexity low (max 10)
- Use descriptive names that explain the purpose
- Extract complex logic into separate methods
- Aim for methods under 20 lines

```java
// Good: Each method has a single, clear responsibility
public void registerUser(UserDTO dto) {
    validateUserData(dto);
    User user = createUser(dto);
    saveUser(user);
    sendWelcomeEmail(user);
}
```

### ❌ DON'T
- Create methods that do validation + logic + email + logging all in one
- Write "god methods" that handle multiple responsibilities

---

## 8. Javadoc

### ✅ DO
- Write Javadoc for public classes and interfaces
- Document public methods when the name and signature alone do not fully convey intent, preconditions, or non-obvious behaviour
- Use `@param`, `@return`, `@throws` when they add information not already obvious from the signature

```java
/**
 * Evaluates a set of solutions on all training problems and returns the
 * aggregated quality indicator values. Modifies the solutions in place.
 *
 * @param solutions non-empty list of solutions to evaluate
 * @throws IllegalArgumentException if solutions is null or empty
 */
public void evaluate(List<S> solutions) { ... }
```

### ❌ DON'T
- Write Javadoc that just restates the method name (`/** Returns the name. */`)
- Add `@param` / `@return` tags whose content is already obvious from the type and name
- Leave Javadoc stale when changing method signatures

---

## 9. Specific Exceptions

### ✅ DO
- Create custom exceptions for different error types
- Provide descriptive messages with context
- Include relevant data in exception messages
- Use checked exceptions for recoverable errors
- Use unchecked exceptions for programming errors

```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }
}

public class InvalidEmailException extends IllegalArgumentException {
    public InvalidEmailException(String email) {
        super("Invalid email format: " + email);
    }
}
```

### ❌ DON'T
- Throw generic `Exception` or `RuntimeException`
- Use exceptions for control flow
- Swallow exceptions without logging or handling

---

## 10. Immutability by Default

### ✅ DO
- Make fields `final` whenever possible
- Use immutable collections: `List.of()`, `Set.of()`, `Map.of()`
- Return defensive copies of mutable collections if necessary

```java
public class OrderService {
    private final OrderRepository repository;
    private final List<String> allowedStatuses = List.of("PENDING", "APPROVED", "REJECTED");
    
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
    
    public List<String> getAllowedStatuses() {
        return allowedStatuses; // Already immutable, safe to return
    }
}
```

### ❌ DON'T
- Expose mutable collections from methods
- Use mutable fields when immutable would suffice

---

## 11. Variable Naming with `var`

### ✅ DO
- Use `var` when type is obvious from context
- Keep variable names descriptive when using `var`
- Use `var` for complex generic types to improve readability

```java
var users = userRepository.findAll(); // Type is clear from method name
var connection = dataSource.getConnection(); // Type is obvious
var result = new HashMap<String, List<UserDTO>>(); // Reduces verbosity
```

### ❌ DON'T
- Use `var` for primitives or when type is ambiguous
- Use `var` with unclear initializers like `var x = getResult();`

---

## 12. Maven Project Structure

### ✅ DO
- Follow standard Maven directory layout
- Keep `pom.xml` organized with properties for versions
- Use dependency management for version control
- Group dependencies logically with comments

```xml
<properties>
    <java.version>21</java.version>
    <junit.version>6.1.0</junit.version>
</properties>

<dependencies>
    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### ❌ DON'T
- Mix build logic with source code
- Hardcode dependency versions throughout the POM
- Leave dependencies without scope definition

---

## 13. Testing

### ✅ DO
- Write tests using JUnit 6 (Jupiter)
- Follow the Given-When-Then naming convention (see subsection below)
- Use `@Nested` and `@DisplayName` to structure tests (see subsection below)
- Follow AAA pattern (Arrange, Act, Assert) inside each test (see subsection below)
- Declare the class under test as an uninitialized field; create its instance in `@BeforeEach void setUp()`
- Use field initializers only for immutable test constants (`private final List<X> VALUES = List.of(...)`)
- Use `@BeforeEach` / `@AfterEach` for setup and teardown shared across all tests in a class
- Test both happy paths and edge cases
- Aim for high code coverage (>80%)

```java
@DisplayName("Unit tests for class UserService")
class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Nested
    @DisplayName("When finding user by ID")
    class FindUserById {

        @Test
        @DisplayName("given valid ID, when user exists, then return user")
        void givenValidId_whenUserExists_thenReturnUser() {
            // Arrange
            String userId = "123";
            User expectedUser = new User(userId, "John Doe");
            when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

            // Act
            Optional<User> result = userService.findUserById(userId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(expectedUser, result.get());
        }

        @Test
        @DisplayName("given null ID, when finding user, then throw IllegalArgumentException")
        void givenNullId_whenFindingUser_thenIllegalArgumentExceptionIsThrown() {
            // Arrange
            Executable executable = () -> userService.findUserById(null);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, executable);
        }
    }
}
```

### Test Naming Convention

Use **Given-When-Then** for method names and `@DisplayName`:
- **given**: initial context / preconditions
- **when**: the action being tested
- **then**: expected outcome

Method format: `given[Context]_when[Action]_then[Outcome]`  
`@DisplayName` format: `given [context], when [action], then [outcome]` (lowercase, sentence style)

### Internal structure: AAA

Inside each test method, use `// Arrange / Act / Assert` comments to delimit the three phases.  
When act and assert cannot be separated (e.g. `assertThrows`), use `// Act & Assert`.

### Using @Nested and @DisplayName

- Use `@Nested` to group tests by scenario or method under test.
- `@DisplayName` on the class: `"Unit tests for class ClassName"`
- `@DisplayName` on `@Nested`: `"When <scenario>"` (sentence describing the context)
- `@DisplayName` on `@Test`: `"given ..., when ..., then ..."` (lowercase GWT summary)

### Mocking with Mockito

Use only `mock()`, `when()`, and `verify()`. Create mocks manually in `@BeforeEach`, never via annotations.

- `mock(Type.class)` — create a mock dependency
- `when(mock.method(...)).thenReturn(value)` — stub a return value
- `verify(mock, times(n)).method(...)` — assert an interaction occurred

```java
UserRepository repo = mock(UserRepository.class);
when(repo.findById("123")).thenReturn(Optional.of(user));

// ... act ...

verify(repo, times(1)).findById("123");
```

### Parameterized tests

Use `@ParameterizedTest` to avoid duplicating test methods for multiple inputs:

```java
@ParameterizedTest
@ValueSource(strings = {"", " ", "  "})
@DisplayName("given blank ID, when finding user, then throw IllegalArgumentException")
void givenBlankId_whenFindingUser_thenIllegalArgumentExceptionIsThrown(String blankId) {
    assertThrows(IllegalArgumentException.class, () -> userService.findUserById(blankId));
}

@ParameterizedTest
@CsvSource({"john@example.com, true", "not-an-email, false"})
@DisplayName("given email, when validating, then return expected result")
void givenEmail_whenValidating_thenReturnExpectedResult(String email, boolean expected) {
    assertEquals(expected, userService.isValidEmail(email));
}
```

### Integration tests

- Name integration test classes with the `IT` suffix: `UserServiceIT`
- Run with `mvn integration-test` (not included in `mvn test`)
- Use for testing interactions with real infrastructure (database, file system, external APIs)
- Do not mix unit and integration tests in the same class

### ❌ DON'T
- Use "JUnit 4" style (`@RunWith`, `Assert.*` static imports from `org.junit`)
- Use vague test method names like `test1()`, `testUser()`, or `shouldDoSomething()`
- Initialize the class under test directly as a field initializer (`private X subject = new X(...)`) — always use `@BeforeEach` instead
- Use Mockito annotations (`@Mock`, `@InjectMocks`, `@Spy`, `@Captor`, `MockitoExtension`) — create mocks manually
- Skip edge cases and error scenarios
- Write tests that depend on execution order
- Test multiple unrelated things in a single test method

---

## Review Checklist

Before submitting code, verify:
- [ ] Code passes `mvn validate` (Google Java Style checkstyle)
- [ ] Records used for DTOs and value objects
- [ ] Pattern matching used instead of instanceof chains
- [ ] Optional returned instead of null
- [ ] Streams used for collection operations
- [ ] Try-with-resources used for all closeable resources
- [ ] Single return point per method (guard clauses at top are allowed)
- [ ] Each method has single responsibility
- [ ] Javadoc present where behaviour is non-obvious
- [ ] Specific custom exceptions used
- [ ] Fields are final where possible
- [ ] Tests follow §13 conventions (GWT naming, @DisplayName, @Nested, AAA, @BeforeEach, mocks manual)
- [ ] Test coverage is above 80%

---

## Integration with AGENTS.md

When AI assistants work on this codebase:
1. Follow ALL guidelines in this document
2. Reference AGENTS.md for project-specific context and conventions
3. Prioritize code quality and readability over brevity
4. Ask for clarification when guidelines conflict with specific requirements
5. Follow §13 for all testing conventions (naming, structure, mocking, parameterized tests, integration tests)

---

*This document should be reviewed and updated as Java evolves and project needs change.*