# Setup GitHub Authentication - Windows/Mac/Linux

## Option 1: Using GitHub Personal Access Token (Recommended for HTTPS)

### Step 1: Create Personal Access Token on GitHub

1. Go to: https://github.com/settings/tokens
2. Click "Generate new token" → "Generate new token (classic)"
3. Name: `test-generator-push`
4. Select scopes:
   - ✓ repo (full control of repositories)
   - ✓ gist
5. Click "Generate token"
6. **Copy the token** (you won't see it again)

### Step 2: Configure Git with Token

```bash
cd /home/giorgio/IdeaProjects/IntelliJ_Platform_Plugin

# Configure local repository
git config credential.helper store

# Try to push - Git will ask for credentials
git push -u origin master

# When prompted:
# Username: giofrac
# Password: (paste your token here)
```

Git will save the token locally for future use.

---

## Option 2: Using SSH Key (More Secure)

### Step 1: Generate SSH Key

```bash
# Generate key (if you don't have one)
ssh-keygen -t ed25519 -C "giorgio@giofrac.com"

# Press Enter for default location
# Enter passphrase (or leave empty)
```

### Step 2: Add SSH Key to GitHub

1. Copy your public key:
```bash
cat ~/.ssh/id_ed25519.pub
```

2. Go to: https://github.com/settings/keys
3. Click "New SSH key"
4. Title: `test-generator`
5. Paste the key
6. Click "Add SSH key"

### Step 3: Test SSH Connection

```bash
ssh -T git@github.com
# Should output: Hi giofrac! You've successfully authenticated...
```

### Step 4: Push to GitHub

```bash
cd /home/giorgio/IdeaProjects/IntelliJ_Platform_Plugin
git remote remove origin
git remote add origin git@github.com:giofrac/test-generator.git
git push -u origin master
```

---

## Troubleshooting

**Getting "Permission denied" with SSH?**
- Ensure SSH key is added to GitHub
- Verify with: `ssh -T git@github.com`

**Token doesn't work?**
- Token might have expired
- Create a new one at https://github.com/settings/tokens
- Make sure scopes include "repo"

**Still having issues?**

Try manual HTTPS with inline token:
```bash
git remote add origin https://giofrac:YOUR_TOKEN@github.com/giofrac/test-generator.git
git push -u origin master
```

Replace `YOUR_TOKEN` with your actual token.

---

## After Authentication Setup

Once you've set up authentication, run:

```bash
cd /home/giorgio/IdeaProjects/IntelliJ_Platform_Plugin
git push -u origin master
```

Then verify at: https://github.com/giofrac/test-generator

---

## Recommended: Option 1 (Token) for Quick Setup

Fastest way:
1. Get token from https://github.com/settings/tokens
2. Run: `git push -u origin master`
3. Provide username and token when prompted
4. Done!

