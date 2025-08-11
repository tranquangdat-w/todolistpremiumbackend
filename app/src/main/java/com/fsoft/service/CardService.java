package com.fsoft.service;

import com.fsoft.exceptions.ApiException;
import com.fsoft.model.Cards;
import com.fsoft.model.Boards;
import com.fsoft.repository.CardRepository;
import com.fsoft.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {
    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;

    public void deleteCard(UUID boardId, UUID cardId, UUID userId) {
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));

        if (!board.getUser().getId().equals(userId)) {
            throw new ApiException("You don't have permission to delete this card", HttpStatus.FORBIDDEN.value());
        }

        Cards card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ApiException("Card not found", HttpStatus.NOT_FOUND.value()));

        cardRepository.delete(card);
    }

    public void pushToMain(UUID boardId, UUID cardId, UUID userId) {
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));

        if (!board.getUser().getId().equals(userId)) {
            throw new ApiException("You don't have permission to push this card", HttpStatus.FORBIDDEN.value());
        }

        Cards card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ApiException("Card not found", HttpStatus.NOT_FOUND.value()));

//        card.setMainBranch(true);
        cardRepository.save(card);
    }
}
