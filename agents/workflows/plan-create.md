# Workflow: plan-create

Write a plan file for user review. Planning only — no implementation.

Input: a task description from the user.

1. Get today's date (`date +%Y-%m-%d`). Derive `<slug>` = 3-6 word kebab-case
   summary of the task. Filename = `assets/plans/<date>-<slug>.md`.

2. Write the plan file with these sections:

   ```markdown
   # <Title>

   ## Goal
   One paragraph: what exists after this task that doesn't exist now.

   ## Assumptions
   Bullet list of anything inferred. Write "None." if fully specified.

   ## Stack / constraints
   Technology choices locked for this plan. Write "To be decided." if not specified.

   ## Affected files
   Every file to be created or modified, tree-style paths.

   ## Ordered steps
   Numbered list. Each step = one coherent unit of work.

   ## Risks
   Minimum 2 bullets. Format: one sentence problem + one sentence mitigation.

   ## Verification
   Exact shell commands: build, test, smoke test.
   Last line: "Done when: <one sentence success criterion>."
   ```

3. Print one line: `Plan written: assets/plans/<date>-<slug>.md`

4. Print the full content of the written plan file, then STOP.

Hard rules:

- DO NOT run any git commands.
- DO NOT create any branches.
- DO NOT open any PRs.
- DO NOT implement anything — stop after step 4.
