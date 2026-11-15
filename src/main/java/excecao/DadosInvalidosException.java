package excecao;

/**
 * Lançada quando um dado do território é inválido
 * (medição fora de faixa, valor não numérico, limiar inválido etc.).
 * Estende Exception (checked): quem chama é obrigado a tratar o erro.
 */
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
