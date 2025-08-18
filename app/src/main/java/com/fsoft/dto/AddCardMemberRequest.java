package com.fsoft.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AddCardMemberRequest {
  private UUID memberId;
  private UUID boardId;
}
