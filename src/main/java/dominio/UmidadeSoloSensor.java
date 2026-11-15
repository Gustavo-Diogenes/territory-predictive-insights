package dominio;

/** Mede a umidade do solo, em %. */
public class UmidadeSoloSensor extends SensorSimulado {

    public UmidadeSoloSensor(String id) {
        super(id, "Umidade do solo", 0, 100);
    }
}
