package com.fsoft.mapper;

import com.fsoft.dto.CardCommentDetailsDto;
import com.fsoft.model.CardComment;

public class CardCommentMapper {
  private CardCommentMapper() {
  }

  public static CardCommentDetailsDto toCardCommentDetailsDto(CardComment cardComment) {
    if (cardComment == null) {
      return null;
    }

    CardCommentDetailsDto comment = new CardCommentDetailsDto();
    comment.setId(cardComment.getId());
    comment.setUser(UserMapper.toUserDto(cardComment.getUser()));
    comment.setContent(cardComment.getContent());
    comment.setCreatedAt(cardComment.getCreatedAt());

    return comment;
  }
}
