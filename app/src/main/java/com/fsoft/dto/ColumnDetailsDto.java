package com.fsoft.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ColumnDetailsDto {
  private UUID id;
  private String title;
  private Date createdAt;
  private BigDecimal position;
  private List<CardDetailsDto> cards;

}
