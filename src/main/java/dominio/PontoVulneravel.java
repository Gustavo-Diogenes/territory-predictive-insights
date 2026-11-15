package dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Ponto do território com risco de alagamento, monitorado por sensores. */
public class PontoVulneravel extends ComponenteTerritorial {

    private final List<Sensor> sensores = new ArrayList<>();

    public PontoVulneravel(String id, String nome, String regiao) {
        super(id, nome, regiao);
    }

    public void adicionarSensor(Sensor sensor) {
        sensores.add(sensor);
    }

    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    @Override
    public String verificarStatus() {
        String resultado = "OPERACIONAL";
        if (sensores.isEmpty()) {
            resultado = "SEM SENSORES";
        }
        for (Sensor sensor : sensores) { // polimorfismo: cada sensor responde por si
            if (!sensor.estaFuncionando()) {
                resultado = "COM FALHA";
            }
        }
        setStatus(resultado);
        return resultado;
    }
}
