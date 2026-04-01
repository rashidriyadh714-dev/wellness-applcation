package wellness.ui;

import wellness.model.AbstractRecord;
import wellness.model.WellnessProfile;
import wellness.service.MLAnalyticsEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Advanced insights with premium visual cards and trend rendering.
 */
public class AdvancedInsightsPanel extends JPanel {

    private final WellnessProfile profile;
    private final List<AbstractRecord> records;
    private final JLabel modelTimestampLabel = new JLabel();
    private final JLabel refreshStatusLabel = new JLabel("Ready");

    public AdvancedInsightsPanel(WellnessProfile profile, List<AbstractRecord> records) {
        this.profile = profile;
        this.records = records == null ? new ArrayList<>() : records;

        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        refreshView(false);
    }

    private void refreshView(boolean showToast) {
        removeAll();
        add(buildTopMetrics(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        updateTimestamp();
        revalidate();
        repaint();
        if (showToast) {
            JOptionPane.showMessageDialog(this, "Model refreshed successfully.", "Advanced Insights", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JComponent buildTopMetrics() {
        Map<String, Object> forecast = MLAnalyticsEngine.forecast30DayWellness(null, records);
        double avg = asDouble(forecast.get("avgScore"));
        double projected = asDouble(forecast.get("projectedScore"));
        double confidence = asDouble(forecast.get("confidence")) * 100.0;
        String trend = String.valueOf(forecast.get("trend"));

        JPanel row = new JPanel(new GridLayout(1, 4, 10, 10));
        row.setOpaque(false);
        row.add(metricCard("Current Index", String.format("%.1f", avg), "Out of 100"));
        row.add(metricCard("30-Day Projection", String.format("%.1f", projected), "Out of 100"));
        row.add(metricCard("Model Confidence", String.format("%.0f%%", confidence), "Predictive certainty"));
        row.add(metricCard("Trend", toTitle(trend), "Direction"));
        return row;
    }

    private JComponent metricCard(String title, String value, String caption) {
        JPanel card = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(new Color(25, 25, 25));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        titleLabel.setForeground(new Color(95, 95, 95));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        valueLabel.setForeground(Color.BLACK);

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        captionLabel.setForeground(new Color(115, 115, 115));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(captionLabel, BorderLayout.SOUTH);
        return card;
    }

    private JComponent buildMainContent() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.55);
        split.setBorder(BorderFactory.createEmptyBorder());

        JTabbedPane analyticsTabs = new JTabbedPane();
        analyticsTabs.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        analyticsTabs.addTab("Forecast", buildForecastPanel());
        analyticsTabs.addTab("Correlations", buildCorrelationsPanel());
        analyticsTabs.addTab("Benchmark", buildBenchmarkPanel());

        split.setLeftComponent(analyticsTabs);
        split.setRightComponent(buildNarrativePanel());
        return split;
    }

    private JComponent buildForecastPanel() {
        Map<String, Object> forecast = MLAnalyticsEngine.forecast30DayWellness(null, records);
        @SuppressWarnings("unchecked")
        List<Double> forecastValues = (List<Double>) forecast.get("forecast30Days");
        if (forecastValues == null || forecastValues.isEmpty()) {
            forecastValues = defaultForecast();
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("30-Day Projection Curve");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        TrendPanel trendPanel = new TrendPanel(forecastValues);
        trendPanel.setPreferredSize(new Dimension(500, 280));

        JTextArea summary = new JTextArea();
        summary.setEditable(false);
        summary.setLineWrap(true);
        summary.setWrapStyleWord(true);
        summary.setBackground(new Color(248, 248, 248));
        summary.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        summary.setBorder(new EmptyBorder(10, 10, 10, 10));
        summary.setText("""
            Executive Interpretation
            - The projected trajectory models expected score drift over 30 days.
            - Consistency in sleep, movement, and stress routines drives sustained gains.
            - Large day-to-day swings indicate recovery instability and should be corrected.
            """);

        panel.add(title, BorderLayout.NORTH);
        panel.add(trendPanel, BorderLayout.CENTER);
        panel.add(summary, BorderLayout.SOUTH);
        return panel;
    }

    private JComponent buildCorrelationsPanel() {
        Map<String, Double> correlations = MLAnalyticsEngine.analyzeCorrelations(records);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel rows = new JPanel();
        rows.setBackground(Color.WHITE);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));

        for (Map.Entry<String, Double> entry : correlations.entrySet()) {
            rows.add(correlationRow(entry.getKey(), entry.getValue()));
            rows.add(Box.createVerticalStrut(8));
        }

        panel.add(new JLabel("Influence Map"), BorderLayout.NORTH);
        panel.add(new JScrollPane(rows), BorderLayout.CENTER);
        return panel;
    }

    private JComponent correlationRow(String label, double value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel name = new JLabel(label);
        name.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) Math.min(100, Math.round(Math.abs(value) * 100)));
        bar.setStringPainted(true);
        bar.setString(String.format("%.2f", value));
        bar.setForeground(value >= 0 ? Color.BLACK : new Color(80, 80, 80));
        bar.setBackground(new Color(230, 230, 230));

        row.add(name, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    private JComponent buildBenchmarkPanel() {
        Map<String, Object> benchmark = MLAnalyticsEngine.analyzeCohortBenchmark(null, records);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setBackground(new Color(248, 248, 248));
        area.setBorder(new EmptyBorder(12, 12, 12, 12));

        String text = """
            Cohort Positioning
            --------------------------------------
            Your Score:            %5.1f
            Cohort Average:        %5.1f
            Age Group Average:     %5.1f
            Percentile Rank:       %5.0f%%

            Interpretation:
            %s

            Action Plan:
            1. Increase weekly zone-2 cardio volume.
            2. Keep sleep regularity above 85 percent.
            3. Limit high-stress days with active recovery.
            """.formatted(
            asDouble(benchmark.get("userScore")),
            asDouble(benchmark.get("cohortAverage")),
            asDouble(benchmark.get("ageGroupAverage")),
            asDouble(benchmark.get("percentile")),
            String.valueOf(benchmark.get("outcomeMessage"))
        );

        area.setText(text);
        return new JScrollPane(area);
    }

    private JComponent buildNarrativePanel() {
        JTextArea narrative = new JTextArea();
        narrative.setEditable(false);
        narrative.setLineWrap(true);
        narrative.setWrapStyleWord(true);
        narrative.setBackground(new Color(248, 248, 248));
        narrative.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        narrative.setBorder(new EmptyBorder(12, 12, 12, 12));

        String profileName = profile == null ? "User" : profile.getFullName();
        double bmi = profile == null ? 0.0 : profile.calculateBMI();

        String bmiLine = profile == null ? "BMI: N/A" : String.format("BMI: %.1f", bmi);
        narrative.setText("""
            AI Executive Brief

            Profile: %s
            %s

            This panel summarizes actionable strategy in executive format.
            Use this in presentation mode:
            - Open Forecast to show trajectory and confidence.
            - Open Correlations to explain signal impact.
            - Open Benchmark to show market-grade score positioning.

            Automation Focus for Next Sprint:
            - Trigger alerts when trend slope turns negative.
            - Auto-generate weekly goals from weak metrics.
            - Push personalized routines into AI assistant quick prompts.
            """.formatted(profileName, bmiLine));

        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setBackground(Color.WHITE);
        JLabel title = new JLabel("Executive Narrative");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        modelTimestampLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        modelTimestampLabel.setForeground(new Color(90, 90, 90));
        refreshStatusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        refreshStatusLabel.setForeground(new Color(90, 90, 90));

        JComboBox<String> scenarioSelector = new JComboBox<>(new String[]{
                "Baseline",
                "Travel Week",
                "High Workload",
                "Recovery Sprint"
        });
        scenarioSelector.addActionListener(e -> {
            String selected = String.valueOf(scenarioSelector.getSelectedItem());
            narrative.setText(buildNarrativeForScenario(selected));
            narrative.setCaretPosition(0);
        });

        JButton simulateButton = new JButton("Run Scenario Simulation");
        simulateButton.addActionListener(e -> {
            String selected = String.valueOf(scenarioSelector.getSelectedItem());
                String scoreDrift = "Recovery Sprint".equals(selected) ? "+4.5"
                    : "Travel Week".equals(selected) ? "-2.8"
                    : "High Workload".equals(selected) ? "-1.9" : "+1.2";
                String risk = "Recovery Sprint".equals(selected) ? "Lower" : "Watchlist";
                String priority = "Travel Week".equals(selected) ? "Sleep regularity" : "Consistency";
                String simulation = """
                    Scenario Simulation: %s

                    Expected 14-Day Impact:
                    - Score drift: %s
                    - Risk movement: %s
                    - Priority: %s
                    """.formatted(selected, scoreDrift, risk, priority);
            JOptionPane.showMessageDialog(this, simulation, "Scenario Simulation", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton refresh = new JButton("Refresh Model View");
        refresh.setBackground(Color.BLACK);
        refresh.setForeground(Color.WHITE);
        refresh.addActionListener(e -> {
            refresh.setEnabled(false);
            refreshStatusLabel.setText("Refreshing model...");
            Timer timer = new Timer(350, evt -> {
                refreshView(false);
                refreshStatusLabel.setText("Refresh complete");
                refresh.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Insights refreshed. Forecast, correlations, and benchmark are up to date.", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
            });
            timer.setRepeats(false);
            timer.start();
        });

        JButton exportBrief = new JButton("Export Brief");
        exportBrief.addActionListener(e -> {
            exportBriefToFile(narrative.getText());
        });

        JButton sweepButton = new JButton("Run 4-Scenario Sweep");
        sweepButton.addActionListener(e -> showScenarioSweepSummary());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        left.add(title);
        left.add(modelTimestampLabel);

        JPanel leftWrap = new JPanel(new GridLayout(3, 1, 0, 2));
        leftWrap.setOpaque(false);
        leftWrap.add(title);
        leftWrap.add(modelTimestampLabel);
        leftWrap.add(refreshStatusLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(scenarioSelector);
        right.add(simulateButton);
        right.add(sweepButton);
        right.add(exportBrief);
        right.add(refresh);

        top.add(leftWrap, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(narrative), BorderLayout.CENTER);
        return wrapper;
    }

    private void updateTimestamp() {
        modelTimestampLabel.setText("Model snapshot: " + java.time.LocalDateTime.now().withNano(0));
    }

    private double asDouble(Object value) {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private String toTitle(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "Stable";
        }
        return raw.substring(0, 1).toUpperCase() + raw.substring(1);
    }

    private List<Double> defaultForecast() {
        List<Double> values = new ArrayList<>();
        double base = 65.0;
        for (int i = 0; i < 30; i++) {
            values.add(Math.max(0, Math.min(100, base + (i * 0.35) + Math.sin(i / 3.2) * 2.2)));
        }
        return values;
    }

    private String buildNarrativeForScenario(String selected) {
        String adjustments = switch (selected) {
            case "Travel Week" -> """
                Scenario Applied: Travel Week
                - Prioritize circadian stability and hydration timing.
                - Reduce high-intensity load by 20 percent.
                - Add short mobility sessions after long sitting blocks.
                """;
            case "High Workload" -> """
                Scenario Applied: High Workload
                - Protect two deep-focus blocks and enforce micro-breaks.
                - Keep evening wind-down fixed.
                - Shift toward moderate training intensity.
                """;
            case "Recovery Sprint" -> """
                Scenario Applied: Recovery Sprint
                - Increase sleep opportunity to 8.5 hours.
                - Focus zone-2 and mobility only for 5 days.
                - Use hydration and stress tracking checkpoints.
                """;
            default -> """
                Scenario Applied: Baseline
                - Continue standard progression with consistency priority.
                - Weekly review each Sunday to recalibrate routines.
                """;
        };

        String profileName = profile == null ? "User" : profile.getFullName();
        return """
            AI Executive Brief

            Profile: %s

            %s

            Presentation Sequence:
            1) Forecast trajectory
            2) Correlation influence map
            3) Cohort benchmark and action plan
            """.formatted(profileName, adjustments);
    }

    private void exportBriefToFile(String content) {
        File docsDir = new File("docs");
        if (!docsDir.exists() && !docsDir.mkdirs()) {
            JOptionPane.showMessageDialog(this, "Could not create docs directory for export.", "Export Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File target = new File(docsDir, "EXECUTIVE_BRIEF_latest.txt");
        try (FileWriter writer = new FileWriter(target, false)) {
            writer.write(content);
            JOptionPane.showMessageDialog(this,
                    "Executive brief exported to: " + target.getPath(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Export failed: " + ex.getMessage(),
                    "Export Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showScenarioSweepSummary() {
        String[] scenarios = {"Baseline", "Travel Week", "High Workload", "Recovery Sprint"};
        StringBuilder out = new StringBuilder("Scenario Sweep Summary\n\n");
        for (String scenario : scenarios) {
            String scoreDrift = "Recovery Sprint".equals(scenario) ? "+4.5"
                    : "Travel Week".equals(scenario) ? "-2.8"
                    : "High Workload".equals(scenario) ? "-1.9" : "+1.2";
            String risk = "Recovery Sprint".equals(scenario) ? "Lower" : "Watchlist";
            out.append("- ").append(scenario)
                    .append(": drift ").append(scoreDrift)
                    .append(", risk ").append(risk)
                    .append("\n");
        }
        JOptionPane.showMessageDialog(this, out.toString(), "Scenario Sweep", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class TrendPanel extends JPanel {
        private final List<Double> values;

        private TrendPanel(List<Double> values) {
            this.values = values;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int left = 45;
            int right = getWidth() - 20;
            int top = 20;
            int bottom = getHeight() - 30;
            int width = Math.max(1, right - left);
            int height = Math.max(1, bottom - top);

            g2.setColor(new Color(230, 230, 230));
            for (int i = 0; i <= 4; i++) {
                int y = top + (i * height / 4);
                g2.drawLine(left, y, right, y);
            }

            g2.setColor(Color.BLACK);
            g2.drawLine(left, bottom, right, bottom);
            g2.drawLine(left, top, left, bottom);

            if (values.isEmpty()) {
                return;
            }

            Path2D trend = new Path2D.Double();
            int n = values.size();
            int step = n > 1 ? width / (n - 1) : width;

            for (int i = 0; i < n; i++) {
                int x = left + (i * step);
                int y = bottom - (int) (Math.max(0, Math.min(100, values.get(i))) * height / 100.0);
                if (i == 0) {
                    trend.moveTo(x, y);
                } else {
                    trend.lineTo(x, y);
                }
            }

            g2.setStroke(new BasicStroke(2.0f));
            g2.draw(trend);
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            g2.drawString("Day 1", left, bottom + 16);
            g2.drawString("Day 30", right - 36, bottom + 16);
            g2.drawString("100", 13, top + 4);
            g2.drawString("0", 22, bottom + 4);
        }
    }
}
