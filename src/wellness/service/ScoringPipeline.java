package wellness.service;

import wellness.model.AbstractRecord;
import wellness.model.WellnessProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Orchestrates analytics steps: weighted scoring, profile baseline, smoothing and telemetry.
 */
public class ScoringPipeline {
    private final AnalyticsEngine engine;
    private final AdvancedAnalytics advanced;
    private final ContributorRegistry registry;
    private final MovingAverage smoother;
    private final TelemetryService telemetry;

    public ScoringPipeline(ContributorRegistry registry, TelemetryService telemetry) {
        this.engine = new AnalyticsEngine();
        this.advanced = new AdvancedAnalytics();
        this.registry = registry == null ? new ContributorRegistry() : registry;
        this.telemetry = telemetry == null ? new TelemetryService() : telemetry;
        this.smoother = new MovingAverage(3);
    }

    /**
     * Compute a final, smoothed 0-100 wellness score.
     */
    public double computeFinalScore(List<AbstractRecord> records, WellnessProfile profile) {
        Map<String, Double> weights = registry.getAllWeights();
        double weighted = advanced.computeWeightedScore(records, weights);

        double personal = engine.computeScore(profile);

        // combine: records (70%) + personal (30%)
        double combined = (weighted * 0.7) + (personal * 0.3);

        // smoothing
        smoother.add(combined);
        double smooth = smoother.average();

        // final blend (favor smoothed historical trend)
        double finalScore = (combined * 0.4) + (smooth * 0.6);

        // log telemetry
        Map<String, String> props = new HashMap<>();
        props.put("final", String.valueOf(finalScore));
        props.put("weighted", String.valueOf(weighted));
        props.put("personal", String.valueOf(personal));
        telemetry.logEvent("score_computed", props);

        return Math.max(0.0, Math.min(100.0, finalScore));
    }

    public List<Double> computeHistoryScores(List<List<AbstractRecord>> history, WellnessProfile profile) {
        return history.stream().map(records -> computeFinalScore(records, profile)).collect(Collectors.toList());
    }
}
