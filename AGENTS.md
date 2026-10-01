# Project context

This repository holds **CortisolBot**, a command-line task-tracking chatbot written in Java. It
began as the starter template for an introductory software engineering course and is being built up
by the student who owns the repo, one graded increment at a time (`Level-0` … `Level-7`, then
`A-Classes`, `A-CodingStandard`, `A-CodeQuality`, `A-Jar`, and so on).

The work is deliberately incremental: each increment is a small, self-contained improvement that is
committed, tagged, and pushed before the next one begins. Prefer the smallest change that completes
the increment at hand over a larger change that anticipates later ones.

# Default user context

Unless the user says otherwise, assume that you are assisting the student who owns this repository.
If the user identifies themselves as an instructor or another project stakeholder, adapt your
response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* Operating system: Windows 11, PowerShell.
* IDE: IntelliJ IDEA (the repo carries `.idea/` and `ip.iml`); level of expertise [to be filled].

# Current state of the code

All source lives in `src/main/java` as a single default package — keep that folder as the source
root, since tools such as Gradle expect it there.

| File | Role |
|---|---|
| `CortisolBot.java` | Entry point. Banner, input loop, command dispatch, and most printing. |
| `Storage.java` | Loads and saves the task list at `data/cortisolbot.txt`. |
| `Task.java` | Base task: description, done-status, file encoding. |
| `ToDo.java`, `Deadline.java`, `Event.java` | The three task types. |
| `CortisolException.java` | Errors whose messages are already phrased for the user. |

Supported commands: `list`, `todo`, `deadline … /by …`, `event … /from … /to …`, `mark`, `unmark`,
`delete`, `bye`.

Tests live in `test/`:

| File | Role |
|---|---|
| `test/ui-test-plan.md` | The test cases: for each, its aim, the lines typed, and the output expected. |
| `test/run-ui-tests.py` | Runs the plan. Standard library only; no packages to install. |

# Project-specific requirements

## Java version

Use Java 25 for running and building. Java 25 is already the default `java` on this machine, so no
version switching is normally needed; verify with `java -version` if something looks off. (On macOS
the course suggests `sdk use java 25.0.3.fx-zulu`, which does not apply here.)

## Build and run

There is no build tool in this repository — no `build.gradle`, no Maven. Compile and run directly:

```powershell
javac -d bin src/main/java/*.java
java -cp bin CortisolBot
```

`bin/`, `out/`, `data/`, and `*.jar` are all gitignored, so build output and the user's saved task
list never enter a commit. An unmerged `origin/add-gradle-support` branch exists if a build tool is
ever wanted; do not merge it without asking.

## Testing — required after every code change

The repository has no unit tests. Its only regression safety net is the text-UI test session:

```powershell
python test/run-ui-tests.py
```

**After any change to `src/main/java`, without exception:**

1. Update `test/ui-test-plan.md` if the change altered or added anything the user can see — a new
   command, a reworded message, a different layout. The plan change belongs in the **same commit** as
   the code change, so that the two never disagree in the repository's history.
2. Invoke the `test-ui` skill, which runs the session and reports it.
3. Show the user the record of the session — the input typed and the output produced — not merely a
   verdict. The script also writes it to `_temp/ui-test-session.txt`.

This applies to refactoring just as much as to new features: a refactoring that changes visible
behaviour has gone wrong, and the session is how that is found out.

A failing test case means either the code is wrong, or the behaviour changed deliberately and the
plan is now out of date. Settle which it is and fix that one. Never edit an expected output merely to
make a test case pass, and never write an expected output by pasting in what the program actually
printed — reason it out from the source and from the voice and formatting rules below.

## The bot's voice — a wealthy household's butler

CortisolBot speaks as an impeccably trained, expensively employed butler: formal, deferential,
unhurried, and dryly witty. **Every** user-facing string must be in this voice. There is no
exemption for errors, confirmations, or incidental system messages — a butler does not drop
character because something went wrong.

Rules for any string the user can see:

* Address the user as `sir/madam`.
* Full sentences and proper punctuation. Contractions (`I've`, `I shall`) are welcome; clipped
  fragments and exclamations are not.
* Dry understatement over enthusiasm. At most one flourish per message, and keep messages to one or
  two lines — the butler is composed, not chatty.
* Errors explain the misunderstanding in character, then offer the correct form, e.g.
  `Do try: deadline <description> /by <when>`.
* **Never ship the stock wordings from the course's starter material** — `"Got it. I've added this
  task:"`, `"Nice!"`, `"OK, I've marked this task as not done yet:"`, `"Noted. I've removed this
  task:"`, `"Here are the tasks in your list:"`. Rewrite them in the butler voice.

Lines already in the code that set the standard:

```
Greetings sir/madam, CortisolBot humbly at your service.
Your list is presently empty, sir/madam. A rare luxury.
A todo without a description is rather like tea without leaves, sir/madam.
I do beg your pardon, sir/madam, but that instruction is not in my repertoire.
Tonight has been an honour. I shall bid thee farewell!
```

