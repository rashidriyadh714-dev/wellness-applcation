package wellness.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import wellness.model.AbstractRecord;
import wellness.model.WellnessProfile;

public class DataStore {
    private final Path dataDir;
    private final Path profileFile;
    private final Path recordsFile;

    public DataStore() {
        this.dataDir = Paths.get("data");
        this.profileFile = dataDir.resolve("profile.txt");
        this.recordsFile = dataDir.resolve("records.txt");
    }

    public void ensureDataDirectory() {
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create data directory.", e);
        }
    }

    public void saveProfile(WellnessProfile profile) {
        ensureDataDirectory();
        try (BufferedWriter writer = Files.newBufferedWriter(profileFile, StandardCharsets.UTF_8)) {
            if (profile != null) {
                writer.write(profile.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile.", e);
        }
    }

    public WellnessProfile loadProfile() {
        ensureDataDirectory();
        if (!Files.exists(profileFile)) {
            return null;
        }
        try (BufferedReader reader = Files.newBufferedReader(profileFile, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                return null;
            }
            return WellnessProfile.fromDataString(line);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load profile.", e);
        }
    }

    public void saveRecords(List<AbstractRecord> records) {
        ensureDataDirectory();
        try (BufferedWriter writer = Files.newBufferedWriter(recordsFile, StandardCharsets.UTF_8)) {
            for (AbstractRecord record : records) {
                writer.write(record.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save records.", e);
        }
    }

    public List<AbstractRecord> loadRecords() {
        ensureDataDirectory();
        List<AbstractRecord> records = new ArrayList<>();
        if (!Files.exists(recordsFile)) {
            return records;
        }
        try (BufferedReader reader = Files.newBufferedReader(recordsFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(AbstractRecord.fromDataString(line));
                }
            }
            return records;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load records.", e);
        }
    }
}
