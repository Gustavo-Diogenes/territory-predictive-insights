package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;

/**
 * Uma leitura feita por um sensor. É imutável: todos os atributos são final,
 * não há setters, e a validação acontece no construtor. Assim, uma Medicao
 * inválida nunca chega a existir.
 */
public class Medicao {

    private final Long id; // null até o banco gravar a medição
    private final String sensorId;
    private final String tipo;
    private final double valor;
    private final LocalDateTime dataHora;

    public Medicao(Long id, String sensorId, String tipo, double valor, LocalDateTime dataHora)
            throws DadosInvalidosException {
        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException("Medição sem identificação do sensor.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new DadosInvalidosException("Medição sem tipo de sensor.");
        }
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DadosInvalidosException("Medição de " + tipo + " inválida: valor não numérico (" + valor + ").");
        }
        if (valor < 0) {
            throw new DadosInvalidosException("Medição de " + tipo + " inválida: valor negativo (" + valor + ").");
        }
        if (dataHora == null) {
            throw new DadosInvalidosException("Medição sem data e hora.");
        }
        this.id = id;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    /** Usado ao ler um sensor agora: sem id (o banco gera) e com a hora atual. */
    public Medicao(String sensorId, String tipo, double valor) throws DadosInvalidosException {
        this(null, sensorId, tipo, valor, LocalDateTime.now());
    }

    public Long getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getTipo() { return tipo; }
    public double getValor() { return valor; }
    public LocalDateTime getDataHora() { return dataHora; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s): %.2f", dataHora, sensorId, tipo, valor);
    }
}
