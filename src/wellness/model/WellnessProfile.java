package wellness.model;

public class WellnessProfile {
    private String userId;
    private String fullName;
    private int age;
    private double heightCm;
    private double weightKg;
    private String goal;

    public WellnessProfile(String userId, String fullName, int age, double heightCm, double weightKg, String goal) {
        this.userId = requireUserId(userId);
        this.fullName = requireFullName(fullName);
        this.age = requireAge(age);
        this.heightCm = requireHeightCm(heightCm);
        this.weightKg = requireWeightKg(weightKg);
        this.goal = sanitizeGoal(goal);
    }

    private static String requireUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }
        return userId.trim();
    }

    private static String requireFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        return fullName.trim();
    }

    private static int requireAge(int age) {
        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120.");
        }
        return age;
    }

    private static double requireHeightCm(double heightCm) {
        if (heightCm < 50 || heightCm > 250) {
            throw new IllegalArgumentException("Height must be between 50 and 250 cm.");
        }
        return heightCm;
    }

    private static double requireWeightKg(double weightKg) {
        if (weightKg < 10 || weightKg > 400) {
            throw new IllegalArgumentException("Weight must be between 10 and 400 kg.");
        }
        return weightKg;
    }

    private static String sanitizeGoal(String goal) {
        return goal == null ? "General Wellness" : goal.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = requireUserId(userId);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = requireFullName(fullName);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = requireAge(age);
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = requireHeightCm(heightCm);
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = requireWeightKg(weightKg);
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = sanitizeGoal(goal);
    }

    public double calculateBMI() {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    public String toDataString() {
        return String.join("|", userId, fullName, String.valueOf(age), String.valueOf(heightCm), String.valueOf(weightKg), goal);
    }

    public static WellnessProfile fromDataString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid profile data.");
        }
        return new WellnessProfile(parts[0], parts[1], Integer.parseInt(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), parts[5]);
    }
}
