package wellness.test;

import wellness.service.TelemetryService;
import wellness.service.AdvancedAnalytics;
import wellness.model.AbstractRecord;
import wellness.model.HealthRecord;
import wellness.model.ActivityRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelemetryTest {
    public static void main(String[] args) {
        TelemetryService t = new TelemetryService();
        Map<String,String> props = new HashMap<>();
        props.put("user","test@example.com");
        props.put("action","start_test");
        t.logEvent("app.start", props);
        t.logEvent("user.login", Map.of("user", "test@example.com"));
        System.out.println("Telemetry events written.");

        AdvancedAnalytics adv = new AdvancedAnalytics();
        List<AbstractRecord> records = new ArrayList<>();
        records.add(new HealthRecord("h1", LocalDate.now().minusDays(2), "note", 6.5, 1800, 6, 5, 70));
        records.add(new ActivityRecord("a1", LocalDate.now(), "run", 10000, 80, 700.0, 60));
        Map<String, Double> weights = new HashMap<>();
        weights.put("health", 1.2);
        weights.put("activity", 0.8);
        double wScore = adv.computeWeightedScore(records, weights);
        double trend = adv.computeTrend(records);
        System.out.println("Weighted score: " + wScore);
        System.out.println("Trend: " + trend);
    }
}
