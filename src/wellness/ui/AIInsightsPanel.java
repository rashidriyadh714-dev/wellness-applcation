package wellness.ui;

import wellness.service.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AI & Insights Panel — AI recommendations, predictive analytics,
 * pattern analysis, automation coaching, and trend forecasting.
 */
public class AIInsightsPanel extends JPanel {
    private final JTabbedPane tabs;

    public AIInsightsPanel(AutomationService automation, PredictiveAnalytics predictive) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        tabs = new JTabbedPane();
        tabs.setBackground(Color.WHITE);
        tabs.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));

        // Tab 1: Insights
        JPanel insightsPanel = new JPanel(new BorderLayout());
        insightsPanel.setBackground(Color.WHITE);
        JTextArea insightsArea = new JTextArea(automation.generateAutoInsights());
        insightsArea.setEditable(false);
        insightsArea.setLineWrap(true);
        insightsArea.setWrapStyleWord(true);
        insightsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        insightsArea.setBackground(new Color(250, 250, 250));
        insightsPanel.add(new JScrollPane(insightsArea), BorderLayout.CENTER);
        tabs.addTab("🧠 Insights", insightsPanel);

        // Tab 2: Workout
        JPanel workoutPanel = new JPanel(new BorderLayout());
        workoutPanel.setBackground(Color.WHITE);
        JTextArea workoutArea = new JTextArea(automation.generatePersonalizedWorkout());
        workoutArea.setEditable(false);
        workoutArea.setLineWrap(true);
        workoutArea.setWrapStyleWord(true);
        workoutArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        workoutArea.setBackground(new Color(250, 250, 250));
        workoutPanel.add(new JScrollPane(workoutArea), BorderLayout.CENTER);
        tabs.addTab("💪 Workout", workoutPanel);

        // Tab 3: Trend
        JPanel trendPanel = new JPanel(new BorderLayout());
        trendPanel.setBackground(Color.WHITE);
        JTextArea trendArea = new JTextArea(automation.predictTrend());
        trendArea.setEditable(false);
        trendArea.setLineWrap(true);
        trendArea.setWrapStyleWord(true);
        trendArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        trendArea.setBackground(new Color(250, 250, 250));
        trendPanel.add(new JScrollPane(trendArea), BorderLayout.CENTER);
        tabs.addTab("📈 Trend", trendPanel);

        // Tab 4: Predictions
        JPanel predictionPanel = new JPanel(new BorderLayout());
        predictionPanel.setBackground(Color.WHITE);
        JTextArea predictionArea = new JTextArea(predictive.predictHealthRisks());
        predictionArea.setEditable(false);
        predictionArea.setLineWrap(true);
        predictionArea.setWrapStyleWord(true);
        predictionArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        predictionArea.setBackground(new Color(250, 250, 250));
        predictionPanel.add(new JScrollPane(predictionArea), BorderLayout.CENTER);
        tabs.addTab("⚡ Predictions", predictionPanel);

        // Tab 5: Patterns
        JPanel patternsPanel = new JPanel(new BorderLayout());
        patternsPanel.setBackground(Color.WHITE);
        JTextArea patternsArea = new JTextArea(predictive.analyzePatterns());
        patternsArea.setEditable(false);
        patternsArea.setLineWrap(true);
        patternsArea.setWrapStyleWord(true);
        patternsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        patternsArea.setBackground(new Color(250, 250, 250));
        patternsPanel.add(new JScrollPane(patternsArea), BorderLayout.CENTER);
        tabs.addTab("📊 Patterns", patternsPanel);

        // Tab 6: Opportunities
        JPanel oppPanel = new JPanel(new BorderLayout());
        oppPanel.setBackground(Color.WHITE);
        JTextArea oppArea = new JTextArea(predictive.identifyOpportunities());
        oppArea.setEditable(false);
        oppArea.setLineWrap(true);
        oppArea.setWrapStyleWord(true);
        oppArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        oppArea.setBackground(new Color(250, 250, 250));
        oppPanel.add(new JScrollPane(oppArea), BorderLayout.CENTER);
        tabs.addTab("💡 Opportunities", oppPanel);

        add(tabs, BorderLayout.CENTER);
    }
}
