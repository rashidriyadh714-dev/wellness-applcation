package wellness.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import wellness.service.WellnessManager;
import wellness.model.WellnessProfile;
import wellness.model.AbstractRecord;

public class DashboardPanel extends JPanel {
    private final WellnessManager manager;
    private final JLabel nameValue = new JLabel("-");
    private final JLabel bmiValue = new JLabel("-");
    private final JLabel scoreValue = new JLabel("0.0 / 100");
    private final JLabel riskValue = new JLabel("-");
    private final JLabel totalValue = new JLabel("0");
    private final JLabel momentumValue = new JLabel("0.0");
    private final JLabel projectionValue = new JLabel("Projected: 0.0 / 100");
    private final JTextArea recommendationArea = new JTextArea(6, 30);
    private final JTextArea summaryArea = new JTextArea(10, 30);
    private final JTextArea whatIfNarrative = new JTextArea(6, 20);
    private final JPanel chartsPanel = new JPanel();
    private final JLabel lastRefreshLabel = new JLabel("Last refresh: -");
    private final TrendChartPanel trendChartPanel = new TrendChartPanel();
    private JSlider sleepDeltaSlider;
    private JSlider activityDeltaSlider;
    private JSlider stressDeltaSlider;
    private double displayedScore = 0.0;
    private int displayedTotal = 0;
    private double displayedMomentum = 0.0;

    public DashboardPanel(WellnessManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        JPanel metricsPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        metricsPanel.setBackground(Color.WHITE);
        metricsPanel.add(buildKPICard("Wellness Score", scoreValue, "High"));
        metricsPanel.add(buildKPICard("BMI Status", bmiValue, "Normal"));
        metricsPanel.add(buildKPICard("Risk Level", riskValue, "Low"));
        metricsPanel.add(buildKPICard("Total Records", totalValue, "Active"));
        metricsPanel.add(buildKPICard("Momentum", momentumValue, "14-day delta"));

        recommendationArea.setLineWrap(true);
        recommendationArea.setWrapStyleWord(true);
        recommendationArea.setEditable(false);
        recommendationArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        recommendationArea.setBackground(new Color(250, 250, 250));
        recommendationArea.setForeground(Color.BLACK);
        recommendationArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        summaryArea.setBackground(new Color(250, 250, 250));
        summaryArea.setForeground(new Color(40, 40, 40));
        summaryArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        chartsPanel.setLayout(new GridLayout(1, 1));
        chartsPanel.setBackground(Color.WHITE);
        chartsPanel.add(buildChartPanel());

        JPanel center = new JPanel(new GridLayout(2, 1, 12, 12));
        center.setBackground(Color.WHITE);
        JPanel top = new JPanel(new GridLayout(1, 3, 12, 12));
        top.setBackground(Color.WHITE);
        top.add(wrap("Recommendations", new JScrollPane(recommendationArea)));
        top.add(chartsPanel);
        top.add(wrap("What-If Simulator", buildWhatIfPanel()));
        center.add(top);
        center.add(wrap("Detailed Summary", new JScrollPane(summaryArea)));

        JPanel north = new JPanel(new BorderLayout(8, 6));
        north.setBackground(Color.WHITE);
        north.add(metricsPanel, BorderLayout.CENTER);
        north.add(buildDashboardToolbar(), BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        refreshInternal();
    }

    private JPanel buildDashboardToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh Insights");
        refreshButton.addActionListener(e -> refresh());

        JButton speakingPointsButton = new JButton("Demo Talking Points");
        speakingPointsButton.addActionListener(e -> {
            String points = """
                Dashboard Talking Points

                1. Start with wellness score and momentum.
                2. Explain risk level and BMI context.
                3. Show 14-day trend for consistency story.
                4. Close with recommendations and next actions.
                """;
            JOptionPane.showMessageDialog(this, points, "Demo Talking Points", JOptionPane.INFORMATION_MESSAGE);
        });

        lastRefreshLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        lastRefreshLabel.setForeground(new Color(90, 90, 90));

        toolbar.add(refreshButton);
        toolbar.add(speakingPointsButton);
        toolbar.add(lastRefreshLabel);
        return toolbar;
    }

