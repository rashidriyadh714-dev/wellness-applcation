package wellness.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class AbstractRecord implements ScoreContributor {
    private String recordId;
    private LocalDate date;
    private String notes;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    protected AbstractRecord(String recordId, LocalDate date, String notes) {
        this.recordId = requireRecordId(recordId);
        this.date = requireDate(date);
        this.notes = sanitizeNotes(notes);
    }

    private static String requireRecordId(String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException("Record ID cannot be empty.");
        }
        return recordId.trim();
    }

    private static LocalDate requireDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        return date;
    }

    private static String sanitizeNotes(String notes) {
        return notes == null ? "" : notes.trim().replace("|", "/");
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = requireRecordId(recordId);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = requireDate(date);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = sanitizeNotes(notes);
    }

    public abstract String getRecordType();

    public abstract String toDataString();

    public static AbstractRecord fromDataString(String line) {
        String[] parts = line.split("\\|");
        String type = parts[0];
        if ("HEALTH".equalsIgnoreCase(type)) {
            return HealthRecord.fromData(parts);
        } else if ("ACTIVITY".equalsIgnoreCase(type)) {
            return ActivityRecord.fromData(parts);
        }
        throw new IllegalArgumentException("Unknown record type: " + type);
    }
}
