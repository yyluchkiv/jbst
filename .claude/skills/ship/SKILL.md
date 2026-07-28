---
name: ship
description: Push the current branch, open a PR, and immediately squash-merge it, deleting the branch. Use when the user wants the current work shipped to main in one step.
---

# Ship

Ship the current branch as a self-merging PR. Every step must succeed before
the next one runs — stop and report on any failure.

1. **Preconditions**
   - Current branch must be in the agent namespace (`claude/*` or `codex/*`).
     Never ship from `main`/`master` — stop and tell the user.
   - Working tree must be clean. If there are uncommitted changes, commit them
     first following the repo's commit conventions (no tool-attribution
     trailers).

2. **Test** — run the unit test suite with Maven on JDK 21:

   ```sh
   export JAVA_HOME=$(/usr/libexec/java_home -v 21)
   mvn clean test
   ```

   The delombok phase prints benign `error:` lines — judge the run by the
   final `BUILD SUCCESS`/`BUILD FAILURE`, not intermediate noise. Do not
   proceed if tests fail.

3. **Push** — `git push -u origin <branch>` (never force-push).

4. **Create the PR** — `gh pr create` with a title and body summarizing the
   branch's commits vs `main`. No attribution footers.

5. **Merge and clean up**:

   ```sh
   gh pr merge <pr-number> --squash
   git push origin --delete <branch>
   ```

   Do NOT use `gh pr merge --delete-branch`: it tries to check out `main`
   locally, which fails when running in a worktree (where `main` is checked
   out in the primary repo) and then skips the remote deletion. Merge first,
   then delete the remote branch explicitly. If the merge fails (e.g.
   conflicts), report the PR URL and stop — do not force anything.

6. **Local cleanup** — remove the local branch, the worktree folder (if
   any), and the stale remote-tracking ref, so nothing is left to clean up
   by hand. Run the whole block as ONE command — it deletes the current
   working directory when running in a worktree, so it must be the last git
   operation of the session:

   ```sh
   BRANCH=$(git branch --show-current)
   TOPLEVEL=$(git rev-parse --show-toplevel)
   MAIN_REPO=$(git worktree list --porcelain | head -1 | cut -d' ' -f2-)
   if [ "$TOPLEVEL" != "$MAIN_REPO" ]; then
     cd "$MAIN_REPO"
     git worktree remove --force "$TOPLEVEL"
   else
     git checkout main
   fi
   git branch -D "$BRANCH"
   git fetch --prune
   ```

   - `git branch -D` (not `-d`) is required: after a squash merge git does
     not consider the branch merged — its content is already on `main`, so
     force-deleting it is safe.
   - `git worktree remove --force` is needed because leftover ignored/
     untracked files (build output, scratch files) make git consider the
     worktree dirty; the committed work is already merged.
   - After this step the original worktree path no longer exists — stay in
     the primary repo and use absolute paths for anything that follows.

7. **Report** — output the PR URL and its final state (merged, remote and
   local branches deleted, worktree removed).
