# AGENTS.md

Purpose
-------
This document provides guidance for automated contributors (human-in-the-loop or AI assistants) working on the jMetal repository. jMetal is a Java-based framework for multi-objective optimization with metaheuristics.

For build commands, architecture, code style, testing conventions, and commit rules, see **`CLAUDE.md`** — it is the authoritative technical reference for this repository.

---

## Code Style

**ALWAYS follow the rules in `JAVA_CODING_GUIDELINES.md`.** The full detail is there; the short version:

- Google Java Style enforced via checkstyle (`mvn validate`)
- Java 21+: records, pattern matching, `Optional<T>`, switch expressions, streams, immutable collections
- All code, comments, and documentation in **English** (US spelling)

---

## Commit Messages

**ALWAYS follow the rules in `GIT_GUIDELINES.md`.** The short version:

- Format: `<type>: <short imperative description>`
- Allowed types: `feat`, `fix`, `test`, `refactor`, `docs`, `chore`
- One logical change per commit — never mix production code, tests, and docs
- The project must build and all tests must pass before committing

Example:
```
feat: add RVEA algorithm implementation
```

---

## Pull Request Checklist

- [ ] Build passes: `mvn -DskipTests=false clean package`
- [ ] Style passes: `mvn validate`
- [ ] Coverage maintained: `mvn test jacoco:report`
- [ ] Code follows `JAVA_CODING_GUIDELINES.md`
- [ ] Commit messages follow `GIT_GUIDELINES.md`
- [ ] Documentation updated if public API changes

---

## Important Notes

- **DO NOT** commit secrets, API keys, or credentials
- **DO NOT** skip tests in pull requests without justification
- **DO ASK** for clarification when guidelines conflict with requirements
- Large refactors or dependency upgrades need approval

---

## References

- `CLAUDE.md` — Build commands, architecture, code style, testing, commit conventions
- `JAVA_CODING_GUIDELINES.md` — Detailed Java style guide
- `GIT_GUIDELINES.md` — Conventional Commits rules, atomic commit policy, allowed types
- `README.rst` — Project overview
- `pom.xml` — Build configuration

---

*Last updated: June 2026*
