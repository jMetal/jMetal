# JAVA_CODING_GUIDELINES.md

## Purpose
This document defines Java coding standards for projects using Java 21+ and Maven. These guidelines work in conjunction with `AGENTS.md` and should be followed by all contributors and AI assistants.

---

## Language and Comments

### ✅ DO
- Write all source code, identifiers (packages, classes, methods, variables), comments, commit messages and PR descriptions in English.
- Use consistent English spelling (prefer US English) across the project.

### ❌ DON'T
- Use non-English comments, identifiers, or documentation text in the repository.
- Mix languages inside comments or docs that are intended to be consumed by contributors.


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

## 2. Sealed Classes for Controlled Hierarchies

### ✅ DO
- Use `sealed interface/class` for known and limited type hierarchies
- Combine with pattern matching in switch expressions
- Explicitly permit all subtypes

```java
public sealed interface PaymentMethod permits CreditCard, PayPal, BankTransfer {}

public record CreditCard(String number) implements PaymentMethod {}
public record PayPal(String email) implements PaymentMethod {}
public record BankTransfer(String iban) implements PaymentMethod {}
```

### ❌ DON'T
- Use open hierarchies with cascading `instanceof` checks
- Allow unlimited subclassing when types are known

---

## 3. Pattern Matching and Switch Expressions

### ✅ DO
- Use pattern matching with `switch` and `instanceof`
- Use switch expressions that return values
- Leverage exhaustiveness checking with sealed types

```java
String processPayment(PaymentMethod method) {
    return switch (method) {
        case CreditCard(var number) -> "Processing credit card: " + number;
        case PayPal(var email) -> "Processing PayPal: " + email;
        case BankTransfer(var iban) -> "Processing transfer: " + iban;
    };
}
```

### ❌ DON'T
- Use if-else chains with `instanceof` and manual casting
- Use traditional switch statements when expressions are cleaner

---

## 4. Optional Instead of null

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

## 5. Streams API

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

## 6. Try-with-Resources

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

## 7. Single Return Point + Guard Clauses

### ✅ DO
- Prefer guard clauses and early returns for validations and fast-fail checks; they improve readability by avoiding deep nesting.
- Keep the method body focused on the primary flow after validation.
- Use a single return at the end only when it improves clarity (for example, when assembling a complex result); do not force a single-return style at the cost of readability.

```java
public String processOrder(Order order) {
    if (order == null) {
        return "Invalid order";
    }
    if (!order.isValid()) {
        return "Order validation failed";
    }
    if (order.isEmpty()) {
        return "Empty order";
    }

    return fulfillOrder(order);
}
```

### ❌ DON'T
- Create deeply nested if-else pyramids (pyramid of doom).
- Force a single return point when guard clauses would make the logic clearer.
- Mix validation logic with business logic; extract validation where it improves readability.
- Create nested if-else pyramids (pyramid of doom)
- Mix validation logic with business logic

---

## 8. Single Responsibility Principle

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

## 9. Complete Javadoc

### ✅ DO
- Document all public classes, interfaces, and methods
- Include description, parameters, return values, and exceptions
- Use `@param`, `@return`, `@throws` tags appropriately
- Write clear, concise descriptions

```java
/**
 * Retrieves a user by their unique identifier.
 *
 * @param userId the unique identifier of the user
 * @return an Optional containing the user if found, empty otherwise
 * @throws IllegalArgumentException if userId is null or blank
 */
public Optional<User> findUserById(String userId) {
    if (userId == null || userId.isBlank()) {
        throw new IllegalArgumentException("User ID cannot be null or blank");
    }
    return userRepository.findById(userId);
}
```

### ❌ DON'T
- Leave public APIs undocumented
- Write vague or redundant documentation
- Forget to update Javadoc when changing method signatures

---

## 10. Specific Exceptions

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

## 11. Immutability by Default

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

## 12. Variable Naming with `var`

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

## 13. Maven Project Structure

### ✅ DO
- Follow standard Maven directory layout
- Keep `pom.xml` organized with properties for versions
- Use dependency management for version control
- Group dependencies logically with comments

