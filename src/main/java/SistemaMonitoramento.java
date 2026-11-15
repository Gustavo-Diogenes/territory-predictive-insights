import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.Limiares;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.NivelBueiroSensor;
import dominio.PluviometroSensor;
import dominio.PontoVulneravel;
import dominio.SensorSimulado;
import dominio.UmidadeSoloSensor;
import excecao.DadosInvalidosException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import persistencia.AlertaDAO;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;
import servico.FabricaPrevisao;
import servico.RelatorioPrevisao;
import servico.ResultadoLeitura;
import servico.ServicoMonitoramento;

/** Menu principal do sistema (console). Monta as peças e conversa com o usuário. */
public class SistemaMonitoramento {

    private final Scanner entrada = new Scanner(System.in);
    private final ServicoMonitoramento servico;
    private final DadosTerritorio dados;
    private final PontoVulneravel ponto;
    private final List<SensorSimulado> sensores;

    public SistemaMonitoramento(ServicoMonitoramento servico, DadosTerritorio dados,
                                PontoVulneravel ponto, List<SensorSimulado> sensores) {
        this.servico = servico;
        this.dados = dados;
        this.ponto = ponto;
        this.sensores = sensores;
    }

    public static void main(String[] args) {
        try {
            ConexaoBanco conexao = ConexaoBanco.padrao();
            conexao.criarTabelas();

            // Dados simulados: território descrito por região e equipamento, nunca por pessoas
            DadosTerritorio dados = new DadosTerritorio("Zona Leste", 12000);
            List<SensorSimulado> sensores = List.of(
                    new PluviometroSensor("PLUV-01"),
                    new NivelBueiroSensor("BUEIRO-01"),
                    new UmidadeSoloSensor("SOLO-01"));
            PontoVulneravel ponto = new PontoVulneravel("PV-01", "Bueiro da Rua A", "Zona Leste");
            for (SensorSimulado sensor : sensores) {
                ponto.adicionarSensor(sensor);
            }

            ServicoMonitoramento servico = new ServicoMonitoramento(
                    new MedicaoDAO(conexao), new AlertaDAO(conexao), dados,
                    new FabricaPrevisao().criarTodas());

            new SistemaMonitoramento(servico, dados, ponto, sensores).executar();
        } catch (SQLException e) {
            System.out.println("Não foi possível iniciar o banco de dados: " + e.getMessage());
        } catch (DadosInvalidosException e) {
            System.out.println("Configuração inicial inválida: " + e.getMessage());
        }
    }

