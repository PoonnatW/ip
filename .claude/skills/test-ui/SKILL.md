---
name: test-ui
description: Run CortisolBot against the text-UI test cases in test/ui-test-plan.md, comparing each test case's console output with its expected output and stopping at the first failure. Use after any change to the Java sources, when asked to test the bot, check its output, or verify that a refactoring changed nothing the user can see.
---

# Text-UI Testing

CortisolBot has no unit tests. This skill is the only regression safety net, so run it after **every**
change to `src/main/java`, including changes meant to be invisible such as a refactoring.

## Run the test session

From the repository root:

```powershell
python test/run-ui-tests.py
```

The script compiles the sources afresh into `_temp/ui-test-classes`, then for each test case in
`test/ui-test-plan.md` starts a new copy of the program in its own empty folder under
`_temp/ui-test-runs/`, types the test case's input lines, and compares everything printed against the
test case's expected output. `_temp/` is gitignored, so nothing it writes can enter a commit, and the
user's real `data/cortisolbot.txt` is never read or written.

Useful variations:

* One test case while fixing it: `python test/run-ui-tests.py --only TC-03`
* Several: `--only TC-03 --only TC-07`

## Report the result

Always show the user the record of the session, not just a verdict — the point of the exercise is
that the input and output can be inspected. The script prints, for every test case, the lines typed
and the console output they produced, and writes the same record to `_temp/ui-test-session.txt`.

* **All test cases passed** — say so, give the number of test cases, and show the transcript or point
  to `_temp/ui-test-session.txt`.
* **A test case failed** — the session stops there by design; later test cases are not run. Report
  the test case that failed, its aim, and the expected and actual outputs the script printed, then
  diagnose. Do not re-run the suite hoping for a different answer, and never edit the expected output
  to match broken behaviour.

A failure means one of two things, and they are settled differently:

1. **The code is wrong.** Fix the code, then run the session again.
2. **The behaviour changed on purpose.** Update the affected expected output in
   `test/ui-test-plan.md`, in the same commit as the code change, so the plan and the code never
   disagree in the repository's history.

## Add a test case when behaviour is added

A new command, a new error message, or a new output format needs a test case before the increment is
finished. Add it to `test/ui-test-plan.md` in the same commit as the code.

The plan's own "How a test case works" section is the reference; in short, a test case is an `## TC-nn
Title` heading holding an `**Aim:**`, an `### Input` block, an `### Expected output` block, and
optionally a `### Data file` block written before the program starts and an `### Expected data file`
block compared after it exits. Points worth repeating:

* The last input line must be `bye`, or the program waits forever for more input.
* `{{greeting}}` and `{{farewell}}` stand in for the banner and the closing lines.
* Write the expected output by **reasoning from the source and from the voice and formatting rules in
  AGENTS.md** — a 55-hyphen separator after each exchange, a leading space on error lines, a leading
  tab on task lines. Never paste the program's actual output into the plan as the expected output: a
  test case that is a copy of what the program happens to do cannot fail, and proves nothing.
* Give each test case an aim that says what is being checked, not what is being typed.

## Keep the plan honest

Where current behaviour contradicts AGENTS.md, the plan records the current behaviour and says so
under "Known deviations recorded here on purpose". That is deliberate: the test case then fails on
the day the deviation is fixed, which is exactly when the plan should be revisited. Add to that
section rather than quietly encoding a deviation as if it were correct.

## Commit messages

Changes to the plan or the runner are `chore:` work — `test:` is not one of this repository's
categories — and belong in their own commit, separate from the code change whose behaviour they
describe. The one exception is the plan being updated *because* the code changed: then both go in a
single commit, so the plan and the code never disagree in the repository's history. Follow the
`seedu-git-standard` skill, and leave the commands for the user to run.
