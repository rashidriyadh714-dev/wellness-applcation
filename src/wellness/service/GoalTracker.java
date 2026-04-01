package wellness.service;

import java.util.*;

/**
 * Goal Tracker — Manages wellness goals, tracks progress,
 * and provides goal-driven recommendations and milestones.
 */
public class GoalTracker {
    private final Map<String, Goal> goals = new HashMap<>();
    private final TelemetryService telemetry;

    public GoalTracker(TelemetryService telemetry) {
        this.telemetry = telemetry;
    }

    public static class Goal {
        public String name;
        public String description;
        public int targetValue;
        public int currentValue;
        public String unit;
        public String startDate;
        public String dueDate;
        public String status;

        public Goal(String name, String desc, int target, String unit, String due) {
            this.name = name;
            this.description = desc;
            this.targetValue = target;
            this.currentValue = 0;
            this.unit = unit;
            this.startDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
            this.dueDate = due;
            this.status = "IN_PROGRESS";
        }

        public double getProgress() {
            return (double) currentValue / targetValue * 100;
        }
    }

    public void addGoal(String name, String description, int target, String unit, String dueDate) {
        goals.put(name, new Goal(name, description, target, unit, dueDate));
        java.util.Map<String, String> details = new java.util.HashMap<>();
        details.put("goal", name);
        telemetry.logEvent("goal_added", details);
    }

    public List<Goal> listGoals() {
        List<Goal> items = new ArrayList<>(goals.values());
        items.sort(Comparator.comparing(g -> g.name.toLowerCase(Locale.ROOT)));
        return items;
    }

    public boolean updateGoal(String existingName, String newName, String description,
                              int target, int current, String unit, String dueDate) {
        Goal goal = goals.get(existingName);
        if (goal == null) {
            return false;
        }

        String normalizedName = newName == null ? "" : newName.trim();
        if (normalizedName.isEmpty()) {
            return false;
        }

        if (!existingName.equals(normalizedName) && goals.containsKey(normalizedName)) {
            return false;
        }

        goals.remove(existingName);
        goal.name = normalizedName;
        goal.description = description == null ? "" : description.trim();
        goal.targetValue = Math.max(1, target);
        goal.currentValue = Math.max(0, Math.min(current, goal.targetValue));
        goal.unit = unit == null ? "unit" : unit.trim();
        goal.dueDate = dueDate == null ? goal.dueDate : dueDate.trim();
        goal.status = goal.currentValue >= goal.targetValue ? "COMPLETED" : "IN_PROGRESS";
        goals.put(goal.name, goal);

        java.util.Map<String, String> details = new java.util.HashMap<>();
        details.put("goal", goal.name);
        telemetry.logEvent("goal_updated", details);
        return true;
    }

    public boolean removeGoal(String name) {
        Goal removed = goals.remove(name);
        if (removed != null) {
            java.util.Map<String, String> details = new java.util.HashMap<>();
            details.put("goal", name);
            telemetry.logEvent("goal_removed", details);
            return true;
        }
        return false;
    }

    public void updateGoalProgress(String goalName, int value) {
        Goal goal = goals.get(goalName);
        if (goal != null) {
            goal.currentValue = Math.min(value, goal.targetValue);
            if (goal.currentValue >= goal.targetValue) {
                goal.status = "COMPLETED";
                java.util.Map<String, String> details = new java.util.HashMap<>();
                details.put("goal", goalName);
                telemetry.logEvent("goal_completed", details);
            }
        }
    }

    public String getGoalStatus() {
        StringBuilder status = new StringBuilder("Goal Portfolio\n\n");

        if (goals.isEmpty()) {
            status.append("No goals configured yet. Add your first objective to begin tracking.\n");
            return status.toString();
        }

        int idx = 1;
        for (Goal goal : goals.values()) {
            double progress = goal.getProgress();
            String bar = buildProgressBar(progress);

            status.append(idx).append(". ").append(goal.name).append("\n");
            status.append("   ").append(bar).append(" ").append(String.format("%.0f%%", progress)).append("\n");
            status.append("   Progress: ").append(goal.currentValue).append("/").append(goal.targetValue)
                    .append(" ").append(goal.unit).append("\n");
            status.append("   Due: ").append(goal.dueDate).append(" | Status: ").append(goal.status).append("\n\n");

            idx++;
        }

        return status.toString();
    }

    public String suggestGoals() {
        StringBuilder suggestions = new StringBuilder("Recommended Goal Library\n\n");

        suggestions.append("Fitness Objectives:\n")
                .append("• 10,000 steps/day\n")
                .append("• 150 min moderate cardio/week\n")
                .append("• Strength training 3x/week\n")
                .append("• First 5K run\n\n");

        suggestions.append("Sleep Objectives:\n")
                .append("• 8 hours nightly\n")
                .append("• Consistent sleep schedule\n")
                .append("• 90% sleep quality\n\n");

        suggestions.append("Nutrition Objectives:\n")
                .append("• 5 servings veggies/day\n")
                .append("• 2L water daily\n")
                .append("• -1 lb/week weight loss\n\n");

        suggestions.append("Wellbeing Objectives:\n")
                .append("• 10 min meditation daily\n")
                .append("• Stress score < 3\n")
                .append("• 7+ social activities/month\n");

        return suggestions.toString();
    }

    private String buildProgressBar(double progress) {
        int filled = (int) (progress / 10);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        bar.append("]");
        return bar.toString();
    }

    public String trackMilestones() {
        StringBuilder milestones = new StringBuilder("Milestone Progress\n\n");

        // Sample milestones
        List<String> unlocked = new ArrayList<>();
        List<String> upcoming = new ArrayList<>();

        // Determine unlocked milestones based on goals
        for (Goal goal : goals.values()) {
            if (goal.getProgress() >= 25) unlocked.add("Quarter completion: " + goal.name);
            if (goal.getProgress() >= 50) unlocked.add("Halfway reached: " + goal.name);
            if (goal.getProgress() >= 75) unlocked.add("Final stretch: " + goal.name);
            if (goal.getProgress() >= 100) unlocked.add("Completed: " + goal.name);
        }

        if (unlocked.isEmpty()) {
            upcoming.add("Start your first goal");
        }

        milestones.append("Completed Milestones:\n");
        for (String m : unlocked) {
            milestones.append("  ").append(m).append("\n");
        }
        if (unlocked.isEmpty()) {
            milestones.append("  (Set a goal to unlock achievements)\n");
        }

        milestones.append("\nUpcoming Milestones:\n");
        for (String m : upcoming) {
            milestones.append("  → ").append(m).append("\n");
        }

        if (upcoming.isEmpty()) {
            milestones.append("  All milestones currently achieved.\n");
        }

        return milestones.toString();
    }
}
