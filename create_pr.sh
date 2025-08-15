#!/bin/bash

# Change these values
GITHUB_REPO="tranquangdat-w/todolistpremiumbackend"
SOURCE_BRANCH="feature/notification-system"
TARGET_BRANCH="main"
PR_TITLE="Merge notification system feature into main"
PR_BODY="This PR merges the notification system feature into main branch. It fixes missing files, logic issues, and adds new functionality for notifications."

# Ensure we're on the source branch
git checkout $SOURCE_BRANCH

# Make sure local branches are up to date
git fetch origin

# Create pull request using GitHub CLI if installed
if command -v gh &> /dev/null; then
  echo "Creating pull request using GitHub CLI..."
  gh pr create --repo $GITHUB_REPO --base $TARGET_BRANCH --head $SOURCE_BRANCH --title "$PR_TITLE" --body "$PR_BODY"
else
  # Alternative: open browser to create PR
  echo "GitHub CLI not found. Opening browser to create PR manually..."
  PR_URL="https://github.com/$GITHUB_REPO/compare/$TARGET_BRANCH...$SOURCE_BRANCH?expand=1&title=$(echo $PR_TITLE | sed 's/ /%20/g')&body=$(echo $PR_BODY | sed 's/ /%20/g')"

  # Try to open URL in browser (platform-dependent)
  if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    start "$PR_URL"
  elif [[ "$OSTYPE" == "darwin"* ]]; then
    open "$PR_URL"
  elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    xdg-open "$PR_URL"
  else
    echo "Please visit this URL to create your PR:"
    echo "$PR_URL"
  fi
fi
