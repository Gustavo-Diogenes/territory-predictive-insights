package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import excecao.DadosInvalidosException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DadosTerritorioTest {

    @Test
    void classificaCadaFaixaDeChuva() throws DadosInvalidosException {
        // Arrange
        Limiares limiares = new DadosTerritorio("Zona Leste", 1000).getLimiares("Chuva");

        // Act + Assert
        assertEquals(Optional.empty(), limiares.classificar(10));
        assertEquals(Optional.of(NivelAlerta.ATENCAO), limiares.classificar(25));
        assertEquals(Optional.of(NivelAlerta.ALERTA), limiares.classificar(45));
        assertEquals(Optional.of(NivelAlerta.CRITICO), limiares.classificar(70));
    }

    @Test
    void alterarLimiaresMudaAClassificacao() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Zona Leste", 1000);

        // Act
        dados.setLimiares("Chuva", 5, 10, 15);

        // Assert
        assertEquals(Optional.of(NivelAlerta.ALERTA), dados.getLimiares("Chuva").classificar(12));
    }

    @Test
    void rejeitaLimiaresForaDeOrdem() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Zona Leste", 1000);

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> dados.setLimiares("Chuva", 50, 40, 60));

        // Assert
        assertTrue(erro.getMessage().contains("crescentes"));
    }

    @Test
    void rejeitaPopulacaoNegativa() {
        // Arrange + Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new DadosTerritorio("Zona Leste", -10));

        // Assert
        assertTrue(erro.getMessage().contains("negativa"));
    }

    @Test
    void lancaExcecaoParaTipoSemLimiar() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Zona Leste", 1000);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> dados.getLimiares("Vento"));
    }
}
