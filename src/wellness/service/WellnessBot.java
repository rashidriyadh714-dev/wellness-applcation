package wellness.service;

import wellness.model.*;
import java.util.*;

/**
 * AI Wellness Chatbot — Provides personalized health advice, coaching,
 * and conversational support using rule-based patterns and ML insights.
 */
public class WellnessBot {
    private static final String MODE_EXECUTIVE = "Executive Coach";
    private static final String MODE_RECOVERY = "Recovery Specialist";
    private static final String MODE_PERFORMANCE = "Performance Trainer";
    private static final String MODE_STRESS = "Stress & Focus Advisor";

    private final WellnessManager manager;
    private final Queue<String> conversationHistory = new LinkedList<>();
    private final Random rand = new Random();

    public WellnessBot(WellnessManager manager) {
        this.manager = manager;
    }

    public String chat(String userMessage) {
        conversationHistory.offer(userMessage);
        if (conversationHistory.size() > 10) {
            conversationHistory.poll();
        }

        String mode = extractMode(userMessage);
        String cleanMessage = stripMode(userMessage).trim();
        String lower = cleanMessage.toLowerCase();

        // Greetings
        if (lower.contains("hello") || lower.contains("hi")) {
            return modeHeader(mode) + "👋 Hi there! I can coach sleep, recovery, strain, stress, and nutrition. " +
                    "Tell me your goal and I will produce a mode-specific plan.";
        }

        if (lower.contains("how to use") || lower.contains("mode") || lower.contains("help")) {
            return modeHeader(mode) +
                "Mode guide:\n" +
                "• Executive Coach: weekly strategy and routines\n" +
                "• Recovery Specialist: sleep and recovery stabilization\n" +
                "• Performance Trainer: training progression and output\n" +
                "• Stress & Focus Advisor: focus, calm, and decompression\n" +
                "Try: 'what should I do today?' for a precise daily plan.";
        }

        // Sleep advice
        if (lower.contains("sleep") || lower.contains("tired")) {
            return modeHeader(mode) + sleepResponse(mode);
        }

        // Activity advice
        if (lower.contains("exercise") || lower.contains("workout") || lower.contains("activity")) {
            return modeHeader(mode) + workoutResponse(mode);
        }

        // Stress advice
        if (lower.contains("stress") || lower.contains("anxious") || lower.contains("calm")) {
            return modeHeader(mode) + stressResponse(mode);
        }

        // Nutrition
        if (lower.contains("eat") || lower.contains("food") || lower.contains("diet") || lower.contains("nutrition")) {
            return modeHeader(mode) + nutritionResponse(mode);
        }

        // Hydration
        if (lower.contains("water") || lower.contains("hydrate")) {
            return modeHeader(mode) + hydrationResponse(mode);
        }

        // Heart health
        if (lower.contains("heart") || lower.contains("heart rate") || lower.contains("cardio")) {
            return """
                ❤️ Heart health is essential!
                • Normal resting HR: 60-100 bpm
                • Athletes: 40-60 bpm
                • Add cardio 150 min/week
                • Monitor for irregularities
                • Manage blood pressure & cholesterol
                Your current heart rate data available in records!
                """;
        }

        // Stats
        if (lower.contains("score") || lower.contains("how am i") || lower.contains("status")) {
            WellnessProfile profile = manager.getProfile();
            double score = manager.getOverallScore();
            double bmi = profile.calculateBMI();
                return """
                    📊 Your Wellness Status:
                    • Wellness Score: %.1f/100
                    • Profile: %s (%d years old)
                    • BMI: %.1f
                    • Goal: %s
                    • Recent focus: Sleep & Activity
                    Keep up the good work!
                    """.formatted(score, profile.getFullName(), profile.getAge(), bmi, profile.getGoal());
        }

        if (lower.contains("plan") || lower.contains("routine") || lower.contains("roadmap")) {
            return modeHeader(mode) + buildPlan(mode);
        }

        if ((lower.contains("today") && (lower.contains("do") || lower.contains("plan"))) || lower.contains("what should i do today")) {
            return modeHeader(mode) + buildTodayPlan(mode);
        }

        // Goal setting
        if (lower.contains("goal") || lower.contains("target")) {
                return """
                    🎯 Setting goals helps!
                    Popular wellness goals:
                    • Lose weight: -1 lb/week safely
                    • Increase fitness: +10K steps/day
                    • Better sleep: 8h nightly
                    • Reduce stress: 10 min meditation
                    • Build strength: 3x/week workouts
                    Which goal excites you?
                    """;
        }

        // General motivation
        if (lower.contains("motivat") || lower.contains("encourage")) {
            String[] motivations = {
                "🌟 You're doing great! Keep pushing forward!",
                "💯 Progress over perfection — keep going!",
                "🚀 Every small step counts toward your goals!",
                "🏆 You've got this! Consistency is key!",
                "⭐ Be proud of your wellness journey!"
            };
            return motivations[rand.nextInt(motivations.length)];
        }

        // Default helpful response
        return modeHeader(mode) + "🤖 I can help with:\n" +
                "• Sleep & rest optimization\n" +
                "• Fitness & workout planning\n" +
                "• Stress & mindfulness\n" +
                "• Nutrition & hydration\n" +
                "• Heart health monitoring\n" +
                "• Goal setting & tracking\n" +
                "Try: 'Build my 7-day plan' or 'Check my recovery status'.";
    }

