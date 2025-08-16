package com.fsoft.service;

import com.fsoft.dto.CardCommentDetailsDto;
import com.fsoft.dto.CreateCardCommentDto;
import com.fsoft.dto.UpdateCardCommentDto;
import com.fsoft.exceptions.ApiException;
import com.fsoft.mapper.CardCommentMapper;
import com.fsoft.model.Card;
import com.fsoft.model.CardComment;
import com.fsoft.model.User;
import com.fsoft.repository.CardCommentRepository;
import com.fsoft.repository.CardRepository;
import com.fsoft.repository.UserRepository;
import com.fsoft.security.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardCommentService {
    private final CardCommentRepository cardCommentRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardCommentDetailsDto createComment(CreateCardCommentDto createDto, JwtPayload payload) {
        Card card = cardRepository.findById(createDto.getCardId())
                .orElseThrow(() -> new ApiException("Card not found", HttpStatus.NOT_FOUND.value()));
        User user = userRepository.findById(payload.getId())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND.value()));

        CardComment newComment = new CardComment();
        newComment.setCards(card);
        newComment.setUser(user);
        newComment.setContent(createDto.getContent());
        newComment.setCreatedAt(Instant.now());

        CardComment savedComment = cardCommentRepository.save(newComment);
        return CardCommentMapper.toCardCommentDetailsDto(savedComment);
    }

    public CardCommentDetailsDto updateComment(UUID commentId, UpdateCardCommentDto updateDto, JwtPayload payload) {
        CardComment comment = cardCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException("Comment not found", HttpStatus.NOT_FOUND.value()));

        if (!comment.getUser().getId().equals(payload.getId())) {
            throw new ApiException("You are not the owner of this comment", HttpStatus.FORBIDDEN.value());
        }

        comment.setContent(updateDto.getContent());
        CardComment updatedComment = cardCommentRepository.save(comment);
        return CardCommentMapper.toCardCommentDetailsDto(updatedComment);
    }

    public void deleteComment(UUID commentId, JwtPayload payload) {
        CardComment comment = cardCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException("Comment not found", HttpStatus.NOT_FOUND.value()));

        if (!comment.getUser().getId().equals(payload.getId())) {
            throw new ApiException("You are not the owner of this comment", HttpStatus.FORBIDDEN.value());
        }

        cardCommentRepository.delete(comment);
    }
}