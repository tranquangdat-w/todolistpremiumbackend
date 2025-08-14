package com.fsoft.service;

import com.fsoft.dto.BoardDto;
import com.fsoft.dto.CreateBoardDto;
import com.fsoft.dto.UpdateBoardDto;
import com.fsoft.exceptions.ApiException;
import com.fsoft.model.Boards;
import com.fsoft.model.User;
import com.fsoft.repository.BoardRepository;
import com.fsoft.mapper.BoardMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public void createBoard(UUID userId, CreateBoardDto createBoardDto) {
        Boards board = new Boards();

        User user = new User();
        user.setId(userId);

        if (createBoardDto.getDescription() != null) {
            board.setDescription(createBoardDto.getDescription());
        }

        board.setTitle(createBoardDto.getTitle());
        board.setUser(user);
        board.setCreatedAt(LocalDate.now());

        boardRepository.save(board);
    }

    @Transactional
    public void updateBoard(UUID boardId, UUID userId, UpdateBoardDto updateBoardDto) {
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));

        if (!board.getUser().getId().equals(userId)) {
            throw new ApiException("You don't have permission to update this board", HttpStatus.FORBIDDEN.value());
        }

        if (updateBoardDto.getTitle() != null) {
            board.setTitle(updateBoardDto.getTitle());
        }

        if (updateBoardDto.getDescription() != null) {
            board.setDescription(updateBoardDto.getDescription());
        }

        boardRepository.save(board);
    }

    @Transactional
    public void deleteBoard(UUID boardId, UUID userId) {
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));

        if (!board.getUser().getId().equals(userId)) {
            throw new ApiException("You don't have permission to delete this board", HttpStatus.FORBIDDEN.value());
        }

        boardRepository.delete(board);
    }

    public BoardDto getBoardDetail(UUID boardId, UUID userId) {
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));

        if (!board.getUser().getId().equals(userId)) {
            throw new ApiException("You don't have permission to view this board", HttpStatus.FORBIDDEN.value());
        }

        return BoardMapper.toBoardDto(board);
    }

    public Page<BoardDto> getBoardsByUserId(UUID userId, Pageable pageable) {
        return boardRepository.findByUser_Id(userId, pageable).map(BoardMapper::toBoardDto);
    }

    public List<Boards> searchBoardByKeyword(UUID owner_id, String keyword) {
        if (keyword.isBlank()) {
            return Collections.emptyList();
        }
        return boardRepository.findByBoardNameContaining(owner_id, keyword);
    }
}
