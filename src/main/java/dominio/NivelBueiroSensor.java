package dominio;

/** Mede o nível de água dentro do bueiro, em cm. */
public class NivelBueiroSensor extends SensorSimulado {

    public NivelBueiroSensor(String id) {
        super(id, "Nível do bueiro", 0, 100);
    }
}
