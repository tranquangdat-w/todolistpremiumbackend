package com.fsoft.service;

import com.fsoft.dto.CardDetailsDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.fsoft.dto.CardUpdateRequest;
import com.fsoft.dto.CreateCardRequest;
import com.fsoft.exceptions.ApiException;
import com.fsoft.mapper.CardMapper;
import com.fsoft.model.Board;
import com.fsoft.model.Card;
import com.fsoft.model.CardMember;
import com.fsoft.model.CardMemberId;
import com.fsoft.model.Columnn;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.CardRepository;
import com.fsoft.repository.CardMemberRepository;
import com.fsoft.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {
  private final CardRepository cardRepository;
  private final BoardRepository boardRepository;
  private final DropboxService dropboxService;
  private final CardMemberRepository cardMemberRepository;
  private final UserRepository userRepository;
  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public CardDetailsDto createCard(UUID userId, CreateCardRequest request) {
    UUID boardId = UUID.fromString(request.getBoardId());
    UUID columnId = UUID.fromString(request.getColumnId());
    Board board = checkBoardExist(boardId);

    checkPermisstionOfUser(board, userId);
    checkBoardContainsColumn(board, columnId);

    Columnn column = new Columnn();
    column.setId(columnId);

    Card card = new Card();
    card.setTitle(request.getTitle());
    card.setColumn(column);
    card.setIsDone(false);
    card.setPosition(request.getPosition());
    card.setCreatedAt(new Date());

    cardRepository.save(card);

    return CardMapper.toCardDetailsDto(card);
  }

  @Transactional
  public CardDetailsDto updateCard(UUID userId, UUID cardId, CardUpdateRequest request) {
    UUID boardId = UUID.fromString(request.getBoardId());

    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);

    Card card = checkCardExist(cardId);

    if (request.getColumnId() != null) {
      Columnn column = new Columnn();
      column.setId(UUID.fromString(request.getColumnId()));
      card.setColumn(column);
    }

    if (request.getDescription() != null) {
      card.setDescription(request.getDescription());
    }

    if (request.getTitle() != null) {
      card.setTitle(request.getTitle());
    }

    if (request.getPosition() != null) {
      card.setPosition(request.getPosition());
    }

    if (request.getIsDone() != null) {
      card.setIsDone(request.getIsDone());
    }

    if (request.getDeadline() != null) {
      card.setDeadline(request.getDeadline());
    }

    cardRepository.save(card);

    return CardMapper.toCardDetailsDto(card);
  }

  @Transactional
  public void deleteCard(UUID boardId, UUID cardId, UUID userId) {
    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);

    Card card = new Card();
    card.setId(cardId);

    cardRepository.delete(card);
  }

  public CardDetailsDto getCardDetails(UUID userId, UUID boardId, UUID cardId) {
    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);

    Card card = checkCardExist(cardId);

    return CardMapper.toCardDetailsDto(card);
  }

  private Card checkCardExist(UUID cardId) {
    return cardRepository.findById(cardId)
        .orElseThrow(() -> new ApiException("Card not found", HttpStatus.NOT_FOUND.value()));
  }

  private void checkBoardContainsColumn(Board board, UUID columnId) {
    boolean exists = board.getColumns().stream()
        .anyMatch(column -> column.getId().equals(columnId));

    if (!exists) {
      throw new ApiException(
          "Column does not belong to the specified board",
          HttpStatus.BAD_REQUEST.value());
    }
  }

  private void checkPermisstionOfUser(Board board, UUID userId) {
    if (!board.getOwner().getId().equals(userId)
        && !board.getMembers().stream().anyMatch(member -> member.getId().equals(userId))) {
      throw new ApiException("You don't have permission to view this board",
          HttpStatus.FORBIDDEN.value());
    }
  }

  private Board checkBoardExist(UUID boardId) {
    return boardRepository.findById(boardId)
        .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));
  }

  @Transactional
  public CardDetailsDto updateCardCover(UUID userId, UUID boardId, UUID cardId, MultipartFile coverFile) {
    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);

    Card card = checkCardExist(cardId);

    try {
      String url = dropboxService
          .uploadCardCover(coverFile, cardId)
          .orElseThrow(
              () -> new ApiException("Some error occur when upload cover"));

      card.setCover(url);

      cardRepository.save(card);

      return CardMapper.toCardDetailsDto(card);
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
      throw new ApiException(e.getMessage());
    }
  }

  @Transactional
  public CardDetailsDto addMemberToCard(UUID userId, UUID boardId, UUID cardId, UUID memberId) {
    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);

    Card card = checkCardExist(cardId);

    userRepository.findById(memberId)
        .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND.value()));

    boolean isBoardMember = board.getMembers().stream().anyMatch(u -> u.getId().equals(memberId))
        || board.getOwner().getId().equals(memberId);
    if (!isBoardMember) {
      throw new ApiException("User is not a member of this board", HttpStatus.BAD_REQUEST.value());
    }

    boolean isCardMember = card.getCardMembers().stream().anyMatch(m -> m.getUserId().equals(memberId));
    if (isCardMember) {
      throw new ApiException("User is already a member of this card", HttpStatus.BAD_REQUEST.value());
    }

    CardMember cardMember = new CardMember();
    cardMember.setCardId(cardId);
    cardMember.setUserId(memberId);

    cardMemberRepository.saveAndFlush(cardMember);

    entityManager.clear();
    Card updatedCard = checkCardExist(cardId);
    return CardMapper.toCardDetailsDto(updatedCard);
  }

  @Transactional
  public CardDetailsDto removeMemberFromCard(UUID userId, UUID boardId, UUID cardId, UUID memberId) {
    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);

    checkCardExist(cardId);

    CardMemberId cardMemberId = new CardMemberId(cardId, memberId);

    CardMember cardMember = cardMemberRepository.findById(cardMemberId)
        .orElseThrow(() -> new ApiException("User is not a member of this card", HttpStatus.BAD_REQUEST.value()));

    cardMemberRepository.delete(cardMember);
    cardMemberRepository.flush();

    Card updatedCard = checkCardExist(cardId);
    return CardMapper.toCardDetailsDto(updatedCard);
  }
}
