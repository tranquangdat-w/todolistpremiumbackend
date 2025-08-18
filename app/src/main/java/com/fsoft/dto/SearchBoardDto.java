package com.fsoft.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class SearchBoardDto {
    private UUID id;
    private String title;
}