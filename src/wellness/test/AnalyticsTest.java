package wellness.test;

import wellness.service.AnalyticsEngine;
import wellness.model.HealthRecord;
import wellness.model.ActivityRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsTest {
    public static void main(String[] args) {
        AnalyticsEngine engine = new AnalyticsEngine();

        double scoreNull = engine.computeScore(null);
        System.out.println("AnalyticsEngine.computeScore(null) => " + scoreNull);

        Map<String, Double> contributors = new HashMap<>();
        contributors.put("sleep_quality", 7.0);
        contributors.put("activity_load", -3.0);
        double fromContrib = engine.computeFromContributors(contributors);
        System.out.println("computeFromContributors => " + fromContrib);

        List<Object> records = new ArrayList<>();
        records.add(new HealthRecord("h1", LocalDate.now(), "ok", 7.0, 2000, 8, 3, 60));
        records.add(new ActivityRecord("a1", LocalDate.now(), "run", 8000, 60, 500.0, 90));
        double collScore = engine.computeScore(records);
        System.out.println("computeScore from records => " + collScore);

        // Basic sanity checks
        if (scoreNull != 50.0) throw new RuntimeException("Baseline score changed: " + scoreNull);
        if (fromContrib <= 0.0 || fromContrib > 100.0) throw new RuntimeException("Contributor score invalid: " + fromContrib);
        if (collScore <= 0.0 || collScore > 100.0) throw new RuntimeException("Records score invalid: " + collScore);

        System.out.println("Analytics test completed successfully.");
    }
}
