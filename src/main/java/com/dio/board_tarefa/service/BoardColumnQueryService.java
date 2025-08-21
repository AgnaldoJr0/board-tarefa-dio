package com.dio.board_tarefa.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import com.dio.board_tarefa.persistence.dao.BoardColumnDAO;
import com.dio.board_tarefa.persistence.entity.BoardColumnEntity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardColumnQueryService {

    private final Connection connection;

    public Optional<BoardColumnEntity> findById(final long id) throws SQLException {
        var dao = new BoardColumnDAO(connection);
        return dao.findById(id);
    }
}
