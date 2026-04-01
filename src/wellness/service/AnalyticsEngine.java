package wellness.service;

import java.util.Collection;
import java.util.Map;

/**
 * Analytics engine: compose multiple contributors, profiles and records
 * into a single 0-100 wellness score. This is still lightweight but
 * provides a clearer pipeline for contributors and tests.
 */
public class AnalyticsEngine {

    public AnalyticsEngine() {}

    /**
     * Compute a wellness score (0-100) from a variety of inputs.
     * - null => baseline 50
     * - WellnessProfile => heuristics (BMI, age)
    * - Collection of objects with calculateImpactScore() => average impact
    * - Single object with calculateImpactScore() => use its impact score
     */
    public double computeScore(Object profileOrRecords) {
        final double baseline = 50.0;
        if (profileOrRecords == null) return baseline;

        if (looksLikeProfile(profileOrRecords)) {
            double bmi = readDouble(profileOrRecords, "calculateBMI");
            double bmiImpact;
            if (bmi < 18.5) bmiImpact = -5.0;
            else if (bmi < 25.0) bmiImpact = 5.0;
            else if (bmi < 30.0) bmiImpact = -3.0;
            else bmiImpact = -8.0;

            double ageImpact;
            int age = readInt(profileOrRecords, "getAge");
            if (age < 30) ageImpact = 3.0;
            else if (age < 50) ageImpact = 1.0;
            else ageImpact = -2.0;

            return clamp(baseline + bmiImpact + ageImpact, 0.0, 100.0);
        }

        if (hasMethod(profileOrRecords, "calculateImpactScore")) {
            double imp = readDouble(profileOrRecords, "calculateImpactScore");
            return clamp(imp, 0.0, 100.0);
        }

        if (profileOrRecords instanceof Collection) {
            Collection<?> coll = (Collection<?>) profileOrRecords;
            double sum = 0.0;
            int count = 0;
            for (Object o : coll) {
                if (hasMethod(o, "calculateImpactScore")) {
                    sum += readDouble(o, "calculateImpactScore");
                    count++;
                }
            }
            if (count == 0) return baseline;
            double avg = sum / (double) count;
            return clamp(avg, 0.0, 100.0);
        }

        // Unknown input: return baseline
        return baseline;
    }

    /**
     * Compute a score by applying simple contributor deltas to baseline 50.
     */
    public double computeFromContributors(Map<String, Double> contributors) {
        double score = 50.0;
        if (contributors == null || contributors.isEmpty()) return score;
        double delta = 0.0;
        for (double v : contributors.values()) delta += v;
        double finalScore = score + delta;
        return clamp(finalScore, 0.0, 100.0);
    }

    private double clamp(double v, double lo, double hi) {
        if (v < lo) return lo;
        if (v > hi) return hi;
        return v;
    }

    private boolean looksLikeProfile(Object value) {
        if (value == null) {
            return false;
        }
        return hasMethod(value, "calculateBMI") && hasMethod(value, "getAge");
    }

    private boolean hasMethod(Object value, String methodName) {
        try {
            value.getClass().getMethod(methodName);
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        }
    }

    private double readDouble(Object value, String methodName) {
        try {
            Object result = value.getClass().getMethod(methodName).invoke(value);
            if (result instanceof Number number) {
                return number.doubleValue();
            }
        } catch (ReflectiveOperationException ex) {
            return 0.0;
        }
        return 0.0;
    }

    private int readInt(Object value, String methodName) {
        try {
            Object result = value.getClass().getMethod(methodName).invoke(value);
            if (result instanceof Number number) {
                return number.intValue();
            }
        } catch (ReflectiveOperationException ex) {
            return 0;
        }
        return 0;
    }
}
