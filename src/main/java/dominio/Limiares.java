package dominio;

import excecao.DadosInvalidosException;
import java.util.Optional;

/**
 * Os três limites (atenção, alerta e crítico) de um tipo de sensor.
 * Imutável: para mudar os limites, cria-se um novo objeto, que é revalidado.
 */
public class Limiares {

    private final double atencao;
    private final double alerta;
    private final double critico;

    public Limiares(double atencao, double alerta, double critico) throws DadosInvalidosException {
        boolean finitos = !Double.isInfinite(atencao) && !Double.isInfinite(alerta) && !Double.isInfinite(critico);
        // "!(a < b)" também rejeita NaN, porque qualquer comparação com NaN é falsa
        if (!finitos || atencao <= 0 || !(atencao < alerta) || !(alerta < critico)) {
            throw new DadosInvalidosException(
                    "Limiares inválidos: devem ser positivos e crescentes (atenção < alerta < crítico). Recebido: "
                            + atencao + ", " + alerta + ", " + critico + ".");
        }
        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    /** Devolve o nível atingido pelo valor, ou vazio se estiver abaixo do limite de atenção. */
    public Optional<NivelAlerta> classificar(double valor) {
        if (valor >= critico) {
            return Optional.of(NivelAlerta.CRITICO);
        }
        if (valor >= alerta) {
            return Optional.of(NivelAlerta.ALERTA);
        }
        if (valor >= atencao) {
            return Optional.of(NivelAlerta.ATENCAO);
        }
        return Optional.empty();
    }

    /** Limite que define o nível informado. */
    public double getLimite(NivelAlerta nivel) {
        return switch (nivel) {
            case CRITICO -> critico;
            case ALERTA -> alerta;
            case ATENCAO -> atencao;
        };
    }
}
