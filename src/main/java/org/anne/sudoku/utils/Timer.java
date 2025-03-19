package org.anne.sudoku.utils;

public class Timer {
    private final long start;

    public Timer() {
        this.start = System.nanoTime();
    }

    public String duration() {
        return "Executed in " + (System.nanoTime() - start) / 1000000 + "ms";
    }
}