    private JPanel buildKPICard(String title, JLabel valueLabel, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(10, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        titleLabel.setForeground(new Color(80, 80, 80));

        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        valueLabel.setForeground(Color.BLACK);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        subtitleLabel.setForeground(new Color(120, 120, 120));

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        textPanel.add(subtitleLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildChartPanel() {
        return trendChartPanel;
    }

    private JPanel buildWhatIfPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);

        sleepDeltaSlider = createSlider(-20, 20, 0);
        activityDeltaSlider = createSlider(-4000, 4000, 0);
        stressDeltaSlider = createSlider(-4, 4, 0);

        JPanel controls = new JPanel(new GridLayout(3, 1, 6, 6));
        controls.setBackground(Color.WHITE);
        controls.add(buildSliderRow("Sleep Delta (h)", sleepDeltaSlider));
        controls.add(buildSliderRow("Steps Delta", activityDeltaSlider));
        controls.add(buildSliderRow("Stress Delta", stressDeltaSlider));

        projectionValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        projectionValue.setForeground(Color.BLACK);

        whatIfNarrative.setEditable(false);
        whatIfNarrative.setLineWrap(true);
        whatIfNarrative.setWrapStyleWord(true);
        whatIfNarrative.setBackground(new Color(248, 248, 248));
        whatIfNarrative.setBorder(new EmptyBorder(8, 8, 8, 8));
        whatIfNarrative.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.add(projectionValue, BorderLayout.WEST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(controls, BorderLayout.CENTER);
        panel.add(new JScrollPane(whatIfNarrative), BorderLayout.SOUTH);

        sleepDeltaSlider.addChangeListener(e -> updateWhatIfProjection());
        activityDeltaSlider.addChangeListener(e -> updateWhatIfProjection());
        stressDeltaSlider.addChangeListener(e -> updateWhatIfProjection());
        updateWhatIfProjection();

        return panel;
    }

    private JSlider createSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setOpaque(false);
        slider.setBackground(Color.WHITE);
        slider.setForeground(Color.BLACK);
        return slider;
    }

    private JPanel buildSliderRow(String label, JSlider slider) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(Color.WHITE);
        JLabel name = new JLabel(label);
        name.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        row.add(name, BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        return row;
    }

    private void updateWhatIfProjection() {
        if (sleepDeltaSlider == null || activityDeltaSlider == null || stressDeltaSlider == null) {
            return;
        }

        double sleepDeltaHours = sleepDeltaSlider.getValue() / 10.0;
        int stepsDelta = activityDeltaSlider.getValue();
        int stressDelta = stressDeltaSlider.getValue();

        double baseline = manager.getOverallScore();
        double projected = baseline
                + (sleepDeltaHours * 2.2)
                + ((stepsDelta / 1000.0) * 1.4)
                - (stressDelta * 2.5);
        projected = Math.max(0.0, Math.min(100.0, projected));

        String direction = projected >= baseline ? "Improves" : "Declines";
        projectionValue.setText(String.format("Projected: %.1f / 100 (%s)", projected, direction));

        whatIfNarrative.setText(String.format(
            """
            Simulation Summary
            - Baseline: %.1f
            - Sleep delta: %+.1f h
            - Steps delta: %+d
            - Stress delta: %+d

            Projected score: %.1f
            Guidance: prioritize sleep regularity and stress control before scaling intensity.
            """,
            baseline,
            sleepDeltaHours,
            stepsDelta,
            stressDelta,
            projected
        ));
    }

    private List<Double> getTrendData() {
        LinkedHashMap<LocalDate, List<Double>> byDate = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 13; i >= 0; i--) {
            byDate.put(today.minusDays(i), new ArrayList<>());
        }

        for (AbstractRecord record : manager.getRecords()) {
            List<Double> bucket = byDate.get(record.getDate());
            if (bucket != null) {
                bucket.add(record.calculateImpactScore());
            }
        }

        List<Double> trend = new ArrayList<>();
        double last = Math.max(40.0, manager.getOverallScore());
        for (Map.Entry<LocalDate, List<Double>> entry : byDate.entrySet()) {
            List<Double> values = entry.getValue();
            if (!values.isEmpty()) {
                double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(last);
                last = avg;
                trend.add(avg);
            } else {
                trend.add(last);
            }
        }
        return trend;
    }

