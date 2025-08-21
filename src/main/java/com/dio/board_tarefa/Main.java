package com.dio.board_tarefa;

import java.sql.SQLException;

import static com.dio.board_tarefa.persistence.config.ConnectionConfig.getConnection;
import com.dio.board_tarefa.persistence.migration.MigrationStrategy;
import com.dio.board_tarefa.ui.MainMenu;

public class Main {

	public static void main(String[] args) throws SQLException {
		try (var connection = getConnection()) {
			new MigrationStrategy(connection).executeMigration();
		}
		new MainMenu().execute();
	}

}
