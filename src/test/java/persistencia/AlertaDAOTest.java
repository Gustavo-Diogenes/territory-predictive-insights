package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dominio.Alerta;
import dominio.NivelAlerta;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Teste de integração com H2 em memória (não toca no banco real). */
class AlertaDAOTest {

    private final ConexaoBanco conexao = new ConexaoBanco("jdbc:h2:mem:teste_alerta;DB_CLOSE_DELAY=-1");
    private final AlertaDAO dao = new AlertaDAO(conexao);

    @BeforeEach
    void prepararBanco() throws SQLException {
        conexao.criarTabelas();
        try (Connection c = conexao.abrir(); Statement s = c.createStatement()) {
            s.execute("DELETE FROM alerta");
        }
    }

    @Test
    void salvaEListaAlertaComTodosOsCampos() throws Exception {
        // Arrange
        LocalDateTime hora = LocalDateTime.of(2026, 11, 14, 15, 30);
        Alerta alerta = new Alerta(null, "PLUV-01", "Chuva", NivelAlerta.CRITICO, 65.5, 60.0, hora);

        // Act
        dao.salvar(alerta);
        List<Alerta> alertas = dao.listarTodos();

        // Assert
        assertEquals(1, alertas.size());
        Alerta lido = alertas.get(0);
        assertEquals("PLUV-01", lido.getSensorId());
        assertEquals(NivelAlerta.CRITICO, lido.getNivel());
        assertEquals(65.5, lido.getValorPrevisto());
        assertEquals(60.0, lido.getLimiteUltrapassado());
        assertEquals(hora, lido.getDataHora());
    }
}
