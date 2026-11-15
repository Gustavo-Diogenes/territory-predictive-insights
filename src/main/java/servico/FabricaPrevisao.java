package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import dominio.TipoPrevisao;
import excecao.DadosInvalidosException;
import java.util.ArrayList;
import java.util.List;

/**
 * Padrão Factory: é o único lugar que conhece as classes concretas de previsão.
 * O resto do sistema pede uma previsão por tipo e usa só EstrategiaPrevisao.
 */
public class FabricaPrevisao {

    private static final int JANELA_PADRAO = 5;
    private static final int PERIODO_PADRAO = 3;

    public EstrategiaPrevisao criar(TipoPrevisao tipo) throws DadosInvalidosException {
        if (tipo == null) {
            throw new DadosInvalidosException("Informe o tipo de previsão a ser criado.");
        }
        return switch (tipo) {
            case MEDIA_MOVEL -> new PrevisaoMediaMovel(JANELA_PADRAO);
            case COMPARACAO -> new PrevisaoComparacao(PERIODO_PADRAO);
        };
    }

    /** Uma estratégia de cada tipo disponível. */
    public List<EstrategiaPrevisao> criarTodas() throws DadosInvalidosException {
        List<EstrategiaPrevisao> estrategias = new ArrayList<>();
        for (TipoPrevisao tipo : TipoPrevisao.values()) {
            estrategias.add(criar(tipo));
        }
        return estrategias;
    }
}
