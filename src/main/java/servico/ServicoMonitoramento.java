package servico;

import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.EstrategiaPrevisao;
import dominio.Limiares;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.PontoVulneravel;
import dominio.Sensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

/**
 * Regras do sistema: ler sensores e gravar medições, rodar as previsões
 * sobre o histórico do banco e emitir alertas comparando com os limiares.
 * Recebe as estratégias prontas, então não conhece as classes concretas.
 */
public class ServicoMonitoramento {

    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final DadosTerritorio dados;
    private final List<EstrategiaPrevisao> estrategias;
    private final int leiturasNecessarias;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO,
                                DadosTerritorio dados, List<EstrategiaPrevisao> estrategias) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.dados = dados;
        this.estrategias = estrategias;
        // Busca no banco leituras suficientes para a estratégia mais exigente
        int maior = 0;
        for (EstrategiaPrevisao estrategia : estrategias) {
            maior = Math.max(maior, estrategia.getMinimoLeituras());
        }
        this.leiturasNecessarias = maior;
    }

    /** Lê todos os sensores do ponto e grava cada medição válida. Uma falha não interrompe as demais. */
    public ResultadoLeitura lerSensores(PontoVulneravel ponto) throws SQLException {
        List<Medicao> medicoes = new ArrayList<>();
        List<String> falhas = new ArrayList<>();
        for (Sensor sensor : ponto.getSensores()) {
            try {
                Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), sensor.lerValor());
                medicaoDAO.salvar(medicao);
                medicoes.add(medicao);
            } catch (FonteIndisponivelException e) {
                falhas.add(e.getMessage());
            } catch (DadosInvalidosException e) {
                falhas.add("Leitura descartada: " + e.getMessage());
            }
        }
        return new ResultadoLeitura(medicoes, falhas);
    }

    /** Roda cada estratégia sobre o histórico de cada sensor e emite alertas quando há limiar estourado. */
    public RelatorioPrevisao executarPrevisoes(PontoVulneravel ponto) throws SQLException {
        List<String> linhas = new ArrayList<>();
        List<Alerta> alertas = new ArrayList<>();
        for (Sensor sensor : ponto.getSensores()) {
            List<Double> leituras = medicaoDAO.buscarUltimosValores(sensor.getId(), leiturasNecessarias);
            for (EstrategiaPrevisao estrategia : estrategias) {
                try {
                    double previsto = estrategia.prever(leituras);
                    linhas.add(sensor.getId() + " | " + estrategia.descrever(previsto));
                    Limiares limiares = dados.getLimiares(sensor.getTipo());
                    Optional<NivelAlerta> nivel = limiares.classificar(previsto);
                    if (nivel.isPresent()) {
                        Alerta alerta = new Alerta(sensor.getId(), sensor.getTipo(), nivel.get(),
                                previsto, limiares.getLimite(nivel.get()));
                        alertaDAO.salvar(alerta);
                        alertas.add(alerta);
                    }
                } catch (DadosInvalidosException e) {
                    linhas.add(sensor.getId() + " | " + estrategia.getNome() + ": sem previsão - " + e.getMessage());
                }
            }
        }
        return new RelatorioPrevisao(linhas, alertas);
    }

    public List<Medicao> listarHistorico(String sensorId) throws SQLException {
        return medicaoDAO.buscarHistoricoPorSensor(sensorId);
    }

    public List<Alerta> listarAlertas() throws SQLException {
        return alertaDAO.listarTodos();
    }
}
