---
name: ship-fast
description: Push the current branch, open a PR, and immediately squash-merge it, deleting the branch — WITHOUT running the test suite. Use only when the user explicitly asks to ship fast / skip tests; otherwise prefer the ship skill.
---

Read agents/workflows/ship-fast.md and follow it exactly. Every step must
succeed before the next one runs — stop and report on any failure.
