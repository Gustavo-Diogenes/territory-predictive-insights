package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import excecao.DadosInvalidosException;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrevisaoMediaMovelTest {

    @Test
    void calculaMediaDasUltimasLeituras() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(3);

        // Act
        double resultado = previsao.prever(List.of(10.0, 20.0, 30.0));

        // Assert
        assertEquals(20.0, resultado, 0.0001);
    }

    @Test
    void usaSomenteALeiturasDaJanela() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(3);

        // Act (a leitura 100 fica fora da janela)
        double resultado = previsao.prever(List.of(100.0, 10.0, 20.0, 30.0));

        // Assert
        assertEquals(20.0, resultado, 0.0001);
    }

    @Test
    void rejeitaHistoricoComPoucasLeituras() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(3);

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> previsao.prever(List.of(10.0, 20.0)));

        // Assert
        assertTrue(erro.getMessage().contains("pelo menos 3"));
    }

    @Test
    void rejeitaJanelaInvalida() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class, () -> new PrevisaoMediaMovel(1));
    }
}
