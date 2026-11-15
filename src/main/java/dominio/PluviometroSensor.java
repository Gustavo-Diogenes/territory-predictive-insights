package dominio;

/** Mede a intensidade da chuva, em mm/h. */
public class PluviometroSensor extends SensorSimulado {

    public PluviometroSensor(String id) {
        super(id, "Chuva", 0, 80);
    }
}