When you add or change a user-facing string, check it against those. If the right phrasing is not
obvious, propose the wording to the user rather than guessing.

## Output formatting

These details are part of the bot's presentation and are what expected-output tests compare against,
so match them exactly rather than inventing new layout:

* A separator line of 55 hyphens closes each exchange.
* Error messages are printed with a single leading space.
* Task lines inside a response are indented with a tab.

## Git

* One branch per increment, named `branch-<Increment>` (e.g. `branch-A-MoreOOP`), merged into
  `master` with a merge commit. Non-increment chores may go straight onto `master`.
* **Every commit message must follow the `seedu-git-standard` skill.** Read it before proposing one.
  In short, the required shape is `<category>: <Capitalized imperative phrase>` — no trailing
  period, 72 characters maximum and ideally 50.
* Commit messages are **a single sentence — subject line only, no body**. Keep them short enough to
  read at a glance in `git log --oneline`. The rationale for a change belongs in your chat
  explanation and in the code's own comments, not in the commit message.
* Categories in use: `feat:`, `refactor:`, `style:`, `chore:`; `fix:` and `docs:` when they apply.
* **One commit per standalone change.** Code changes and agent-file changes never share a commit.
* Use lightweight tags (`git tag A-MoreOOP`) unless an annotated tag is requested. One tag per
  increment, placed on `master` after the merge.
* The remote is `origin` → `https://github.com/PoonnatW/ip.git`.

### The user runs all Git commands — you never do

**Do not execute Git commands yourself.** No `git add`, `commit`, `tag`, `merge`, `push`,
`checkout`, `branch`, `reset`, or `stash` — not even when the user says "commit this", and not even
when a command appears to be pre-approved. The user wants to inspect every change before it enters
the repository's history.

Read-only inspection is the one exception: `git status`, `git diff`, `git log`, `git show`, and
`git branch --list` are fine to run on your own when you need to understand the current state.

When a Git action is needed, **put the exact commands in a copy-pasteable block at the very end of
your message**, after the explanation, so the user can review them and run them. Then stop and wait
— do not assume the commands were run, and do not continue with work that depends on them until the
user confirms. Since commit messages are one sentence, a plain `-m "…"` is always enough — never
reach for a here-string or heredoc.

Format it like this:

~~~
```powershell
git checkout -b branch-A-MoreOOP
git add src/main/java/Ui.java src/main/java/CortisolBot.java
git commit -m "refactor: Extract user interaction into a Ui class"
```
~~~

Explain briefly what any unfamiliar command does, per the guidance below. If the user reports that a
command failed, diagnose it and propose a corrected command — still for them to run.

# Project skills — mandatory, not optional

Project skills live in `.claude/skills/<name>/SKILL.md` and are committed to the repo so they are
available in every session. Read the relevant skill *before* doing the work it governs, not after.

| Skill | When it is mandatory |
|---|---|
| `seedu-java-coding-standard` | Writing, reviewing, or reformatting **any** Java code in this project. |
| `seedu-git-standard` | Proposing or reviewing **any** commit message, and naming any branch. |
| `present-changes-visually` | Presenting the result of an increment, or whenever the user asks to see changes. |
| `test-ui` | After **any** change to `src/main/java`, and whenever the user asks for the bot to be tested. |

These are standards, not suggestions: code that does not follow `seedu-java-coding-standard` is not
finished, a commit message that does not follow `seedu-git-standard` should not be proposed, and a
code change that has not been through `test-ui` is not ready to be committed.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For
  example:
  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes, and to nontrivial methods and fields whose
    purpose or behaviour is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they
    improve understanding.
  * When faced with a design choice, choose the simplest option sufficient for the requirements,
    while briefly explaining relevant more advanced alternatives.

# Known gaps

Honest record of what is missing, so it is not mistaken for something that already works:

* **No unit tests.** Every class is exercised only from the outside, through the text-UI test session
  in `test/ui-test-plan.md` (11 test cases, run by the `test-ui` skill). That session covers the
  commands, the error messages and the data file, but it cannot reach a method that no command calls,
  and it says nothing about how the code is arranged inside. JUnit is the course's answer and has not
  been introduced yet.
* **`.gitignore` still mentions `text-ui-test/`**, which is the course template's harness layout. This
  repository never rebuilt that folder and uses `test/` instead; the stale lines are harmless and have
  been left alone.
* **`docs/README.md` is still the unedited template** — placeholder headings, no screenshot, no
  product intro.
* **Voice is not yet consistent.** Several messages in `CortisolBot.java` are still the stock
  starter wordings listed above and need a pass to bring them into the butler voice. The test plan
  records them as they currently are, under "Known deviations recorded here on purpose", so the
  rewording pass must update those expected outputs in the same commit.
* **`list` does not indent its task lines**, printing `1.[T][ ] read book` where every other command
  prefixes a tab. This contradicts the output-formatting rule above and is likewise recorded in the
  test plan as a known deviation. Both it and the voice pass are to be settled before `A-MoreOOP`.
