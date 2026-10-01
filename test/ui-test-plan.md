# CortisolBot text-UI test plan

Every test case below is run by the `test-ui` skill. Run them all with:

```powershell
python test/run-ui-tests.py
```

## How a test case works

Each test case starts a fresh copy of the program in an empty working folder, types the lines of its
**Input** block one after another, and compares everything the program printed with its **Expected
output** block. A test case may also supply a **Data file** block, which is written to
`data/cortisolbot.txt` before the program starts, and an **Expected data file** block, which is
compared with that file after the program exits.

Rules for writing a test case:

* The last input line must be `bye`. Without it the program waits forever for more input.
* `{{greeting}}` and `{{farewell}}` on a line of their own stand for the blocks under **Snippets**,
  so that the banner need not be repeated in all eleven test cases.
* Indentation is compared exactly, so the task lines printed after `added`, `marked` and `removed`
  must carry their leading **tab**. Trailing spaces and trailing blank lines are ignored, since they
  cannot be seen on screen and most editors strip them.
* Each test case runs in its own folder under `_temp/ui-test-runs/`, so the task list saved by one
  test case can never reach another, and the real `data/cortisolbot.txt` is never touched.

## Known deviations recorded here on purpose

These two are current behaviour, deliberately captured so that the day they are changed, the test
session fails and this plan must be updated to match. They are **not** endorsements.

1. `list` prints its tasks with no leading tab (`1.[T][ ] read book`), while `mark`, `unmark`,
   `delete` and the add commands all indent theirs with a tab. AGENTS.md calls for the tab.
2. The add, mark, unmark, delete and list replies still use the course's stock wordings
   (`Got It. I've added this task:`, `Nice! ...`, `Ok, ...`, `Noted. ...`, `Here are the tasks in
   your list:`) rather than the butler voice AGENTS.md requires.

Both are to be fixed before the `A-MoreOOP` increment. When they are, update the affected expected
output here in the same commit as the code change.

## Snippets

### greeting

```text
  ____           _   _           _ ____        _
 / ___|___  _ __| |_(_)___  ___ | | __ )  ___ | |_
| |   / _ \| '__| __| / __|/ _ \| |  _ \ / _ \| __|
| |__| (_) | |  | |_| \__ \ (_) | | |_) | (_) | |_
 \____\___/|_|   \__|_|___/\___/|_|____/ \___/ \__|
-------------------------------------------------------
Greetings sir/madam, CortisolBot humbly at your service.
How may I serve you at this evening?
-------------------------------------------------------
```

### farewell

```text
-------------------------------------------------------
Tonight has been an honour. I shall bid thee farewell!
-------------------------------------------------------
```

## TC-01 Starting and leaving

**Aim:** Check that the program greets the user, survives a session in which nothing is asked of it,
and takes its leave on `bye`.

### Input

```text
bye
```

### Expected output

```text
{{greeting}}
{{farewell}}
```

## TC-02 Listing an empty list

**Aim:** Check that `list` on a fresh start reports an empty list rather than an empty heading.

### Input

```text
list
bye
```

### Expected output

```text
{{greeting}}
-------------------------------------------------------
Your list is presently empty, sir/madam. A rare luxury.
-------------------------------------------------------
{{farewell}}
```

## TC-03 Adding one task of each type

**Aim:** Check that `todo`, `deadline` and `event` each add a task, report the growing count, display
in their own format, and are written to the data file in the encoding `Storage` expects.

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
list
bye
```

### Expected output

```text
{{greeting}}
-------------------------------------------------------
Got It. I've added this task:
	[T][ ] read book
 Now you have 1 tasks in the list.
-------------------------------------------------------
-------------------------------------------------------
Got It. I've added this task:
	[D][ ] return book (by: June 6th)
 Now you have 2 tasks in the list.
