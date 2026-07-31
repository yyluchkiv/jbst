# Workflow: ship

Ship the current branch to `main` as a self-merging squash PR.

Preconditions (verify before running anything):

- Current branch is in your agent namespace (`claude/*`, `codex/*`, …) —
  never `main`/`master`. If on `main`/`master`, stop and tell the user.
- Working tree is clean. If there are uncommitted changes, commit them first
  following the repo conventions (Conventional Commits subject, a
  `CHANGELOG.md` line ending with two trailing spaces, no tool-attribution
  trailers).

Then run:

```sh
./ship.sh
```

The script runs the unit test suite (`./mvnw clean test` on JDK 21), pushes
the branch, opens a PR (title/body derived from the branch commits — pass
`./ship.sh "<title>" "<body>"` when the commits don't tell the story),
squash-merges it, waits for the merge, deletes the remote and local branches,
removes the worktree if shipping from one, and fast-forwards `main`.

Rules:

- Judge the Maven run by the final `BUILD SUCCESS`/`BUILD FAILURE` — the
  delombok phase prints benign `error:`-looking lines.
- If any step fails (tests, push, merge conflict), stop and report — never
  force-push, never skip tests, never retry blindly.
- When shipping from a worktree, the working directory is deleted at the
  end — continue from the primary checkout using absolute paths.

Report: the PR URL and final state (merged, branches deleted, worktree
removed).