    private String extractMode(String message) {
        if (message == null) {
            return MODE_EXECUTIVE;
        }
        int start = message.indexOf('[');
        int end = message.indexOf(']');
        if (start == 0 && end > 1) {
            String mode = message.substring(1, end).trim();
            if (MODE_RECOVERY.equals(mode) || MODE_PERFORMANCE.equals(mode) || MODE_STRESS.equals(mode)) {
                return mode;
            }
        }
        return MODE_EXECUTIVE;
    }

    private String stripMode(String message) {
        if (message == null) {
            return "";
        }
        int start = message.indexOf('[');
        int end = message.indexOf(']');
        if (start == 0 && end > 1 && end + 1 < message.length()) {
            return message.substring(end + 1);
        }
        return message;
    }

    private String modeHeader(String mode) {
        return "[" + mode + "] ";
    }

    private String sleepResponse(String mode) {
        if (MODE_RECOVERY.equals(mode)) {
            return """
                😴 Recovery sleep protocol:
                • Target 8h sleep window for 7 days
                • Keep bedtime/wake time within 30 minutes
                • Add a 20-minute wind-down block
                • Keep room cool and fully dark
                • Reduce late caffeine to protect deep sleep
                """;
        }
        if (MODE_PERFORMANCE.equals(mode)) {
            return """
                ⚡ Performance sleep strategy:
                • Lock 7.5-8.5h for cognitive and physical output
                • Use consistent wake time to stabilize rhythm
                • Last intense workout at least 3h before bed
                • Track morning readiness and adjust strain
                """;
        }
        if (MODE_STRESS.equals(mode)) {
            return """
                🧘 Calm-sleep reset:
                • 10 minutes breathing before bed (4-7-8)
                • No work/news 60 minutes pre-sleep
                • Journal 3 tasks for tomorrow to unload stress
                • Keep sleep cue ritual identical each night
                """;
        }
        return """
            📈 Executive sleep guidance:
            • Protect 7-9 hours as a non-negotiable meeting
            • Set a fixed shutdown time for screens
            • Keep bedroom dark, quiet, and cool
            • Review sleep trend weekly and rebalance workload
            """;
    }

