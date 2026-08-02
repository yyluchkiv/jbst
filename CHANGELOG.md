### Changelog [v1.75]
— docs: AGENTS.md — agent build commands, module map, and guardrail rules  
— build: japicmp binary-compatibility gate on jbst-foundation — `verify` fails on binary-incompatible public-API changes vs. the latest release  
— test: deflake JbstUserTokenTest — compute expiry offsets at execution time instead of pre-captured wall-clock timestamps  
— chore: PreToolUse hooks enforcing AGENTS.md — block Maven test-skip flags and Liquibase changelog edits  
— docs: consolidate CLAUDE.md into AGENTS.md as the single tool-agnostic agent-instruction source; CLAUDE.md is now a pointer  
— build: ship.sh / ship-fast.sh — scripted ship flow (test or compile gate, PR create, squash-merge, branch + worktree cleanup)  
— docs: tool-neutral agent workflow playbooks in agents/workflows/ (plan-create, plan-execute, ship, ship-fast); .claude/skills become thin wrappers  
— docs: plan — registration via GitHub OAuth for the IAM server (assets/plans/2026-07-31-github-oauth-registration.md)  
— build: ship.sh — fall back to a direct squash merge when GitHub rejects auto-merge on an already-clean PR  
— build: scrap.sh + scrap workflow/skill — abandon the current worktree (delete worktree, local and remote branch) without merging  
— build: basic opencode.json configuration — schema reference + AGENTS.md as the instruction source    
