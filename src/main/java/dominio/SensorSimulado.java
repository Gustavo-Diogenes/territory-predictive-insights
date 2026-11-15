package dominio;

import excecao.FonteIndisponivelException;
import java.util.Random;

/**
 * Base para sensores cujas leituras são simuladas com Random,
 * dentro de uma faixa realista (mínimo e máximo).
 */
public abstract class SensorSimulado implements Sensor {

    private final String id;
    private final String tipo;
    private final double minimo;
    private final double maximo;
    private final Random random = new Random();
    private boolean funcionando = true;

    protected SensorSimulado(String id, String tipo, double minimo, double maximo) {
        this.id = id;
        this.tipo = tipo;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getTipo() { return tipo; }

    @Override
    public boolean estaFuncionando() { return funcionando; }

    /** Permite simular uma falha (ou o reparo) do sensor. */
    public void setFuncionando(boolean funcionando) { this.funcionando = funcionando; }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!funcionando) {
            throw new FonteIndisponivelException("Sensor " + id + " (" + tipo + ") está indisponível.");
        }
        return minimo + random.nextDouble() * (maximo - minimo);
    }
}
