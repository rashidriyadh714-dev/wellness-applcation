package wellness.service;

/**
 * OnboardingService - Manages user onboarding experience
 * Guides new users through setup, wearable connections, and preference configuration
 */
public class OnboardingService {
    
    public enum OnboardingStep {
        WELCOME,
        ACCOUNT_SETUP,
        HEALTH_GOALS,
        WEARABLE_CONNECT,
        PRIVACY_CONSENT,
        NOTIFICATION_PREFERENCES,
        COMPLETION
    }
    
    private final String userId;
    private OnboardingStep currentStep;
    private final boolean[] completedSteps;
    
    public OnboardingService(String userId) {
        this.userId = userId;
        this.currentStep = OnboardingStep.WELCOME;
        this.completedSteps = new boolean[OnboardingStep.values().length];
    }
    
    /**
     * Get current onboarding step
     */
    public OnboardingStep getCurrentStep() {
        return currentStep;
    }
    
    /**
     * Move to next onboarding step
     */
    public OnboardingStep nextStep() {
        int index = currentStep.ordinal();
        if (index < OnboardingStep.values().length - 1) {
            currentStep = OnboardingStep.values()[index + 1];
            completedSteps[index] = true;
            return currentStep;
        }
        return OnboardingStep.COMPLETION;
    }
    
    /**
     * Collect health goals from user
     */
    public HealthGoals collectHealthGoals(String goalType, int targetValue) {
        return new HealthGoals(goalType, targetValue);
    }
    
    /**
     * Get wearable connection instructions
     */
    public String getWearableInstructions(String wearableType) {
        return switch (wearableType.toLowerCase()) {
            case "apple" -> "Open Apple Health settings > Share > WellnessApp > Toggle all metrics";
            case "fitbit" -> "Open Fitbit app > Settings > Connected Services > Connect WellnessApp";
            case "google" -> "Go to myaccount.google.com > Security > Connected apps > Authorize WellnessApp";
            default -> "Instructions not available for " + wearableType;
        };
    }
    
    /**
     * Record user consent for privacy
     */
    public void recordPrivacyConsent(boolean termsAccepted, boolean privacyAccepted, boolean analyticsAllowed) {
        if (termsAccepted && privacyAccepted) {
            System.out.println("Privacy consent recorded for user: " + userId);
            System.out.println("Analytics allowed: " + analyticsAllowed);
        }
    }
    
    /**
     * Configure notification preferences
     */
    public void setNotificationPreferences(boolean dailySummary, boolean alerts, boolean achievements) {
        System.out.println("Notification preferences updated:");
        System.out.println("  Daily summary: " + dailySummary);
        System.out.println("  Alerts: " + alerts);
        System.out.println("  Achievements: " + achievements);
    }
    
    /**
     * Mark onboarding as complete
     */
    public void completeOnboarding() {
        currentStep = OnboardingStep.COMPLETION;
        System.out.println("Onboarding completed for user: " + userId);
    }
    
    /**
     * Get onboarding progress percentage
     */
    public int getProgressPercentage() {
        int completedCount = 0;
        for (boolean completed : completedSteps) {
            if (completed) completedCount++;
        }
        return (completedCount * 100) / OnboardingStep.values().length;
    }
    
    /**
     * HealthGoals inner class
     */
    public static class HealthGoals {
        private final String goalType;
        private final int targetValue;
        
        public HealthGoals(String goalType, int targetValue) {
            this.goalType = goalType;
            this.targetValue = targetValue;
        }
        
        public String getGoalType() { return goalType; }
        public int getTargetValue() { return targetValue; }
        
        @Override
        public String toString() {
            return "Goal: " + goalType + ", Target: " + targetValue;
        }
    }
}
