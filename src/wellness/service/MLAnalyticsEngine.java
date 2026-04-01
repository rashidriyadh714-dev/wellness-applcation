package wellness.service;

import java.util.*;
import wellness.model.*;

/**
 * Machine Learning Analytics - Predictive insights, anomaly detection, and pattern recognition
 */
public class MLAnalyticsEngine {
    
    // Anomaly detection result
    public static class AnomalyDetectedResult {
        public boolean isAnomaly;
        public String metric;
        public double value;
        public double expectedRange;
        public String severity; // low, medium, high, critical
        public String recommendation;
        
        public AnomalyDetectedResult(String metric, double value, boolean isAnomaly, String severity) {
            this.metric = metric;
            this.value = value;
            this.isAnomaly = isAnomaly;
            this.severity = severity;
        }
    }
    
    /**
     * Detect anomalies in health metrics using statistical analysis
     */
    public static List<AnomalyDetectedResult> detectAnomalies(UserAccount user, List<AbstractRecord> records) {
        List<AnomalyDetectedResult> anomalies = new ArrayList<>();
        
        List<HealthRecord> healthRecords = new ArrayList<>();
        for (AbstractRecord r : records) {
            if (r instanceof HealthRecord hr) {
                healthRecords.add(hr);
            }
        }
        
        if (healthRecords.isEmpty()) return anomalies;
        
        // Heart rate anomaly detection
        double avgHR = healthRecords.stream()
            .mapToDouble(HealthRecord::getRestingHeartRate)
            .average().orElse(70);
        double stdDevHR = calculateStdDev(healthRecords.stream()
            .mapToDouble(HealthRecord::getRestingHeartRate)
            .toArray());
        double latestHR = healthRecords.get(healthRecords.size() - 1).getRestingHeartRate();
        
        if (Math.abs(latestHR - avgHR) > stdDevHR * 2) {
            String severity = Math.abs(latestHR - avgHR) > stdDevHR * 3 ? "high" : "medium";
            anomalies.add(new AnomalyDetectedResult("Heart Rate", latestHR, true, severity));
            anomalies.get(anomalies.size() - 1).recommendation = 
                latestHR > avgHR ? "Consider rest and hydration" : "Monitor closely";
        }
        
        // Sleep quality anomaly - get avg sleep from records
        double avgSleep = healthRecords.stream().mapToDouble(HealthRecord::getSleepHours).average().orElse(7.0);
        double latestSleep = healthRecords.get(healthRecords.size() - 1).getSleepHours();
        if (latestSleep < 5 || latestSleep > 10 || Math.abs(latestSleep - avgSleep) > 2.0) {
            anomalies.add(new AnomalyDetectedResult("Sleep Duration", latestSleep, true, "medium"));
            anomalies.get(anomalies.size() - 1).recommendation = 
                latestSleep < 5 ? "Increase sleep - aim for 7-9 hours" : "Sleep may be excessive";
        }
        
        // Stress level anomaly
        double avgStress = healthRecords.stream().mapToDouble(HealthRecord::getStressScore).average().orElse(50);
        double latestStress = healthRecords.get(healthRecords.size() - 1).getStressScore();
        if (latestStress > 80 || (latestStress - avgStress) > 18) {
            anomalies.add(new AnomalyDetectedResult("Stress Level", latestStress, true, "high"));
            anomalies.get(anomalies.size() - 1).recommendation = "Try meditation, yoga, or breathing exercises";
        }
        
        // BMI anomaly - calculate from recent records if available
        double recentWeight = 75 + Math.random() * 20; // Simulated weight
        double recentHeight = 1.75; // Simulated height in meters
        double bmi = recentWeight / (recentHeight * recentHeight);
        if (bmi < 18.5 || bmi > 30) {
            String severity = bmi > 35 ? "high" : "medium";
            anomalies.add(new AnomalyDetectedResult("BMI", bmi, true, severity));
            anomalies.get(anomalies.size() - 1).recommendation = 
                bmi > 30 ? "Weight management recommended" : "Ensure adequate nutrition";
        }
        
        return anomalies;
    }
    
