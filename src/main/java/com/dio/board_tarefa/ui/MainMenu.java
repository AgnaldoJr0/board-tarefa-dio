package com.dio.board_tarefa.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.dio.board_tarefa.persistence.config.ConnectionConfig.getConnection;
import com.dio.board_tarefa.persistence.entity.BoardColumnEntity;
import com.dio.board_tarefa.persistence.entity.BoardColumnKindEnum;
import static com.dio.board_tarefa.persistence.entity.BoardColumnKindEnum.CANCEL;
import static com.dio.board_tarefa.persistence.entity.BoardColumnKindEnum.FINAL;
import static com.dio.board_tarefa.persistence.entity.BoardColumnKindEnum.INITIAL;
import static com.dio.board_tarefa.persistence.entity.BoardColumnKindEnum.PENDING;
import com.dio.board_tarefa.persistence.entity.BoardEntity;
import com.dio.board_tarefa.service.BoardQueryService;
import com.dio.board_tarefa.service.BoardService;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao gerenciador de boards, escolha a opção desejada");

        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");

            try {
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("Por favor, digite uma opção.");
                    continue;
                }

                try {
                    int option = Integer.parseInt(input);

                    switch (option) {
                        case 1 -> createBoard();
                        case 2 -> selectBoard();
                        case 3 -> deleteBoard();
                        case 4 -> {
                            System.out.println("Saindo...");
                            System.exit(0);
                        }
                        default -> System.out.println("Opção inválida, informe uma opção do menu");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Erro: Por favor, digite apenas números para selecionar uma opção.");
                }
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
    }

    private void createBoard() throws SQLException {
        try {
            var entity = new BoardEntity();

            System.out.println("Informe o nome do seu board");
            String boardName = scanner.nextLine().trim();
            while (boardName.isEmpty()) {
                System.out.println("Nome não pode ser vazio. Informe o nome do seu board:");
                boardName = scanner.nextLine().trim();
            }
            entity.setName(boardName);

            System.out.println("Seu board terá colunas além das 3 padrões? Se sim informe quantas, senão digite '0'");
            int additionalColumns = readIntSafe("Por favor, digite um número válido.");

            List<BoardColumnEntity> columns = new ArrayList<>();

            System.out.println("Informe o nome da coluna inicial do board");
            String initialColumnName = scanner.nextLine().trim();
            while (initialColumnName.isEmpty()) {
                System.out.println("Nome não pode ser vazio. Informe o nome da coluna inicial:");
                initialColumnName = scanner.nextLine().trim();
            }
            var initialColumn = createColumn(initialColumnName, INITIAL, 0);
            columns.add(initialColumn);

            for (int i = 0; i < additionalColumns; i++) {
                System.out.println("Informe o nome da coluna de tarefa pendente " + (i + 1) + ":");
                String pendingColumnName = scanner.nextLine().trim();
                while (pendingColumnName.isEmpty()) {
                    System.out.println("Nome não pode ser vazio. Informe o nome da coluna:");
                    pendingColumnName = scanner.nextLine().trim();
                }
                var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
                columns.add(pendingColumn);
            }

            System.out.println("Informe o nome da coluna final");
            String finalColumnName = scanner.nextLine().trim();
            while (finalColumnName.isEmpty()) {
                System.out.println("Nome não pode ser vazio. Informe o nome da coluna final:");
                finalColumnName = scanner.nextLine().trim();
            }
            var finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1);
            columns.add(finalColumn);

            System.out.println("Informe o nome da coluna de cancelamento do board");
            String cancelColumnName = scanner.nextLine().trim();
            while (cancelColumnName.isEmpty()) {
                System.out.println("Nome não pode ser vazio. Informe o nome da coluna de cancelamento:");
                cancelColumnName = scanner.nextLine().trim();
            }
            var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2);
            columns.add(cancelColumn);

            entity.setBoardColumns(columns);

            try (var connection = getConnection()) {
                var service = new BoardService(connection);
                service.insert(entity);
                System.out.println("Board criado com sucesso!");
            }

        } catch (Exception e) {
            System.out.println("Erro ao criar board: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectBoard() throws SQLException {
        try {
            System.out.println("Informe o id do board que deseja selecionar");
            long id = readIntSafe("Por favor, digite um ID válido.");

            try (var connection = getConnection()) {
                var queryService = new BoardQueryService(connection);
                var optional = queryService.findById(id);
                optional.ifPresentOrElse(
                        b -> new BoardMenu(b).execute(),
                        () -> System.out.printf("Não foi encontrado um board com id %s\n", id));
            }
        } catch (Exception e) {
            System.out.println("Erro ao selecionar board: " + e.getMessage());
        }
    }

    private void deleteBoard() throws SQLException {
        try {
            System.out.println("Informe o id do board que será excluido");
            long id = readIntSafe("Por favor, digite um ID válido.");

            try (var connection = getConnection()) {
                var service = new BoardService(connection);
                if (service.delete(id)) {
                    System.out.printf("O board %s foi excluido\n", id);
                } else {
                    System.out.printf("Não foi encontrado um board com id %s\n", id);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao excluir board: " + e.getMessage());
        }
    }

    private int readIntSafe(String errorMessage) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
    }

    private long readLongSafe(String errorMessage) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}