package wellness.model;

import java.time.Instant;
import java.time.format.DateTimeParseException;

public class UserAccount {
    private String userId;
    private String email;
    private String passwordHash;
    private Instant createdAt;

    public UserAccount(String userId, String email, String passwordHash) {
        this.userId = requireUserId(userId);
        this.email = requireEmail(email);
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

    private static String requireUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId required");
        }
        return userId.trim();
    }

    private static String requireEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("email required");
        }
        return email.trim().toLowerCase();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = requireUserId(userId);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = requireEmail(email);
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String toDataString() {
        long epoch = createdAt == null ? 0L : createdAt.getEpochSecond();
        return String.join("|", userId, email, passwordHash, String.valueOf(epoch));
    }

    public static UserAccount fromDataString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 4) throw new IllegalArgumentException("Invalid user data");
        UserAccount u = new UserAccount(parts[0], parts[1], parts[2]);
        try {
            long epoch = Long.parseLong(parts[3]);
            if (epoch > 0) u.setCreatedAt(Instant.ofEpochSecond(epoch));
        } catch (NumberFormatException | DateTimeParseException ignored) {}
        return u;
    }
}
