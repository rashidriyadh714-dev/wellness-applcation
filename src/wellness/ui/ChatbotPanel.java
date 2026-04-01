package wellness.ui;

import wellness.service.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Chatbot Panel — Interactive AI assistant for wellness coaching,
 * personalized advice, and conversational support.
 */
public class ChatbotPanel extends JPanel {
    private final WellnessBot bot;
    private final JTextArea chatDisplay;
    private final JTextField inputField;
    private final JButton sendButton;
    private final JLabel statusLabel;
    private final JLabel modeLabel;
    private final JLabel modeHintLabel;
    private final JComboBox<String> modeSelector;
    private String lastAssistantMessage = "";

    public ChatbotPanel(WellnessBot bot) {
        this.bot = bot;
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel title = new JLabel("AI Wellness Concierge");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        title.setForeground(Color.BLACK);
        statusLabel = new JLabel("Online");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        statusLabel.setForeground(new Color(70, 70, 70));
        modeLabel = new JLabel("Mode: Executive Coach");
        modeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        modeHintLabel = new JLabel(getModeHint("Executive Coach"));
        modeHintLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        modeHintLabel.setForeground(new Color(70, 70, 70));

        JPanel rightMeta = new JPanel(new GridLayout(3, 1, 0, 1));
        rightMeta.setOpaque(false);
        rightMeta.add(statusLabel);
        rightMeta.add(modeLabel);
        rightMeta.add(modeHintLabel);
        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightMeta, BorderLayout.EAST);

