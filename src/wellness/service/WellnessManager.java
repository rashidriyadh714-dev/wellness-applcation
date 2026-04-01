package wellness.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wellness.model.AbstractRecord;
import wellness.model.ActivityRecord;
import wellness.model.AlertLevel;
import wellness.model.HealthRecord;
import wellness.model.WellnessProfile;

public class WellnessManager {
    private WellnessProfile profile;
    private final List<AbstractRecord> records;
    private final Map<String, AbstractRecord> recordMap;
    private final WellnessAnalyzer analyzer;
    private final DataStore dataStore;

    public WellnessManager() {
        this.records = new ArrayList<>();
        this.recordMap = new HashMap<>();
        this.analyzer = new WellnessAnalyzer();
        this.dataStore = new DataStore();
    }

    public void load() {
        profile = dataStore.loadProfile();
        records.clear();
        recordMap.clear();
        for (AbstractRecord record : dataStore.loadRecords()) {
            records.add(record);
            recordMap.put(record.getRecordId(), record);
        }
    }

    public void saveAll() {
        dataStore.saveProfile(profile);
        dataStore.saveRecords(records);
    }

    public WellnessProfile getProfile() {
        return profile;
    }

    public void setProfile(WellnessProfile profile) {
        this.profile = profile;
        saveAll();
    }

    public List<AbstractRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public Map<String, Integer> getRecordTypeCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("Health", 0);
        counts.put("Activity", 0);
        for (AbstractRecord record : records) {
            String type = record.getRecordType();
            counts.put(type, counts.get(type) + 1);
        }
        return counts;
    }

    public void addHealthRecord(String recordId, String dateText, String notes, double sleepHours,
                                int waterIntakeMl, int moodScore, int stressScore, int restingHeartRate) {
        LocalDate date = parseDate(dateText);
        ensureUniqueRecordId(recordId);
        HealthRecord record = new HealthRecord(recordId, date, notes, sleepHours, waterIntakeMl, moodScore, stressScore, restingHeartRate);
        addRecord(record);
    }

    public void addActivityRecord(String recordId, String dateText, String notes, int steps,
                                  int activeMinutes, double caloriesBurned, int screenTimeMinutes) {
        LocalDate date = parseDate(dateText);
        ensureUniqueRecordId(recordId);
        ActivityRecord record = new ActivityRecord(recordId, date, notes, steps, activeMinutes, caloriesBurned, screenTimeMinutes);
        addRecord(record);
    }

    public void deleteRecord(String recordId) {
        AbstractRecord existing = recordMap.remove(recordId);
        if (existing != null) {
            records.remove(existing);
            saveAll();
        }
    }

    private void addRecord(AbstractRecord record) {
        records.add(record);
        recordMap.put(record.getRecordId(), record);
        saveAll();
    }

    private void ensureUniqueRecordId(String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException("Record ID is required.");
        }
        if (recordMap.containsKey(recordId.trim())) {
            throw new IllegalArgumentException("Record ID already exists. Use a unique ID.");
        }
    }

    private LocalDate parseDate(String dateText) {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");
        }
    }

    public double getOverallScore() {
        return analyzer.calculateOverallScore(records);
    }

    public AlertLevel getAlertLevel() {
        return analyzer.classifyRisk(getOverallScore());
    }

    public String getRecommendation() {
        return analyzer.buildRecommendation(profile, records);
    }

    public String getSummary() {
        int totalRecords = records.size();
        double score = getOverallScore();
        AlertLevel level = getAlertLevel();
        Map<String, Integer> counts = getRecordTypeCounts();
        return "Profile: " + (profile == null ? "Not created" : profile.getFullName()) +
            "\nTotal records: " + totalRecords +
            "\nHealth records: " + counts.get("Health") +
            "\nActivity records: " + counts.get("Activity") +
            "\nWellness score: " + String.format("%.1f", score) + "/100" +
            "\nRisk level: " + level.getLabel() +
            "\nRecommendation: " + getRecommendation();
    }
}
