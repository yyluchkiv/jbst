#!/bin/sh
# PreToolUse hook (matcher: Write|Edit|MultiEdit) — enforces AGENTS.md:
# "Never touch database migrations (Liquibase changelogs)."
# Blocks file writes/edits targeting the Liquibase changelog trees:
#   - jbst-server-iam/src/main/resources/postgres/changelog.yml
#   - jbst-server-iam/src/main/resources/postgres/changes/*.sql
#   - jbst-foundation/src/test/resources/db/changelog/** (master yaml + changes)
#
# Contract: exit 0 allows the tool call; exit 2 blocks it and surfaces stderr.
# The settings.json wrapper converts every other failure into exit 2 (fail closed).

payload=$(cat) || {
    echo "jbst hook: could not read tool input; blocking as a precaution." >&2
    exit 2
}

# Extract only the file_path value — matching the whole payload would false-positive
# on edits whose *content* merely mentions a changelog path.
fp=$(printf '%s' "$payload" | grep -o '"file_path"[[:space:]]*:[[:space:]]*"[^"]*"' | head -n 1)

if [ -z "$fp" ]; then
    echo "jbst hook: could not determine target file_path; blocking as a precaution." >&2
    exit 2
fi

# Case-insensitive path check (macOS filesystems are case-insensitive).
fp_lc=$(printf '%s' "$fp" | tr 'A-Z' 'a-z')

case "$fp_lc" in
    *src/main/resources/postgres/changelog.yml* | \
    *src/main/resources/postgres/changes/* | \
    */db/changelog/* | \
    *db.changelog*)
        echo "Editing Liquibase changelogs is not permitted in this repository (AGENTS.md: never touch database migrations). Blocked path: $fp. If a schema change is genuinely required, stop and report it instead of modifying the changelog." >&2
        exit 2
        ;;
esac

exit 0
