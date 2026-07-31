### Changelog [v1.75]
— docs: AGENTS.md — agent build commands, module map, and guardrail rules  
— build: japicmp binary-compatibility gate on jbst-foundation — `verify` fails on binary-incompatible public-API changes vs. the latest release  
— test: deflake JbstUserTokenTest — compute expiry offsets at execution time instead of pre-captured wall-clock timestamps  
— chore: PreToolUse hooks enforcing AGENTS.md — block Maven test-skip flags and Liquibase changelog edits  
— docs: consolidate CLAUDE.md into AGENTS.md as the single tool-agnostic agent-instruction source; CLAUDE.md is now a pointer  
