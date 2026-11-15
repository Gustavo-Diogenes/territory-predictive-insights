package dominio;

import excecao.DadosInvalidosException;
import java.util.HashMap;
import java.util.Map;

/**
 * Dados da região monitorada: nome, população e os limiares de cada tipo de sensor.
 * Os atributos são privados; a alteração só acontece por métodos que validam.
 */
public class DadosTerritorio {

    private final String regiao;
    private int populacao;
    private final Map<String, Limiares> limiaresPorTipo = new HashMap<>();

    public DadosTerritorio(String regiao, int populacao) throws DadosInvalidosException {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("A região do território deve ser informada.");
        }
        validarPopulacao(populacao);
        this.regiao = regiao;
        this.populacao = populacao;
        // Limiares iniciais (podem ser alterados depois)
        limiaresPorTipo.put("Chuva", new Limiares(20, 40, 60));
        limiaresPorTipo.put("Nível do bueiro", new Limiares(40, 60, 80));
        limiaresPorTipo.put("Umidade do solo", new Limiares(60, 75, 90));
    }

    private static void validarPopulacao(int populacao) throws DadosInvalidosException {
        if (populacao < 0) {
            throw new DadosInvalidosException("População inválida: " + populacao + " (não pode ser negativa).");
        }
    }

    public String getRegiao() { return regiao; }

    public int getPopulacao() { return populacao; }

    public void setPopulacao(int populacao) throws DadosInvalidosException {
        validarPopulacao(populacao);
        this.populacao = populacao;
    }

    /** Altera os limiares de um tipo de sensor. Lança exceção se forem inválidos. */
    public void setLimiares(String tipo, double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        if (tipo == null || tipo.isBlank()) {
            throw new DadosInvalidosException("Informe o tipo de sensor para alterar os limiares.");
        }
        limiaresPorTipo.put(tipo, new Limiares(atencao, alerta, critico));
    }

    public Limiares getLimiares(String tipo) throws DadosInvalidosException {
        Limiares limiares = limiaresPorTipo.get(tipo);
        if (limiares == null) {
            throw new DadosInvalidosException("Não há limiares definidos para o sensor do tipo \"" + tipo + "\".");
        }
        return limiares;
    }
}
