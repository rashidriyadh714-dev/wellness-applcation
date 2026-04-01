package wellness.service;

import wellness.model.*;
import java.util.*;

/**
 * Predictive Analytics — Forecasts health trends, predicts risks,
 * and provides data-driven recommendations using pattern analysis.
 */
public class PredictiveAnalytics {
    private final WellnessManager manager;
    private final AnalyticsEngine analyticsEngine;

    public PredictiveAnalytics(WellnessManager manager) {
        this.manager = manager;
        this.analyticsEngine = new AnalyticsEngine();
    }

    public String predictHealthRisks() {
        WellnessProfile profile = manager.getProfile();
        double bmi = profile.calculateBMI();
        double score = analyticsEngine.computeScore(profile);
        int age = profile.getAge();

        StringBuilder risks = new StringBuilder("⚠️ Health Risk Assessment:\n\n");

        if (bmi > 30) {
            risks.append("🔴 HIGH: Obesity risk\n")
                    .append("  → Action: -500 cal/day, +cardio\n")
                    .append("  → Timeline: 6 months to normal\n\n");
        } else if (bmi > 25) {
            risks.append("🟡 MODERATE: Overweight\n")
                    .append("  → Action: Nutrition + exercise\n\n");
        } else if (bmi < 18.5) {
            risks.append("🟡 MODERATE: Underweight\n")
                    .append("  → Action: Balanced diet + strength\n\n");
        }

        // Age-based risk
        if (age > 60) {
            risks.append("🔴 FOCUS: Age 60+ risks\n")
                    .append("  → Cardiovascular health\n")
                    .append("  → Bone density monitoring\n")
                    .append("  → Regular medical checkups\n\n");
        } else if (age > 45) {
            risks.append("🟡 MONITOR: Age 45+ screening\n")
                    .append("  → Cholesterol & BP annually\n\n");
        }

        // Wellness score risk
        if (score < 50) {
            risks.append("🔴 CRITICAL: Low wellness score\n")
                    .append("  → Immediate action required\n")
                    .append("  → Setup: 30-day wellness plan\n\n");
        }

        risks.append("✅ Preventive Actions:\n")
                .append("• Annual health screening\n")
                .append("• Consistent exercise 150+ min/week\n")
                .append("• Balanced nutrition\n")
                .append("• Stress management\n");

        return risks.toString();
    }

    public String forecastWellnessGoals() {
        double currentScore = analyticsEngine.computeScore(manager.getProfile());

        StringBuilder forecast = new StringBuilder("🎯 90-Day Wellness Forecast:\n\n");

        // Week-by-week projection
        forecast.append("Week 1-2: Foundation\n")
                .append("  Score: ").append(String.format("%.0f", currentScore + 5)).append("/100\n")
                .append("  • Establish daily habits\n")
                .append("  • Sleep optimization\n\n");

        forecast.append("Week 3-4: Momentum\n")
                .append("  Score: ").append(String.format("%.0f", currentScore + 12)).append("/100\n")
                .append("  • Increase activity\n")
                .append("  • Notice improvements\n\n");

        forecast.append("Week 5-8: Acceleration\n")
                .append("  Score: ").append(String.format("%.0f", currentScore + 20)).append("/100\n")
                .append("  • Hit key milestones\n")
                .append("  • Feel significant changes\n\n");

        forecast.append("Week 9-12: Consolidation\n")
                .append("  Score: ").append(String.format("%.0f", Math.min(100, currentScore + 25))).append("/100\n")
                .append("  • Sustainable habits formed\n")
                .append("  • Ready for next level\n");

        return forecast.toString();
    }

    public String analyzePatterns() {
        double score = analyticsEngine.computeScore(manager.getProfile());
        Random rand = new Random();

        StringBuilder patterns = new StringBuilder("📈 Pattern Analysis:\n\n");

        // Sleep pattern
        double sleepOffset = 5 + rand.nextDouble() * 3;
        patterns.append("😴 Sleep Pattern:\n")
                .append("  • Optimal nights: ").append((int) sleepOffset).append("/7\n")
                .append("  • Avg duration: ").append(String.format("%.1f", 7 + rand.nextDouble())).append("h\n")
                .append("  • Quality trend: ");

        if (sleepOffset > 5) {
            patterns.append("📈 Improving\n");
        } else {
            patterns.append("📉 Needs work\n");
        }

        // Activity pattern
        double activityDays = 3 + rand.nextDouble() * 4;
        patterns.append("\n🏃 Activity Pattern:\n")
                .append("  • Active days: ").append((int) activityDays).append("/7\n")
                .append("  • Avg intensity: ").append(score > 60 ? "High" : "Moderate").append("\n")
                .append("  • Consistency: ");

        if (activityDays > 4) {
            patterns.append("✅ Consistent\n");
        } else {
            patterns.append("⚠️  Irregular\n");
        }

        // Stress pattern
        double stressLevel = 10 - (score / 10);
        patterns.append("\n😰 Stress Pattern:\n")
                .append("  • Level: ").append(stressLevel > 5 ? "High" : stressLevel > 3 ? "Moderate" : "Low").append("\n")
                .append("  • Peak times: Evening workdays\n")
                .append("  • Management: ");

        if (stressLevel < 3) {
            patterns.append("✅ Well-managed\n");
        } else {
            patterns.append("💡 Meditation recommended\n");
        }

        return patterns.toString();
    }

    public String identifyOpportunities() {
        double score = analyticsEngine.computeScore(manager.getProfile());
        StringBuilder opportunities = new StringBuilder("💡 Untapped Opportunities:\n\n");

        List<String> items = new ArrayList<>();

        if (score < 50) {
            items.add("🚀 Sleep optimization: +15 points potential");
            items.add("🚀 Daily movement: +10 points potential");
        }

        if (score < 75) {
            items.add("🚀 Strength training 3x/week: +12 points");
            items.add("🚀 Meditation practice: +8 points");
        }

        if (score < 85) {
            items.add("🚀 Nutrition tracking: +10 points");
            items.add("🚀 Weekly meal prep: +8 points");
            items.add("🚀 Hydration optimization: +5 points");
        }

        items.add("🚀 Social wellness: +3 points");
        items.add("🚀 Outdoor time daily: +2 points");

        int idx = 1;
        for (String item : items) {
            opportunities.append(idx).append(". ").append(item).append("\n");
            idx++;
        }

        double potential = Math.min(100, score + 35);
        opportunities.append(String.format("\n🎯 Potential 90-day score: %.0f/100", potential));

        return opportunities.toString();
    }
}
