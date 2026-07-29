#!/bin/sh
# PreToolUse hook (matcher: Bash) — enforces AGENTS.md: "Never modify tests to
# make a build pass" / run the full suite. Blocks any Bash command carrying a
# Maven test-skipping (or test-failure-ignoring) flag.
#
# Contract: exit 0 allows the tool call; exit 2 blocks it and surfaces stderr.
# The settings.json wrapper converts every other failure into exit 2 (fail closed).

payload=$(cat) || {
    echo "jbst hook: could not read tool input; blocking as a precaution." >&2
    exit 2
}

if [ -z "$payload" ]; then
    echo "jbst hook: empty tool input; blocking as a precaution." >&2
    exit 2
fi

# Matches -Dflag, -D flag (Maven accepts the space), and --define flag forms.
# Substring match on the whole payload: over-blocking (e.g. the flag quoted in a
# commit message) is acceptable; under-blocking is not.
if printf '%s' "$payload" | grep -qE -- '(-D[[:space:]]*|--define[[:space:]]+)(skipTests|skipITs|skipUTs|maven\.test\.skip|surefire\.skip|failsafe\.skip|maven\.test\.failure\.ignore|testFailureIgnore)'; then
    echo "Test skipping is not permitted in this repository (AGENTS.md). Flags like -DskipTests, -DskipITs, -Dmaven.test.skip, -Dsurefire.skip, -Dfailsafe.skip and -Dmaven.test.failure.ignore are blocked. Run the full suite (./mvnw -B -q clean verify) and, if it fails, stop and report the failure instead of skipping tests." >&2
    exit 2
fi

exit 0
