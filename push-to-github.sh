#!/bin/bash

# GitHub Push Script
# Automates pushing local repository to GitHub

set -e

echo "╔════════════════════════════════════════════════════════════╗"
echo "║  Test Generator - GitHub Push Automation                   ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo

# Get GitHub username
read -p "GitHub Username: " USERNAME

if [ -z "$USERNAME" ]; then
    echo "Error: GitHub username is required"
    exit 1
fi

# Determine protocol
echo
echo "Choose protocol:"
echo "1. HTTPS (default, requires token)"
echo "2. SSH (requires SSH key)"
read -p "Choice [1-2]: " PROTOCOL

if [ "$PROTOCOL" = "2" ]; then
    REMOTE_URL="git@github.com:${USERNAME}/test-generator.git"
else
    REMOTE_URL="https://github.com/${USERNAME}/test-generator.git"
fi

echo
echo "📝 Summary:"
echo "  Username: $USERNAME"
echo "  Remote URL: $REMOTE_URL"
echo "  Branch: master"
echo "  Commits: $(git rev-list --count HEAD)"
echo

read -p "Push to GitHub? [y/N] " CONFIRM

if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
    echo "Aborted"
    exit 0
fi

# Add remote
echo
echo "Adding remote..."
git remote add origin "$REMOTE_URL" 2>/dev/null || git remote set-url origin "$REMOTE_URL"
echo "✓ Remote configured"

# Verify branch name
echo "Setting up branch..."
git branch -M master
echo "✓ Branch: master"

# Push
echo
echo "Pushing to GitHub..."
git push -u origin master
echo "✓ Push successful!"

echo
echo "✅ Done! Your repository is now on GitHub"
echo
echo "Next steps:"
echo "1. Visit: https://github.com/${USERNAME}/test-generator"
echo "2. Verify files are visible"
echo "3. Check Actions tab for CI/CD status"
echo
echo "To create a release:"
echo "  git tag -a v1.0.0 -m 'Test Generator MVP v1.0.0'"
echo "  git push origin --tags"
echo