        chatDisplay = new JTextArea();
        chatDisplay.setEditable(false);
        chatDisplay.setLineWrap(true);
        chatDisplay.setWrapStyleWord(true);
        chatDisplay.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        chatDisplay.setBackground(new Color(248, 248, 248));
        chatDisplay.setForeground(Color.BLACK);
        chatDisplay.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(chatDisplay);
        scrollPane.setPreferredSize(new Dimension(500, 420));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210), 1));

        chatDisplay.setText("""
            Elite Wellness AI Assistant

            Ask for elite plans in any area:
            - Sleep performance
            - Recovery and strain
            - Fitness programming
            - Stress regulation
            - Nutrition and hydration

            Tip: Use quick prompts below for a polished live demo.
            """);

            JPanel rightRail = buildRightRail();

        JPanel quickPrompts = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        quickPrompts.setOpaque(false);
        addQuickPrompt(quickPrompts, "Build my 7-day performance plan");
        addQuickPrompt(quickPrompts, "How can I improve sleep score?");
        addQuickPrompt(quickPrompts, "Give me a stress reset routine");
        addQuickPrompt(quickPrompts, "Create a hydration strategy");

        modeSelector = new JComboBox<>(new String[]{
            "Executive Coach",
            "Recovery Specialist",
            "Performance Trainer",
            "Stress & Focus Advisor"
        });
        modeSelector.setBackground(Color.WHITE);
        modeSelector.addActionListener((ActionEvent e) -> {
            String mode = String.valueOf(modeSelector.getSelectedItem());
            modeLabel.setText("Mode: " + mode);
            modeHintLabel.setText(getModeHint(mode));
            statusLabel.setText("Switched to " + mode);
            appendSystemMessage("Assistant mode changed to " + mode + ". " + getModeHint(mode));
        });

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(Color.WHITE);

        inputField = new JTextField();
        inputField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        inputField.addActionListener(e -> sendMessage());
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                new EmptyBorder(7, 10, 7, 10)
        ));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        sendButton.setBackground(Color.BLACK);
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(new EmptyBorder(8, 16, 8, 16));
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JButton suggestionsButton = new JButton("Personalized Suggestions");
        suggestionsButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        suggestionsButton.setBackground(new Color(60, 60, 60));
        suggestionsButton.setForeground(Color.WHITE);
        suggestionsButton.addActionListener(e -> showSuggestions());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(suggestionsButton);

        JButton howToUseButton = new JButton("How To Use Modes");
        howToUseButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        howToUseButton.addActionListener(e -> showModeGuide());
        buttonPanel.add(howToUseButton);

        JButton modePromptButton = new JButton("Use Mode Prompt");
        modePromptButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        modePromptButton.addActionListener(e -> {
            String mode = String.valueOf(modeSelector.getSelectedItem());
            inputField.setText(getStarterPromptForMode(mode));
            sendMessage();
        });
        buttonPanel.add(modePromptButton);

        JButton compareModesButton = new JButton("Compare All Modes");
        compareModesButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        compareModesButton.addActionListener(e -> runModeComparisonDemo());
        buttonPanel.add(compareModesButton);

        JButton guidedTourButton = new JButton("Guided Tour");
        guidedTourButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        guidedTourButton.addActionListener(e -> runGuidedTour());
        buttonPanel.add(guidedTourButton);

        JButton copyLastButton = new JButton("Copy Last Reply");
        copyLastButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        copyLastButton.addActionListener((ActionEvent e) -> {
            if (!lastAssistantMessage.isEmpty()) {
                java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(lastAssistantMessage);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            }
        });
        buttonPanel.add(copyLastButton);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(quickPrompts, BorderLayout.NORTH);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        bottomPanel.add(modeSelector, BorderLayout.WEST);

        add(topBar, BorderLayout.NORTH);
        JPanel middlePanel = new JPanel(new BorderLayout(10, 0));
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(scrollPane, BorderLayout.CENTER);
        middlePanel.add(rightRail, BorderLayout.EAST);

        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel buildRightRail() {
        JPanel rail = new JPanel(new BorderLayout(6, 6));
        rail.setBackground(new Color(248, 248, 248));
        rail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        rail.setPreferredSize(new Dimension(260, 0));

        JLabel title = new JLabel("Live Assistant Intelligence");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        JTextArea bullets = new JTextArea("""
            - Personalized advice based on profile and records
            - Recovery, strain, sleep, and stress balancing
            - Presentation-ready executive responses

            Demo Prompts:
            1) Build a 14-day recovery reset
            2) Optimize sleep for travel weeks
            3) Create a high-performance hydration protocol
            """);
        bullets.setEditable(false);
        bullets.setLineWrap(true);
        bullets.setWrapStyleWord(true);
        bullets.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        bullets.setBackground(new Color(248, 248, 248));

        rail.add(title, BorderLayout.NORTH);
        rail.add(new JScrollPane(bullets), BorderLayout.CENTER);
        return rail;
    }

    private void addQuickPrompt(JPanel parent, String prompt) {
        JButton chip = new JButton(prompt);
        chip.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        chip.setBackground(new Color(235, 235, 235));
        chip.setForeground(Color.BLACK);
        chip.setFocusPainted(false);
        chip.setBorder(new EmptyBorder(5, 10, 5, 10));
        chip.addActionListener(e -> {
            inputField.setText(prompt);
            sendMessage();
        });
        parent.add(chip);
    }

    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;

        chatDisplay.append("\nYou: " + userInput + "\n");
        statusLabel.setText("Thinking...");
        String selectedMode = String.valueOf(modeSelector.getSelectedItem());
        String response = bot.chat("[" + selectedMode + "] " + userInput);
        lastAssistantMessage = response;
        chatDisplay.append("\nAssistant: " + response + "\n");
        chatDisplay.append("\n" + "-".repeat(56) + "\n");

        inputField.setText("");
        chatDisplay.setCaretPosition(chatDisplay.getDocument().getLength());
        statusLabel.setText("Online");
    }

    private void appendSystemMessage(String message) {
        chatDisplay.append("\nSystem: " + message + "\n");
        chatDisplay.append("\n" + "-".repeat(56) + "\n");
        chatDisplay.setCaretPosition(chatDisplay.getDocument().getLength());
    }

    private String getModeHint(String mode) {
        if ("Recovery Specialist".equals(mode)) {
            return "Best for low energy days and recovery plans.";
        }
        if ("Performance Trainer".equals(mode)) {
            return "Best for workout progression and high output.";
        }
        if ("Stress & Focus Advisor".equals(mode)) {
            return "Best for stress control and focus routines.";
        }
        return "Best for strategic daily planning and consistency.";
    }

    private String getStarterPromptForMode(String mode) {
        if ("Recovery Specialist".equals(mode)) {
            return "Build a 7-day recovery reset with sleep and low-strain activity.";
        }
        if ("Performance Trainer".equals(mode)) {
            return "Build a 7-day performance program with progressive overload.";
        }
        if ("Stress & Focus Advisor".equals(mode)) {
            return "Create a daily stress and deep-focus routine for workdays.";
        }
        return "Create my executive weekly wellness operating plan.";
    }

    private void showModeGuide() {
        JTextArea guide = new JTextArea("""
            How To Use AI Modes

            1) Executive Coach
            Use this for strategic routines, weekly planning, and consistency.

            2) Recovery Specialist
            Use this when sleep is low, stress is high, or readiness drops.

            3) Performance Trainer
            Use this for workout blocks, progression, and athletic goals.

            4) Stress & Focus Advisor
            Use this for focus cycles, mental recovery, and burnout prevention.

            Live Demo Flow:
            - Pick mode
            - Click 'Use Mode Prompt'
            - Ask for a 7-day plan
            - Ask 'what should I do today?' for actionable output
            """);
        guide.setEditable(false);
        guide.setLineWrap(true);
        guide.setWrapStyleWord(true);
        guide.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        guide.setBackground(Color.WHITE);
        guide.setBorder(new EmptyBorder(10, 10, 10, 10));
        JOptionPane.showMessageDialog(this, new JScrollPane(guide), "AI Mode Guide", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSuggestions() {
        chatDisplay.setText("AI Suggestions for You:\n\n");
        for (String suggestion : bot.suggest()) {
            chatDisplay.append(suggestion + "\n");
        }
        chatDisplay.append("\nAsk me how to implement these!\n");
        statusLabel.setText("Online");
    }

    private void runModeComparisonDemo() {
        String prompt = "what should I do today";
        String[] modes = {
                "Executive Coach",
                "Recovery Specialist",
                "Performance Trainer",
                "Stress & Focus Advisor"
        };
        chatDisplay.append("\nSystem: Running mode comparison for prompt: '" + prompt + "'\n");
        chatDisplay.append("\n" + "-".repeat(56) + "\n");
        for (String mode : modes) {
            String response = bot.chat("[" + mode + "] " + prompt);
            chatDisplay.append("\n" + mode + ":\n" + response + "\n");
            chatDisplay.append("\n" + "-".repeat(56) + "\n");
            lastAssistantMessage = response;
        }
        chatDisplay.setCaretPosition(chatDisplay.getDocument().getLength());
        statusLabel.setText("Online");
    }

    private void runGuidedTour() {
        chatDisplay.append("\nSystem: Starting AI assistant guided tour.\n");
        chatDisplay.append("\n1) Pick a mode from dropdown.\n");
        chatDisplay.append("2) Click 'Use Mode Prompt'.\n");
        chatDisplay.append("3) Ask follow-up: 'build my 7-day plan'.\n");
        chatDisplay.append("4) Click 'Compare All Modes' to show mode differences live.\n");
        chatDisplay.append("5) Click 'Copy Last Reply' to paste into your presentation notes.\n");
        chatDisplay.append("\n" + "-".repeat(56) + "\n");
        chatDisplay.setCaretPosition(chatDisplay.getDocument().getLength());
    }
}
