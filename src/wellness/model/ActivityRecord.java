package wellness.model;

import java.time.LocalDate;

public class ActivityRecord extends AbstractRecord {
    private int steps;
    private int activeMinutes;
    private double caloriesBurned;
    private int screenTimeMinutes;

    public ActivityRecord(String recordId, LocalDate date, String notes, int steps, int activeMinutes,
                          double caloriesBurned, int screenTimeMinutes) {
        super(recordId, date, notes);
        this.steps = requireSteps(steps);
        this.activeMinutes = requireActiveMinutes(activeMinutes);
        this.caloriesBurned = requireCaloriesBurned(caloriesBurned);
        this.screenTimeMinutes = requireScreenTimeMinutes(screenTimeMinutes);
    }

    private static int requireSteps(int steps) {
        if (steps < 0 || steps > 100000) {
            throw new IllegalArgumentException("Steps must be between 0 and 100000.");
        }
        return steps;
    }

    private static int requireActiveMinutes(int activeMinutes) {
        if (activeMinutes < 0 || activeMinutes > 1440) {
            throw new IllegalArgumentException("Active minutes must be between 0 and 1440.");
        }
        return activeMinutes;
    }

    private static double requireCaloriesBurned(double caloriesBurned) {
        if (caloriesBurned < 0 || caloriesBurned > 10000) {
            throw new IllegalArgumentException("Calories burned must be between 0 and 10000.");
        }
        return caloriesBurned;
    }

    private static int requireScreenTimeMinutes(int screenTimeMinutes) {
        if (screenTimeMinutes < 0 || screenTimeMinutes > 1440) {
            throw new IllegalArgumentException("Screen time must be between 0 and 1440.");
        }
        return screenTimeMinutes;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = requireSteps(steps);
    }

    public int getActiveMinutes() {
        return activeMinutes;
    }

    public void setActiveMinutes(int activeMinutes) {
        this.activeMinutes = requireActiveMinutes(activeMinutes);
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = requireCaloriesBurned(caloriesBurned);
    }

    public int getScreenTimeMinutes() {
        return screenTimeMinutes;
    }

    public void setScreenTimeMinutes(int screenTimeMinutes) {
        this.screenTimeMinutes = requireScreenTimeMinutes(screenTimeMinutes);
    }

    @Override
    public String getRecordType() {
        return "Activity";
    }

    @Override
    public double calculateImpactScore() {
        double stepScore = Math.min(100, (steps / 10000.0) * 100.0);
        double activityScore = Math.min(100, (activeMinutes / 60.0) * 100.0);
        double calorieScore = Math.min(100, (caloriesBurned / 500.0) * 100.0);
        double screenPenaltyScore = Math.max(20, 100 - Math.max(0, screenTimeMinutes - 120) * 0.25);

        return (stepScore * 0.40) + (activityScore * 0.30) + (calorieScore * 0.15) + (screenPenaltyScore * 0.15);
    }

    @Override
    public String toDataString() {
        return String.join("|",
                "ACTIVITY",
                getRecordId(),
                getDate().format(FORMATTER),
                getNotes(),
                String.valueOf(steps),
                String.valueOf(activeMinutes),
                String.valueOf(caloriesBurned),
                String.valueOf(screenTimeMinutes));
    }

    public static ActivityRecord fromData(String[] parts) {
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid activity record data.");
        }
        return new ActivityRecord(
                parts[1],
                LocalDate.parse(parts[2], FORMATTER),
                parts[3],
                Integer.parseInt(parts[4]),
                Integer.parseInt(parts[5]),
                Double.parseDouble(parts[6]),
                Integer.parseInt(parts[7])
        );
    }
}
