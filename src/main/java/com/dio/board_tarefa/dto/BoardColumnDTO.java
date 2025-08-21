package com.dio.board_tarefa.dto;

import com.dio.board_tarefa.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
                String name,
                BoardColumnKindEnum kind,
                int cardsAmount) {
}
