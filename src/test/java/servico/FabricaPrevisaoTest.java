package servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import dominio.TipoPrevisao;
import excecao.DadosInvalidosException;
import java.util.List;
import org.junit.jupiter.api.Test;

class FabricaPrevisaoTest {

    private final FabricaPrevisao fabrica = new FabricaPrevisao();

    @Test
    void criaCadaTipoDePrevisao() throws DadosInvalidosException {
        // Act + Assert
        assertInstanceOf(PrevisaoMediaMovel.class, fabrica.criar(TipoPrevisao.MEDIA_MOVEL));
        assertInstanceOf(PrevisaoComparacao.class, fabrica.criar(TipoPrevisao.COMPARACAO));
    }

    @Test
    void criaUmaEstrategiaDeCadaTipo() throws DadosInvalidosException {
        // Act
        List<EstrategiaPrevisao> todas = fabrica.criarTodas();

        // Assert
        assertEquals(TipoPrevisao.values().length, todas.size());
    }

    @Test
    void estrategiasDaFabricaDaoResultadosDiferentes() throws DadosInvalidosException {
        // Arrange
        List<Double> leituras = List.of(10.0, 10.0, 10.0, 20.0, 20.0, 20.0);
        EstrategiaPrevisao mediaMovel = fabrica.criar(TipoPrevisao.MEDIA_MOVEL);
        EstrategiaPrevisao comparacao = fabrica.criar(TipoPrevisao.COMPARACAO);

        // Act (mesma chamada, comportamento diferente: polimorfismo)
        double porMedia = mediaMovel.prever(leituras);
        double porComparacao = comparacao.prever(leituras);

        // Assert
        assertEquals(16.0, porMedia, 0.0001);
        assertEquals(30.0, porComparacao, 0.0001);
        assertNotEquals(porMedia, porComparacao);
    }

    @Test
    void rejeitaTipoNulo() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class, () -> fabrica.criar(null));
    }
}
