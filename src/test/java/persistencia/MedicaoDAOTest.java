package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dominio.Medicao;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Teste de integração com H2 em memória (não toca no banco real). */
class MedicaoDAOTest {

    private final ConexaoBanco conexao = new ConexaoBanco("jdbc:h2:mem:teste_medicao;DB_CLOSE_DELAY=-1");
    private final MedicaoDAO dao = new MedicaoDAO(conexao);

    @BeforeEach
    void prepararBanco() throws SQLException {
        conexao.criarTabelas();
        try (Connection c = conexao.abrir(); Statement s = c.createStatement()) {
            s.execute("DELETE FROM medicao");
        }
    }

    private Medicao medicao(String sensor, double valor, int minuto) throws Exception {
        return new Medicao(null, sensor, "Chuva", valor, LocalDateTime.of(2026, 11, 14, 12, minuto));
    }

    @Test
    void salvaEBuscaHistoricoDoSensor() throws Exception {
        // Arrange
        dao.salvar(medicao("PLUV-01", 10.0, 0));
        dao.salvar(medicao("PLUV-01", 20.0, 5));

        // Act
        List<Medicao> historico = dao.buscarHistoricoPorSensor("PLUV-01");

        // Assert (mais recente primeiro)
        assertEquals(2, historico.size());
        assertEquals(20.0, historico.get(0).getValor());
        assertNotNull(historico.get(0).getId());
    }

    @Test
    void historicoNaoMisturaSensores() throws Exception {
        // Arrange
        dao.salvar(medicao("PLUV-01", 10.0, 0));
        dao.salvar(medicao("PLUV-02", 99.0, 1));

        // Act
        List<Medicao> historico = dao.buscarHistoricoPorSensor("PLUV-02");

        // Assert
        assertEquals(1, historico.size());
        assertEquals("PLUV-02", historico.get(0).getSensorId());
    }

    @Test
    void ultimosValoresVoltamEmOrdemCronologica() throws Exception {
        // Arrange
        dao.salvar(medicao("PLUV-01", 10.0, 0));
        dao.salvar(medicao("PLUV-01", 20.0, 5));
        dao.salvar(medicao("PLUV-01", 30.0, 10));

        // Act
        List<Double> ultimos = dao.buscarUltimosValores("PLUV-01", 2);

        // Assert
        assertEquals(List.of(20.0, 30.0), ultimos);
        assertTrue(dao.buscarUltimosValores("SEM-DADOS", 5).isEmpty());
    }
}