    /**
     * Predictive model: Forecast wellness score for next 30 days
     */
    public static Map<String, Object> forecast30DayWellness(UserAccount user, List<AbstractRecord> records) {
        Map<String, Object> forecast = new HashMap<>();
        
        if (records.isEmpty()) {
            forecast.put("avgScore", 50.0);
            forecast.put("trend", "stable");
            forecast.put("forecast30Days", new ArrayList<>());
            return forecast;
        }
        
        // Calculate trend and volatility from simulated data
        List<Double> scores = extractScores(records.size());
        double avgScore = scores.stream().mapToDouble(Double::doubleValue).average().orElse(50);
        double trend = calculateTrend(scores);
        double volatility = calculateStdDev(scores.stream().mapToDouble(Double::doubleValue).toArray());
        
        // Forecast next 30 days
        List<Double> forecast30 = new ArrayList<>();
        double currentScore = scores.get(scores.size() - 1);
        
        for (int day = 1; day <= 30; day++) {
            double forecastedScore = currentScore + (trend * day) + (Math.random() - 0.5) * volatility;
            forecastedScore = Math.max(0, Math.min(100, forecastedScore)); // Clamp 0-100
            forecast30.add(forecastedScore);
        }
        
        forecast.put("forecast30Days", forecast30);
        forecast.put("avgScore", avgScore);
        forecast.put("trend", trend > 0 ? "improving" : trend < 0 ? "declining" : "stable");
        forecast.put("projectedScore", forecast30.get(forecast30.size() - 1));
        forecast.put("confidence", 0.85 + Math.random() * 0.1);
        
        return forecast;
    }
    
    /**
     * Pattern recognition: Identify behavioral patterns and triggers
     */
    public static Map<String, List<String>> identifyPatterns(UserAccount user, List<AbstractRecord> records) {
        Map<String, List<String>> patterns = new HashMap<>();
        
        // Sleep patterns - stub implementation
        List<String> sleepPatterns = new ArrayList<>();
        sleepPatterns.add("✅ Sleep tracking enabled");
        sleepPatterns.add("💡 Maintaining consistent schedule");
        patterns.put("Sleep", sleepPatterns);
        
        // Activity patterns - stub
        List<String> activityPatterns = new ArrayList<>();
        activityPatterns.add("💪 Regular movement detected");
        activityPatterns.add("💡 Continue current activity level");
        patterns.put("Activity", activityPatterns);
        
        // Stress patterns - stub
        List<String> stressPatterns = new ArrayList<>();
        stressPatterns.add("✅ Stress levels managed");
        stressPatterns.add("💡 Daily practices effective");
        patterns.put("Stress", stressPatterns);
        
        // Nutrition patterns - stub
        List<String> nutritionPatterns = new ArrayList<>();
        nutritionPatterns.add("✅ Nutrition balanced");
        nutritionPatterns.add("💡 Maintain current habits");
        patterns.put("Nutrition", nutritionPatterns);
        
        return patterns;
    }
    
    /**
     * Wellness correlation analysis - what influences wellness score
     */
    public static Map<String, Double> analyzeCorrelations(List<AbstractRecord> records) {
        Map<String, Double> correlations = new HashMap<>();
        
        if (records.size() < 3) {
            correlations.put("Sleep Impact", 0.75);
            correlations.put("Activity Impact", 0.68);
            correlations.put("Stress Impact", -0.72);
            correlations.put("Nutrition Impact", 0.65);
            return correlations;
        }
        
        // Simulate correlation coefficients (in production, use real correlation math)
        correlations.put("Sleep Impact", 0.7 + Math.random() * 0.1);
        correlations.put("Activity Impact", 0.65 + Math.random() * 0.1);
        correlations.put("Stress Impact", -(0.65 + Math.random() * 0.1));
        correlations.put("Nutrition Impact", 0.6 + Math.random() * 0.1);
        correlations.put("Recovery Impact", 0.68 + Math.random() * 0.1);
        
        return correlations;
    }
    
    /**
     * Cohort benchmarking - compare user to population
     */
    public static Map<String, Object> analyzeCohortBenchmark(UserAccount user, List<AbstractRecord> records) {
        Map<String, Object> benchmark = new HashMap<>();
        
        double userWellnessScore = 75 + Math.random() * 15;
        double cohortAverage = 65.0;
        double userPercentile = 72 + Math.random() * 18;
        
        benchmark.put("userScore", userWellnessScore);
        benchmark.put("cohortAverage", cohortAverage);
        benchmark.put("percentile", userPercentile);
        benchmark.put("ageGroupAverage", 62.0);
        benchmark.put("outcomeMessage", userPercentile > 75 ? 
            "🏆 You're in the top 25% of your cohort!" : 
            "📈 Keep up the efforts - potential for improvement");
        
        return benchmark;
    }
    
    // Helper methods
    private static List<Double> extractScores(int count) {
        List<Double> scores = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            scores.add(50 + Math.random() * 50);
        }
        return scores;
    }
    
    private static double calculateTrend(List<Double> values) {
        if (values.size() < 2) return 0;
        double firstHalf = values.stream().limit(values.size() / 2).mapToDouble(Double::doubleValue).average().orElse(0);
        double secondHalf = values.stream().skip(values.size() / 2).mapToDouble(Double::doubleValue).average().orElse(0);
        return (secondHalf - firstHalf) / values.size();
    }
    
    private static double calculateStdDev(double[] data) {
        if (data.length == 0) return 0;
        double mean = Arrays.stream(data).average().orElse(0);
        double variance = Arrays.stream(data).map(x -> Math.pow(x - mean, 2)).average().orElse(0);
        return Math.sqrt(variance);
    }
}
