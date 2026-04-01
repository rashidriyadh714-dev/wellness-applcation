package wellness.ui;

import wellness.service.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Goals & Habits Panel — Unified view for goal tracking,
 * habit streaks, and streak building with automation.
 */
public class GoalsHabitsPanel extends JPanel {
    private final GoalTracker goalTracker;
    private final HabitTracker habitTracker;
    private final JTabbedPane tabs;
    private JTable goalsTable;
    private JTable habitsTable;
    private JTabbedPane goalViewsTabs;
    private JTabbedPane habitViewsTabs;
    private JEditorPane goalsBoardPane;
    private JEditorPane habitsBoardPane;
    private JTextArea goalsDisplayArea;
    private JTextArea habitsDisplayArea;
    private JTextArea streaksDisplayArea;
    private JTextArea suggestionsDisplayArea;
    private final DefaultTableModel goalTableModel = new DefaultTableModel(
            new Object[]{"Goal", "Target", "Current", "Unit", "Due", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel habitTableModel = new DefaultTableModel(
            new Object[]{"Habit", "Category", "Streak", "Best", "Total", "Today"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JLabel goalsCountLabel = new JLabel("Goals: 0");
    private final JLabel habitStreakLabel = new JLabel("Top Streak: 0");
    private final JLabel momentumLabel = new JLabel("Momentum: Building");
    private final JProgressBar goalsProgressBar = new JProgressBar(0, 100);
    private final JProgressBar streakProgressBar = new JProgressBar(0, 30);
    private final DefaultTableModel executiveBoardModel = new DefaultTableModel(
            new Object[]{"KPI", "Current", "Target", "Status", "Priority"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final StreakHeatmapPanel heatmapPanel = new StreakHeatmapPanel();

    public GoalsHabitsPanel(GoalTracker goalTracker, HabitTracker habitTracker) {
        this.goalTracker = goalTracker;
        this.habitTracker = habitTracker;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        JPanel north = new JPanel(new BorderLayout(8, 8));
        north.setBackground(Color.WHITE);
        north.add(buildQuickGuide(), BorderLayout.NORTH);
        north.add(buildTopKpiStrip(), BorderLayout.CENTER);
        north.add(buildVisualMeters(), BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setBackground(Color.WHITE);
        tabs.setForeground(Color.BLACK);
        tabs.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        // Goals tab
        JPanel goalsPanel = buildGoalsPanel();
        tabs.addTab("Goals", goalsPanel);

        // Habits tab
        JPanel habitsPanel = buildHabitsPanel();
        tabs.addTab("Habits", habitsPanel);

        // Streaks tab
        JPanel streaksPanel = buildStreaksPanel();
        tabs.addTab("Streaks", streaksPanel);

        // Executive board tab
        JPanel boardPanel = buildExecutiveBoardPanel();
        tabs.addTab("Executive Board", boardPanel);

        // Suggestions tab
        JPanel suggestionsPanel = buildSuggestionsPanel();
        tabs.addTab("Recommendations", suggestionsPanel);

        add(tabs, BorderLayout.CENTER);
        refreshKpis();
        refreshAllViews();
    }

    private JPanel buildTopKpiStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 3, 8, 8));
        strip.setBackground(Color.WHITE);
        strip.add(kpiCard("Goals", goalsCountLabel));
        strip.add(kpiCard("Habit Streak", habitStreakLabel));
        strip.add(kpiCard("Momentum", momentumLabel));
        return strip;
    }

    private JPanel kpiCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 205, 205)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        card.setBackground(new Color(248, 248, 248));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        value.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildQuickGuide() {
        JPanel guide = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        guide.setBackground(new Color(248, 248, 248));
        guide.setBorder(new EmptyBorder(6, 8, 6, 8));
        JLabel text = new JLabel("Workflow: Refresh metrics, apply progress updates, complete habits, then review streak quality.");
        text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        guide.add(text);
        return guide;
    }

    private JPanel buildVisualMeters() {
        JPanel meters = new JPanel(new GridLayout(1, 2, 8, 8));
        meters.setBackground(Color.WHITE);

        goalsProgressBar.setStringPainted(true);
        goalsProgressBar.setBackground(new Color(235, 235, 235));
        goalsProgressBar.setForeground(Color.BLACK);

        streakProgressBar.setStringPainted(true);
        streakProgressBar.setBackground(new Color(235, 235, 235));
        streakProgressBar.setForeground(Color.BLACK);

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setBackground(Color.WHITE);
        left.add(new JLabel("Goal Completion Meter"), BorderLayout.NORTH);
        left.add(goalsProgressBar, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(0, 4));
        right.setBackground(Color.WHITE);
        right.add(new JLabel("Streak Strength Meter"), BorderLayout.NORTH);
        right.add(streakProgressBar, BorderLayout.CENTER);

        meters.add(left);
        meters.add(right);
        return meters;
    }

    private JPanel buildGoalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        goalsTable = new JTable(goalTableModel);
        goalsTable.setRowHeight(36);
        goalsTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        goalsTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        goalsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        goalsTable.setAutoCreateRowSorter(true);
        JScrollPane tableScroll = new JScrollPane(goalsTable);
        tableScroll.setPreferredSize(new Dimension(880, 170));

        JTextField nameField = new JTextField(14);
        JTextField targetField = new JTextField(6);
        JTextField currentField = new JTextField(6);
        JTextField unitField = new JTextField(8);
        JTextField dueField = new JTextField(10);
        JTextField descField = new JTextField(20);
        final String[] selectedGoal = {null};

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 4, 2, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; form.add(new JLabel("Name"), gc);
        gc.gridx = 1; form.add(nameField, gc);
        gc.gridx = 2; form.add(new JLabel("Target"), gc);
        gc.gridx = 3; form.add(targetField, gc);
        gc.gridx = 4; form.add(new JLabel("Current"), gc);
        gc.gridx = 5; form.add(currentField, gc);

        gc.gridx = 0; gc.gridy = 1; form.add(new JLabel("Unit"), gc);
        gc.gridx = 1; form.add(unitField, gc);
        gc.gridx = 2; form.add(new JLabel("Due (yyyy-MM-dd)"), gc);
        gc.gridx = 3; form.add(dueField, gc);
        gc.gridx = 4; form.add(new JLabel("Description"), gc);
        gc.gridx = 5; gc.weightx = 1.0; form.add(descField, gc);
        gc.weightx = 0;

        JButton addGoalBtn = new JButton("Add Goal");
        JButton updateGoalBtn = new JButton("Update Goal");
        JButton deleteGoalBtn = new JButton("Delete Goal");
        JButton clearGoalBtn = new JButton("Clear");

        JPanel formActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        formActions.setBackground(Color.WHITE);
        formActions.add(addGoalBtn);
        formActions.add(updateGoalBtn);
        formActions.add(deleteGoalBtn);
        formActions.add(clearGoalBtn);

        JPanel editor = new JPanel(new BorderLayout(6, 6));
        editor.setBackground(Color.WHITE);
        editor.add(tableScroll, BorderLayout.NORTH);
        editor.add(form, BorderLayout.CENTER);
        editor.add(formActions, BorderLayout.SOUTH);
        editor.setMinimumSize(new Dimension(780, 180));

        goalsBoardPane = createHtmlPane();
        goalsBoardPane.setText(renderGoalPortfolioHtml());

        goalsDisplayArea = new JTextArea();
        goalsDisplayArea.setEditable(false);
        goalsDisplayArea.setLineWrap(false);
        goalsDisplayArea.setWrapStyleWord(false);
        goalsDisplayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        goalsDisplayArea.setBackground(new Color(250, 250, 250));
        goalsDisplayArea.setForeground(Color.BLACK);
        goalsDisplayArea.setMargin(new Insets(14, 14, 14, 14));
        goalsDisplayArea.setText(goalTracker.getGoalStatus());

        goalViewsTabs = new JTabbedPane();
        goalViewsTabs.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        JScrollPane goalsBoardScroll = new JScrollPane(goalsBoardPane);
        goalsBoardScroll.setPreferredSize(new Dimension(920, 560));
        JScrollPane goalsTextScroll = new JScrollPane(goalsDisplayArea);
        goalsTextScroll.setPreferredSize(new Dimension(920, 560));
        goalViewsTabs.addTab("Portfolio View", goalsBoardScroll);
        goalViewsTabs.addTab("Detailed Report", goalsTextScroll);
        goalViewsTabs.setSelectedIndex(0);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editor, goalViewsTabs);
        splitPane.setResizeWeight(0.15);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerLocation(170);
        panel.add(splitPane, BorderLayout.CENTER);

        java.lang.Runnable refreshGoalEditor = this::refreshAllViews;

        goalsTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = goalsTable.getSelectedRow();
            if (row < 0) {
                return;
            }
            selectedGoal[0] = String.valueOf(goalTableModel.getValueAt(row, 0));
            nameField.setText(selectedGoal[0]);
            targetField.setText(String.valueOf(goalTableModel.getValueAt(row, 1)));
            currentField.setText(String.valueOf(goalTableModel.getValueAt(row, 2)));
            unitField.setText(String.valueOf(goalTableModel.getValueAt(row, 3)));
            dueField.setText(String.valueOf(goalTableModel.getValueAt(row, 4)));
            descField.setText("");
        });

        addGoalBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Goal name is required.", "Goals", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int target = parseInt(targetField.getText(), 1);
            int current = parseInt(currentField.getText(), 0);
            String unit = unitField.getText().trim().isEmpty() ? "unit" : unitField.getText().trim();
            String due = dueField.getText().trim().isEmpty() ? LocalDate.now().plusDays(30).toString() : dueField.getText().trim();
            goalTracker.addGoal(name, descField.getText().trim(), target, unit, due);
            goalTracker.updateGoalProgress(name, current);
            selectedGoal[0] = name;
            refreshGoalEditor.run();
        });

        updateGoalBtn.addActionListener(e -> {
            String existing = selectedGoal[0] == null ? nameField.getText().trim() : selectedGoal[0];
            if (existing == null || existing.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Select a goal to update.", "Goals", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean updated = goalTracker.updateGoal(
                    existing,
                    nameField.getText().trim(),
                    descField.getText().trim(),
                    parseInt(targetField.getText(), 1),
                    parseInt(currentField.getText(), 0),
                    unitField.getText().trim(),
                    dueField.getText().trim()
            );
            if (!updated) {
                JOptionPane.showMessageDialog(panel, "Unable to update goal. Check name uniqueness.", "Goals", JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedGoal[0] = nameField.getText().trim();
            refreshGoalEditor.run();
        });

        deleteGoalBtn.addActionListener(e -> {
            String targetName = selectedGoal[0] == null ? nameField.getText().trim() : selectedGoal[0];
            if (targetName == null || targetName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Select a goal to delete.", "Goals", JOptionPane.WARNING_MESSAGE);
                return;
            }
            goalTracker.removeGoal(targetName);
            selectedGoal[0] = null;
            nameField.setText("");
            targetField.setText("");
            currentField.setText("");
            unitField.setText("");
            dueField.setText("");
            descField.setText("");
            refreshGoalEditor.run();
        });

        clearGoalBtn.addActionListener(e -> {
            selectedGoal[0] = null;
            goalsTable.clearSelection();
            nameField.setText("");
            targetField.setText("");
            currentField.setText("");
            unitField.setText("");
            dueField.setText("");
            descField.setText("");
        });

        refreshGoalTable();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            refreshAllViews();
        });

        JButton suggestBtn = new JButton("Strategy Recommendations");
        suggestBtn.addActionListener(e -> {
            goalsDisplayArea.setText(goalTracker.suggestGoals());
            if (goalViewsTabs != null && goalViewsTabs.getTabCount() > 1) {
                goalViewsTabs.setSelectedIndex(1);
            }
        });

        JButton milestonesBtn = new JButton("Milestones");
        milestonesBtn.addActionListener(e -> {
            goalsDisplayArea.setText(goalTracker.trackMilestones());
            if (goalViewsTabs != null && goalViewsTabs.getTabCount() > 1) {
                goalViewsTabs.setSelectedIndex(1);
            }
        });

        JButton addProgressBtn = new JButton("Apply Progress Update");
        addProgressBtn.addActionListener((ActionEvent e) -> {
            goalTracker.updateGoalProgress("Daily Sleep", 6);
            goalTracker.updateGoalProgress("Daily Steps", 7600);
            goalTracker.updateGoalProgress("Daily Water", 2100);
            refreshAllViews();
        });

        buttonPanel.add(refreshBtn);
        buttonPanel.add(suggestBtn);
        buttonPanel.add(milestonesBtn);
        buttonPanel.add(addProgressBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildHabitsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        habitsTable = new JTable(habitTableModel);
        habitsTable.setRowHeight(36);
        habitsTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        habitsTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        habitsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        habitsTable.setAutoCreateRowSorter(true);
        JScrollPane tableScroll = new JScrollPane(habitsTable);
        tableScroll.setPreferredSize(new Dimension(880, 170));

        JTextField nameField = new JTextField(16);
        JTextField categoryField = new JTextField(12);
        final String[] selectedHabit = {null};

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        form.setBackground(Color.WHITE);
        form.add(new JLabel("Habit"));
        form.add(nameField);
        form.add(new JLabel("Category"));
        form.add(categoryField);

        JButton addHabitBtn = new JButton("Add Habit");
        JButton updateHabitBtn = new JButton("Update Habit");
        JButton deleteHabitBtn = new JButton("Delete Habit");
        JButton completeHabitBtn = new JButton("Complete Selected");
        JButton clearHabitBtn = new JButton("Clear");

        JPanel formActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        formActions.setBackground(Color.WHITE);
        formActions.add(addHabitBtn);
        formActions.add(updateHabitBtn);
        formActions.add(deleteHabitBtn);
        formActions.add(completeHabitBtn);
        formActions.add(clearHabitBtn);

        JPanel editor = new JPanel(new BorderLayout(6, 6));
        editor.setBackground(Color.WHITE);
        editor.add(tableScroll, BorderLayout.NORTH);
        editor.add(form, BorderLayout.CENTER);
        editor.add(formActions, BorderLayout.SOUTH);
        editor.setMinimumSize(new Dimension(780, 180));

        habitsBoardPane = createHtmlPane();
        habitsBoardPane.setText(renderHabitOperationsHtml());

        habitsDisplayArea = new JTextArea();
        habitsDisplayArea.setEditable(false);
        habitsDisplayArea.setLineWrap(false);
        habitsDisplayArea.setWrapStyleWord(false);
        habitsDisplayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        habitsDisplayArea.setBackground(new Color(250, 250, 250));
        habitsDisplayArea.setForeground(Color.BLACK);
        habitsDisplayArea.setMargin(new Insets(14, 14, 14, 14));
        habitsDisplayArea.setText(habitTracker.getHabitStatus());

        habitViewsTabs = new JTabbedPane();
        habitViewsTabs.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        JScrollPane habitsBoardScroll = new JScrollPane(habitsBoardPane);
        habitsBoardScroll.setPreferredSize(new Dimension(920, 560));
        JScrollPane habitsTextScroll = new JScrollPane(habitsDisplayArea);
        habitsTextScroll.setPreferredSize(new Dimension(920, 560));
        habitViewsTabs.addTab("Operations Board", habitsBoardScroll);
        habitViewsTabs.addTab("Detailed Report", habitsTextScroll);
        habitViewsTabs.setSelectedIndex(0);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editor, habitViewsTabs);
        splitPane.setResizeWeight(0.15);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerLocation(170);
        panel.add(splitPane, BorderLayout.CENTER);

        java.lang.Runnable refreshHabitEditor = this::refreshAllViews;

        habitsTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = habitsTable.getSelectedRow();
            if (row < 0) {
                return;
            }
            selectedHabit[0] = String.valueOf(habitTableModel.getValueAt(row, 0));
            nameField.setText(selectedHabit[0]);
            categoryField.setText(String.valueOf(habitTableModel.getValueAt(row, 1)));
        });

        addHabitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Habit name is required.", "Habits", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String category = categoryField.getText().trim().isEmpty() ? "General" : categoryField.getText().trim();
            boolean added = habitTracker.addHabit(name, category);
            if (!added) {
                JOptionPane.showMessageDialog(panel,
                        "Habit already exists (name match is case-insensitive).",
                        "Habits",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedHabit[0] = name;
            refreshHabitEditor.run();
        });

        updateHabitBtn.addActionListener(e -> {
            String existing = selectedHabit[0] == null ? nameField.getText().trim() : selectedHabit[0];
            if (existing == null || existing.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Select a habit to update.", "Habits", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean updated = habitTracker.updateHabit(existing, nameField.getText().trim(), categoryField.getText().trim());
            if (!updated) {
                JOptionPane.showMessageDialog(panel, "Unable to update habit. Check name uniqueness.", "Habits", JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedHabit[0] = nameField.getText().trim();
            refreshHabitEditor.run();
        });

        deleteHabitBtn.addActionListener(e -> {
            String targetName = selectedHabit[0] == null ? nameField.getText().trim() : selectedHabit[0];
            if (targetName == null || targetName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Select a habit to delete.", "Habits", JOptionPane.WARNING_MESSAGE);
                return;
            }
            habitTracker.removeHabit(targetName);
            selectedHabit[0] = null;
            nameField.setText("");
            categoryField.setText("");
            refreshHabitEditor.run();
        });

        completeHabitBtn.addActionListener(e -> {
            String targetName = selectedHabit[0] == null ? nameField.getText().trim() : selectedHabit[0];
            if (targetName == null || targetName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Select a habit to complete.", "Habits", JOptionPane.WARNING_MESSAGE);
                return;
            }
            habitTracker.completeHabit(targetName);
            refreshHabitEditor.run();
        });

        clearHabitBtn.addActionListener(e -> {
            selectedHabit[0] = null;
            habitsTable.clearSelection();
            nameField.setText("");
            categoryField.setText("");
        });

        refreshHabitTable();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            refreshAllViews();
        });

        JButton suggestBtn = new JButton("Habit Recommendations");
        suggestBtn.addActionListener(e -> {
            habitsDisplayArea.setText(habitTracker.suggestHabits());
            if (habitViewsTabs != null && habitViewsTabs.getTabCount() > 1) {
                habitViewsTabs.setSelectedIndex(1);
            }
        });

        JButton remindersBtn = new JButton("Daily Reminders");
        remindersBtn.addActionListener(e -> {
            habitsDisplayArea.setText(habitTracker.automatedHabitReminders());
            if (habitViewsTabs != null && habitViewsTabs.getTabCount() > 1) {
                habitViewsTabs.setSelectedIndex(1);
            }
        });

        JButton completeDemoBtn = new JButton("Mark Completion");
        completeDemoBtn.addActionListener((ActionEvent e) -> {
            int completed = habitTracker.completeDailyPriorityHabits();
            refreshAllViews();
            JOptionPane.showMessageDialog(panel,
                    completed > 0 ? ("Completed " + completed + " habit(s).") : "No pending habits to complete.",
                    "Habits",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(refreshBtn);
        buttonPanel.add(suggestBtn);
        buttonPanel.add(remindersBtn);
        buttonPanel.add(completeDemoBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildStreaksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        heatmapPanel.setPreferredSize(new Dimension(500, 150));
        panel.add(wrapPanel("4-Week Consistency Heatmap", heatmapPanel), BorderLayout.NORTH);

        streaksDisplayArea = new JTextArea();
        streaksDisplayArea.setEditable(false);
        streaksDisplayArea.setLineWrap(true);
        streaksDisplayArea.setWrapStyleWord(true);
        streaksDisplayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 17));
        streaksDisplayArea.setBackground(new Color(250, 250, 250));
        streaksDisplayArea.setForeground(Color.BLACK);
        streaksDisplayArea.setMargin(new Insets(10, 10, 10, 10));
        streaksDisplayArea.setText(habitTracker.trackStreak());

        JScrollPane scroll = new JScrollPane(streaksDisplayArea);
        panel.add(scroll, BorderLayout.CENTER);

        JButton refreshStreaks = new JButton("Refresh Streak Analytics");
        refreshStreaks.addActionListener(e -> {
            refreshAllViews();
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.setBackground(Color.WHITE);
        controls.add(refreshStreaks);
        panel.add(controls, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildExecutiveBoardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(executiveBoardModel);
        table.setRowHeight(30);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);

        JTextArea notes = new JTextArea();
        notes.setEditable(false);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        notes.setBackground(new Color(248, 248, 248));
        notes.setBorder(new EmptyBorder(10, 10, 10, 10));
        notes.setText("""
            Executive View
            Use this table to report habit momentum and goal execution in review meetings.
            Priority should remain on consistency first, intensity second.
            """);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(notes, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildSuggestionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        suggestionsDisplayArea = new JTextArea();
        suggestionsDisplayArea.setEditable(false);
        suggestionsDisplayArea.setLineWrap(true);
        suggestionsDisplayArea.setWrapStyleWord(true);
        suggestionsDisplayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        suggestionsDisplayArea.setBackground(new Color(250, 250, 250));
        suggestionsDisplayArea.setForeground(Color.BLACK);
        suggestionsDisplayArea.setMargin(new Insets(10, 10, 10, 10));
        suggestionsDisplayArea.setText("Wellness Recommendations:\n\n" +
                goalTracker.suggestGoals() + "\n\n" + habitTracker.suggestHabits());

        JScrollPane scroll = new JScrollPane(suggestionsDisplayArea);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void refreshAllViews() {
        refreshGoalTable();
        refreshHabitTable();
        if (goalsBoardPane != null) {
            goalsBoardPane.setText(renderGoalPortfolioHtml());
            goalsBoardPane.setCaretPosition(0);
        }
        if (goalsDisplayArea != null) {
            goalsDisplayArea.setText(goalTracker.getGoalStatus());
            goalsDisplayArea.setCaretPosition(0);
        }
        if (habitsBoardPane != null) {
            habitsBoardPane.setText(renderHabitOperationsHtml());
            habitsBoardPane.setCaretPosition(0);
        }
        if (habitsDisplayArea != null) {
            habitsDisplayArea.setText(habitTracker.getHabitStatus());
            habitsDisplayArea.setCaretPosition(0);
        }
        if (streaksDisplayArea != null) {
            streaksDisplayArea.setText(habitTracker.trackStreak());
            streaksDisplayArea.setCaretPosition(0);
        }
        if (suggestionsDisplayArea != null) {
            suggestionsDisplayArea.setText("Wellness Recommendations:\n\n" +
                    goalTracker.suggestGoals() + "\n\n" + habitTracker.suggestHabits());
            suggestionsDisplayArea.setCaretPosition(0);
        }
        refreshKpis();
        if (tabs != null) {
            tabs.revalidate();
            tabs.repaint();
        }
    }

    private JEditorPane createHtmlPane() {
        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");
        pane.setEditable(false);
        pane.setBackground(new Color(250, 250, 250));
        pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        pane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        pane.setBorder(new EmptyBorder(16, 16, 16, 16));
        return pane;
    }

    private String renderGoalPortfolioHtml() {
        List<GoalTracker.Goal> goals = goalTracker.listGoals();
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Sans-Serif;background:#FAFAFA;color:#111;margin:0;line-height:1.5;'>");
        html.append("<div style='font-size:26px;font-weight:800;margin-bottom:14px;'>Goal Portfolio</div>");

        if (goals.isEmpty()) {
                html.append("<div style='padding:16px;border:1px solid #E0E0E0;border-radius:12px;background:white;font-size:16px;'>")
                    .append("No goals yet. Add a goal to see your portfolio board.")
                    .append("</div></body></html>");
            return html.toString();
        }

        int idx = 1;
        for (GoalTracker.Goal goal : goals) {
            int progress = (int) Math.max(0, Math.min(100, Math.round(goal.getProgress())));
            String statusColor = "COMPLETED".equals(goal.status) ? "#0A7A2F" : "#333333";

                html.append("<div style='margin-bottom:14px;padding:14px;border:1px solid #D9D9D9;border-radius:14px;background:white;'>");
            html.append("<div style='display:flex;justify-content:space-between;align-items:center;'>");
                html.append("<div style='font-size:18px;font-weight:800;'>")
                    .append(idx++).append(". ").append(escapeHtml(goal.name)).append("</div>");
                html.append("<div style='font-size:13px;color:").append(statusColor).append(";font-weight:800;'>")
                    .append(escapeHtml(goal.status)).append("</div>");
            html.append("</div>");
                html.append("<div style='margin-top:10px;height:12px;background:#E8E8E8;border-radius:8px;overflow:hidden;'>")
                    .append("<div style='height:12px;background:#111;width:").append(progress).append("%;'></div></div>");
                html.append("<div style='font-size:14px;margin-top:8px;'>Progress: <b>")
                    .append(goal.currentValue).append("/").append(goal.targetValue).append(" ")
                    .append(escapeHtml(goal.unit)).append("</b> ( ").append(progress).append("% )</div>");
                html.append("<div style='font-size:13px;color:#444;margin-top:5px;'>Due: ")
                    .append(escapeHtml(goal.dueDate)).append("</div>");
            html.append("</div>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    private String renderHabitOperationsHtml() {
        List<HabitTracker.Habit> habits = habitTracker.listHabits();
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Sans-Serif;background:#FAFAFA;color:#111;margin:0;line-height:1.5;'>");
        html.append("<div style='font-size:26px;font-weight:800;margin-bottom:14px;'>Daily Habit Operations</div>");

        if (habits.isEmpty()) {
                html.append("<div style='padding:16px;border:1px solid #E0E0E0;border-radius:12px;background:white;font-size:16px;'>")
                    .append("No habits yet. Add habits to activate the operations board.")
                    .append("</div></body></html>");
            return html.toString();
        }

        Map<String, List<HabitTracker.Habit>> grouped = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (HabitTracker.Habit habit : habits) {
            grouped.computeIfAbsent(habit.category, key -> new ArrayList<>()).add(habit);
        }

        for (Map.Entry<String, List<HabitTracker.Habit>> entry : grouped.entrySet()) {
            List<HabitTracker.Habit> items = entry.getValue();
            items.sort(Comparator.comparing(h -> h.name.toLowerCase(Locale.ROOT)));

                html.append("<div style='margin-bottom:12px;padding:12px;border:1px solid #D9D9D9;border-radius:14px;background:white;'>");
                html.append("<div style='font-size:17px;font-weight:800;margin-bottom:8px;'>")
                    .append(escapeHtml(entry.getKey())).append("</div>");

            for (HabitTracker.Habit habit : items) {
                String badgeBg = habit.completedToday ? "#0A7A2F" : "#555";
                String badgeText = habit.completedToday ? "DONE" : "PENDING";

                html.append("<div style='padding:10px 12px;margin-bottom:8px;border:1px solid #ECECEC;border-radius:12px;background:#FCFCFC;'>");
                html.append("<span style='display:inline-block;padding:2px 8px;border-radius:10px;background:")
                    .append(badgeBg).append(";color:white;font-size:12px;font-weight:800;margin-right:10px;'>")
                    .append(badgeText).append("</span>&nbsp;");
                html.append("<span style='font-size:16px;font-weight:800;'>")
                        .append(escapeHtml(habit.name)).append("</span>");
                html.append("<div style='font-size:13px;color:#333;margin-top:6px;'>Streak: <b>")
                        .append(habit.currentStreak).append(" days</b> | Best: ")
                        .append(habit.longestStreak).append(" | Total: ")
                        .append(habit.totalDone).append("</div>");
                html.append("</div>");
            }

            html.append("</div>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void refreshKpis() {
        int goalsCount = goalTracker.listGoals().size();
        int topStreak = 0;
        int achievedGoals = 0;
        double progressTotal = 0.0;

        for (GoalTracker.Goal goal : goalTracker.listGoals()) {
            progressTotal += goal.getProgress();
            if ("COMPLETED".equals(goal.status)) {
                achievedGoals++;
            }
        }

        for (HabitTracker.Habit habit : habitTracker.listHabits()) {
            topStreak = Math.max(topStreak, habit.currentStreak);
        }

        goalsCountLabel.setText("Goals: " + goalsCount);
        habitStreakLabel.setText("Top Streak: " + topStreak + " days");
        momentumLabel.setText(topStreak >= 7 || achievedGoals >= 2 ? "Momentum: Strong" : "Momentum: Building");

        int averageProgress = goalsCount == 0 ? 0 : (int) Math.round(progressTotal / goalsCount);
        int estimatedProgress = Math.min(100, Math.max(averageProgress, Math.min(95, achievedGoals * 25 + Math.min(25, topStreak))));
        goalsProgressBar.setValue(estimatedProgress);
        goalsProgressBar.setString(estimatedProgress + "%");

        streakProgressBar.setValue(Math.min(30, topStreak));
        streakProgressBar.setString(topStreak + " / 30 days");

        refreshExecutiveBoard(goalsCount, topStreak, estimatedProgress);
        heatmapPanel.setDailyScores(buildHeatmapData(topStreak));
    }

    private void refreshGoalTable() {
        goalTableModel.setRowCount(0);
        for (GoalTracker.Goal goal : goalTracker.listGoals()) {
            goalTableModel.addRow(new Object[]{
                    goal.name,
                    goal.targetValue,
                    goal.currentValue,
                    goal.unit,
                    goal.dueDate,
                    goal.status
            });
        }
    }

    private void refreshHabitTable() {
        habitTableModel.setRowCount(0);
        for (HabitTracker.Habit habit : habitTracker.listHabits()) {
            habitTableModel.addRow(new Object[]{
                    habit.name,
                    habit.category,
                    habit.currentStreak,
                    habit.longestStreak,
                    habit.totalDone,
                    habit.completedToday ? "Yes" : "No"
            });
        }
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private void refreshExecutiveBoard(int goalsCount, int topStreak, int estimatedProgress) {
        executiveBoardModel.setRowCount(0);
        executiveBoardModel.addRow(new Object[]{"Goal Portfolio", goalsCount, 4, goalsCount >= 3 ? "Healthy" : "Needs Expansion", "Medium"});
        executiveBoardModel.addRow(new Object[]{"Streak Strength", topStreak + " days", "14+ days", topStreak >= 14 ? "Strong" : "Building", "High"});
        executiveBoardModel.addRow(new Object[]{"Execution Index", estimatedProgress + "%", "85%", estimatedProgress >= 85 ? "On Track" : "At Risk", "High"});
        executiveBoardModel.addRow(new Object[]{"Momentum", momentumLabel.getText().replace("Momentum: ", ""), "Strong", topStreak >= 7 ? "Positive" : "Watch", "Medium"});
    }

    private int[] buildHeatmapData(int topStreak) {
        int[] values = new int[28];
        int base = Math.max(1, Math.min(5, topStreak / 4 + 1));
        for (int i = 0; i < values.length; i++) {
            int dayOfWeekBias = (LocalDate.now().minusDays(values.length - i).getDayOfWeek().getValue() >= 6) ? -1 : 1;
            values[i] = Math.max(0, Math.min(5, base + dayOfWeekBias + ((i % 6 == 0) ? 1 : 0)));
        }
        return values;
    }

    private JPanel wrapPanel(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private static class StreakHeatmapPanel extends JPanel {
        private int[] dailyScores = new int[28];

        private StreakHeatmapPanel() {
            setBackground(Color.WHITE);
        }

        private void setDailyScores(int[] values) {
            if (values != null && values.length == 28) {
                this.dailyScores = values;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cols = 7;
            int cellSize = Math.min(24, Math.max(14, getWidth() / (cols + 3)));
            int gap = 6;
            int startX = 20;
            int startY = 20;

            g2.setColor(new Color(80, 80, 80));
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            g2.drawString("Mon", startX - 2, startY - 6);

            for (int i = 0; i < dailyScores.length; i++) {
                int r = i / cols;
                int c = i % cols;
                int x = startX + c * (cellSize + gap);
                int y = startY + r * (cellSize + gap);
                int score = dailyScores[i];
                int shade = 245 - (score * 28);
                shade = Math.max(60, Math.min(245, shade));
                g2.setColor(new Color(shade, shade, shade));
                g2.fillRoundRect(x, y, cellSize, cellSize, 6, 6);
                g2.setColor(new Color(35, 35, 35));
                g2.drawRoundRect(x, y, cellSize, cellSize, 6, 6);
            }
        }
    }
}
