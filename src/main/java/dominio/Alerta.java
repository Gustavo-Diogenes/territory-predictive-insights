package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;

/** Um alerta emitido quando uma previsão ultrapassa um limiar. Imutável. */
public class Alerta {

    private final Long id; // null até o banco gravar
    private final String sensorId;
    private final String tipo;
    private final NivelAlerta nivel;
    private final double valorPrevisto;
    private final double limiteUltrapassado;
    private final LocalDateTime dataHora;

    public Alerta(Long id, String sensorId, String tipo, NivelAlerta nivel, double valorPrevisto,
                  double limiteUltrapassado, LocalDateTime dataHora) throws DadosInvalidosException {
        if (sensorId == null || sensorId.isBlank() || tipo == null || tipo.isBlank()) {
            throw new DadosInvalidosException("Alerta sem sensor ou tipo de sensor.");
        }
        if (nivel == null || dataHora == null) {
            throw new DadosInvalidosException("Alerta sem nível ou sem data e hora.");
        }
        if (Double.isNaN(valorPrevisto) || Double.isInfinite(valorPrevisto) || valorPrevisto < 0) {
            throw new DadosInvalidosException("Alerta com valor previsto inválido: " + valorPrevisto + ".");
        }
        if (Double.isNaN(limiteUltrapassado) || limiteUltrapassado <= 0) {
            throw new DadosInvalidosException("Alerta com limite inválido: " + limiteUltrapassado + ".");
        }
        this.id = id;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiteUltrapassado = limiteUltrapassado;
        this.dataHora = dataHora;
    }

    /** Alerta emitido agora: sem id (o banco gera) e com a hora atual. */
    public Alerta(String sensorId, String tipo, NivelAlerta nivel, double valorPrevisto,
                  double limiteUltrapassado) throws DadosInvalidosException {
        this(null, sensorId, tipo, nivel, valorPrevisto, limiteUltrapassado, LocalDateTime.now());
    }

    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getTipo() { return tipo; }
    public NivelAlerta getNivel() { return nivel; }
    public double getValorPrevisto() { return valorPrevisto; }
    public double getLimiteUltrapassado() { return limiteUltrapassado; }
    public LocalDateTime getDataHora() { return dataHora; }

    /** Explica o porquê do alerta: qual sensor, valor previsto e limite estourado. */
    public String getMotivo() {
        return String.format("%s - Previsão de %s: %.2f no sensor %s ultrapassou o limite de %.2f.",
                nivel.getRotulo(), tipo, valorPrevisto, sensorId, limiteUltrapassado);
    }

    @Override
    public String toString() {
        return "[" + dataHora + "] " + getMotivo();
    }
}
