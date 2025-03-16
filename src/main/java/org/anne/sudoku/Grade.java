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

    public static Grade fromScore(int score) {
        if (score < 0) {
            return UNKNOWN;
        }
        if (score < 10) {
            return VERY_EASY;
        }
        if (score < 20) {
            return EASY;
        }
        if (score < 30) {
            return MODERATE;
        }
        if (score < 40) {
            return HARD;
        }
        if (score < 50) {
            return VERY_HARD;
        }
        return INSANE;
    }

    public static Grade fromSudokuWiki(String wiki) {
        return switch (wiki) {
            case "Gentle/Very Easy Grade" -> VERY_EASY;
            case "Gentle/Easy Grade" -> EASY;
            case "Moderate Grade" -> MODERATE;
            case "Tough Grade" -> HARD;
            case "Very Hard Grade" -> VERY_HARD;
            case "Extreme Grade" -> INSANE;
            default -> UNKNOWN;
        };
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
