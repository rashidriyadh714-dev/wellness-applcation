package wellness.service;

import wellness.model.UserAccount;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthService {
    private final Path usersFile;
    private final Path dataDir;

    public AuthService() {
        this.dataDir = Paths.get("data");
        this.usersFile = dataDir.resolve("users.txt");
    }

    private void ensureDataDirectory() {
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create data directory.", e);
        }
    }

    public UserAccount createUser(String email, String password) {
        ensureDataDirectory();
        List<UserAccount> existing = loadAllUsers();
        for (UserAccount u : existing) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        String userId = UUID.randomUUID().toString();
        String hash = hashPassword(email, password);
        UserAccount user = new UserAccount(userId, email, hash);
        appendUser(user);
        return user;
    }

    public UserAccount authenticate(String email, String password) {
        List<UserAccount> existing = loadAllUsers();
        String hash = hashPassword(email, password);
        for (UserAccount u : existing) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPasswordHash().equals(hash)) {
                return u;
            }
        }
        return null;
    }

    private void appendUser(UserAccount user) {
        ensureDataDirectory();
        try (BufferedWriter writer = Files.newBufferedWriter(usersFile, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND)) {
            writer.write(user.toDataString());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user.", e);
        }
    }

    public List<UserAccount> loadAllUsers() {
        ensureDataDirectory();
        List<UserAccount> users = new ArrayList<>();
        if (!Files.exists(usersFile)) return users;
        try (BufferedReader reader = Files.newBufferedReader(usersFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) users.add(UserAccount.fromDataString(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users.", e);
        }
        return users;
    }

    private String hashPassword(String email, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String salted = (email == null ? "" : email.toLowerCase()) + ":" + (password == null ? "" : password);
            byte[] digest = md.digest(salted.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Missing hashing algorithm.", e);
        }
    }
}
