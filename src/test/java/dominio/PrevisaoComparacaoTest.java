package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import excecao.DadosInvalidosException;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrevisaoComparacaoTest {

    @Test
    void projetaTendenciaDeAlta() throws DadosInvalidosException {
        // Arrange (anterior = 10, recente = 20)
        PrevisaoComparacao previsao = new PrevisaoComparacao(3);

        // Act
        double resultado = previsao.prever(List.of(10.0, 10.0, 10.0, 20.0, 20.0, 20.0));

        // Assert (20 + (20 - 10))
        assertEquals(30.0, resultado, 0.0001);
    }

    @Test
    void projetaTendenciaDeQueda() throws DadosInvalidosException {
        // Arrange (anterior = 30, recente = 20)
        PrevisaoComparacao previsao = new PrevisaoComparacao(3);

        // Act
        double resultado = previsao.prever(List.of(30.0, 30.0, 30.0, 20.0, 20.0, 20.0));

        // Assert (20 + (20 - 30))
        assertEquals(10.0, resultado, 0.0001);
    }

    @Test
    void nuncaPrevValorNegativo() throws DadosInvalidosException {
        // Arrange
        PrevisaoComparacao previsao = new PrevisaoComparacao(3);

        // Act (5 + (5 - 30) seria negativo)
        double resultado = previsao.prever(List.of(30.0, 30.0, 30.0, 5.0, 5.0, 5.0));

        // Assert
        assertEquals(0.0, resultado, 0.0001);
    }

    @Test
    void rejeitaHistoricoComPoucasLeituras() throws DadosInvalidosException {
        // Arrange
        PrevisaoComparacao previsao = new PrevisaoComparacao(3);

        // Act + Assert (precisa de 6 leituras)
        assertThrows(DadosInvalidosException.class,
                () -> previsao.prever(List.of(1.0, 2.0, 3.0, 4.0, 5.0)));
    }
}
