package excecao;

/**
 * Lançada quando uma fonte de dados (sensor ou gerador de leituras) falha.
 */
public class FonteIndisponivelException extends Exception {

    public FonteIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
