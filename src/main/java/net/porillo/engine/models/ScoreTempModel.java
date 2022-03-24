package net.porillo.engine.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.porillo.engine.api.Distribution;
import net.porillo.engine.api.Model;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class ScoreTempModel extends Model {

    /**
     * Carbon sensitivity
     * - LOW: [-1M, 1M] (e.g., many players)
     * - HIGH: [-50K, 50K] (e.g., fewer players)
     * - VERY_HIGH: [-800, 1200] (e.g., demo)
     */
    public enum CarbonSensitivity {
        LOW,
        HIGH,
        VERY_HIGH
    }

    @Getter private Map<Integer, Double> indexMap;
    private CarbonSensitivity sensitivity;
    private Distribution distribution;
    Map<CarbonSensitivity, Map<Integer, Double>> temperatureMap;

    public ScoreTempModel(String worldName, CarbonSensitivity sensitivity) {
        super(worldName, "scoreTempModel.json");
        this.sensitivity = sensitivity;
        this.loadModel();
    }

    public Map<Integer, Double> getTemperatureMap() {
        return temperatureMap.get(sensitivity);
    }

    /**
     * Load the score / temperature model
     * - CarbonSensitivity specifies the range of scores being mapped onto [10C, 20C]
     */
    @Override
    public void loadModel() {
        //Deserialize the model from JSON:
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.enableComplexMapKeySerialization().create();
        Type listType = new TypeToken<Map<CarbonSensitivity, Map<Integer, Double>>>() {
        }.getType();
        temperatureMap = gson.fromJson(super.getContents(), listType);

        //Validate the result:
        this.indexMap = new TreeMap<>(Comparator.naturalOrder());
        if (temperatureMap == null || !temperatureMap.containsKey(sensitivity)) {
            throw new RuntimeException(String.format("Invalid score-temperature model (%s): [%s]", sensitivity, super.getPath()));
        }

        if (temperatureMap.get(sensitivity).isEmpty()) {
            throw new RuntimeException(String.format("No values found in (%s): [%s]", sensitivity, super.getPath()));
        }

        //Copy the result:
        this.indexMap.putAll(temperatureMap.get(sensitivity));

        //Create a lookup function:
        int i = 0;
        double[] scores = new double[indexMap.size()];
        double[] temps = new double[indexMap.size()];
        for (Map.Entry<Integer, Double> entry : indexMap.entrySet()) {
            scores[i] = (double) entry.getKey();
            temps[i++] = entry.getValue();
        }
        this.distribution = new Distribution(scores, temps);
    }

    /**
     * Get the temperature using linear interpolation
     * - Limits score to the domain to prevent exceptions
     * - This value is used by most models
     */
    public double getTemperature(int score) {
        return distribution.getValue((double) score);
    }
}
