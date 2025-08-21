package com.dio.board_tarefa.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import com.dio.board_tarefa.dto.CardDetailsDTO;
import com.dio.board_tarefa.persistence.dao.CardDAO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardQueryService {

    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var dao = new CardDAO(connection);
        return dao.findById(id);
    }
}
