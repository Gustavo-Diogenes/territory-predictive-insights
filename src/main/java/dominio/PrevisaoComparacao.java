package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

/**
 * Compara a média do período recente com a do período anterior e projeta
 * a mesma variação para o próximo período. Nunca devolve valor negativo.
 */
public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final int periodo;

    public PrevisaoComparacao(int periodo) throws DadosInvalidosException {
        super("Comparação com o período anterior", 2 * validarTamanho(periodo),
                "projeta a variação entre a média das últimas " + periodo
                        + " leituras e a das " + periodo + " anteriores");
        this.periodo = periodo;
    }

    private static int validarTamanho(int periodo) throws DadosInvalidosException {
        if (periodo < 1) {
            throw new DadosInvalidosException(
                    "O período de comparação deve ter pelo menos 1 leitura (recebido: " + periodo + ").");
        }
        return periodo;
    }

    @Override
    public double prever(List<Double> leituras) throws DadosInvalidosException {
        validarLeituras(leituras);
        int fim = leituras.size();
        double recente = media(leituras.subList(fim - periodo, fim));
        double anterior = media(leituras.subList(fim - 2 * periodo, fim - periodo));
        return Math.max(0, recente + (recente - anterior));
    }
}
