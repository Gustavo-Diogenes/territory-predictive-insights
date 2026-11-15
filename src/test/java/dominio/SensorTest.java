package dominio;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import excecao.FonteIndisponivelException;
import org.junit.jupiter.api.Test;

class SensorTest {

    @Test
    void pluviometroLeValorDentroDaFaixa() throws FonteIndisponivelException {
        // Arrange
        Sensor sensor = new PluviometroSensor("PLUV-01");

        // Act + Assert
        for (int i = 0; i < 100; i++) {
            double valor = sensor.lerValor();
            assertTrue(valor >= 0 && valor <= 80, "Valor fora da faixa: " + valor);
        }
    }

    @Test
    void nivelBueiroLeValorDentroDaFaixa() throws FonteIndisponivelException {
        // Arrange
        Sensor sensor = new NivelBueiroSensor("BUEIRO-01");

        // Act + Assert
        for (int i = 0; i < 100; i++) {
            double valor = sensor.lerValor();
            assertTrue(valor >= 0 && valor <= 100, "Valor fora da faixa: " + valor);
        }
    }

    @Test
    void sensorComFalhaLancaExcecao() {
        // Arrange
        UmidadeSoloSensor sensor = new UmidadeSoloSensor("SOLO-01");
        sensor.setFuncionando(false);

        // Act
        FonteIndisponivelException erro =
                assertThrows(FonteIndisponivelException.class, sensor::lerValor);

        // Assert
        assertTrue(erro.getMessage().contains("SOLO-01"));
    }
}
