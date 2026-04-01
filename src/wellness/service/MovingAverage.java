package wellness.service;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Lightweight fixed-window moving average for smoothing scores.
 */
public class MovingAverage {
    private final int window;
    private final Deque<Double> values = new ArrayDeque<>();
    private double sum = 0.0;

    public MovingAverage(int window) {
        this.window = Math.max(1, window);
    }

    public void add(double v) {
        values.addLast(v);
        sum += v;
        if (values.size() > window) {
            sum -= values.removeFirst();
        }
    }

    public double average() {
        if (values.isEmpty()) return 0.0;
        return sum / values.size();
    }

    public void reset() {
        values.clear();
        sum = 0.0;
    }
}
