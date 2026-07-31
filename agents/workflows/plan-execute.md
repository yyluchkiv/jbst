# Workflow: plan-execute

Implement an approved plan from `assets/plans/` in its own PR.

Input: optionally a plan file path or partial name.

0. Resolve the plan file against `ls -t assets/plans/*.md` (newest first):
   - Exact path from the list → use it.
   - Empty input → the newest plan if there is only one candidate; otherwise
     ask the user which one.
   - Otherwise treat the input as a fuzzy match (date prefix or slug
     substring) against the list. One match → use it. Zero or several → ask
     the user, listing the candidates.

   Read the chosen plan file before proceeding.

Precondition: the plan PR is already merged into the default branch, so the
plan file lives there and its `<agent>/<stem>` branch was deleted by cleanup.
The branch name is therefore free to reuse for this code PR.

1. Derive `<stem>` = the plan filename without path or extension
   (e.g. `2026-06-24-foo`).
2. From the up-to-date default branch, create branch `<agent>/<stem>` in your
   agent namespace (Claude Code → `claude/<stem>`, Codex → `codex/<stem>`).
3. Implement the plan step by step, in order. Run build/tests after each
   major step.
4. Make logical commits, push with `git push origin <agent>/<stem>`, and open
   a SEPARATE PR via `gh pr create`. Title: `<stem>`. In the body, link the
   merged plan PR. Do not deviate from the plan — if you must, stop and ask
   the user first.
