#!/bin/sh
# scrap.sh — abandon the current worktree: delete the worktree directory,
# its local branch, and (if it was pushed) the remote branch. Nothing is
# merged; all work on the branch is discarded.
#
# Usage: ./scrap.sh [--yes]
#   --yes    skip the interactive confirmation (required when run
#            non-interactively, e.g. by an AI agent)
#
# Deterministic core of agents/workflows/scrap.md — used by AI agents and
# humans alike. Refuses to run from the primary checkout or from
# main/master; only a worktree on an agent/feature branch can be scrapped.

set -eu

# The whole flow lives in main() so the parser has consumed the entire file
# before the cleanup deletes the script's own directory.
main() {
    YES=false
    if [ "${1:-}" = "--yes" ]; then
        YES=true
    fi

    BRANCH=$(git branch --show-current)
    case "$BRANCH" in
        main|master|"")
            echo "scrap.sh: refusing to scrap '${BRANCH:-detached HEAD}' — only agent/feature branches (claude/*, codex/*, ...) can be scrapped." >&2
            exit 1
            ;;
    esac

    TOPLEVEL=$(git rev-parse --show-toplevel)
    MAIN_REPO=$(git worktree list --porcelain | head -1 | cut -d' ' -f2-)
    if [ "$TOPLEVEL" = "$MAIN_REPO" ]; then
        echo "scrap.sh: current directory is the primary checkout, not a worktree — nothing to scrap." >&2
        exit 1
    fi

    echo "scrap.sh: about to discard worktree '$TOPLEVEL' and branch '$BRANCH'."
    UNCOMMITTED=$(git status --porcelain | wc -l | tr -d ' ')
    UNMERGED=$(git log --oneline origin/main.."$BRANCH" 2>/dev/null || true)
    [ "$UNCOMMITTED" -gt 0 ] && echo "  uncommitted changes: $UNCOMMITTED file(s)"
    if [ -n "$UNMERGED" ]; then
        echo "  commits not on origin/main:"
        echo "$UNMERGED" | sed 's/^/    /'
    fi

    if [ "$YES" != "true" ]; then
        if [ -t 0 ]; then
            printf "Discard all of the above permanently? [y/N] "
            read -r ANSWER
            case "$ANSWER" in
                y|Y|yes|YES) ;;
                *) echo "scrap.sh: aborted."; exit 1 ;;
            esac
        else
            echo "scrap.sh: non-interactive run — pass --yes to confirm discarding the work." >&2
            exit 1
        fi
    fi

    # Deleting the worktree removes the current working directory, so from
    # here on operate from the primary checkout.
    cd "$MAIN_REPO"
    git worktree remove --force "$TOPLEVEL"
    # -D: the branch was never merged; discarding its content is the point.
    git branch -D "$BRANCH"
    git push origin --delete "$BRANCH" 2>/dev/null \
        || echo "scrap.sh: no remote branch to delete."
    git worktree prune

    echo ""
    echo "scrap.sh: done — worktree and branch '$BRANCH' deleted; nothing was merged."
}

main "$@"
