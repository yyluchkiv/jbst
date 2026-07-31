# Workflow: scrap

Abandon the current worktree session: delete the worktree directory, its
local branch, and any pushed remote branch. Nothing is merged — the user has
decided the work is not worth keeping and will typically delete the chat
session afterwards.

Preconditions (verify before running anything):

- The working directory is a git worktree, not the primary checkout — the
  script refuses otherwise.
- Current branch is in an agent namespace (`claude/*`, `codex/*`, …) —
  never `main`/`master`.
- Do NOT commit, stash, or push anything first. Discarding uncommitted
  work is the point of this workflow.

Then run:

```sh
./scrap.sh --yes
```

The script prints what is being discarded (uncommitted file count, commits
not on `origin/main`), removes the worktree with `git worktree remove
--force`, force-deletes the local branch, and best-effort deletes the remote
branch if it was ever pushed.

Rules:

- The user invoking this workflow IS the confirmation — pass `--yes` and do
  not ask again. Only stop and ask if the branch has commits that were
  already merged into `main` via a PR (then scrapping is unnecessary — the
  worktree just needs normal cleanup).
- The working directory is deleted by the script — everything after it must
  use absolute paths from the primary checkout. Do not try to `cd` back.
- Never run this from the primary checkout, and never delete `main`/`master`.

Report: confirm the worktree and branch are gone, list what was discarded
(commit subjects, uncommitted-change count), and remind the user they can
now delete this chat/session — the local repo no longer references it.
