package wellness.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple registry for contributor weights used by the scoring pipeline.
 */
public class ContributorRegistry {
    private final Map<String, Double> weights = new HashMap<>();

    public ContributorRegistry() {
        // sensible defaults
        weights.put("health", 1.0);
        weights.put("activity", 1.0);
    }

    public void setWeight(String key, double weight) {
        if (key == null) return;
        weights.put(key.toLowerCase(), weight);
    }

    public Double getWeight(String key) {
        if (key == null) return null;
        return weights.get(key.toLowerCase());
    }

    public Map<String, Double> getAllWeights() {
        return Collections.unmodifiableMap(weights);
    }
}