    public void executar() {
        System.out.println("=== Territory Predictive Insights ===");
        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            int opcao = lerInteiro("Escolha uma opção: ");
            try {
                switch (opcao) {
                    case 1 -> verificarSensores();
                    case 2 -> rodarPrevisoes();
                    case 3 -> verHistorico();
                    case 4 -> gerenciarDados();
                    case 5 -> statusCompleto();
                    case 0 -> rodando = false;
                    default -> System.out.println("Opção inválida. Escolha um número do menu.");
                }
            } catch (SQLException e) {
                System.out.println("Erro no banco de dados: " + e.getMessage());
            } catch (DadosInvalidosException e) {
                System.out.println("Dado inválido: " + e.getMessage());
            }
        }
        System.out.println("Sistema encerrado. Os dados continuam salvos no banco.");
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("1 - Verificar sensores (ler e gravar medições)");
        System.out.println("2 - Rodar previsões e alertas");
        System.out.println("3 - Ver histórico");
        System.out.println("4 - Gerenciar dados do território");
        System.out.println("5 - Status completo");
        System.out.println("0 - Encerrar");
    }

    private void verificarSensores() throws SQLException {
        System.out.println("Status do ponto " + ponto.getNome() + ": " + ponto.verificarStatus());
        ResultadoLeitura resultado = servico.lerSensores(ponto);
        for (Medicao medicao : resultado.medicoes()) {
            System.out.println("  " + medicao);
        }
        for (String falha : resultado.falhas()) {
            System.out.println("  FALHA: " + falha);
        }
        System.out.println(resultado.medicoes().size() + " medição(ões) gravada(s).");
    }

    private void rodarPrevisoes() throws SQLException {
        RelatorioPrevisao relatorio = servico.executarPrevisoes(ponto);
        for (String linha : relatorio.linhas()) {
            System.out.println("  " + linha);
        }
        if (relatorio.alertas().isEmpty()) {
            System.out.println("Nenhum alerta emitido.");
        } else {
            System.out.println("ALERTAS EMITIDOS:");
            for (Alerta alerta : relatorio.alertas()) {
                System.out.println("  " + alerta.getMotivo());
            }
            System.out.println("Os alertas apoiam a decisão; quem conhece o território decide o que fazer.");
        }
    }

    private void verHistorico() throws SQLException {
        System.out.println("1 - Medições de um sensor");
        System.out.println("2 - Alertas já emitidos");
        int opcao = lerInteiro("Escolha: ");
        if (opcao == 1) {
            SensorSimulado sensor = escolherSensor();
            if (sensor == null) {
                return;
            }
            List<Medicao> historico = servico.listarHistorico(sensor.getId());
            if (historico.isEmpty()) {
                System.out.println("Nenhuma medição gravada para " + sensor.getId() + ".");
            }
            for (Medicao medicao : historico) {
                System.out.println("  " + medicao);
            }
        } else if (opcao == 2) {
            List<Alerta> alertas = servico.listarAlertas();
            if (alertas.isEmpty()) {
                System.out.println("Nenhum alerta emitido até agora.");
            }
            for (Alerta alerta : alertas) {
                System.out.println("  " + alerta);
            }
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private void gerenciarDados() throws DadosInvalidosException {
        System.out.println("1 - Alterar população");
        System.out.println("2 - Alterar limiares de um sensor");
        System.out.println("3 - Simular falha ou reparo de um sensor");
        int opcao = lerInteiro("Escolha: ");
        switch (opcao) {
            case 1 -> {
                dados.setPopulacao(lerInteiro("Nova população: "));
                System.out.println("População atualizada para " + dados.getPopulacao() + ".");
            }
            case 2 -> alterarLimiares();
            case 3 -> alternarFalha();
            default -> System.out.println("Opção inválida.");
        }
    }

    private void alterarLimiares() throws DadosInvalidosException {
        SensorSimulado sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        double atencao = lerDecimal("Novo limite de ATENÇÃO: ");
        double alerta = lerDecimal("Novo limite de ALERTA: ");
        double critico = lerDecimal("Novo limite CRÍTICO: ");
        dados.setLimiares(sensor.getTipo(), atencao, alerta, critico);
        System.out.println("Limiares de " + sensor.getTipo() + " atualizados.");
    }

    private void alternarFalha() {
        SensorSimulado sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        sensor.setFuncionando(!sensor.estaFuncionando());
        System.out.println("Sensor " + sensor.getId() + " agora está "
                + (sensor.estaFuncionando() ? "funcionando." : "COM FALHA."));
    }

    private void statusCompleto() throws SQLException, DadosInvalidosException {
        System.out.println("Região: " + dados.getRegiao() + " | População: " + dados.getPopulacao());
        System.out.println("Ponto " + ponto.getNome() + " - status: " + ponto.verificarStatus());
        for (SensorSimulado sensor : sensores) {
            Limiares limiares = dados.getLimiares(sensor.getTipo());
            System.out.printf("  %s (%s): %s | limites: atenção %.1f, alerta %.1f, crítico %.1f%n",
                    sensor.getId(), sensor.getTipo(),
                    sensor.estaFuncionando() ? "funcionando" : "COM FALHA",
                    limiares.getLimite(NivelAlerta.ATENCAO),
                    limiares.getLimite(NivelAlerta.ALERTA),
                    limiares.getLimite(NivelAlerta.CRITICO));
        }
        System.out.println("Alertas já emitidos: " + servico.listarAlertas().size());
    }

    /** Mostra os sensores numerados e devolve o escolhido (ou null se a escolha for inválida). */
    private SensorSimulado escolherSensor() {
        for (int i = 0; i < sensores.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + sensores.get(i).getId()
                    + " (" + sensores.get(i).getTipo() + ")");
        }
        int escolha = lerInteiro("Sensor: ");
        if (escolha < 1 || escolha > sensores.size()) {
            System.out.println("Sensor inexistente.");
            return null;
        }
        return sensores.get(escolha - 1);
    }

    private int lerInteiro(String texto) {
        while (true) {
            System.out.print(texto);
            String linha = entrada.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido: digite um número inteiro.");
            }
        }
    }

    private double lerDecimal(String texto) {
        while (true) {
            System.out.print(texto);
            String linha = entrada.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido: digite um número (ex.: 12,5).");
            }
        }
    }
}
