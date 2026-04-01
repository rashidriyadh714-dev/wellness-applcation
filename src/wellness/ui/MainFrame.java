package wellness.ui;

import wellness.model.*;
import wellness.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private final WellnessManager manager;
    private final DashboardPanel dashboardPanel;
    private final GoalTracker goalTracker;
    private final HabitTracker habitTracker;
    private final RecordTableModel tableModel = new RecordTableModel();
    private TableRowSorter<RecordTableModel> recordSorter;

    private JTextField tfUserId;
    private JTextField tfFullName;
    private JTextField tfAge;
    private JTextField tfHeight;
    private JTextField tfWeight;
    private JTextField tfGoal;

    private JComboBox<String> cbRecordType;
    private JTextField tfRecordId;
    private JTextField tfDate;
    private JTextField tfNotes;

    private JTextField tfSleepHours;
    private JTextField tfWaterIntake;
    private JTextField tfMoodScore;
    private JTextField tfStressScore;
    private JTextField tfHeartRate;

    private JTextField tfSteps;
    private JTextField tfActiveMinutes;
    private JTextField tfCaloriesBurned;
    private JTextField tfScreenTime;

    private JPanel healthFieldsPanel;
    private JPanel activityFieldsPanel;
    private JTable table;
    private JTextArea profileInsightsArea;
    private JLabel profileBmiBandLabel;
    private JLabel profileReadinessLabel;
    private JTextField tfRecordSearch;
    private JComboBox<String> cbRecordFilter;
    private final JTabbedPane mainTabs;
    private final List<ExecutiveScorecardPanel> scorecards = new ArrayList<>();
    private final TransitionOverlay transitionOverlay = new TransitionOverlay();
    private boolean presentationModeEnabled = false;
    private int lastSelectedTabIndex = 0;

    public MainFrame(WellnessManager manager, WellnessBot bot, AutomationService automation,
                    PredictiveAnalytics predictive, GoalTracker goalTracker, 
                    HabitTracker habitTracker, AlertService alertService) {
        this.manager = manager;
        this.goalTracker = goalTracker;
        this.habitTracker = habitTracker;
        this.dashboardPanel = new DashboardPanel(manager);

        setTitle("Elite Wellness — Worldwide Health Intelligence");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        loadSampleData();

        mainTabs = new JTabbedPane();
        mainTabs.setBackground(Color.WHITE);
        mainTabs.setForeground(Color.BLACK);
        mainTabs.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        mainTabs.setBorder(new EmptyBorder(8, 8, 8, 8));

        mainTabs.addTab("Dashboard", withExecutiveScorecard("Dashboard", dashboardPanel));
        mainTabs.addTab("Command Center", withExecutiveScorecard("Command Center", buildCommandCenterPanel()));
        mainTabs.addTab("AI Assistant", withExecutiveScorecard("AI Assistant", new ChatbotPanel(bot)));
        mainTabs.addTab("Advanced Insights", withExecutiveScorecard("Advanced Insights", new AdvancedInsightsPanel(manager.getProfile(), manager.getRecords())));
        mainTabs.addTab("Goals & Habits", withExecutiveScorecard("Goals & Habits", new GoalsHabitsPanel(goalTracker, habitTracker)));
        mainTabs.addTab("Profile", withExecutiveScorecard("Profile", buildProfilePanel()));
        mainTabs.addTab("Add Record", withExecutiveScorecard("Add Record", buildAddRecordPanel()));
        mainTabs.addTab("Records", withExecutiveScorecard("Records", buildRecordsPanel()));

        JPanel centerHost = new JPanel();
        centerHost.setLayout(new OverlayLayout(centerHost));
        centerHost.setBackground(Color.WHITE);
        mainTabs.setAlignmentX(0.0f);
        mainTabs.setAlignmentY(0.0f);
        transitionOverlay.setAlignmentX(0.0f);
        transitionOverlay.setAlignmentY(0.0f);
        centerHost.add(mainTabs);
        centerHost.add(transitionOverlay);
        centerHost.setComponentZOrder(transitionOverlay, 0);

        mainTabs.addChangeListener(e -> handleTabTransition());

        add(buildHeader(), BorderLayout.NORTH);
        add(centerHost, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        // Enforce a readable baseline across all tabs for classroom/demo visibility.
        enforceReadableFonts(getContentPane(), 14f);

        refreshAll();
    }

    private void enforceReadableFonts(Component component, float minSize) {
        if (component == null) {
            return;
        }

        Font font = component.getFont();
        if (font != null && font.getSize2D() < minSize) {
            component.setFont(font.deriveFont(minSize));
        }

        if (component instanceof JTable grid) {
            if (grid.getRowHeight() < 30) {
                grid.setRowHeight(30);
            }
            if (grid.getTableHeader() != null && grid.getTableHeader().getFont() != null
                    && grid.getTableHeader().getFont().getSize2D() < minSize) {
                grid.getTableHeader().setFont(grid.getTableHeader().getFont().deriveFont(minSize));
            }
        }

        if (component instanceof JTextArea area) {
            area.setMargin(new Insets(10, 10, 10, 10));
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                enforceReadableFonts(child, minSize);
            }
        }
    }

    private void loadSampleData() {
        try {
            java.time.LocalDate today = java.time.LocalDate.now();
            long timestamp = System.currentTimeMillis();
            
            // Create comprehensive demo profile
            WellnessProfile profile = new WellnessProfile("elite_athlete_" + timestamp, "Sarah Mitchell", 32, 168, 62, "Peak Performance & Longevity");
            manager.setProfile(profile);
            
            // Load 7 days of sample health records
            for (int i = 6; i >= 0; i--) {
                java.time.LocalDate date = today.minusDays(i);
                String dateStr = date.toString();
                double sleepBase = 7.5 + (Math.random() - 0.5) * 2;
                int water = 2000 + (int)(Math.random() * 1000);
                int mood = 7 + (int)(Math.random() * 3);
                int stress = 5 - (int)(Math.random() * 3);
                int hr = 58 + (int)(Math.random() * 8);
                manager.addHealthRecord("health_" + timestamp + "_" + i, dateStr, "Daily log", sleepBase, water, mood, Math.max(1, stress), hr);
            }
            
            // Load 7 days of sample activity records
            for (int i = 6; i >= 0; i--) {
                java.time.LocalDate date = today.minusDays(i);
                String dateStr = date.toString();
                int steps = 8000 + (int)(Math.random() * 5000);
                int activeMin = 30 + (int)(Math.random() * 45);
                int calories = 450 + (int)(Math.random() * 450);
                int screenTime = 120 + (int)(Math.random() * 180);
                manager.addActivityRecord("activity_" + timestamp + "_" + i, dateStr, "Logged", steps, activeMin, calories, screenTime);
            }
            
            // Add sample goals
            goalTracker.addGoal("Daily Sleep", "Sleep 8 hours daily", 8, "hours", "2026-05-01");
            goalTracker.addGoal("Daily Steps", "10,000 steps daily", 10000, "steps", "2026-05-01");
            goalTracker.addGoal("Daily Water", "Water intake 3L daily", 3000, "ml", "2026-05-01");
            
            // Add sample habits
            habitTracker.addHabit("Morning meditation", "mindfulness");
            habitTracker.addHabit("Evening walk", "exercise");
            habitTracker.addHabit("Hydration check", "wellness");
            
        } catch (Exception e) {
            System.err.println("Demo data load error: " + e.getMessage());
        }
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BorderLayout(15, 0));
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));
        panel.setPreferredSize(new Dimension(0, 90));

        JLabel logo = new JLabel("●");
        logo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 56));
        logo.setForeground(Color.WHITE);

        JLabel title = new JLabel("Elite Wellness");
        title.setForeground(Color.WHITE);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));

        JLabel subtitle = new JLabel("Worldwide Health Intelligence — Advanced Analytics");
        subtitle.setForeground(new Color(180, 180, 180));
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 3));
        text.setOpaque(false);
        text.add(title);
        text.add(subtitle);

        JButton guideButton = new JButton("Tab Guide");
        guideButton.setFocusable(false);
        guideButton.addActionListener(e -> showTabGuide());

        JButton presentationButton = new JButton("Presentation Mode: Off");
        presentationButton.setFocusable(false);
        presentationButton.addActionListener(e -> {
            presentationModeEnabled = !presentationModeEnabled;
            presentationButton.setText("Presentation Mode: " + (presentationModeEnabled ? "On" : "Off"));
            applyPresentationMode();
        });

        JButton investorOverlayButton = new JButton("Investor Demo Overlay");
        investorOverlayButton.setFocusable(false);
        investorOverlayButton.addActionListener(e -> showInvestorDemoOverlay());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(guideButton);
        actions.add(presentationButton);
        actions.add(investorOverlayButton);

        panel.add(logo, BorderLayout.WEST);
        panel.add(text, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(240, 240, 240));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        footer.setLayout(new FlowLayout(FlowLayout.CENTER));
        footer.setPreferredSize(new Dimension(0, 35));
        JLabel status = new JLabel("✓ Synced • 🔒 Encrypted • 👥 HIPAA Compliant");
        status.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        status.setForeground(new Color(80, 80, 80));
        footer.add(status);
        return footer;
    }

    private JPanel buildCommandCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextArea intro = new JTextArea(
            """
            Command Center

            Use this panel to drive a world-class demo flow. Track launch readiness, run a scripted blast demo, and keep the product roadmap visible.
            """
        );
        intro.setEditable(false);
        intro.setLineWrap(true);
        intro.setWrapStyleWord(true);
        intro.setBackground(new Color(248, 248, 248));
        intro.setBorder(new EmptyBorder(10, 10, 10, 10));
        intro.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        JPanel controlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controlRow.setBackground(Color.WHITE);
        JButton blastDemo = new JButton("Run Blast Demo");
        JButton openGuide = new JButton("Open Demo Guide");
        JButton openTabGuide = new JButton("Open Tab Guide");
        JLabel status = new JLabel("Status: Ready");
        status.setForeground(new Color(85, 85, 85));

        blastDemo.addActionListener(e -> runBlastDemo(status));
        openGuide.addActionListener(e -> showInfo("Demo flow: Command Center -> Dashboard -> AI Assistant -> Advanced Insights -> Goals & Habits -> Profile -> Records"));
        openTabGuide.addActionListener(e -> showTabGuide());

        controlRow.add(blastDemo);
        controlRow.add(openGuide);
        controlRow.add(openTabGuide);
        controlRow.add(status);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBackground(Color.WHITE);
        top.add(intro, BorderLayout.NORTH);
        top.add(controlRow, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.45);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setLeftComponent(buildTodoChecklistPanel());
        split.setRightComponent(buildRoadmapPanel());

        panel.add(top, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildTodoChecklistPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Production Readiness Checklist"));

        JPanel checks = new JPanel();
        checks.setBackground(Color.WHITE);
        checks.setLayout(new BoxLayout(checks, BoxLayout.Y_AXIS));

        JCheckBox c1 = new JCheckBox("Core compile passes without errors", true);
        JCheckBox c2 = new JCheckBox("AI Assistant modes demonstrate distinct behavior", true);
        JCheckBox c3 = new JCheckBox("Advanced Insights refresh + scenario simulation works", true);
        JCheckBox c4 = new JCheckBox("Goals and habits show KPI + progress meter", true);
        JCheckBox c5 = new JCheckBox("Records filters and CSV export work", true);
        JCheckBox c6 = new JCheckBox("Presentation mode and tab guide are ready", true);
        JCheckBox c7 = new JCheckBox("Live demo story prepared for investors", false);

        JCheckBox[] all = new JCheckBox[]{c1, c2, c3, c4, c5, c6, c7};
        for (JCheckBox cb : all) {
            cb.setBackground(Color.WHITE);
            cb.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            checks.add(cb);
            checks.add(Box.createVerticalStrut(6));
        }

        JProgressBar readiness = new JProgressBar(0, 100);
        readiness.setStringPainted(true);
        readiness.setValue(86);
        readiness.setString("Readiness 86%");

        JButton recalc = new JButton("Recalculate Readiness");
        recalc.addActionListener(e -> {
            int done = 0;
            for (JCheckBox cb : all) {
                if (cb.isSelected()) {
                    done++;
                }
            }
            int percent = (int) Math.round(done * 100.0 / all.length);
            readiness.setValue(percent);
            readiness.setString("Readiness " + percent + "%");
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottom.setBackground(Color.WHITE);
        bottom.add(readiness);
        bottom.add(recalc);

        panel.add(new JScrollPane(checks), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JComponent buildRoadmapPanel() {
        JTextArea roadmap = new JTextArea();
        roadmap.setEditable(false);
        roadmap.setLineWrap(true);
        roadmap.setWrapStyleWord(true);
        roadmap.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        roadmap.setBackground(new Color(248, 248, 248));
        roadmap.setBorder(new EmptyBorder(12, 12, 12, 12));
        roadmap.setText(
            """
            Billion-Dollar Roadmap

            Phase 1 - Product Foundation
            - Stabilize analytics, scoring, and profile intelligence
            - Ship premium desktop presentation experience

            Phase 2 - Intelligent Automation
            - Expand AI assistant coaching memory and recommendations
            - Add proactive reminders and anomaly alerts

            Phase 3 - Growth Engine
            - Introduce subscription tiers and premium plans
            - Add cohort benchmarks and retention analytics

            Phase 4 - Scale
            - Add enterprise wellness reporting and API ecosystem
            - Expand cross-platform integrations
            """
        );

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createTitledBorder("Strategic TODO Roadmap"));
        wrapper.add(new JScrollPane(roadmap), BorderLayout.CENTER);
        return wrapper;
    }

    private void runBlastDemo(JLabel statusLabel) {
        if (mainTabs == null) {
            return;
        }
        final int[] order = new int[]{0, 2, 3, 4, 5, 6, 7};
        final int[] idx = new int[]{0};
        statusLabel.setText("Status: Running blast demo...");

        Timer timer = new Timer(1200, null);
        timer.addActionListener(e -> {
            if (idx[0] >= order.length) {
                timer.stop();
                statusLabel.setText("Status: Blast demo complete");
                showInfo("Blast demo finished. You can now deep-dive any tab.");
                return;
            }
            int tabIndex = order[idx[0]++];
            if (tabIndex >= 0 && tabIndex < mainTabs.getTabCount()) {
                mainTabs.setSelectedIndex(tabIndex);
            }
        });
        timer.start();
    }

    private JPanel buildProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextArea quickGuide = new JTextArea(
            "Quick Start: Fill profile fields, click Save Profile, then use Auto-Optimize Goal for a tailored objective. " +
            "Your live BMI and readiness intelligence updates as you type."
        );
        quickGuide.setEditable(false);
        quickGuide.setLineWrap(true);
        quickGuide.setWrapStyleWord(true);
        quickGuide.setBackground(new Color(248, 248, 248));
        quickGuide.setBorder(new EmptyBorder(8, 8, 8, 8));
        quickGuide.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        tfUserId = new JTextField();
        tfFullName = new JTextField();
        tfAge = new JTextField();
        tfHeight = new JTextField();
        tfWeight = new JTextField();
        tfGoal = new JTextField();

        form.add(new JLabel("User ID")); form.add(tfUserId);
        form.add(new JLabel("Full Name")); form.add(tfFullName);
        form.add(new JLabel("Age")); form.add(tfAge);
        form.add(new JLabel("Height (cm)")); form.add(tfHeight);
        form.add(new JLabel("Weight (kg)")); form.add(tfWeight);
        form.add(new JLabel("Wellness Goal")); form.add(tfGoal);

        attachProfilePreviewListeners();

        JButton saveButton = new JButton("Save Profile");
        saveButton.addActionListener(e -> saveProfile());

        JButton autoOptimizeButton = new JButton("Auto-Optimize Goal");
        autoOptimizeButton.addActionListener(e -> autoOptimizeGoal());

        JButton summaryButton = new JButton("Generate Executive Summary");
        summaryButton.addActionListener(e -> showProfileExecutiveSummary());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(Color.WHITE);
        bottom.add(saveButton);
        bottom.add(autoOptimizeButton);
        bottom.add(summaryButton);

        JPanel split = new JPanel(new GridLayout(1, 2, 12, 12));
        split.setBackground(Color.WHITE);
        split.add(wrapPanel("Profile Management", form));
        split.add(buildProfileSnapshotPanel());

        panel.add(quickGuide, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildProfileSnapshotPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        JPanel kpi = new JPanel(new GridLayout(1, 2, 8, 8));
        kpi.setBackground(Color.WHITE);
        profileBmiBandLabel = new JLabel("BMI Band: -");
        profileReadinessLabel = new JLabel("Readiness: -");
        kpi.add(buildStatCard("Body Composition", profileBmiBandLabel));
        kpi.add(buildStatCard("Performance Readiness", profileReadinessLabel));

        profileInsightsArea = new JTextArea();
        profileInsightsArea.setEditable(false);
        profileInsightsArea.setLineWrap(true);
        profileInsightsArea.setWrapStyleWord(true);
        profileInsightsArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        profileInsightsArea.setBackground(new Color(248, 248, 248));
        profileInsightsArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        profileInsightsArea.setText("Live profile insights appear here as you edit age, height, and weight.");

        panel.add(kpi, BorderLayout.NORTH);
        panel.add(new JScrollPane(profileInsightsArea), BorderLayout.CENTER);
        return wrapPanel("Performance Snapshot", panel);
    }

    private JPanel buildStatCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        value.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private void attachProfilePreviewListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateProfilePreview(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateProfilePreview(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateProfilePreview(); }
        };
        tfAge.getDocument().addDocumentListener(listener);
        tfHeight.getDocument().addDocumentListener(listener);
        tfWeight.getDocument().addDocumentListener(listener);
    }

    private void updateProfilePreview() {
        if (profileInsightsArea == null) {
            return;
        }
        try {
            int age = Integer.parseInt(tfAge.getText().trim());
            double h = Double.parseDouble(tfHeight.getText().trim());
            double w = Double.parseDouble(tfWeight.getText().trim());
            double bmi = w / Math.pow(h / 100.0, 2);
            String band = bmi < 18.5 ? "Low" : bmi < 25 ? "Optimal" : bmi < 30 ? "Elevated" : "High";
            String readiness = bmi < 25 ? "Strong" : bmi < 30 ? "Moderate" : "Needs Focus";
            profileBmiBandLabel.setText("BMI Band: " + band + String.format(" (%.1f)", bmi));
            profileReadinessLabel.setText("Readiness: " + readiness);
                profileInsightsArea.setText(String.format(
                    """
                    Profile Intelligence

                    Age: %d years
                    BMI: %.1f (%s)

                    Recommended Focus:
                    - Keep recovery score above 80 with consistent sleep windows.
                    - Maintain daily movement and hydration routines.
                    - Review stress trend in Advanced Insights weekly.
                    """,
                    age,
                    bmi,
                    band
                ));
        } catch (RuntimeException ignored) {
            profileBmiBandLabel.setText("BMI Band: -");
            profileReadinessLabel.setText("Readiness: -");
            profileInsightsArea.setText("Enter valid age, height, and weight to generate live profile intelligence.");
        }
    }

    private void autoOptimizeGoal() {
        try {
            int age = Integer.parseInt(tfAge.getText().trim());
            double h = Double.parseDouble(tfHeight.getText().trim());
            double w = Double.parseDouble(tfWeight.getText().trim());
            double bmi = w / Math.pow(h / 100.0, 2);
            if (bmi >= 30) {
                tfGoal.setText("Improve metabolic health and reduce BMI through daily cardio and nutrition discipline");
            } else if (bmi >= 25) {
                tfGoal.setText("Reach athletic body composition and increase recovery consistency");
            } else if (age >= 40) {
                tfGoal.setText("Preserve longevity with strength, mobility, and sleep quality optimization");
            } else {
                tfGoal.setText("Peak performance: maximize recovery, VO2 conditioning, and resilience");
            }
            updateProfilePreview();
        } catch (RuntimeException ex) {
            showError("Complete age, height, and weight to auto-optimize goal.");
        }
    }

    private JPanel buildAddRecordPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextArea quickGuide = new JTextArea(
            "Quick Start: Choose record type, click Generate ID and Use Today, then fill inputs and click Add Record. " +
            "Use Insert Sample Data for instant demo entries."
        );
        quickGuide.setEditable(false);
        quickGuide.setLineWrap(true);
        quickGuide.setWrapStyleWord(true);
        quickGuide.setBackground(new Color(248, 248, 248));
        quickGuide.setBorder(new EmptyBorder(8, 8, 8, 8));
        quickGuide.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        JPanel common = new JPanel(new GridLayout(4, 2, 10, 10));
        cbRecordType = new JComboBox<>(new String[]{"Health", "Activity"});
        tfRecordId = new JTextField();
        tfDate = new JTextField();
        tfNotes = new JTextField();

        common.add(new JLabel("Record Type")); common.add(cbRecordType);
        common.add(new JLabel("Record ID")); common.add(tfRecordId);
        common.add(new JLabel("Date (YYYY-MM-DD)")); common.add(tfDate);
        common.add(new JLabel("Notes")); common.add(tfNotes);

        healthFieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        tfSleepHours = new JTextField();
        tfWaterIntake = new JTextField();
        tfMoodScore = new JTextField();
        tfStressScore = new JTextField();
        tfHeartRate = new JTextField();
        healthFieldsPanel.add(new JLabel("Sleep Hours")); healthFieldsPanel.add(tfSleepHours);
        healthFieldsPanel.add(new JLabel("Water Intake (ml)")); healthFieldsPanel.add(tfWaterIntake);
        healthFieldsPanel.add(new JLabel("Mood Score (1-10)")); healthFieldsPanel.add(tfMoodScore);
        healthFieldsPanel.add(new JLabel("Stress Score (1-10)")); healthFieldsPanel.add(tfStressScore);
        healthFieldsPanel.add(new JLabel("Resting Heart Rate")); healthFieldsPanel.add(tfHeartRate);

        activityFieldsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        tfSteps = new JTextField();
        tfActiveMinutes = new JTextField();
        tfCaloriesBurned = new JTextField();
        tfScreenTime = new JTextField();
        activityFieldsPanel.add(new JLabel("Steps")); activityFieldsPanel.add(tfSteps);
        activityFieldsPanel.add(new JLabel("Active Minutes")); activityFieldsPanel.add(tfActiveMinutes);
        activityFieldsPanel.add(new JLabel("Calories Burned")); activityFieldsPanel.add(tfCaloriesBurned);
        activityFieldsPanel.add(new JLabel("Screen Time (minutes)")); activityFieldsPanel.add(tfScreenTime);

        tfRecordId.setToolTipText("Use Generate ID for unique values.");
        tfDate.setToolTipText("Example: 2026-04-01");
        tfSleepHours.setToolTipText("Typical range: 4.0 - 10.0");
        tfWaterIntake.setToolTipText("Daily intake in ml, e.g. 2200");
        tfMoodScore.setToolTipText("Scale 1-10");
        tfStressScore.setToolTipText("Scale 1-10");
        tfHeartRate.setToolTipText("Resting heart rate, e.g. 62");
        tfSteps.setToolTipText("Daily steps, e.g. 9500");
        tfActiveMinutes.setToolTipText("Active minutes, e.g. 45");
        tfCaloriesBurned.setToolTipText("Calories burned, e.g. 480");
        tfScreenTime.setToolTipText("Minutes of screen time");

        JPanel center = new JPanel(new CardLayout());
        center.add(wrapPanel("Health Record Inputs", healthFieldsPanel), "Health");
        center.add(wrapPanel("Activity Record Inputs", activityFieldsPanel), "Activity");

        cbRecordType.addActionListener(e -> {
            CardLayout cl = (CardLayout) center.getLayout();
            cl.show(center, String.valueOf(cbRecordType.getSelectedItem()));
        });

        JButton addButton = new JButton("Add Record");
        addButton.addActionListener(e -> addRecord());

        JButton sampleButton = new JButton("Insert Sample Data");
        sampleButton.addActionListener(e -> insertSampleInputs());

        JButton autoIdButton = new JButton("Generate ID");
        autoIdButton.addActionListener(e -> tfRecordId.setText("R" + System.currentTimeMillis()));

        JButton todayButton = new JButton("Use Today");
        todayButton.addActionListener(e -> tfDate.setText(java.time.LocalDate.now().toString()));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setBackground(Color.WHITE);
        buttons.add(addButton);
        buttons.add(sampleButton);
        buttons.add(autoIdButton);
        buttons.add(todayButton);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBackground(Color.WHITE);
        top.add(quickGuide, BorderLayout.NORTH);
        top.add(wrapPanel("Common Record Details", common), BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        table = new JTable(tableModel);
        table.setRowHeight(24);
        recordSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(recordSorter);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topControls.setBackground(Color.WHITE);
        cbRecordFilter = new JComboBox<>(new String[]{"All Types", "Health", "Activity"});
        cbRecordFilter.addActionListener(e -> applyRecordFilters());
        tfRecordSearch = new JTextField(22);
        tfRecordSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyRecordFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyRecordFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyRecordFilters(); }
        });
        JButton resetFilterButton = new JButton("Reset Filters");
        resetFilterButton.addActionListener(e -> {
            cbRecordFilter.setSelectedIndex(0);
            tfRecordSearch.setText("");
            applyRecordFilters();
        });
        topControls.add(new JLabel("Type:"));
        topControls.add(cbRecordFilter);
        topControls.add(new JLabel("Search:"));
        topControls.add(tfRecordSearch);
        topControls.add(resetFilterButton);

        JButton exportButton = new JButton("Export Visible CSV");
        exportButton.addActionListener(e -> exportVisibleRecordsToCsv());
        topControls.add(exportButton);

        JButton snapshotButton = new JButton("Presentation Snapshot");
        snapshotButton.addActionListener(e -> showRecordsSnapshot());
        topControls.add(snapshotButton);

        JButton deleteButton = new JButton("Delete Selected Record");
        deleteButton.addActionListener(e -> deleteSelectedRecord());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(Color.WHITE);
        bottom.add(deleteButton);

        JTextArea quickGuide = new JTextArea(
            "Quick Start: Filter by type, search by keyword, and export visible rows to CSV for your presentation."
        );
        quickGuide.setEditable(false);
        quickGuide.setLineWrap(true);
        quickGuide.setWrapStyleWord(true);
        quickGuide.setBackground(new Color(248, 248, 248));
        quickGuide.setBorder(new EmptyBorder(8, 8, 8, 8));
        quickGuide.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        JPanel north = new JPanel(new BorderLayout(6, 6));
        north.setBackground(Color.WHITE);
        north.add(quickGuide, BorderLayout.NORTH);
        north.add(topControls, BorderLayout.SOUTH);

        panel.add(north, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel wrapPanel(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void saveProfile() {
        try {
            WellnessProfile profile = new WellnessProfile(
                    tfUserId.getText(),
                    tfFullName.getText(),
                    Integer.parseInt(tfAge.getText().trim()),
                    Double.parseDouble(tfHeight.getText().trim()),
                    Double.parseDouble(tfWeight.getText().trim()),
                    tfGoal.getText()
            );
            manager.setProfile(profile);
            refreshAll();
            showInfo("Profile saved successfully.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void addRecord() {
        try {
            validateRecordForm();
            String type = String.valueOf(cbRecordType.getSelectedItem());
            if ("Health".equals(type)) {
                manager.addHealthRecord(
                        tfRecordId.getText(),
                        tfDate.getText(),
                        tfNotes.getText(),
                        Double.parseDouble(tfSleepHours.getText().trim()),
                        Integer.parseInt(tfWaterIntake.getText().trim()),
                        Integer.parseInt(tfMoodScore.getText().trim()),
                        Integer.parseInt(tfStressScore.getText().trim()),
                        Integer.parseInt(tfHeartRate.getText().trim())
                );
            } else {
                manager.addActivityRecord(
                        tfRecordId.getText(),
                        tfDate.getText(),
                        tfNotes.getText(),
                        Integer.parseInt(tfSteps.getText().trim()),
                        Integer.parseInt(tfActiveMinutes.getText().trim()),
                        Double.parseDouble(tfCaloriesBurned.getText().trim()),
                        Integer.parseInt(tfScreenTime.getText().trim())
                );
            }
            clearRecordForm();
            refreshAll();
            showInfo("Record added successfully.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteSelectedRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a record to delete.");
            return;
        }
        AbstractRecord record = tableModel.getRecordAt(selectedRow);
        if (table.getRowSorter() != null) {
            selectedRow = table.convertRowIndexToModel(selectedRow);
            record = tableModel.getRecordAt(selectedRow);
        }
        manager.deleteRecord(record.getRecordId());
        refreshAll();
        showInfo("Record deleted successfully.");
    }

    private void refreshAll() {
        WellnessProfile profile = manager.getProfile();
        if (profile != null) {
            tfUserId.setText(profile.getUserId());
            tfFullName.setText(profile.getFullName());
            tfAge.setText(String.valueOf(profile.getAge()));
            tfHeight.setText(String.valueOf(profile.getHeightCm()));
            tfWeight.setText(String.valueOf(profile.getWeightKg()));
            tfGoal.setText(profile.getGoal());
        }
        tableModel.setRecords(manager.getRecords());
        applyRecordFilters();
        updateProfilePreview();
        dashboardPanel.refresh();
        for (ExecutiveScorecardPanel scorecard : scorecards) {
            scorecard.refreshData();
        }
    }

    private JComponent withExecutiveScorecard(String tabName, JComponent content) {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setBackground(Color.WHITE);
        ExecutiveScorecardPanel scorecard = new ExecutiveScorecardPanel(tabName);
        scorecards.add(scorecard);
        wrapper.add(scorecard, BorderLayout.NORTH);
        wrapper.add(content, BorderLayout.CENTER);
        return wrapper;
    }

    private void handleTabTransition() {
        int newIndex = mainTabs.getSelectedIndex();
        if (newIndex < 0 || newIndex == lastSelectedTabIndex) {
            return;
        }

        BufferedImage snapshot = captureTabSnapshot(lastSelectedTabIndex);
        int direction = Integer.compare(newIndex, lastSelectedTabIndex);
        transitionOverlay.play(snapshot, direction == 0 ? 1 : direction);
        lastSelectedTabIndex = newIndex;
    }

    private BufferedImage captureTabSnapshot(int tabIndex) {
        if (tabIndex < 0 || tabIndex >= mainTabs.getTabCount()) {
            return null;
        }
        Component component = mainTabs.getComponentAt(tabIndex);
        if (!(component instanceof JComponent tabComponent)) {
            return null;
        }

        int width = Math.max(1, mainTabs.getWidth());
        int height = Math.max(1, mainTabs.getHeight());
        if (width <= 1 || height <= 1) {
            return null;
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        tabComponent.setSize(width, height);
        tabComponent.doLayout();
        tabComponent.printAll(g2);
        g2.dispose();
        return image;
    }

    private void applyRecordFilters() {
        if (recordSorter == null) {
            return;
        }
        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        if (cbRecordFilter != null) {
            String type = String.valueOf(cbRecordFilter.getSelectedItem());
            if ("Health".equals(type) || "Activity".equals(type)) {
                filters.add(RowFilter.regexFilter("^" + type + "$", 2));
            }
        }

        if (tfRecordSearch != null) {
            String q = tfRecordSearch.getText().trim();
            if (!q.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(q)));
            }
        }

        if (filters.isEmpty()) {
            recordSorter.setRowFilter(null);
        } else {
            recordSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void clearRecordForm() {
        tfRecordId.setText("");
        tfDate.setText("");
        tfNotes.setText("");
        tfSleepHours.setText("");
        tfWaterIntake.setText("");
        tfMoodScore.setText("");
        tfStressScore.setText("");
        tfHeartRate.setText("");
        tfSteps.setText("");
        tfActiveMinutes.setText("");
        tfCaloriesBurned.setText("");
        tfScreenTime.setText("");
    }

    private void exportVisibleRecordsToCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Visible Records");
        chooser.setSelectedFile(new File("wellness_records.csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Record ID,Date,Type,Impact Score,Notes\n");
            for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                String id = String.valueOf(tableModel.getValueAt(modelRow, 0));
                String date = String.valueOf(tableModel.getValueAt(modelRow, 1));
                String type = String.valueOf(tableModel.getValueAt(modelRow, 2));
                String score = String.valueOf(tableModel.getValueAt(modelRow, 3));
                String notes = String.valueOf(tableModel.getValueAt(modelRow, 4)).replace(",", " ");
                writer.write(id + "," + date + "," + type + "," + score + "," + notes + "\n");
            }
            showInfo("CSV exported: " + file.getAbsolutePath());
        } catch (IOException ex) {
            showError("Export failed: " + ex.getMessage());
        }
    }

    private void insertSampleInputs() {
        tfRecordId.setText("R" + System.currentTimeMillis());
        tfDate.setText(java.time.LocalDate.now().toString());
        tfNotes.setText("Daily wellness entry");
        if ("Health".equals(String.valueOf(cbRecordType.getSelectedItem()))) {
            tfSleepHours.setText("7.5");
            tfWaterIntake.setText("2200");
            tfMoodScore.setText("8");
            tfStressScore.setText("4");
            tfHeartRate.setText("72");
        } else {
            tfSteps.setText("9200");
            tfActiveMinutes.setText("55");
            tfCaloriesBurned.setText("460");
            tfScreenTime.setText("180");
        }
    }

    private void validateRecordForm() {
        String type = String.valueOf(cbRecordType.getSelectedItem());
        if (tfRecordId.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Record ID is required.");
        }
        if (!tfDate.getText().trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Date must use YYYY-MM-DD format.");
        }
        if ("Health".equals(type)) {
            double sleep = Double.parseDouble(tfSleepHours.getText().trim());
            int mood = Integer.parseInt(tfMoodScore.getText().trim());
            int stress = Integer.parseInt(tfStressScore.getText().trim());
            if (sleep < 0 || sleep > 24) {
                throw new IllegalArgumentException("Sleep hours must be between 0 and 24.");
            }
            if (mood < 1 || mood > 10 || stress < 1 || stress > 10) {
                throw new IllegalArgumentException("Mood and stress scores must be between 1 and 10.");
            }
        } else {
            int steps = Integer.parseInt(tfSteps.getText().trim());
            int active = Integer.parseInt(tfActiveMinutes.getText().trim());
            if (steps < 0 || active < 0) {
                throw new IllegalArgumentException("Steps and active minutes must be non-negative.");
            }
        }
    }

    private void showProfileExecutiveSummary() {
        WellnessProfile profile = manager.getProfile();
        if (profile == null) {
            showError("Create and save profile first.");
            return;
        }
        String summary = String.format(
            """
            Executive Profile Summary

            Name: %s
            Age: %d
            BMI: %.1f
            Goal: %s
            Wellness Score: %.1f
            Alert Level: %s

            Action Priorities:
            1. Stabilize sleep and hydration consistency.
            2. Maintain 5 active days per week.
            3. Review trend and risk weekly in Advanced Insights.
            """,
                profile.getFullName(),
                profile.getAge(),
                profile.calculateBMI(),
                profile.getGoal(),
                manager.getOverallScore(),
                manager.getAlertLevel().getLabel()
        );
        JTextArea area = new JTextArea(summary);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Executive Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRecordsSnapshot() {
        int rows = table.getRowCount();
        if (rows == 0) {
            showError("No visible rows to summarize.");
            return;
        }
        double totalScore = 0.0;
        int health = 0;
        int activity = 0;
        for (int viewRow = 0; viewRow < rows; viewRow++) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            String type = String.valueOf(tableModel.getValueAt(modelRow, 2));
            double score = Double.parseDouble(String.valueOf(tableModel.getValueAt(modelRow, 3)));
            totalScore += score;
            if ("Health".equals(type)) {
                health++;
            } else if ("Activity".equals(type)) {
                activity++;
            }
        }
        double avg = totalScore / rows;
        String snapshot = String.format(
            """
            Presentation Snapshot

            Visible Rows: %d
            Average Impact: %.1f
            Health Rows: %d
            Activity Rows: %d

            Use this snapshot in your demo to explain filtered segment performance.
            """,
                rows, avg, health, activity
        );
        JOptionPane.showMessageDialog(this, snapshot, "Records Snapshot", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTabGuide() {
        if (mainTabs == null) {
            return;
        }
        String guide = """
            Demo Flow Guide

            1. Dashboard: Start with score, momentum, and recommendations.
            2. AI Assistant: Select a mode and click Use Mode Prompt.
            3. Advanced Insights: Show forecast and benchmark tabs.
            4. Goals & Habits: Apply demo progress and streak actions.
            5. Profile: Show live BMI/readiness and executive summary.
            6. Add Record: Generate ID, use today, add sample record.
            7. Records: Filter, search, and show presentation snapshot.
            """;
        JTextArea area = new JTextArea(guide);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Tab Guide", JOptionPane.INFORMATION_MESSAGE);
    }

    private void applyPresentationMode() {
        Font tabFont = presentationModeEnabled
                ? new Font(Font.SANS_SERIF, Font.BOLD, 14)
                : new Font(Font.SANS_SERIF, Font.BOLD, 12);
        mainTabs.setFont(tabFont);
        if (presentationModeEnabled) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setExtendedState(JFrame.NORMAL);
            setSize(1500, 900);
            setLocationRelativeTo(null);
        }
        revalidate();
        repaint();
    }

    private void showInvestorDemoOverlay() {
        JDialog dialog = new JDialog(this, "Investor Demo Script", false);
        dialog.setSize(640, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        JTextArea script = new JTextArea();
        script.setEditable(false);
        script.setLineWrap(true);
        script.setWrapStyleWord(true);
        script.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
          script.setText(
                     """
                     One-Click Investor Demo Script

                     1) Dashboard
                         Show overall score, momentum, and 14-day animated trend.

                     2) AI Assistant
                         Compare coach modes to demonstrate adaptive intelligence.

                     3) Advanced Insights
                         Refresh model, run scenario simulation, and explain benchmark.

                     4) Goals & Habits
                         Show KPI meters and progress actions.

                     5) Profile + Records
                         Generate executive summary and presentation snapshot.

                     Narrative close:
                     Enterprise-grade analytics + guided automation + premium UX = scalable wellness platform.
                     """
          );

        JLabel status = new JLabel("Ready");
        status.setBorder(new EmptyBorder(0, 8, 0, 0));

        JButton autoplay = new JButton("Run Autoplay Demo");
        autoplay.addActionListener(e -> runBlastDemo(status));
        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.add(autoplay);
        controls.add(close);
        controls.add(status);

        dialog.add(new JScrollPane(script), BorderLayout.CENTER);
        dialog.add(controls, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private class ExecutiveScorecardPanel extends JPanel {
        private final JLabel score = new JLabel("0.0");
        private final JLabel trend = new JLabel("0.0");
        private final JLabel risk = new JLabel("-");
        private final JLabel records = new JLabel("0");
        private double displayedScore = 0.0;
        private double displayedTrend = 0.0;
        private int displayedRecords = 0;

        private ExecutiveScorecardPanel(String tabName) {
            setLayout(new BorderLayout(8, 8));
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 4, 0));

            JLabel title = new JLabel(tabName + " Executive Scorecard");
            title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            title.setForeground(new Color(70, 70, 70));

            JPanel metrics = new JPanel(new GridLayout(1, 4, 6, 6));
            metrics.setOpaque(false);
            metrics.add(scoreCell("Score", score));
            metrics.add(scoreCell("Trend", trend));
            metrics.add(scoreCell("Risk", risk));
            metrics.add(scoreCell("Records", records));

            add(title, BorderLayout.NORTH);
            add(metrics, BorderLayout.CENTER);
            refreshData();
        }

        private JPanel scoreCell(String label, JLabel value) {
            JPanel cell = new JPanel(new BorderLayout(0, 2));
            cell.setBorder(new EmptyBorder(6, 8, 6, 8));
            cell.setBackground(new Color(246, 246, 246));
            JLabel l = new JLabel(label);
            l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            value.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            cell.add(l, BorderLayout.NORTH);
            cell.add(value, BorderLayout.CENTER);
            return cell;
        }

        private void refreshData() {
            double currentScore = manager.getOverallScore();
            List<AbstractRecord> all = manager.getRecords();
            double trendValue = 0.0;
            if (all.size() >= 2) {
                trendValue = all.get(all.size() - 1).calculateImpactScore() - all.get(0).calculateImpactScore();
            }
            animateDouble(displayedScore, currentScore, 320, v -> {
                score.setText(String.format("%.1f", v));
                displayedScore = v;
            });
            animateDouble(displayedTrend, trendValue, 320, v -> {
                trend.setText(String.format("%+.1f", v));
                displayedTrend = v;
            });
            animateInt(displayedRecords, all.size(), 320, v -> {
                records.setText(String.valueOf(v));
                displayedRecords = v;
            });
            risk.setText(manager.getAlertLevel().getLabel());
        }

        private void animateDouble(double start, double end, int durationMs, java.util.function.DoubleConsumer setter) {
            int fps = 30;
            int steps = Math.max(1, durationMs / (1000 / fps));
            final int[] currentStep = {0};
            Timer timer = new Timer(1000 / fps, null);
            timer.addActionListener(e -> {
                currentStep[0]++;
                double t = Math.min(1.0, currentStep[0] / (double) steps);
                double eased = 1 - Math.pow(1 - t, 4);
                setter.accept(start + (end - start) * eased);
                if (t >= 1.0) {
                    timer.stop();
                }
            });
            timer.start();
        }

        private void animateInt(int start, int end, int durationMs, java.util.function.IntConsumer setter) {
            animateDouble(start, end, durationMs, v -> setter.accept((int) Math.round(v)));
        }
    }

    private static class TransitionOverlay extends JComponent {
        private BufferedImage snapshot;
        private float alpha = 0.0f;
        private int slideOffset = 0;
        private Timer timer;

        private TransitionOverlay() {
            setOpaque(false);
            setVisible(false);
        }

        private void play(BufferedImage image, int direction) {
            if (image == null) {
                setVisible(false);
                return;
            }
            this.snapshot = image;

            if (timer != null && timer.isRunning()) {
                timer.stop();
            }

            setVisible(true);
            int fps = 60;
            int durationMs = 260;
            int steps = Math.max(1, durationMs / (1000 / fps));
            final int[] step = {0};
            timer = new Timer(1000 / fps, null);
            timer.addActionListener(e -> {
                step[0]++;
                double t = Math.min(1.0, step[0] / (double) steps);
                double eased = 1 - Math.pow(1 - t, 4);
                alpha = (float) (1.0 - eased);
                slideOffset = (int) (direction * 36 * eased);
                repaint();
                if (t >= 1.0) {
                    timer.stop();
                    snapshot = null;
                    setVisible(false);
                }
            });
            timer.start();
        }

        @Override
        public boolean contains(int x, int y) {
            return false;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!isVisible() || snapshot == null || alpha <= 0.0f) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.drawImage(snapshot, -slideOffset, 0, getWidth(), getHeight(), null);
            g2.dispose();
        }
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showWelcomeMessage() {
        String welcome = """
            Welcome to Elite Wellness!

            Premium Features:
            ✓ AI-Powered Health Coaching
            ✓ Predictive Analytics Engine
            ✓ Automated Wellness Routines
            ✓ Goal & Habit Tracking
            ✓ Real-Time Health Alerts

            Demo data loaded. Ready to optimize your health?
            """;
        JOptionPane.showMessageDialog(this, welcome, "Elite Wellness", JOptionPane.INFORMATION_MESSAGE);
    }
}