    private String workoutResponse(String mode) {
        if (MODE_RECOVERY.equals(mode)) {
            return """
                💚 Recovery-first training:
                • Zone-2 cardio 25-35 min
                • Mobility 10-15 min daily
                • 2 lighter strength days this week
                • 1 complete rest day
                """;
        }
        if (MODE_PERFORMANCE.equals(mode)) {
            return """
                🏋️ Performance block:
                • 3 strength sessions (compound focus)
                • 2 cardio sessions (interval + steady)
                • Progressive overload: add 2-5 percent weekly
                • Track readiness before high-intensity days
                """;
        }
        if (MODE_STRESS.equals(mode)) {
            return """
                🌿 Stress-balancing movement:
                • 30 min walk or light cycle daily
                • 2 yoga or mobility sessions
                • Keep intensity moderate this week
                • End sessions with 5-minute breathing
                """;
        }
        return """
            💼 Executive performance routine:
            • 150 minutes moderate activity weekly
            • 3 strength sessions to support energy and posture
            • Schedule workouts on calendar like key meetings
            • Use a weekly review to remove bottlenecks
            """;
    }

    private String stressResponse(String mode) {
        if (MODE_STRESS.equals(mode)) {
            return """
                🧠 Focus protocol:
                • 2 x 10-minute breathing blocks per day
                • Work in 50/10 focus cycles
                • Reduce late caffeine and notifications
                • 15-minute evening decompression walk
                """;
        }
        if (MODE_RECOVERY.equals(mode)) {
            return """
                💤 Recovery stress control:
                • Prioritize parasympathetic activities nightly
                • Keep training load lower for 48 hours
                • Hydrate consistently through the day
                • Use one mindfulness session post-work
                """;
        }
        if (MODE_PERFORMANCE.equals(mode)) {
            return """
                🎯 High-performance stress strategy:
                • Short pre-performance breathing routine
                • One deep-work block before noon
                • Hard stop for work in evening
                • Track stress trend with sleep quality
                """;
        }
        return """
            🧭 Executive stress framework:
            • Triage priorities into must/should/could
            • Reserve two deep-focus windows daily
            • Add short recovery breaks between meetings
            • Protect a non-negotiable evening reset
            """;
    }

    private String buildPlan(String mode) {
        WellnessProfile profile = manager.getProfile();
        double score = manager.getOverallScore();
        String goal = profile.getGoal() == null ? "General Wellness" : profile.getGoal();

        if (MODE_RECOVERY.equals(mode)) {
            return """
                7-Day Recovery Plan for %s:
                • Days 1-2: lower strain, improve sleep timing
                • Days 3-5: zone-2 + mobility + hydration consistency
                • Days 6-7: reassess readiness and reintroduce intensity
                • Current score %.1f -> target +8 points
                """.formatted(goal, score);
        }
        if (MODE_PERFORMANCE.equals(mode)) {
            return """
                7-Day Performance Plan for %s:
                • 3 strength sessions, 2 cardio sessions, 1 mobility day
                • Sleep target 7.5-8.5h and protein at each meal
                • Use readiness to auto-adjust hard sessions
                • Current score %.1f -> target +10 points
                """.formatted(goal, score);
        }
        if (MODE_STRESS.equals(mode)) {
            return """
                7-Day Stress & Focus Plan for %s:
                • Daily breathwork morning/evening
                • 2 deep-work blocks and reduced context switching
                • Evening decompression ritual and sleep reset
                • Current score %.1f -> target +7 points
                """.formatted(goal, score);
        }
        return """
            7-Day Executive Plan for %s:
            • Protect sleep, calendar workouts, and nutrition windows
            • Run daily 5-minute metric review
            • Focus on consistency over intensity spikes
            • Current score %.1f -> target +9 points
            """.formatted(goal, score);
    }

