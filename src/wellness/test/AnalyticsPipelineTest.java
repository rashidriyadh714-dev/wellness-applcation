package wellness.test;

import wellness.model.ActivityRecord;
import wellness.model.AbstractRecord;
import wellness.model.HealthRecord;
import wellness.model.WellnessProfile;
import wellness.service.ContributorRegistry;
import wellness.service.ScoringPipeline;
import wellness.service.TelemetryService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsPipelineTest {
    public static void main(String[] args) {
        ContributorRegistry registry = new ContributorRegistry();
        registry.setWeight("health", 1.2);
        registry.setWeight("activity", 0.9);

        TelemetryService telemetry = new TelemetryService();
        ScoringPipeline pipeline = new ScoringPipeline(registry, telemetry);

        List<AbstractRecord> day1 = new ArrayList<>();
        day1.add(new HealthRecord("h1", LocalDate.now().minusDays(2), "ok", 7.0, 2000, 8, 3, 60));
        day1.add(new ActivityRecord("a1", LocalDate.now().minusDays(2), "run", 8000, 60, 500.0, 90));

        WellnessProfile profile = new WellnessProfile("u1", "Test User", 35, 175.0, 75.0, "Fitness");

        double score1 = pipeline.computeFinalScore(day1, profile);
        System.out.println("Final score (day1) => " + score1);

        List<AbstractRecord> day2 = new ArrayList<>();
        day2.add(new HealthRecord("h2", LocalDate.now(), "better sleep", 8.5, 2200, 9, 2, 58));
        day2.add(new ActivityRecord("a2", LocalDate.now(), "run", 10000, 80, 700.0, 60));
        double score2 = pipeline.computeFinalScore(day2, profile);
        System.out.println("Final score (day2) => " + score2);

        if (score1 <= 0 || score1 > 100) throw new RuntimeException("Invalid score1: " + score1);
        if (score2 <= 0 || score2 > 100) throw new RuntimeException("Invalid score2: " + score2);

        System.out.println("Analytics pipeline test completed.");
    }
}
