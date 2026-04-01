package wellness.ui;

import wellness.service.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Advanced Dashboard Panel — AI insights, predictions, automation,
 * goals, habits, and alert status in one comprehensive view.
 */
public class AdvancedDashboardPanel extends JPanel {
    public AdvancedDashboardPanel(WellnessManager manager, AutomationService automation,
                                   PredictiveAnalytics predictive, GoalTracker goalTracker,
                                   HabitTracker habitTracker, AlertService alerts) {
        setLayout(new GridLayout(2, 2, 12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        // Automation insights
        JPanel automationPanel = wrapPanel("🤖 AI Automation", automation.generateAutoInsights());
        add(automationPanel);

        // Predictive analytics
        JPanel predictivePanel = wrapPanel("📊 Wellness Forecast", predictive.forecastWellnessGoals());
        add(predictivePanel);

        // Active goals
        JPanel goalsPanel = wrapPanel("🎯 Active Goals", goalTracker.getGoalStatus());
        add(goalsPanel);

        // Habit streaks
        JPanel habitsPanel = wrapPanel("🔥 Habit Streaks", habitTracker.getHabitStatus());
        add(habitsPanel);
    }

    private JPanel wrapPanel(String title, String content) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        titleLabel.setForeground(Color.BLACK);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        contentArea.setBackground(new Color(250, 250, 250));
        contentArea.setForeground(Color.BLACK);
        contentArea.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(contentArea);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
