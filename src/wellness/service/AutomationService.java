package wellness.service;

import wellness.model.*;
import java.util.*;

public class AutomationService {
    private final WellnessManager manager;
    private final TelemetryService telemetry;
    private final AnalyticsEngine analyticsEngine;

    public AutomationService(WellnessManager manager, TelemetryService telemetry) {
        this.manager = manager;
        this.telemetry = telemetry;
        this.analyticsEngine = new AnalyticsEngine();
    }

    public String generatePersonalizedWorkout() {
        WellnessProfile profile = manager.getProfile();
        int age = profile.getAge();
        double score = analyticsEngine.computeScore(profile);
        StringBuilder workout = new StringBuilder("Personalized Workout:\n");
        if (age >= 60) {
            workout.append("Low-Impact (30 min): Tai chi, light stretching\n");
        } else if (age >= 40) {
            workout.append("Balanced (45 min): Cardio + strength training\n");
        } else {
            workout.append("High-Intensity (60 min): HIIT + power training\n");
        }
        workout.append(String.format("Current readiness score: %.1f/100\n", score));
        Map<String, String> details = new HashMap<>();
        details.put("type", "automated");
        telemetry.logEvent("workout_generated", details);
        return workout.toString();
    }

    public List<String> generateSmartReminders() {
        List<String> reminders = new ArrayList<>();
        reminders.add("Morning: 500ml water");
        reminders.add("Hourly: 5-min movement");
        reminders.add("Balanced lunch");
        reminders.add("Evening: No screens 1h before bed");
        Map<String, String> details = new HashMap<>();
        details.put("type", "daily");
        telemetry.logEvent("reminders_generated", details);
        return reminders;
    }

    public String generateAutoInsights() {
        double score = analyticsEngine.computeScore(manager.getProfile());
        StringBuilder insights = new StringBuilder("AI Insights:\n");
        if (score >= 80) insights.append("Elite status achieved!\n");
        else if (score >= 60) insights.append("Strong progress!\n");
        else insights.append("Growth opportunity ahead!\n");
        Map<String, String> details = new HashMap<>();
        details.put("type", "automated");
        telemetry.logEvent("insights_generated", details);
        return insights.toString();
    }

    public String predictTrend() {
        double score = analyticsEngine.computeScore(manager.getProfile());
        String trend = "7-Day Forecast: ";
        if (score >= 60) trend += "Positive trend expected!";
        else trend += "Focus on improvements.";
        Map<String, String> details = new HashMap<>();
        details.put("type", "forecast");
        telemetry.logEvent("trend_predicted", details);
        return trend;
    }

    public String suggestHabit() {
        return "Start with meditation or a daily walk!";
    }

    public String adaptiveCoaching() {
        double score = analyticsEngine.computeScore(manager.getProfile());
        if (score < 40) return "Foundation level: Build basics";
        else if (score < 70) return "Progress level: Advance your routine";
        else return "Elite level: Maintain excellence";
    }
}
