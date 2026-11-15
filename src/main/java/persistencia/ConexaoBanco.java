package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Sabe abrir conexões com o banco H2 e criar as tabelas.
 * A URL é recebida no construtor para os testes poderem usar um banco em memória.
 */
public class ConexaoBanco {

    private static final String URL_ARQUIVO = "jdbc:h2:file:./dados/territorio";

    private final String url;

    public ConexaoBanco(String url) {
        this.url = url;
    }

    /** Banco em arquivo (pasta ./dados): os dados sobrevivem ao reiniciar o programa. */
    public static ConexaoBanco padrao() {
        return new ConexaoBanco(URL_ARQUIVO);
    }

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    public void criarTabelas() throws SQLException {
        try (Connection conexao = abrir(); Statement comando = conexao.createStatement()) {
            comando.execute("CREATE TABLE IF NOT EXISTS medicao ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "sensor_id VARCHAR(50) NOT NULL, "
                    + "tipo VARCHAR(50) NOT NULL, "
                    + "valor DOUBLE NOT NULL, "
                    + "data_hora TIMESTAMP NOT NULL)");
            comando.execute("CREATE TABLE IF NOT EXISTS alerta ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "sensor_id VARCHAR(50) NOT NULL, "
                    + "tipo VARCHAR(50) NOT NULL, "
                    + "nivel VARCHAR(20) NOT NULL, "
                    + "valor_previsto DOUBLE NOT NULL, "
                    + "limite_ultrapassado DOUBLE NOT NULL, "
                    + "data_hora TIMESTAMP NOT NULL)");
        }
    }
}
