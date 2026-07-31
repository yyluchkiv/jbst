#!/bin/sh
# ship.sh — ship the current branch as a self-merging squash PR:
# run the test suite (or compile-only with --fast), push, open a PR,
# squash-merge it, then clean up the remote branch, local branch, and
# worktree (if shipping from one).
#
# Usage: ./ship.sh [--fast] [pr-title] [pr-body]
#   --fast    skip the test suite; compile + test-compile gate only
#
# Deterministic core of agents/workflows/ship.md and ship-fast.md — used by
# AI agents and humans alike. Judgment (what to commit, PR wording) stays
# with the caller; title/body default to the branch commits vs origin/main.

set -eu

# The whole flow lives in main() so the parser has consumed the entire file
# before the worktree cleanup at the end deletes the script's own directory.
main() {
    FAST=false
    if [ "${1:-}" = "--fast" ]; then
        FAST=true
        shift
    fi

    BRANCH=$(git branch --show-current)
    case "$BRANCH" in
        main|master|"")
            echo "ship.sh: refusing to ship from '${BRANCH:-detached HEAD}' — use an agent/feature branch (claude/*, codex/*, ...)." >&2
            exit 1
            ;;
    esac

    if [ -n "$(git status --porcelain)" ]; then
        echo "ship.sh: working tree not clean — commit (following the repo commit conventions) or stash first." >&2
        exit 1
    fi

    # JDK 21 (macOS convenience; elsewhere rely on the caller's JAVA_HOME)
    if [ -x /usr/libexec/java_home ]; then
        JAVA_HOME=$(/usr/libexec/java_home -v 21)
        export JAVA_HOME
    fi

    # Delombok prints benign error-looking lines — the exit code is the only
    # signal that matters (set -e aborts on BUILD FAILURE).
    if [ "$FAST" = true ]; then
        ./mvnw clean compile test-compile
    else
        ./mvnw clean test
    fi

    git push -u origin "$BRANCH"

    TITLE="${1:-$(git log -1 --pretty=%s)}"
    BODY="${2:-$(git log --reverse --pretty='- %s' origin/main..HEAD)}"
    if [ "$FAST" = true ]; then
        BODY=$(printf '%s\n\nNote: test suite was skipped (ship --fast); compile check only.' "$BODY")
    fi

    PR_URL=$(gh pr create --title "$TITLE" --body "$BODY")
    PR_NUMBER=${PR_URL##*/}

    # Not --delete-branch: it checks out main locally, which fails from a
    # worktree (main is checked out in the primary repo) and then skips the
    # remote deletion. Merge first, clean up explicitly afterwards.
    gh pr merge "$PR_NUMBER" --squash --auto

    # No required checks on main -> the merge normally lands in seconds.
    i=0
    while [ "$(gh pr view "$PR_NUMBER" --json state --jq .state)" != "MERGED" ]; do
        i=$((i + 1))
        if [ "$i" -gt 15 ]; then
            echo "ship.sh: PR not merged after 30s — resolve manually: $PR_URL" >&2
            exit 1
        fi
        sleep 2
    done

    # GitHub may have auto-deleted the remote branch on merge.
    git push origin --delete "$BRANCH" 2>/dev/null \
        || echo "ship.sh: remote branch already deleted."

    # Local cleanup — in a worktree this deletes the current working
    # directory, so it must stay the last part of the flow. --force because
    # ignored build output makes git consider the worktree dirty; the
    # committed work is already merged.
    TOPLEVEL=$(git rev-parse --show-toplevel)
    MAIN_REPO=$(git worktree list --porcelain | head -1 | cut -d' ' -f2-)
    if [ "$TOPLEVEL" != "$MAIN_REPO" ]; then
        cd "$MAIN_REPO"
        git worktree remove --force "$TOPLEVEL"
    else
        git checkout main
    fi
    # -D: after a squash merge git never considers the branch merged; its
    # content is already on main, so force-deleting is safe.
    git branch -D "$BRANCH"
    git fetch --prune
    if [ "$(git branch --show-current)" = "main" ]; then
        git pull --ff-only
    fi

    echo ""
    echo "ship.sh: done — $PR_URL merged; remote + local branches deleted; main is up to date."
    if [ "$FAST" = true ]; then
        echo "ship.sh: REMINDER — the test suite was NOT run."
    fi
}

main "$@"