    private String buildTodayPlan(String mode) {
        if (MODE_RECOVERY.equals(mode)) {
            return """
                Today's Recovery Plan:
                • Morning: 20 min easy walk + hydration
                • Midday: mobility session 10 min
                • Evening: caffeine cutoff and 30 min wind-down
                • Target: improve readiness for tomorrow
                """;
        }
        if (MODE_PERFORMANCE.equals(mode)) {
            return """
                Today's Performance Plan:
                • Morning: dynamic warmup + high-value training block
                • Midday: protein-forward recovery meal
                • Evening: 15 min mobility + sleep protection
                • Target: quality output with controlled strain
                """;
        }
        if (MODE_STRESS.equals(mode)) {
            return """
                Today's Focus Plan:
                • Morning: breathing drill 6 min + top-3 priorities
                • Workday: 2 deep-work cycles (50/10)
                • Evening: decompression walk + no late-scroll window
                • Target: lower stress while maintaining productivity
                """;
        }
        return """
            Today's Executive Plan:
            • Morning: readiness check and schedule alignment
            • Midday: movement break + hydration checkpoint
            • Evening: short review and tomorrow prep
            • Target: high consistency and stable energy
            """;
    }

    private String nutritionResponse(String mode) {
        if (MODE_PERFORMANCE.equals(mode)) {
            return """
                🥗 Performance nutrition:
                • Lean protein every meal
                • Carbs around training windows
                • Post-workout recovery meal in 60 minutes
                • Minimize ultra-processed snacks
                """;
        }
        if (MODE_RECOVERY.equals(mode)) {
            return """
                🥗 Recovery nutrition:
                • Anti-inflammatory whole foods
                • Omega-3 sources several times weekly
                • Consistent mealtimes to support sleep rhythm
                • Keep evening meals lighter
                """;
        }
        if (MODE_STRESS.equals(mode)) {
            return """
                🥗 Stress-support nutrition:
                • Stable blood sugar meals (fiber + protein)
                • Reduce late caffeine and excess sugar
                • Add magnesium-rich foods in evening
                • Hydrate steadily through work blocks
                """;
        }
        return """
            🥗 Executive nutrition strategy:
            • Keep meals predictable and high-quality
            • Prioritize hydration and protein at each meal
            • Use simple defaults to avoid decision fatigue
            • Protect energy before important meetings
            """;
    }

    private String hydrationResponse(String mode) {
        if (MODE_PERFORMANCE.equals(mode)) {
            return """
                💧 Performance hydration:
                • 2.5-3.0L baseline daily
                • Add fluids around sessions
                • Include electrolytes on heavy sweat days
                • Track hydration with consistent checkpoints
                """;
        }
        if (MODE_RECOVERY.equals(mode)) {
            return """
                💧 Recovery hydration:
                • 2.2-2.8L daily in steady intervals
                • Avoid long dehydration windows
                • Pair fluids with meals for consistency
                • Keep evening intake balanced for sleep
                """;
        }
        if (MODE_STRESS.equals(mode)) {
            return """
                💧 Focus hydration:
                • Start day with water before caffeine
                • Use bottle checkpoints per focus block
                • Reduce sugary drinks during work peaks
                • Rehydrate after stress spikes
                """;
        }
        return """
            💧 Executive hydration:
            • 2-3L daily with scheduled checkpoints
            • Hydrate before major decision blocks
            • Keep intake consistent instead of late catch-up
            • Review hydration trend with your daily score
            """;
    }

    public List<String> suggest() {
        WellnessProfile profile = manager.getProfile();
        double score = manager.getOverallScore();
        List<String> suggestions = new ArrayList<>();

        if (score < 50) {
            suggestions.add("⚠️ Your wellness score is low. Focus on sleep and activity!");
        } else if (score < 70) {
            suggestions.add("📈 You're improving! Keep building on recent wins.");
        } else {
            suggestions.add("🌟 Excellent wellness! Maintain this momentum!");
        }

        if (profile.getAge() > 40) {
            suggestions.add("❤️ After 40, focus on cardiac health & flexibility.");
        }

        double bmi = profile.calculateBMI();
        if (bmi > 25) {
            suggestions.add("🏃 Consider increasing cardio 30 min/day.");
        } else if (bmi < 18.5) {
            suggestions.add("💪 Add strength training to build lean muscle.");
        }

        return suggestions;
    }
}