-------------------------------------------------------
-------------------------------------------------------
Got It. I've added this task:
	[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 3 tasks in the list.
-------------------------------------------------------
-------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
-------------------------------------------------------
{{farewell}}
```

### Expected data file

```text
T | 0 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

## TC-04 Marking and unmarking a task

**Aim:** Check that `mark` and `unmark` change the status icon, that the change shows up in a later
`list`, and that the done-status reaches the data file.

### Input

```text
todo read book
mark 1
list
unmark 1
list
bye
```

### Expected output

```text
{{greeting}}
-------------------------------------------------------
Got It. I've added this task:
	[T][ ] read book
 Now you have 1 tasks in the list.
-------------------------------------------------------
-------------------------------------------------------
Nice! I've marked this task as done:
	[T][X] read book
-------------------------------------------------------
-------------------------------------------------------
Here are the tasks in your list:
1.[T][X] read book
-------------------------------------------------------
-------------------------------------------------------
Ok, I've marked this task as not done yet:
	[T][ ] read book
-------------------------------------------------------
-------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] read book
-------------------------------------------------------
{{farewell}}
```

### Expected data file

```text
T | 0 | read book
```

## TC-05 Deleting a task

**Aim:** Check that `delete` removes the task it names, reports the task it removed along with the
new count, renumbers what is left, and shrinks the data file.

### Input

```text
todo read book
todo return book
delete 1
list
bye
```

### Expected output

```text
{{greeting}}
-------------------------------------------------------
Got It. I've added this task:
	[T][ ] read book
 Now you have 1 tasks in the list.
-------------------------------------------------------
-------------------------------------------------------
Got It. I've added this task:
	[T][ ] return book
 Now you have 2 tasks in the list.
-------------------------------------------------------
-------------------------------------------------------
Noted. I've removed this task:
	[T][ ] read book
 Now you have 1 tasks in the list.
-------------------------------------------------------
-------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] return book
-------------------------------------------------------
{{farewell}}
```

### Expected data file

```text
T | 0 | return book
```

## TC-06 Commands the bot does not know

**Aim:** Check that an unrecognised word, and an empty line, are both refused in character and
followed by the list of commands the bot does understand, without ending the session.

### Input

```text
sing

bye
```

### Expected output

```text
{{greeting}}
-------------------------------------------------------
 I do beg your pardon, sir/madam, but that instruction is not in my repertoire.
 I can manage: todo, deadline, event, list, mark, unmark, delete, bye.
-------------------------------------------------------
-------------------------------------------------------
 I do beg your pardon, sir/madam, but that instruction is not in my repertoire.
 I can manage: todo, deadline, event, list, mark, unmark, delete, bye.
-------------------------------------------------------
{{farewell}}
```

## TC-07 Add commands missing their parts

**Aim:** Check each way an add command can be incomplete: no description at all, a deadline with no
`/by`, a deadline with no description, and an event with no `/to`. Each must explain the
misunderstanding and offer the correct form.

### Input

```text
todo
deadline return book
deadline /by June 6th
event project meeting /from Aug 6th 2pm
bye
```

### Expected output

```text
{{greeting}}
-------------------------------------------------------
 A todo without a description is rather like tea without leaves, sir/madam.
 Do try: todo <description>
-------------------------------------------------------
-------------------------------------------------------
 A deadline is of little use without a date, sir/madam.
 Do try: deadline <description> /by <when>
-------------------------------------------------------
-------------------------------------------------------
 You have not told me what is due, sir/madam.
 Do try: deadline <description> /by <when>
-------------------------------------------------------
-------------------------------------------------------
 An event requires both a start and an end, sir/madam.
 Do try: event <description> /from <start> /to <end>
-------------------------------------------------------
{{farewell}}
```

## TC-08 Task numbers that refer to nothing

**Aim:** Check each way a task number can be wrong: missing, not a number, given while the list is
empty, and beyond the end of a list that does have tasks.

### Input

```text
mark
delete abc
mark 1
todo read book
unmark 5
bye
```

### Expected output

```text
{{greeting}}
-------------------------------------------------------
 Which task shall I mark, sir/madam?
 Do try: mark <task number>
-------------------------------------------------------
-------------------------------------------------------
 'abc' is not a number I recognise, sir/madam.
 Do try: delete <task number>
-------------------------------------------------------
-------------------------------------------------------
 Your list is presently empty, sir/madam. There is nothing to mark just yet.
-------------------------------------------------------
-------------------------------------------------------
Got It. I've added this task:
	[T][ ] read book
 Now you have 1 tasks in the list.
-------------------------------------------------------
-------------------------------------------------------
 I keep no task numbered 5, sir/madam.
 Your list runs from 1 to 1.
-------------------------------------------------------
{{farewell}}
```

## TC-09 Tasks survive to the next session

**Aim:** Check that a data file left by an earlier session is read back at startup, that the
retrieval is announced, and that every task type and the done-status come back intact.

### Data file

```text
T | 1 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

### Input

```text
list
bye
```

### Expected output

```text
{{greeting}}
 I have retrieved 3 task(s) from my records, sir/madam.
-------------------------------------------------------
-------------------------------------------------------
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
-------------------------------------------------------
{{farewell}}
```

## TC-10 A damaged data file

**Aim:** Check that lines the data file cannot explain -- an unknown type letter, and a deadline with
no date -- are counted and set aside rather than costing the user the readable tasks around them.

### Data file

```text
T | 1 | read book
X | 0 | mystery parcel
D | 0 | missing date |
```

### Input

```text
list
bye
```

### Expected output

```text
{{greeting}}
 2 line(s) of my records were illegible, sir/madam. I have set them aside.
 I have retrieved 1 task(s) from my records, sir/madam.
-------------------------------------------------------
-------------------------------------------------------
Here are the tasks in your list:
1.[T][X] read book
-------------------------------------------------------
{{farewell}}
```

## TC-11 Untidy typing

**Aim:** Check that spaces before the command word, and extra spaces between the command word and
its description, do not reach the stored description.

### Input

```text
   todo    walk the dog
list
bye
```

### Expected output

```text
{{greeting}}
-------------------------------------------------------
Got It. I've added this task:
	[T][ ] walk the dog
 Now you have 1 tasks in the list.
-------------------------------------------------------
-------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] walk the dog
-------------------------------------------------------
{{farewell}}
```

### Expected data file

```text
T | 0 | walk the dog
```
