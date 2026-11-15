package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

/** Prevê o próximo valor como a média das últimas N leituras. */
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoMediaMovel(int janela) throws DadosInvalidosException {
        super("Média móvel", validarTamanho(janela), "média das últimas " + janela + " leituras");
        this.janela = janela;
    }

    private static int validarTamanho(int janela) throws DadosInvalidosException {
        if (janela < 2) {
            throw new DadosInvalidosException(
                    "A janela da média móvel deve ter pelo menos 2 leituras (recebido: " + janela + ").");
        }
        return janela;
    }

    @Override
    public double prever(List<Double> leituras) throws DadosInvalidosException {
        validarLeituras(leituras);
        return media(leituras.subList(leituras.size() - janela, leituras.size()));
    }
}
