# AGENTS.md

Purpose
-------
This document provides minimal guidance for automated contributors (human-in-the-loop or AI assistants) working on the jMetal repository.

General principles
------------------
- Always follow the rules in `JAVA_CODING_GUIDELINES.md` when modifying Java sources or writing tests.
- Prioritise safety, readability, and reproducible tests over rapid or large-scale edits.
- Ask a human maintainer for clarification when a guideline conflicts with a special requirement.

Conventions for assistants
--------------------------
- Before changing code, read `README.rst` and `JAVA_CODING_GUIDELINES.md` to understand project layout and coding rules.
- Make small, atomic changes and document the purpose in the commit message.
- For changes that affect public APIs or behaviour, require human review and do not merge automatically.
- Avoid large refactors or dependency upgrades without an approved plan.

Tests and CI
-----------
- Run the test suite locally before proposing changes:

```bash
mvn test
```

- Generate coverage reports with JaCoCo:

```bash
mvn test jacoco:report
```

- If you change tests or the build configuration, provide clear reproduction steps in the PR description.

Commit messages and PRs
----------------------
- Use the repository commit style (prefixes: `feat:`, `fix:`, `chore:`, `test:`).
- In the commit/PR body include:
  - What changed
  - Why it changed
  - How to verify locally (commands)

Minimal PR checklist
--------------------
- [ ] Build locally: `mvn -DskipTests=false clean package`
- [ ] Run relevant tests: `mvn -Dtest=MyTest test`
- [ ] Verify `JAVA_CODING_GUIDELINES.md` rules are respected
- [ ] Update documentation if public behaviour changes

References
----------
- `JAVA_CODING_GUIDELINES.md` — Java style and practices for the project
- `README.rst` — project overview and module structure

Contact
-------
If in doubt or proposing large changes, open an issue describing the proposal and tag the maintainers.

---
This file may be extended with CI/formatter rules (Spotless, Checkstyle) as they are adopted in the repository.