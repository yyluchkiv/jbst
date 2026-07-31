# Workflow: ship-fast

Same as [ship.md](ship.md) but the test suite is skipped — only a compile +
test-compile gate. Use ONLY when the user explicitly asked to ship fast /
skip tests; otherwise use the ship workflow.

```sh
./ship.sh --fast
```

The PR body automatically notes that tests were skipped. All preconditions,
rules, and reporting from [ship.md](ship.md) apply, plus: in your final
report, remind the user that the test suite was not run.
