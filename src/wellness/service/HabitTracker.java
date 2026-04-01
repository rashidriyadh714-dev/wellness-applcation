package wellness.service;

import java.util.*;
import java.time.LocalDate;

/**
 * Habit Tracker — Tracks daily habits, builds streaks,
 * and provides habit-based recommendations and automation.
 */
public class HabitTracker {
    private final Map<String, Habit> habits = new HashMap<>();
    private final TelemetryService telemetry;

    private String keyOf(String name) {
        if (name == null) {
            return "";
        }
        return name.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    public HabitTracker(TelemetryService telemetry) {
        this.telemetry = telemetry;
    }

    public static class Habit {
        public String name;
        public String category;
        public int currentStreak;
        public int longestStreak;
        public int totalDone;
        public boolean completedToday;
        public LocalDate startDate;

        public Habit(String name, String category) {
            this.name = name;
            this.category = category;
            this.currentStreak = 0;
            this.longestStreak = 0;
            this.totalDone = 0;
            this.completedToday = false;
            this.startDate = LocalDate.now();
        }
    }

    public boolean addHabit(String name, String category) {
        String key = keyOf(name);
        if (key.isEmpty()) {
            return false;
        }
        if (habits.containsKey(key)) {
            return false;
        }

        String cleanName = name.trim().replaceAll("\\s+", " ");
        String cleanCategory = category == null || category.trim().isEmpty()
                ? "General"
                : category.trim().replaceAll("\\s+", " ");
        habits.put(key, new Habit(cleanName, cleanCategory));
        java.util.Map<String, String> details = new java.util.HashMap<>();
        details.put("habit", cleanName);
        telemetry.logEvent("habit_added", details);
        return true;
    }

    public List<Habit> listHabits() {
        List<Habit> items = new ArrayList<>(habits.values());
        items.sort(Comparator.comparing(h -> h.name.toLowerCase(Locale.ROOT)));
        return items;
    }

    public boolean updateHabit(String existingName, String newName, String newCategory) {
        Habit habit = habits.get(keyOf(existingName));
        if (habit == null) {
            return false;
        }

        String normalizedName = newName == null ? "" : newName.trim().replaceAll("\\s+", " ");
        if (normalizedName.isEmpty()) {
            return false;
        }

        String existingKey = keyOf(existingName);
        String newKey = keyOf(normalizedName);
        if (!existingKey.equals(newKey) && habits.containsKey(newKey)) {
            return false;
        }

        habits.remove(existingKey);
        habit.name = normalizedName;
        habit.category = newCategory == null || newCategory.trim().isEmpty()
                ? "General"
                : newCategory.trim().replaceAll("\\s+", " ");
        habits.put(newKey, habit);

        java.util.Map<String, String> details = new java.util.HashMap<>();
        details.put("habit", habit.name);
        telemetry.logEvent("habit_updated", details);
        return true;
    }

    public boolean removeHabit(String name) {
        Habit removed = habits.remove(keyOf(name));
        if (removed != null) {
            java.util.Map<String, String> details = new java.util.HashMap<>();
            details.put("habit", removed.name);
            telemetry.logEvent("habit_removed", details);
            return true;
        }
        return false;
    }

    public void completeHabit(String habitName) {
        Habit habit = habits.get(keyOf(habitName));
        if (habit != null && !habit.completedToday) {
            habit.completedToday = true;
            habit.currentStreak++;
            habit.totalDone++;
            habit.longestStreak = Math.max(habit.longestStreak, habit.currentStreak);
            java.util.Map<String, String> details = new java.util.HashMap<>();
            details.put("habit", habit.name);
            telemetry.logEvent("habit_completed", details);
        }
    }

    public int completeDailyPriorityHabits() {
        Map<String, Habit> firstPendingByCategory = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Habit habit : listHabits()) {
            if (!habit.completedToday && !firstPendingByCategory.containsKey(habit.category)) {
                firstPendingByCategory.put(habit.category, habit);
            }
        }

        for (Habit habit : firstPendingByCategory.values()) {
            completeHabit(habit.name);
        }
        return firstPendingByCategory.size();
    }

