package com.fsoft.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsoft.dto.CardCommentDto;
import com.fsoft.dto.CardCommentRegistrationRequest;
import com.fsoft.dto.CardCommentUpdateRequest;
import com.fsoft.exceptions.ApiException;
import com.fsoft.model.CardComment;
import com.fsoft.model.Cards;
import com.fsoft.model.User;
import com.fsoft.repository.CardCommentRepository;
import com.fsoft.repository.CardRepository;
import com.fsoft.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardCommentServiceImpl implements CardCommentService {
    @Autowired
    private final CardCommentRepository cardCommentRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CardRepository cardRepository;
    @Autowired
    private final ObjectMapper objectMapper;

    @Override
    public List<CardCommentDto> getCardComments(UUID cardId) {
        Cards cards = cardRepository.findById(cardId)
                .orElseThrow(() -> new ApiException("Card not found"));
        ArrayList<CardComment> cardComments = cardCommentRepository.findByCardsCardId(cardId);
        ArrayList<CardCommentDto> cardCommentDtos = new ArrayList<>();
        for (CardComment cardComment : cardComments) {
            CardCommentDto dto = new CardCommentDto();

            dto.setId(cardComment.getId());
            dto.setContent(cardComment.getContent());
            dto.setCreatedAt(cardComment.getCreatedAt());
            dto.setUserAvatarUrl(cardComment.getUserAvatarUrl());
            dto.setUserId(cardComment.getUser().getId());
            dto.setCardId(cardComment.getCards().getCardId());
            cardCommentDtos.add(dto);
        }
        return cardCommentDtos;
    }

    @Override
    public CardCommentDto addCardComment(CardCommentRegistrationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ApiException("user not found"));
        Cards card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new ApiException("card not found"));
        CardComment newCardComment = objectMapper.convertValue(request, CardComment.class);
        newCardComment.setUser(user);
        newCardComment.setCards(card);
        newCardComment.setUserAvatarUrl(user.getAvatar());
        cardCommentRepository.save(newCardComment);

        CardCommentDto cardComment = new CardCommentDto();
        cardComment.setId(newCardComment.getId());
        cardComment.setUserId(user.getId());
        cardComment.setUserId(user.getId());
        cardComment.setUserAvatarUrl(newCardComment.getUserAvatarUrl());
        cardComment.setContent(request.getContent());
        cardComment.setCreatedAt(newCardComment.getCreatedAt());

        return cardComment;
    }

    @Override
    public void deleteCardComment(UUID cardCommentId, UUID userId) {
        CardComment cardComment = cardCommentRepository.findById(cardCommentId)
                .orElseThrow(() -> new ApiException("Card comment not found"));
        if (!cardComment.getUser().getId().equals(userId)) {
            throw new ApiException("user id not authorized");
        }
        cardCommentRepository.delete(cardComment);
    }

    @Override
    public CardCommentDto updateCardComment(CardCommentUpdateRequest cardCommentUpdateRequest,
                                            UUID userId,
                                            UUID cardCommentId) {
        System.out.println("Card comment id: " + cardCommentId);
        CardComment cardComment = cardCommentRepository.findById(cardCommentId)
                .orElseThrow(() -> new ApiException("Card comment not found", 404));

        if (!cardComment.getUser().getId().equals(userId)) {
            throw new ApiException("user is not authorized");
        }

        cardComment.setContent(cardCommentUpdateRequest.getContent());
        cardCommentRepository.save(cardComment);

        CardCommentDto dto = new CardCommentDto();
        dto.setId(cardComment.getId());
        dto.setUserId(cardComment.getUser().getId());
        dto.setUserAvatarUrl(cardComment.getUserAvatarUrl());
        dto.setContent(cardComment.getContent());
        dto.setCreatedAt(cardComment.getCreatedAt());
        return dto;
    }
}
