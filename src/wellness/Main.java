package wellness;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.util.Enumeration;
import wellness.service.*;
import wellness.ui.LoginDialog;
import wellness.ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
                }

                setGlobalUIFont(14);

                // Allow demo continuation even if user closes login.
                AuthService auth = new AuthService();
                LoginDialog login = new LoginDialog(auth);
                login.showDialog();

                WellnessManager manager = new WellnessManager();
                manager.load();

                TelemetryService telemetry = new TelemetryService();
                WellnessBot bot = new WellnessBot(manager);
                AutomationService automation = new AutomationService(manager, telemetry);
                PredictiveAnalytics predictive = new PredictiveAnalytics(manager);
                GoalTracker goalTracker = new GoalTracker(telemetry);
                HabitTracker habitTracker = new HabitTracker(telemetry);
                AlertService alertService = new AlertService(telemetry);

                goalTracker.addGoal("Daily Steps", "Walk 10,000 steps daily", 10000, "steps", "2026-05-01");
                goalTracker.addGoal("Weekly Workouts", "3 exercise sessions per week", 3, "sessions", "2026-05-01");
                habitTracker.addHabit("Morning Meditation", "mindfulness");
                habitTracker.addHabit("Daily Walk", "exercise");
                habitTracker.addHabit("8h Sleep", "sleep");

                MainFrame frame = new MainFrame(manager, bot, automation, predictive,
                        goalTracker, habitTracker, alertService);
                frame.setVisible(true);
                frame.showWelcomeMessage();
            } catch (Throwable t) {
                System.err.println("Startup failed: " + t.getMessage());
                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        "Startup failed: " + t.getMessage(),
                        "Elite Wellness",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private static void setGlobalUIFont(int size) {
        FontUIResource readable = new FontUIResource(new Font(Font.SANS_SERIF, Font.PLAIN, size));
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, readable);
            }
        }
    }
}
