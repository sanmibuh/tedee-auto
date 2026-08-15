---
name: tdd
description: Guide the developer through strict TDD mini-steps (Red → Green → Refactor), pausing after each step for developer review and commit before continuing.
license: GPL-3.0
compatibility: opencode
---

# TDD Skill — Red → Green → Refactor

## Core principle

Work in the **smallest possible steps**. Each mini-step must be **independently committable**. Never write implementation before a failing test exists.

---

## The three phases

### 🔴 RED — Write a failing test

1. Identify the **single next behavior** to implement (one assertion, one case).
2. Write **only** the test. No implementation yet.
3. Run the test suite and confirm the new test **fails** (and only that test).
4. Show the failure output to the developer.
5. **STOP.** Suggest a commit message and wait:

   > **Suggested commit:** `test: <describe the behavior being tested>`
   >
   > Please review the test, commit it, and tell me to continue.

---

### 🟢 GREEN — Make the test pass

1. Write the **minimum code** to make the failing test pass.
   - No extra logic, no anticipating future cases.
   - Hardcoding is acceptable if it makes the test green.
2. Run the full test suite and confirm **all tests pass**.
3. **STOP.** Suggest a commit message and wait:

   > **Suggested commit:** `feat: <describe the behavior implemented>`
   >
   > Please review the implementation, commit it, and tell me to continue.

---

### 🔵 REFACTOR — Improve without changing behavior

Trigger this phase when **any of these conditions are met**:

- The same structural pattern appears **3 or more times** across the codebase.
- There is obvious duplication introduced in the last GREEN step.
- A class or method has grown beyond a single responsibility.

Steps:
1. Identify the duplication or design smell explicitly.
2. Propose the refactor and explain why.
3. Apply the refactor. **All tests must stay green.**
4. Run the full test suite and confirm nothing broke.
5. **STOP.** Suggest a commit message and wait:

   > **Suggested commit:** `refactor: <describe what was cleaned up>`
   >
   > Please review the refactor, commit it, and tell me to continue.

---

## Pattern detection rule

After each GREEN step, scan for repeated structural patterns (e.g., same boilerplate in multiple tests, same conditional block in multiple classes, same mapping logic). If the **same pattern appears 3+ times**, insert a REFACTOR step before the next RED step.

---

## Rules for this project (tedee-automation)

- Tests use `BDDAssertions`: `then(...)` / `thenThrownBy(...)`. Never `assertThat`.
- Multiple assertions → `BDDSoftAssertions` via `@InjectSoftAssertions`.
- Test naming: `should_doSomething_whenCondition`.
- Use `@SneakyThrows` instead of `throws` on test methods.
- `domain` layer must have zero Spring or infrastructure imports.
- Run `./mvnw spotless:apply` before any GREEN commit to auto-format.
- Run `./mvnw verify` to confirm all checks pass.

---

## Conversation checkpoints

After **every** phase (RED, GREEN, or REFACTOR):
1. Show the diff of what was written.
2. Show test output (pass/fail summary).
3. Suggest a precise commit message.
4. Explicitly say: **"Waiting for your review and commit. Reply 'continue' when ready."**

Do **not** proceed to the next phase until the developer confirms.
