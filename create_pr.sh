#!/bin/bash

# Thay đổi các giá trị sau đây
GITHUB_TOKEN="your_personal_access_token"
REPO_OWNER="tranquangdat-w"
REPO_NAME="todolistpremiumbackend"
HEAD_BRANCH="feature/notification-system"
BASE_BRANCH="main"  # hoặc master, tùy thuộc vào nhánh chính của bạn
PR_TITLE="Add notification system with multiple notification types"
PR_BODY="## Changes
- Added Notification model and NotificationType enum
- Created DTOs for different notification types (Invitation, Accepted, Rejected, Comment)
- Implemented NotificationRepository with methods to find and update notification read status
- Created NotificationService to handle notification business logic
- Added NotificationController for API endpoints
- All notification types include isRead field as requested

## Features
- Structured JSON response for different notification types
- Support for marking notifications as read individually or all at once
- Customized data for each notification type"

# Tạo pull request thông qua GitHub API
curl -X POST \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/pulls \
  -d "{\"title\":\"$PR_TITLE\",\"head\":\"$HEAD_BRANCH\",\"base\":\"$BASE_BRANCH\",\"body\":\"$PR_BODY\"}"
