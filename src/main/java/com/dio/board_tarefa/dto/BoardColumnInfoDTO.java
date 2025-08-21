package com.dio.board_tarefa.dto;

import com.dio.board_tarefa.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {
}