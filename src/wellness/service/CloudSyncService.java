package wellness.service;

import wellness.model.AbstractRecord;
import wellness.model.WellnessProfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CloudSyncService {
    private final Path remoteDir;
    private final Path remoteProfile;
    private final Path remoteRecords;

    public CloudSyncService() {
        this.remoteDir = Paths.get("data").resolve("remote");
        this.remoteProfile = remoteDir.resolve("profile.txt");
        this.remoteRecords = remoteDir.resolve("records.txt");
        try {
            Files.createDirectories(remoteDir);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create remote directory.", e);
        }
    }

    public void pushProfile(WellnessProfile profile) {
        try (BufferedWriter writer = Files.newBufferedWriter(remoteProfile, StandardCharsets.UTF_8)) {
            if (profile != null) {
                writer.write(profile.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to push profile.", e);
        }
    }

    public WellnessProfile pullProfile() {
        if (!Files.exists(remoteProfile)) return null;
        try (BufferedReader reader = Files.newBufferedReader(remoteProfile, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) return null;
            return WellnessProfile.fromDataString(line);
        } catch (IOException e) {
            throw new RuntimeException("Failed to pull profile.", e);
        }
    }

    public void pushRecords(List<AbstractRecord> records) {
        try (BufferedWriter writer = Files.newBufferedWriter(remoteRecords, StandardCharsets.UTF_8)) {
            if (records == null) return;
            for (AbstractRecord r : records) {
                writer.write(r.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to push records.", e);
        }
    }

    public List<AbstractRecord> pullRecords() {
        List<AbstractRecord> out = new ArrayList<>();
        if (!Files.exists(remoteRecords)) return out;
        try (BufferedReader reader = Files.newBufferedReader(remoteRecords, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) out.add(AbstractRecord.fromDataString(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to pull records.", e);
        }
        return out;
    }
}
