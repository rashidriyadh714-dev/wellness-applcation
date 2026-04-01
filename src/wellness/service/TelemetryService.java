package wellness.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Very small telemetry service for local event collection.
 * Events are appended to `data/telemetry.log` as timestamped lines.
 */
public class TelemetryService {
    private final Path telemetryFile;
    private final ReentrantLock lock = new ReentrantLock(true);

    public TelemetryService() {
        this.telemetryFile = Paths.get("data").resolve("telemetry.log");
        try {
            Files.createDirectories(telemetryFile.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Unable to create telemetry directory.", e);
        }
    }

    public void logEvent(String eventName, Map<String, String> properties) {
        String line = buildLine(eventName, properties);
        lock.lock();
        try (BufferedWriter writer = Files.newBufferedWriter(telemetryFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write telemetry.", e);
        } finally {
            lock.unlock();
        }
    }

    private String buildLine(String eventName, Map<String, String> properties) {
        StringBuilder sb = new StringBuilder();
        sb.append(Instant.now().toString()).append(" | ").append(eventName);
        if (properties != null && !properties.isEmpty()) {
            sb.append(" | ");
            boolean first = true;
            for (Map.Entry<String, String> e : properties.entrySet()) {
                if (!first) sb.append(",");
                sb.append(e.getKey()).append("=").append(e.getValue());
                first = false;
            }
        }
        return sb.toString();
    }
}
