package servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.NivelBueiroSensor;
import dominio.PluviometroSensor;
import dominio.PontoVulneravel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

/** Testa a lógica de alertas com DAOs simulados (Mockito): sem banco de dados. */
class ServicoMonitoramentoTest {

    private MedicaoDAO medicaoDAO;
    private AlertaDAO alertaDAO;
    private PontoVulneravel ponto;
    private ServicoMonitoramento servico;

    @BeforeEach
    void preparar() throws Exception {
        medicaoDAO = mock(MedicaoDAO.class);
        alertaDAO = mock(AlertaDAO.class);
        ponto = new PontoVulneravel("PV-01", "Bueiro da Rua A", "Zona Leste");
        ponto.adicionarSensor(new PluviometroSensor("PLUV-01"));
        // Limiares iniciais de chuva: atenção 20, alerta 40, crítico 60
        servico = new ServicoMonitoramento(medicaoDAO, alertaDAO,
                new DadosTerritorio("Zona Leste", 1000), new FabricaPrevisao().criarTodas());
    }

    @Test
    void emiteAlertaQuandoPrevisaoUltrapassaLimiar() throws Exception {
        // Arrange (média móvel = 16, sem alerta; comparação = 30, acima de 20)
        when(medicaoDAO.buscarUltimosValores("PLUV-01", 6))
                .thenReturn(List.of(10.0, 10.0, 10.0, 20.0, 20.0, 20.0));

        // Act
        RelatorioPrevisao relatorio = servico.executarPrevisoes(ponto);

        // Assert
        assertEquals(1, relatorio.alertas().size());
        assertEquals(NivelAlerta.ATENCAO, relatorio.alertas().get(0).getNivel());
        verify(alertaDAO, times(1)).salvar(any(Alerta.class));
    }

    @Test
    void emiteAlertaCriticoQuandoPrevisaoPassaDoLimiteCritico() throws Exception {
        // Arrange (comparação = 50 + (50 - 10) = 90, acima de 60)
        when(medicaoDAO.buscarUltimosValores("PLUV-01", 6))
                .thenReturn(List.of(10.0, 10.0, 10.0, 50.0, 50.0, 50.0));

        // Act
        RelatorioPrevisao relatorio = servico.executarPrevisoes(ponto);

        // Assert
        Alerta critico = relatorio.alertas().stream()
                .filter(a -> a.getNivel() == NivelAlerta.CRITICO).findFirst().orElseThrow();
        assertEquals(60.0, critico.getLimiteUltrapassado());
        assertTrue(critico.getMotivo().contains("PLUV-01"));
    }

    @Test
    void naoEmiteAlertaQuandoPrevisoesFicamAbaixoDosLimiares() throws Exception {
        // Arrange
        when(medicaoDAO.buscarUltimosValores("PLUV-01", 6))
                .thenReturn(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0));

        // Act
        RelatorioPrevisao relatorio = servico.executarPrevisoes(ponto);

        // Assert
        assertTrue(relatorio.alertas().isEmpty());
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void poucasLeiturasNaoGeramAlertaEExplicamOMotivo() throws Exception {
        // Arrange (histórico curto demais para qualquer estratégia)
        when(medicaoDAO.buscarUltimosValores("PLUV-01", 6)).thenReturn(List.of(5.0, 6.0));

        // Act
        RelatorioPrevisao relatorio = servico.executarPrevisoes(ponto);

        // Assert
        assertTrue(relatorio.alertas().isEmpty());
        assertEquals(2, relatorio.linhas().size());
        assertTrue(relatorio.linhas().get(0).contains("pelo menos"));
        verify(alertaDAO, never()).salvar(any(Alerta.class));
    }

    @Test
    void gravaMedicoesERegistraSensorComFalha() throws Exception {
        // Arrange
        NivelBueiroSensor bueiro = new NivelBueiroSensor("BUEIRO-01");
        bueiro.setFuncionando(false);
        ponto.adicionarSensor(bueiro);

        // Act
        ResultadoLeitura resultado = servico.lerSensores(ponto);

        // Assert
        assertEquals(1, resultado.medicoes().size());
        assertEquals(1, resultado.falhas().size());
        assertTrue(resultado.falhas().get(0).contains("BUEIRO-01"));
        verify(medicaoDAO, times(1)).salvar(any(Medicao.class));
    }
}
