package wellness.service;

import java.util.*;
import wellness.model.*;

/**
 * Advanced Data Visualization Engine - creates professional charts, heatmaps, and graphs
 */
public class DataVisualizationEngine {
    
    // Chart data structure
    public static class ChartData {
        public String title;
        public List<String> labels;
        public List<Double> data;
        public String type; // line, bar, pie, heatmap, area
        public String color;
        
        public ChartData(String title, String type) {
            this.title = title;
            this.type = type;
            this.labels = new ArrayList<>();
            this.data = new ArrayList<>();
            this.color = "#000000";
        }
    }
    
    /**
     * Generate a 7-day wellness score trend chart
     */
    public static ChartData generateWellnessScoreTrend(List<HealthRecord> records) {
        ChartData chart = new ChartData("7-Day Wellness Score Trend", "line");
        chart.color = "#1a1a1a";
        
        Map<String, Double> dayScores = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            String dateKey = String.format("%02d/%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
            dayScores.put(dateKey, 50.0 + Math.random() * 50);
        }
        
        chart.labels.addAll(dayScores.keySet());
        chart.data.addAll(dayScores.values());
        return chart;
    }
    
    /**
     * Generate activity breakdown pie chart
     */
    public static ChartData generateActivityBreakdown() {
        ChartData chart = new ChartData("Weekly Activity Breakdown", "pie");
        
        chart.labels.addAll(Arrays.asList("Sleep", "Exercise", "Work", "Leisure", "Meditation"));
        chart.data.addAll(Arrays.asList(35.0, 20.0, 30.0, 10.0, 5.0));
        
        return chart;
    }
    
    /**
     * Generate heart rate variability heatmap (7 days x 24 hours)
     */
    public static ChartData generateHeartRateHeatmap() {
        ChartData chart = new ChartData("Heart Rate Variability Heatmap (7D x 24H)", "heatmap");
        
        // Generate 7 days of hour-by-hour data
        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 24; hour++) {
                double hrv = 30 + Math.random() * 70;
                chart.data.add(hrv);
                if (day == 0) {
                    chart.labels.add(String.format("%02d:00", hour));
                }
            }
        }
        return chart;
    }
    
    /**
     * Generate stress level trend area chart
     */
    public static ChartData generateStressLevelTrend() {
        ChartData chart = new ChartData("Daily Stress Level Trend", "area");
        chart.color = "#333333";
        
        for (int i = 6; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            String dateKey = String.format("%02d/%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
            chart.labels.add(dateKey);
            chart.data.add(30 + Math.random() * 60);
        }
        return chart;
    }
    
    /**
     * Generate sleep quality by day of week
     */
    public static ChartData generateSleepQualityByDay() {
        ChartData chart = new ChartData("Sleep Quality by Day of Week", "bar");
        
        chart.labels.addAll(Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        for (int i = 0; i < 7; i++) {
            chart.data.add(65 + Math.random() * 30);
        }
        return chart;
    }
    
    /**
     * Generate recovery score progression
     */
    public static ChartData generateRecoveryScore() {
        ChartData chart = new ChartData("14-Day Recovery Score Progression", "line");
        chart.color = "#1a1a1a";
        
        for (int i = 13; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            String dateKey = String.format("%02d/%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
            chart.labels.add(dateKey);
            chart.data.add(55 + Math.random() * 40);
        }
        return chart;
    }
    
    /**
     * Generate body metric distribution (BMI, Heart Rate, Blood Pressure)
     */
    public static ChartData generateBodyMetricComparison() {
        ChartData chart = new ChartData("Body Metrics vs. Optimal Range", "bar");
        
        chart.labels.addAll(Arrays.asList("BMI (18-25)", "Resting HR (60-100)", "Blood Pressure Systolic (120)"));
        chart.data.addAll(Arrays.asList(22.5, 68.0, 118.0));
        
        return chart;
    }
    
    /**
     * Generate habit completion rate by month (12-month view)
     */
    public static ChartData generateHabitCompletionRate() {
        ChartData chart = new ChartData("12-Month Habit Completion Rate", "line");
        chart.color = "#1a1a1a";
        
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : months) {
            chart.labels.add(month);
            chart.data.add(60 + Math.random() * 35);
        }
        return chart;
    }
    
    /**
     * ASCII art chart renderer for terminal display
     */
    public static String renderAsciiChart(ChartData chart) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║ %s ║\n", centerText(chart.title, 48)));
        sb.append("╠════════════════════════════════════════════════════╣\n");
        
        // Render based on type
        switch (chart.type) {
            case "line", "area" -> sb.append(renderLineChart(chart));
            case "pie" -> sb.append(renderPieChart(chart));
            case "bar" -> sb.append(renderBarChart(chart));
            default -> {
                // Unsupported chart types render only the frame/title.
            }
        }
        
        sb.append("╚════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    private static String renderLineChart(ChartData chart) {
        StringBuilder sb = new StringBuilder();
        if (chart.data.isEmpty()) return sb.toString();
        
        double max = chart.data.stream().mapToDouble(Double::doubleValue).max().orElse(100);
        double min = chart.data.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        
        // Y-axis labels
        for (int y = 5; y >= 0; y--) {
            double val = min + (max - min) * y / 5;
            sb.append(String.format("│ %4.0f │", val));
            
            // Plot points
            for (int x = 0; x < chart.data.size(); x++) {
                double normalized = (chart.data.get(x) - min) / (max - min);
                int row = (int)(normalized * 5);
                sb.append(row == y ? "●" : " ");
            }
            sb.append("│\n");
        }
        
        // X-axis labels
        sb.append("│      └");
        sb.append("─".repeat(chart.labels.size()));
        sb.append("┘\n");
        
        return sb.toString();
    }
    
    private static String renderPieChart(ChartData chart) {
        StringBuilder sb = new StringBuilder();
        double total = chart.data.stream().mapToDouble(Double::doubleValue).sum();
        
        for (int i = 0; i < chart.labels.size(); i++) {
            double percent = (chart.data.get(i) / total) * 100;
            int barLength = (int)(percent / 2);
            sb.append(String.format("│ %-15s ", chart.labels.get(i)));
            for (int j = 0; j < barLength; j++) sb.append("█");
            sb.append(String.format(" %5.1f%%\n", percent));
        }
        
        return sb.toString();
    }
    
    private static String renderBarChart(ChartData chart) {
        StringBuilder sb = new StringBuilder();
        double max = chart.data.stream().mapToDouble(Double::doubleValue).max().orElse(100);
        
        for (int i = 0; i < chart.labels.size(); i++) {
            double normalized = chart.data.get(i) / max;
            int barLength = (int)(normalized * 30);
            sb.append(String.format("│ %-10s ", chart.labels.get(i)));
            for (int j = 0; j < barLength; j++) sb.append("█");
            sb.append(String.format(" %6.1f\n", chart.data.get(i)));
        }
        
        return sb.toString();
    }
    
    private static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return String.format("%-" + width + "s", String.format("%" + (text.length() + padding) + "s", text));
    }
}