    private JPanel wrap(String title, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void refreshInternal() {
        WellnessProfile profile = manager.getProfile();
        nameValue.setText(profile == null ? "Not created" : profile.getFullName());
        bmiValue.setText(profile == null ? "-" : String.format("%.1f", profile.calculateBMI()));
        riskValue.setText(manager.getAlertLevel().getLabel());
        recommendationArea.setText(manager.getRecommendation());

        Map<String, Integer> counts = manager.getRecordTypeCounts();
        List<Double> trend = getTrendData();
        double momentum = trend.isEmpty() ? 0.0 : trend.get(trend.size() - 1) - trend.get(0);

        animateDouble(displayedScore, manager.getOverallScore(), 450, v -> {
            scoreValue.setText(String.format("%.1f / 100", v));
            displayedScore = v;
        });
        animateInt(displayedTotal, manager.getRecords().size(), 450, v -> {
            totalValue.setText(String.valueOf(v));
            displayedTotal = v;
        });
        animateDouble(displayedMomentum, momentum, 450, v -> {
            momentumValue.setText(String.format("%+.1f", v));
            displayedMomentum = v;
        });

        String summary = manager.getSummary() +
                "\n\nRecord Distribution:" +
                "\n- Health: " + counts.get("Health") +
            "\n- Activity: " + counts.get("Activity") +
            "\n\nPerformance Notes:" +
            "\n- 14-day momentum: " + String.format("%+.1f", momentum) +
            "\n- Latest score: " + String.format("%.1f", manager.getOverallScore()) +
            "\n- Priority: " + (momentum < 0 ? "Recovery stabilization" : "Consistency compounding");
        summaryArea.setText(summary);
        trendChartPanel.animateTo(trend);
        lastRefreshLabel.setText("Last refresh: " + java.time.LocalTime.now().withNano(0));
        updateWhatIfProjection();
    }

    private void animateDouble(double start, double end, int durationMs, java.util.function.DoubleConsumer setter) {
        int fps = 30;
        int steps = Math.max(1, durationMs / (1000 / fps));
        final int[] currentStep = {0};
        Timer timer = new Timer(1000 / fps, null);
        timer.addActionListener(e -> {
            currentStep[0]++;
            double t = Math.min(1.0, currentStep[0] / (double) steps);
            double eased = easeOutBack(t);
            double value = start + (end - start) * eased;
            setter.accept(value);
            if (t >= 1.0) {
                timer.stop();
            }
        });
        timer.start();
    }

    private double easeOutBack(double t) {
        double c1 = 1.70158;
        double c3 = c1 + 1.0;
        double p = t - 1.0;
        return 1.0 + c3 * Math.pow(p, 3) + c1 * Math.pow(p, 2);
    }

    private static double easeInOutCubic(double t) {
        if (t < 0.5) {
            return 4 * t * t * t;
        }
        return 1 - Math.pow(-2 * t + 2, 3) / 2;
    }

    private static Path2D buildSmoothPath(List<Point2D.Double> points) {
        Path2D path = new Path2D.Double();
        if (points.isEmpty()) {
            return path;
        }
        path.moveTo(points.get(0).x, points.get(0).y);
        if (points.size() == 1) {
            return path;
        }
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D.Double p0 = i > 0 ? points.get(i - 1) : points.get(i);
            Point2D.Double p1 = points.get(i);
            Point2D.Double p2 = points.get(i + 1);
            Point2D.Double p3 = i + 2 < points.size() ? points.get(i + 2) : p2;

            double cp1x = p1.x + (p2.x - p0.x) / 6.0;
            double cp1y = p1.y + (p2.y - p0.y) / 6.0;
            double cp2x = p2.x - (p3.x - p1.x) / 6.0;
            double cp2y = p2.y - (p3.y - p1.y) / 6.0;
            path.curveTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y);
        }
        return path;
    }

    private void animateInt(int start, int end, int durationMs, java.util.function.IntConsumer setter) {
        animateDouble(start, end, durationMs, v -> setter.accept((int) Math.round(v)));
    }

    public final void refresh() {
        refreshInternal();
    }

    private static class TrendChartPanel extends JPanel {
        private List<Double> rendered = new ArrayList<>();
        private List<Double> target = new ArrayList<>();

        private TrendChartPanel() {
            setBackground(Color.WHITE);
        }

        private void animateTo(List<Double> nextValues) {
            if (nextValues == null || nextValues.isEmpty()) {
                return;
            }
            if (rendered.isEmpty()) {
                rendered = new ArrayList<>(nextValues);
                target = new ArrayList<>(nextValues);
                repaint();
                return;
            }

            target = new ArrayList<>(nextValues);
            int fps = 60;
            int steps = 24;
            final int[] step = {0};
            List<Double> start = new ArrayList<>(rendered);
            while (start.size() < target.size()) {
                start.add(start.get(start.size() - 1));
            }

            Timer timer = new Timer(1000 / fps, null);
            timer.addActionListener(e -> {
                step[0]++;
                double t = Math.min(1.0, step[0] / (double) steps);
                double eased = easeInOutCubic(t);
                double spring = eased + (Math.sin(Math.PI * eased) * 0.025 * (1.0 - eased));
                spring = Math.max(0.0, Math.min(1.0, spring));
                rendered = new ArrayList<>();
                for (int i = 0; i < target.size(); i++) {
                    double s = i < start.size() ? start.get(i) : target.get(i);
                    double v = s + (target.get(i) - s) * spring;
                    rendered.add(v);
                }
                repaint();
                if (t >= 1.0) {
                    timer.stop();
                }
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            g2d.drawString("14-Day Wellness Trend", 15, 25);

            List<Double> data = rendered.isEmpty() ? target : rendered;
            if (data.isEmpty()) {
                return;
            }

            int padding = 40;
            int top = padding + 5;
            int bottom = getHeight() - padding;
            int left = padding;
            int right = getWidth() - padding;
            int chartWidth = Math.max(1, right - left);
            int chartHeight = Math.max(1, bottom - top);

            g2d.setColor(new Color(180, 180, 180));
            g2d.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 4; i++) {
                int y = top + (i * chartHeight / 4);
                g2d.drawLine(left, y, right, y);
            }

            g2d.setColor(Color.BLACK);
            g2d.drawLine(left, bottom, right, bottom);
            g2d.drawLine(left, top, left, bottom);

            double min = 0.0;
            double max = 100.0;
            int n = data.size();
            int segmentWidth = n > 1 ? chartWidth / (n - 1) : chartWidth;

            List<Point2D.Double> points = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int x = left + i * segmentWidth;
                int y = bottom - (int) ((data.get(i) - min) * chartHeight / (max - min));
                points.add(new Point2D.Double(x, y));
            }

            Path2D path = buildSmoothPath(points);
            Path2D fillPath = new Path2D.Double(path);
            fillPath.lineTo(points.get(points.size() - 1).x, bottom);
            fillPath.lineTo(points.get(0).x, bottom);
            fillPath.closePath();

            g2d.setColor(new Color(220, 220, 220));
            g2d.fill(fillPath);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.2f));
            g2d.draw(path);

            for (Point2D.Double point : points) {
                g2d.fillOval((int) point.x - 3, (int) point.y - 3, 6, 6);
            }

            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            g2d.drawString("14d ago", left, bottom + 15);
            g2d.drawString("Today", right - 28, bottom + 15);
            g2d.drawString("100", left - 26, top + 5);
            g2d.drawString("50", left - 18, top + chartHeight / 2 + 4);
            g2d.drawString("0", left - 11, bottom + 4);
        }
    }
}
