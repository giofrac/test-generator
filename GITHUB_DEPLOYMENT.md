# GitHub Deployment Guide

## Prerequisites

Before pushing to GitHub, ensure you have:
- Git installed and configured
- GitHub account
- SSH key configured (or HTTPS credentials)

## Setup Repository on GitHub

### Step 1: Create Repository on GitHub

1. Go to https://github.com/new
2. Repository name: `test-generator`
3. Description: "Automatically generates JUnit 5 test skeletons from Java methods"
4. **Do NOT initialize** with README, .gitignore, or license (already in repository)
5. Click "Create repository"

### Step 2: Add Remote and Push

```bash
# Navigate to project directory
cd /home/giorgio/IdeaProjects/IntelliJ_Platform_Plugin

# Add GitHub remote (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/test-generator.git

# Or use SSH (if configured)
git remote add origin git@github.com:YOUR_USERNAME/test-generator.git

# Verify remote
git remote -v

# Push to master branch
git branch -M master
git push -u origin master
```

### Step 3: Verify Push

Visit `https://github.com/YOUR_USERNAME/test-generator` and verify:
- ✓ Files uploaded
- ✓ README.md displays correctly
- ✓ Commit history visible

## After Initial Push

### Releases

Create a release on GitHub:

```bash
# Tag the release
git tag -a v1.0.0 -m "Test Generator MVP v1.0.0"

# Push tags
git push origin --tags
```

Then go to GitHub → Releases → Create Release from the tag.

### Branches

Recommended workflow:

```bash
# Create develop branch
git checkout -b develop
git push -u origin develop

# Feature branches
git checkout -b feature/new-feature
# ... make changes ...
git push -u origin feature/new-feature
# Create Pull Request on GitHub
```

## GitHub Actions

The CI/CD workflow is already configured in `.github/workflows/build.yml`.

It automatically:
- Builds on Linux, Windows, macOS
- Tests with Java 17 and 21
- Runs JUnit tests
- Tests CLI functionality

No additional setup required! Workflows start automatically on push.

## Protecting Master Branch

Recommended GitHub settings:

1. Go to Settings → Branches
2. Add rule for `master`
3. Enable:
   - ✓ Require pull request reviews
   - ✓ Require status checks to pass
   - ✓ Require branches to be up to date
   - ✓ Dismiss stale pull request approvals

## Adding to IDE/Editor Marketplaces (Future)

For IntelliJ Marketplace:
1. Create JetBrains account
2. Build plugin JAR: `./gradlew build`
3. Submit to https://plugins.jetbrains.com

For Maven Central:
1. Create account on Sonatype
2. Configure signing
3. Deploy via Gradle

## Continuous Deployment (Optional)

To auto-publish releases on GitHub:

Create `.github/workflows/release.yml` with automated build and release creation.

## Support

For questions about GitHub setup, refer to:
- https://docs.github.com/en/get-started
- https://docs.github.com/en/github/administering-a-repository

