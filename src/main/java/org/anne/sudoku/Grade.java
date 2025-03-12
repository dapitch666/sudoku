package org.anne.sudoku;

public enum Grade {
    UNKNOWN(0),
    VERY_EASY(1),
    EASY(2),
    MODERATE(3),
    HARD(4),
    VERY_HARD(5),
    INSANE(6);

    private final int level;

    Grade(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static Grade fromLevel(int level) {
        for (Grade grade : values()) {
            if (grade.level == level) {
                return grade;
            }
        }
        throw new IllegalArgumentException("Invalid level: " + level);
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace("_", " ");
    }
}
