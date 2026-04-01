package wellness.model;

import java.time.LocalDate;

public class HealthRecord extends AbstractRecord {
    private double sleepHours;
    private int waterIntakeMl;
    private int moodScore;
    private int stressScore;
    private int restingHeartRate;

    public HealthRecord(String recordId, LocalDate date, String notes, double sleepHours, int waterIntakeMl,
                        int moodScore, int stressScore, int restingHeartRate) {
        super(recordId, date, notes);
        this.sleepHours = requireSleepHours(sleepHours);
        this.waterIntakeMl = requireWaterIntakeMl(waterIntakeMl);
        this.moodScore = requireMoodScore(moodScore);
        this.stressScore = requireStressScore(stressScore);
        this.restingHeartRate = requireRestingHeartRate(restingHeartRate);
    }

    private static double requireSleepHours(double sleepHours) {
        if (sleepHours < 0 || sleepHours > 24) {
            throw new IllegalArgumentException("Sleep hours must be between 0 and 24.");
        }
        return sleepHours;
    }

    private static int requireWaterIntakeMl(int waterIntakeMl) {
        if (waterIntakeMl < 0 || waterIntakeMl > 10000) {
            throw new IllegalArgumentException("Water intake must be between 0 and 10000 ml.");
        }
        return waterIntakeMl;
    }

    private static int requireMoodScore(int moodScore) {
        if (moodScore < 1 || moodScore > 10) {
            throw new IllegalArgumentException("Mood score must be between 1 and 10.");
        }
        return moodScore;
    }

    private static int requireStressScore(int stressScore) {
        if (stressScore < 1 || stressScore > 10) {
            throw new IllegalArgumentException("Stress score must be between 1 and 10.");
        }
        return stressScore;
    }

    private static int requireRestingHeartRate(int restingHeartRate) {
        if (restingHeartRate < 30 || restingHeartRate > 220) {
            throw new IllegalArgumentException("Resting heart rate must be between 30 and 220 bpm.");
        }
        return restingHeartRate;
    }

    public double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(double sleepHours) {
        this.sleepHours = requireSleepHours(sleepHours);
    }

    public int getWaterIntakeMl() {
        return waterIntakeMl;
    }

    public void setWaterIntakeMl(int waterIntakeMl) {
        this.waterIntakeMl = requireWaterIntakeMl(waterIntakeMl);
    }

    public int getMoodScore() {
        return moodScore;
    }

    public void setMoodScore(int moodScore) {
        this.moodScore = requireMoodScore(moodScore);
    }

    public int getStressScore() {
        return stressScore;
    }

    public void setStressScore(int stressScore) {
        this.stressScore = requireStressScore(stressScore);
    }

    public int getRestingHeartRate() {
        return restingHeartRate;
    }

    public void setRestingHeartRate(int restingHeartRate) {
        this.restingHeartRate = requireRestingHeartRate(restingHeartRate);
    }

    @Override
    public String getRecordType() {
        return "Health";
    }

    @Override
    public double calculateImpactScore() {
        double sleepScore = Math.max(0, 100 - Math.abs(8 - sleepHours) * 12.5);
        double waterScore = Math.min(100, (waterIntakeMl / 2000.0) * 100.0);
        double moodNormalized = moodScore * 10.0;
        double stressNormalized = 110.0 - (stressScore * 10.0);
        double heartRateScore = restingHeartRate >= 55 && restingHeartRate <= 85 ? 100 : Math.max(40, 100 - Math.abs(70 - restingHeartRate) * 1.8);

        return (sleepScore * 0.30) + (waterScore * 0.15) + (moodNormalized * 0.25) + (stressNormalized * 0.20) + (heartRateScore * 0.10);
    }

    @Override
    public String toDataString() {
        return String.join("|",
                "HEALTH",
                getRecordId(),
                getDate().format(FORMATTER),
                getNotes(),
                String.valueOf(sleepHours),
                String.valueOf(waterIntakeMl),
                String.valueOf(moodScore),
                String.valueOf(stressScore),
                String.valueOf(restingHeartRate));
    }

    public static HealthRecord fromData(String[] parts) {
        if (parts.length < 9) {
            throw new IllegalArgumentException("Invalid health record data.");
        }
        return new HealthRecord(
                parts[1],
                LocalDate.parse(parts[2], FORMATTER),
                parts[3],
                Double.parseDouble(parts[4]),
                Integer.parseInt(parts[5]),
                Integer.parseInt(parts[6]),
                Integer.parseInt(parts[7]),
                Integer.parseInt(parts[8])
        );
    }
}
