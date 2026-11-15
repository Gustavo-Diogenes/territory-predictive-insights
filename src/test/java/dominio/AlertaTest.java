package dominio;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class AlertaTest {

    @Test
    void motivoExplicaSensorValorELimite() throws DadosInvalidosException {
        // Arrange
        Alerta alerta = new Alerta("PLUV-01", "Chuva", NivelAlerta.ALERTA, 45.3, 40.0);

        // Act
        String motivo = alerta.getMotivo();

        // Assert
        assertTrue(motivo.contains("PLUV-01"));
        assertTrue(motivo.contains("ALERTA"));
        assertTrue(motivo.contains(String.format("%.2f", 45.3)));
        assertTrue(motivo.contains(String.format("%.2f", 40.0)));
    }

    @Test
    void rejeitaAlertaSemSensor() {
        // Arrange + Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new Alerta(" ", "Chuva", NivelAlerta.ATENCAO, 25.0, 20.0));
    }
}