```xml
<properties>
    <java.version>21</java.version>
    <junit.version>5.10.1</junit.version>
    <spring.version>6.1.0</spring.version>
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

## 14. Testing

### ✅ DO
- Write tests using JUnit 5
- Use Given-When-Then naming convention for test methods
- Use `@DisplayName` for readable test descriptions
- Use `@Nested` classes to group related tests
- Follow AAA pattern (Arrange, Act, Assert)
    - Aim for appropriate, module-specific coverage targets (see Coverage policy)
- Test both happy paths and edge cases

```java
@DisplayName("User Service Tests")
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
        @DisplayName("Given valid ID, when user exists, then return user")
        void givenValidId_whenUserExists_thenReturnUser() {
            // Given
            String userId = "123";
            User expectedUser = new User(userId, "John Doe");
            when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
            
            // When
            Optional<User> result = userService.findUserById(userId);
            
            // Then
            assertTrue(result.isPresent());
            assertEquals(expectedUser, result.get());
        }
        
        @Test
        @DisplayName("Given valid ID, when user does not exist, then return empty")
        void givenValidId_whenUserDoesNotExist_thenReturnEmpty() {
            // Given
            String userId = "999";
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            
            // When
            Optional<User> result = userService.findUserById(userId);
            
            // Then
            assertTrue(result.isEmpty());
        }
        
        @Test
        @DisplayName("Given null ID, when finding user, then throw exception")
        void givenNullId_whenFindingUser_thenThrowException() {
            // Given
            String userId = null;
            
            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> userService.findUserById(userId));
        }
    }
    
    @Nested
    @DisplayName("When creating new user")
    class CreateUser {
        
        @Test
        @DisplayName("Given valid data, when creating user, then save and return user")
        void givenValidData_whenCreatingUser_thenSaveAndReturnUser() {
            // Given
            UserDTO dto = new UserDTO("Jane Doe", "jane@example.com");
            User expectedUser = new User("456", "Jane Doe");
            when(userRepository.save(any(User.class))).thenReturn(expectedUser);
            
            // When
            User result = userService.createUser(dto);
            
            // Then
            assertNotNull(result);
            assertEquals(expectedUser.name(), result.name());
            verify(userRepository, times(1)).save(any(User.class));
        }
        
        @Test
        @DisplayName("Given invalid email, when creating user, then throw exception")
        void givenInvalidEmail_whenCreatingUser_thenThrowException() {
            // Given
            UserDTO dto = new UserDTO("Jane Doe", "invalid-email");
            
            // When & Then
            assertThrows(InvalidEmailException.class, 
                () -> userService.createUser(dto));
        }
    }
}
```

### Test Naming Convention

Follow the **Given-When-Then** pattern:
- **Given**: Initial context/preconditions
- **When**: The action being tested
- **Then**: Expected outcome

Method naming format: `given[Context]_when[Action]_then[Outcome]`

### Using @Nested and @DisplayName

- Use `@Nested` to group tests by functionality or scenario
- Use `@DisplayName` at class level to describe the test suite
- Use `@DisplayName` at method level for human-readable test descriptions
- Nested classes improve test report readability and organization

### ❌ DON'T
- Write tests without clear Given-When-Then structure
- Use vague test method names like `test1()`, `testUser()`
- Skip edge cases and error scenarios
- Write tests that depend on execution order
- Test multiple unrelated things in a single test method

---

## Review Checklist

Before submitting code, verify:
- [ ] Records used for DTOs and value objects
- [ ] Sealed classes used for controlled hierarchies
- [ ] Pattern matching used instead of instanceof chains
- [ ] Optional returned instead of null
- [ ] Streams used for collection operations
- [ ] Try-with-resources used for all closeable resources
- [ ] Single return point per method
- [ ] Each method has single responsibility
- [ ] Complete Javadoc for public APIs
- [ ] Specific custom exceptions used
- [ ] Fields are final where possible
- [ ] Tests follow Given-When-Then naming
- [ ] Tests use @DisplayName and @Nested appropriately

- [ ] All code, identifiers and comments are written in English

- [ ] Test coverage: module-specific targets set (see Coverage policy)

---

## Coverage policy

To keep expectations realistic and actionable across a multi-module repository, use module-specific coverage targets and measure with JaCoCo.

- Recommended defaults (adjust per module):
    - Core algorithm modules: 80% line coverage
    - Utility and library modules: 85% line coverage
    - Integration/IT-heavy modules: 65–75% line coverage

- Measurement and enforcement:
    - Add JaCoCo reporting to `pom.xml` for each module or in the parent `pom.xml`.
    - Use the Maven `jacoco:report` goal to generate reports locally.
    - Enforce thresholds in CI (e.g., Maven `jacoco:check`) with module-specific limits.

Example to run locally:

```bash
mvn test jacoco:report
```

CI suggestion: fail the pipeline only if a module drops below its agreed threshold; prefer warnings for marginal changes and require a remediation plan when coverage decreases significantly.


---

## Integration with AGENTS.md

When AI assistants work on this codebase:
1. Follow ALL guidelines in this document
2. Reference AGENTS.md for project-specific context and conventions
3. Prioritize code quality and readability over brevity
4. Ask for clarification when guidelines conflict with specific requirements
5. When writing tests, always use Given-When-Then naming and @DisplayName
6. Organize related tests using @Nested classes

---

*This document should be reviewed and updated as Java evolves and project needs change.*