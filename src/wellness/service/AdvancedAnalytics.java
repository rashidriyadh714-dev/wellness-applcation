package wellness.service;

import wellness.model.AbstractRecord;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Small extension providing weighted scoring and simple trend calculation.
 */
public class AdvancedAnalytics {
    public AdvancedAnalytics() {
    }

    /**
     * Compute a weighted score across records. Weight map keys should be lower-case record types
     * such as "health" or "activity".
     */
    public double computeWeightedScore(List<AbstractRecord> records, Map<String, Double> weights) {
        if (records == null || records.isEmpty()) return 50.0;
        double weightedSum = 0.0;
        double weightTotal = 0.0;
        for (AbstractRecord r : records) {
            double base = r.calculateImpactScore();
            double w = 1.0;
            if (weights != null) {
                Double wv = weights.get(r.getRecordType().toLowerCase());
                if (wv != null) w = wv;
            }
            weightedSum += base * w;
            weightTotal += w;
        }
        double avg = weightedSum / (weightTotal == 0 ? records.size() : weightTotal);
        return Math.max(0.0, Math.min(100.0, avg));
    }

    /**
     * Compute a simple trend = (most recent score) - (oldest score)
     */
    public double computeTrend(List<AbstractRecord> records) {
        if (records == null || records.size() < 2) return 0.0;
        List<AbstractRecord> sorted = records.stream().sorted((a, b) -> a.getDate().compareTo(b.getDate())).collect(Collectors.toList());
        double first = sorted.get(0).calculateImpactScore();
        double last = sorted.get(sorted.size() - 1).calculateImpactScore();
        return last - first;
    }
}
