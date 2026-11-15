package dominio;

import excecao.FonteIndisponivelException;

/** Contrato que todo sensor do sistema precisa cumprir. */
public interface Sensor {

    String getId();

    /** Tipo da grandeza medida (ex.: "Chuva"). */
    String getTipo();

    boolean estaFuncionando();

    /** Faz uma leitura. Lança exceção se o sensor estiver indisponível. */
    double lerValor() throws FonteIndisponivelException;
}
