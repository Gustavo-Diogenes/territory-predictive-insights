package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PontoVulneravelTest {

    @Test
    void ficaOperacionalQuandoTodosOsSensoresFuncionam() {
        // Arrange
        PontoVulneravel ponto = new PontoVulneravel("PV-01", "Bueiro da Rua A", "Zona Leste");
        ponto.adicionarSensor(new PluviometroSensor("PLUV-01"));
        ponto.adicionarSensor(new NivelBueiroSensor("BUEIRO-01"));

        // Act
        String status = ponto.verificarStatus();

        // Assert
        assertEquals("OPERACIONAL", status);
        assertEquals("OPERACIONAL", ponto.getStatus());
    }

    @Test
    void ficaComFalhaQuandoUmSensorParaDeFuncionar() {
        // Arrange
        PontoVulneravel ponto = new PontoVulneravel("PV-01", "Bueiro da Rua A", "Zona Leste");
        PluviometroSensor pluviometro = new PluviometroSensor("PLUV-01");
        ponto.adicionarSensor(pluviometro);
        ponto.adicionarSensor(new NivelBueiroSensor("BUEIRO-01"));
        pluviometro.setFuncionando(false);

        // Act
        String status = ponto.verificarStatus();

        // Assert
        assertEquals("COM FALHA", status);
    }

    @Test
    void ficaSemSensoresQuandoNaoHaNenhum() {
        // Arrange
        PontoVulneravel ponto = new PontoVulneravel("PV-02", "Praça B", "Zona Norte");

        // Act + Assert
        assertEquals("SEM SENSORES", ponto.verificarStatus());
    }
}
