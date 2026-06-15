# Git Workflow Rules

Do not add "🤖 Generated with Claude Code" or Co-Authored-By footers to commit messages.

After finishing a task in a worktree, before ending the session, clean up:
- run `git worktree remove <path> --force`
- run `git branch -D <branch>` for the claude/* branch used
- run `git worktree prune`
