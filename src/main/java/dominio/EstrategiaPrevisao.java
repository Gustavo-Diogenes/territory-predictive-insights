package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

/**
 * Forma de prever o próximo valor de um sensor a partir do histórico.
 * Cada filha implementa prever() do seu jeito (polimorfismo).
 * As leituras chegam em ordem cronológica (da mais antiga para a mais nova).
 */
public abstract class EstrategiaPrevisao {

    private final String nome;
    private final int minimoLeituras;
    private final String descricao;

    protected EstrategiaPrevisao(String nome, int minimoLeituras, String descricao) {
        this.nome = nome;
        this.minimoLeituras = minimoLeituras;
        this.descricao = descricao;
    }

    public String getNome() { return nome; }

    public int getMinimoLeituras() { return minimoLeituras; }

    /** Calcula o valor previsto. Lança exceção se não houver leituras suficientes. */
    public abstract double prever(List<Double> leituras) throws DadosInvalidosException;

    /** Texto que diz o valor previsto e o que ele significa. */
    public String descrever(double valorPrevisto) {
        return String.format("Previsão por %s: %.2f (%s).", nome, valorPrevisto, descricao);
    }

    /** Sem leituras suficientes, a previsão não é confiável: o sistema "se cala". */
    protected void validarLeituras(List<Double> leituras) throws DadosInvalidosException {
        int quantidade = leituras == null ? 0 : leituras.size();
        if (quantidade < minimoLeituras) {
            throw new DadosInvalidosException(nome + " exige pelo menos " + minimoLeituras
                    + " leituras, mas o histórico tem " + quantidade + ".");
        }
    }

    protected static double media(List<Double> valores) {
        double soma = 0;
        for (double valor : valores) {
            soma += valor;
        }
        return soma / valores.size();
    }
}
