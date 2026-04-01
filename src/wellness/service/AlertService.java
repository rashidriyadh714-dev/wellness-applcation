package wellness.service;

import wellness.model.WellnessProfile;
import java.util.*;

/**
 * Alert Service — Generates real-time health alerts, anomaly detection,
 * and urgent notifications based on health metrics and rules.
 */
public class AlertService {
    private final List<HealthAlert> alerts = new ArrayList<>();
    private final TelemetryService telemetry;

    public static class HealthAlert {
        public String severity; // CRITICAL, WARNING, INFO
        public String title;
        public String message;
        public String timestamp;
        public boolean acknowledged;

        public HealthAlert(String severity, String title, String message) {
            this.severity = severity;
            this.title = title;
            this.message = message;
            this.timestamp = new java.text.SimpleDateFormat("HH:mm").format(new Date());
            this.acknowledged = false;
        }
    }

    public AlertService(TelemetryService telemetry) {
        this.telemetry = telemetry;
    }

    public void checkAndAlert(double wellnessScore, WellnessProfile profile, double heartRate) {
        // Wellness score alerts
        if (wellnessScore < 30) {
            addAlert("CRITICAL", "🚨 Critical Health Status",
                    "Your wellness score is critically low. Please seek medical attention if feeling unwell.");
        } else if (wellnessScore < 50) {
            addAlert("WARNING", "⚠️  Low Wellness Score",
                    "Your wellness score has dropped significantly. Check your recent activities and health metrics.");
        }

        // Heart rate alerts
        if ((heartRate > 100 || heartRate < 60) && !(heartRate >= 60 && heartRate <= 100)) {
            addAlert("WARNING", "❤️ Abnormal Heart Rate",
                    String.format("Your heart rate is %.0f bpm. This may indicate stress or health issues.", heartRate));
        }

        // Age-based alerts
        if (profile.getAge() > 60) {
            addAlert("INFO", "💙 Senior Health Check",
                    "Regular health screening recommended for age 60+. Schedule your annual checkup!");
        }

        // BMI alerts
        double bmi = profile.calculateBMI();
        if (bmi > 30) {
            addAlert("WARNING", "⚠️  Obesity Risk",
                    "BMI indicates obesity. Consider nutrition consultation and increased activity.");
        } else if (bmi < 18.5) {
            addAlert("WARNING", "⚠️  Underweight",
                    "BMI is below normal range. Ensure balanced nutrition and strength training.");
        }
    }

    public void addAlert(String severity, String title, String message) {
        HealthAlert alert = new HealthAlert(severity, title, message);
        alerts.add(alert);
        java.util.Map<String, String> details = new java.util.HashMap<>();
        details.put("severity", severity);
        telemetry.logEvent("alert_generated", details);
    }

    public String getAlerts() {
        StringBuilder sb = new StringBuilder("🔔 Health Alerts:\n\n");

        if (alerts.isEmpty()) {
            sb.append("✅ No alerts. You're doing great!\n");
            return sb.toString();
        }

        // Sort by severity
        List<HealthAlert> sorted = new ArrayList<>(alerts);
        sorted.sort((a, b) -> getSeverityLevel(b.severity) - getSeverityLevel(a.severity));

        for (HealthAlert alert : sorted) {
            String icon = alert.severity.equals("CRITICAL") ? "🚨" :
                         alert.severity.equals("WARNING") ? "⚠️ " : "ℹ️ ";

            sb.append(icon).append(" [").append(alert.timestamp).append("] ")
                    .append(alert.title).append("\n")
                    .append("   ").append(alert.message).append("\n")
                    .append("   Status: ").append(alert.acknowledged ? "✅ Acknowledged" : "🔔 Pending").append("\n\n");
        }

        return sb.toString();
    }

    public void acknowledgeAlert(int index) {
        if (index >= 0 && index < alerts.size()) {
            alerts.get(index).acknowledged = true;
            java.util.Map<String,String> details = new java.util.HashMap<>();
            details.put("alert_index", String.valueOf(index));
            telemetry.logEvent("alert_acknowledged", details);
        }
    }

    public String getRecentAlerts(int count) {
        StringBuilder sb = new StringBuilder("📌 Recent Alerts:\n\n");

        List<HealthAlert> recent = alerts.stream()
                .skip(Math.max(0, alerts.size() - count))
                .toList();

        if (recent.isEmpty()) {
            sb.append("✅ No recent alerts\n");
        } else {
            for (HealthAlert a : recent) {
                sb.append(a.severity).append(": ").append(a.title).append("\n");
            }
        }

        return sb.toString();
    }

    public String emergencyProtocol() {
        return """
            🚨 EMERGENCY PROTOCOL:

            If experiencing:
            • Chest pain → Call 911
            • Difficulty breathing → Call 911
            • Severe dizziness → Call 911
            • Persistent pain → See doctor
            • Mental health crisis → Contact crisis line

            This app is NOT a substitute for medical care.
            Always consult healthcare providers.
            """;
    }

    private int getSeverityLevel(String severity) {
        return switch (severity) {
            case "CRITICAL" -> 3;
            case "WARNING" -> 2;
            case "INFO" -> 1;
            default -> 0;
        };
    }
}
