---
name: seedu-java-coding-standard
description: The se-education Java coding standard (basic + intermediate rules) that all Java code in this project must follow. Use when writing, reviewing, refactoring, or reformatting any Java code, when checking a file for style compliance, or when asked about naming, layout, braces, imports, or Javadoc conventions in this project.
---

# seedu Java Coding Standard

Source: https://se-education.org/guides/conventions/java/intermediate.html (basic + intermediate rules).
All Java code in this repository must follow these rules. For anything not covered here, fall back to
the Google Java Style Guide.

## Naming

| Element | Rule | Good | Bad |
|---|---|---|---|
| Package | all lower case | `todobuddy.ui` | `todoBuddy.UI` |
| Class / Enum | noun, PascalCase | `Line`, `AudioSystem` | `line`, `audio_system` |
| Variable | camelCase | `line`, `audioSystem` | `Line`, `audio_system` |
| Constant | ALL_CAPS with underscores | `MAX_ITERATIONS` | `maxIterations` |
| Method | verb, camelCase | `getName()`, `computeTotalWidth()` | `name()`, `GetName()` |

- **Abbreviations are not uppercased** inside names: `exportHtmlSource()`, not `exportHTMLSource()`.
- **All names in English.**
- **Scope governs length**: large scope gets a long name; small scope may be short. Scratch variables
  (`i`, `j`, `k`, `m`, `n`, `c`, `d`) are acceptable for temporary use only.
- **Booleans** read as predicates — prefix with `is`, `has`, `was`: `isSet`, `isVisible`, `hasData`.
  Setter parameter form: `void setFound(boolean isFound)`.
- **Collections take the plural**: `Collection<Point> points`.
- **Related constants share a prefix**: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.
- **Test methods** use three parts: `featureUnderTest_testScenario_expectedBehavior()`.

## Layout

- **Indentation is 4 spaces, never tabs.**
- **Line length**: soft limit 110 characters, hard limit 120.
- **Wrapped lines indent 8 spaces** (double the normal indent).
- **Break lines** after commas, and *before* operators and operator-like symbols (`.`, `&`, `|`).
  Keep a method or constructor name attached to its opening parenthesis. Prefer breaking at a
  higher syntactic level over a lower one.
- **Ternary operators** go either entirely on one line or across three lines.
- **K&R ("Egyptian") braces** — the opening brace stays on the line that opens the block:

  ```java
  public void someMethod() throws SomeException {
      ...
  }

  if (condition) {
      statements;
  } else {
      statements;
  }

  for (initialization; condition; update) {
      statements;
  }

  while (condition) {
      statements;
  }

  do {
      statements;
  } while (condition);

  try {
      statements;
  } catch (Exception exception) {
      statements;
  } finally {
      statements;
  }
  ```

- **Intentional switch fallthrough** must carry a `// Fallthrough` comment.
- **Whitespace**: surround operators with spaces; follow reserved words with a space; follow commas
  with a space; surround `:` with spaces when binary or ternary; follow the `;` in a `for` header
  with a space.
- **Blank lines** separate logical units within a block — one blank line, not several.

## Statements

- **Put every class in a package.** (This repository is still in the default package; that is a
  deliberate, temporary state until the packaging increment. Do not introduce packages without
  asking.)
- **Import order must be consistent**: static imports, then `java.*`, `javax.*`, `org.*`, `com.*`,
  `javafx.*`.
- **List imports explicitly** — never use wildcard imports.
- **Array brackets attach to the type**: `int[] a`, not `int a[]`.
- **Initialize variables where they are declared**, in the smallest possible scope.
- **Class variables are never `public`**, unless the class is a pure data class with no behaviour.
- **Always brace loop and conditional bodies**, even a single statement on one line.
- **Put the conditional on its own line.**

## Comments and Javadoc

- **All comments in English.**
- **Header comments are required** for every public class and public method. They may be omitted for
  getters and setters, for overridden methods where the parent's Javadoc applies exactly, and for
  test classes and methods.
- **Javadoc format:**

  ```java
  /**
   * First sentence is the short summary.
   *
   * @param name Description, ending with punctuation.
   * @return Description.
   * @throws ExceptionType Description.
   */
  ```

  - `/**` sits on its own line; a space follows each `*`.
  - One empty line between the description and the block tags.
  - Parameter and return descriptions end with punctuation.
  - No blank line between the Javadoc block and the code it documents.
  - Omit `@return` for `void` methods.
  - `@param` is all-or-none: document every parameter or none of them.
  - A single-line Javadoc is acceptable for member variables.
- **Indent comments to match the code** they describe.

## How to apply this in this project

When writing new Java code, follow the rules above as you write — do not write first and reformat
after. When asked to bring existing code into compliance:

1. Check the whole file against this checklist, in this order: naming → layout → statements →
   comments.
2. Make only style changes. Never mix a behaviour change into a compliance pass; if you spot a bug,
   report it separately rather than fixing it in the same commit.
3. Verify the code still compiles: `javac -d bin src/main/java/*.java`.
4. Verify behaviour is unchanged by running the text UI tests (see the `run-text-ui-test` skill if
   present, or run the app and compare output).
5. Present the result with the `present-changes-visually` skill so the user can review it.

Remember that this project also mandates a consistent butler voice for all user-facing strings — see
AGENTS.md. A style pass must not flatten that voice into generic wording.