    public String getHabitStatus() {
        StringBuilder status = new StringBuilder("Daily Habit Operations\n\n");

        if (habits.isEmpty()) {
            status.append("No habits tracked yet. Add routines to begin daily consistency tracking.\n");
            return status.toString();
        }

        // Group by category
        Map<String, List<Habit>> byCategory = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Habit h : habits.values()) {
            byCategory.computeIfAbsent(h.category, k -> new ArrayList<>()).add(h);
        }

        for (Map.Entry<String, List<Habit>> entry : byCategory.entrySet()) {
            String category = entry.getKey();
            List<Habit> categoryHabits = entry.getValue();
            categoryHabits.sort(Comparator.comparing(h -> h.name.toLowerCase(Locale.ROOT)));
            status.append(category).append(":\n");
            for (Habit h : categoryHabits) {
                status.append("  ").append(h.completedToday ? "[DONE]" : "[PENDING]").append(" ")
                        .append(h.name).append(" | Streak: ").append(h.currentStreak)
                        .append(" days | Total: ").append(h.totalDone).append("\n");
            }
            status.append("\n");
        }

        return status.toString();
    }

    public String trackStreak() {
        StringBuilder streaks = new StringBuilder("Habit Streak Report\n\n");

        List<Habit> sortedByStreak = new ArrayList<>(habits.values());
        sortedByStreak.sort((a, b) -> Integer.compare(b.currentStreak, a.currentStreak));

        if (sortedByStreak.isEmpty()) {
            streaks.append("No active habits. Add at least one habit to begin streak analysis.\n");
            return streaks.toString();
        }

        int idx = 1;
        for (Habit h : sortedByStreak) {
                streaks.append(idx).append(". ").append(h.name)
                    .append("\n   Current: ").append(h.currentStreak).append(" days")
                    .append(" | Best: ").append(h.longestStreak).append(" days\n\n");
            idx++;
        }

        return streaks.toString();
    }

    public String suggestHabits() {
        StringBuilder suggestions = new StringBuilder("Habit Recommendation Library\n\n");

        suggestions.append("Morning Routine:\n")
                .append("• Drink 500ml water\n")
                .append("• 5-min meditation\n")
                .append("• Stretch routine\n\n");

        suggestions.append("Workday Routine:\n")
                .append("• 10K steps\n")
                .append("• 8 glasses water\n")
                .append("• 30-min activity\n\n");

        suggestions.append("Evening Routine:\n")
                .append("• 10-min walk\n")
                .append("• Journaling (3 gratitudes)\n")
                .append("• Screen-free hour\n\n");

        suggestions.append("Weekly Routine:\n")
                .append("• Strength training\n")
                .append("• Meal prep\n")
                .append("• Social activity\n");

        return suggestions.toString();
    }

    public String automatedHabitReminders() {
        StringBuilder reminders = new StringBuilder("Daily Reminder Queue\n\n");

        // Check which habits not completed today
        int notComplete = 0;
        for (Habit h : habits.values()) {
            if (!h.completedToday) {
                notComplete++;
            }
        }

        if (notComplete == 0) {
            reminders.append("All scheduled habits are complete for today.\n");
        } else {
            reminders.append("Incomplete habits for today:\n");
            for (Habit h : habits.values()) {
                if (!h.completedToday) {
                    reminders.append("• ").append(h.name)
                            .append(" (").append(h.currentStreak).append("-day streak)\n");
                }
            }
        }

        reminders.append("\nRecommendation: complete pending habits before end-of-day cut-off.\n");
        return reminders.toString();
    }

    public String buildStreak(String habitName) {
        Habit h = habits.get(habitName);
        if (h == null) return "Habit not found";

        StringBuilder build = new StringBuilder("Streak Development Plan\n\n");
        build.append("Habit: ").append(h.name).append("\n");
        build.append("Current Streak: ").append(h.currentStreak).append(" days\n");
        build.append("Personal Record: ").append(h.longestStreak).append(" days\n");
        build.append("Total Completions: ").append(h.totalDone).append("\n\n");

        if (h.currentStreak > 0) {
            build.append("Maintain sequence integrity.\n");
            build.append("Complete tomorrow to reach: ").append(h.currentStreak + 1).append(" days\n");
        } else {
            build.append("Starting new streak cycle.\n");
            build.append("One completion = 1-day streak\n");
        }

        return build.toString();
    }
}
