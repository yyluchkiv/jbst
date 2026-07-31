---
name: plan-execute
description: Implement an approved plan in a separate PR. User-invoked only.
argument-hint: [plan-file or partial name]
allowed-tools: Bash(git:*), Bash(gh:*), Read, Edit, Write
disable-model-invocation: true
---
Available plans (newest first):
!`ls -t assets/plans/*.md 2>/dev/null`

Requested plan: "$ARGUMENTS"

Read agents/workflows/plan-execute.md and follow it exactly. Your agent branch
namespace is claude/*. When the plan choice is ambiguous, ask via
AskUserQuestion.
