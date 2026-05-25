# Git and Commit Guidelines

This project follows the [Conventional Commits](https://www.conventionalcommits.org/) specification.

## Message format

```
<type>: <short imperative description>
```

## Allowed types

| Type       | When to use                                          |
|------------|------------------------------------------------------|
| `feat`     | A new feature or public method                       |
| `fix`      | A bug fix                                            |
| `test`     | Adding or correcting tests (no production code)      |
| `refactor` | Code change that is neither a fix nor a new feature  |
| `docs`     | Documentation only (`README.md`, `AGENTS.md`, etc.)  |
| `chore`    | Build config, dependencies, `.gitignore`, etc.       |

## Atomic commits

Each commit must represent **one single logical change**. Guidelines:

- If the commit message needs "and" to describe what it does, split it into two commits.
- Ensure the project builds and all tests pass before committing (e.g., `mvn clean test`).
- Never mix production code changes with test changes in the same commit.
- Never mix code changes with documentation changes in the same commit.

## Examples

```bash
# Good
git commit -m "feat: add isEven method to MathUtils"
git commit -m "test: add tests for MathUtils.isEven"
git commit -m "fix: throw ArithmeticException on divide by zero"
git commit -m "docs: add README.md"

# Bad — too broad, mixes concerns
git commit -m "add stuff and fix tests and update readme"
```
