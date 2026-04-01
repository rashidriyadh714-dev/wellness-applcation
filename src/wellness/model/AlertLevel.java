package wellness.model;

public enum AlertLevel {
    LOW_RISK("Low Risk"),
    MODERATE_RISK("Moderate Risk"),
    HIGH_RISK("High Risk");

    private final String label;

    AlertLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
