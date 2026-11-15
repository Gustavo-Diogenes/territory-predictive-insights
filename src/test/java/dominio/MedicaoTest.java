package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MedicaoTest {

    @Test
    void criaMedicaoValida() throws DadosInvalidosException {
        // Arrange
        LocalDateTime agora = LocalDateTime.of(2026, 11, 14, 12, 0);

        // Act
        Medicao medicao = new Medicao(1L, "PLUV-01", "Chuva", 12.5, agora);

        // Assert
        assertEquals("PLUV-01", medicao.getSensorId());
        assertEquals("Chuva", medicao.getTipo());
        assertEquals(12.5, medicao.getValor());
        assertEquals(agora, medicao.getDataHora());
    }

    @Test
    void rejeitaValorNegativo() {
        // Arrange + Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLUV-01", "Chuva", -3.0));

        // Assert
        assertTrue(erro.getMessage().contains("negativo"));
    }

    @Test
    void rejeitaValorNaoNumerico() {
        // Arrange + Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLUV-01", "Chuva", Double.NaN));

        // Assert
        assertTrue(erro.getMessage().contains("não numérico"));
    }

    @Test
    void rejeitaSensorSemIdentificacao() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao(" ", "Chuva", 5.0));
    }
}
