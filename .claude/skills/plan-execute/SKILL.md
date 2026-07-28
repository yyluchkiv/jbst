---
name: plan-execute
description: Implement an approved plan in a separate PR. User-invoked only.
argument-hint: [plan-file]
allowed-tools: Bash(git:*), Bash(gh:*), Read, Edit, Write
disable-model-invocation: true
---
Approved plan: @$ARGUMENTS

Precondition: the plan PR is already merged into the default branch, so the plan
file lives there and its claude/<stem> branch was deleted by cleanup. The branch
name is therefore free to reuse for this code PR.

1. Derive <stem> = the plan filename without path or extension (e.g. 2026-06-24-foo).
2. From the up-to-date default branch, create branch claude/<stem>.
3. Implement the plan step by step, in order. Run build/tests after each major step.
4. Make logical commits, push with `git push origin claude/<stem>`, and open a
   SEPARATE PR via `gh pr create`. Title: "<stem>". In the body, link the merged plan PR.
   Do not deviate from the plan — if you must, stop and ask first.
