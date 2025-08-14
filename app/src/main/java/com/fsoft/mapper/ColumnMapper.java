package com.fsoft.mapper;

import com.fsoft.dto.ColumnDetailsDto;
import com.fsoft.model.Columnn;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class ColumnMapper {
  private ColumnMapper() {
  }

  public static ColumnDetailsDto toColumnDetailsDto(Columnn column) {
    if (column == null) {
      return null;
    }

    ColumnDetailsDto columnDetailsDto = new ColumnDetailsDto();
    columnDetailsDto.setId(column.getId());
    columnDetailsDto.setTitle(column.getTitle());
    columnDetailsDto.setCreatedAt(column.getCreatedAt());
    columnDetailsDto.setPosition(column.getPosition());
    if (column.getCards() != null) {
      columnDetailsDto
          .setCards(column.getCards()
              .stream()
              .map(CardMapper::toCardDetailsDto)
              .collect(Collectors.toList()));
    } else {
      columnDetailsDto.setCards(new LinkedList<>());
    }
    return columnDetailsDto;
  }
}
