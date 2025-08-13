package com.fsoft.service;

import com.fsoft.dto.CardCommentRegistrationRequest;
import com.fsoft.dto.CardCommentUpdateRequest;
import com.fsoft.model.CardComment;
import com.fsoft.dto.CardCommentDto;

import java.util.List;
import java.util.UUID;

public interface CardCommentService {
    public List<CardCommentDto> getCardComments(UUID cardId);

    public CardCommentDto addCardComment(CardCommentRegistrationRequest cardCommentRegistrationRequest);

    public void deleteCardComment(UUID cardCommentId, UUID userId);

    public CardCommentDto updateCardComment(CardCommentUpdateRequest cardCommentUpdateRequest, UUID userId, UUID cardCommentId);
}
