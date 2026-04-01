package wellness.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import wellness.model.AbstractRecord;
import wellness.model.AlertLevel;
import wellness.model.HealthRecord;
import wellness.model.ActivityRecord;
import wellness.model.WellnessProfile;

public class WellnessAnalyzer {

    public double calculateOverallScore(List<AbstractRecord> records) {
        if (records == null || records.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (AbstractRecord record : records) {
            total += record.calculateImpactScore(); // runtime polymorphism
        }
        return total / records.size();
    }

    public AlertLevel classifyRisk(double score) {
        if (score >= 75) {
            return AlertLevel.LOW_RISK;
        } else if (score >= 50) {
            return AlertLevel.MODERATE_RISK;
        }
        return AlertLevel.HIGH_RISK;
    }

    public String buildRecommendation(WellnessProfile profile, List<AbstractRecord> records) {
        if (profile == null) {
            return "Create a user profile first to receive personalized recommendations.";
        }
        if (records == null || records.isEmpty()) {
            return "Add health and activity records to generate wellness insights.";
        }

        List<AbstractRecord> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparing(AbstractRecord::getDate).reversed());
        AbstractRecord latest = sorted.get(0);
        double overall = calculateOverallScore(records);
        StringBuilder sb = new StringBuilder();

        sb.append("Overall wellness score: ").append(String.format("%.1f", overall)).append("/100. ");
        sb.append("Current risk category: ").append(classifyRisk(overall).getLabel()).append(". ");

        double bmi = profile.calculateBMI();
        if (bmi < 18.5) {
            sb.append("BMI indicates underweight; consider balanced nutritional planning. ");
        } else if (bmi > 24.9) {
            sb.append("BMI is above the healthy reference range; aim for steady activity and nutrition habits. ");
        } else {
            sb.append("BMI is within the healthy reference range. ");
        }

        switch (latest) {
            case HealthRecord hr -> {
                if (hr.getSleepHours() < 7) sb.append("Increase sleep duration toward 7-9 hours. ");
                if (hr.getWaterIntakeMl() < 2000) sb.append("Improve hydration toward at least 2000 ml/day. ");
                if (hr.getStressScore() > 6) sb.append("Stress is elevated; schedule recovery, breathing, or relaxation periods. ");
            }
            case ActivityRecord ar -> {
                if (ar.getSteps() < 8000) sb.append("Aim for more walking and movement breaks throughout the day. ");
                if (ar.getActiveMinutes() < 45) sb.append("Increase moderate activity duration to build consistency. ");
                if (ar.getScreenTimeMinutes() > 240) sb.append("Reduce recreational screen time to support better energy and sleep quality. ");
            }
            default -> {
                // No extra guidance for unsupported record types.
            }
        }

        sb.append("Primary goal: ").append(profile.getGoal()).append(".");
        return sb.toString();
    }
}
