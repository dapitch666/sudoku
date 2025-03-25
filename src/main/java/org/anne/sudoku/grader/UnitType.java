package org.anne.sudoku.grader;

public enum UnitType {
    ROW, COLUMN, SQUARE;

    public String toString(int unitIndex) {
        return switch (this) {
            case ROW -> "Row " + "ABCDEFGHJ".charAt(unitIndex);
            case COLUMN -> "Col " + (unitIndex + 1);
            case SQUARE -> "Box " + (unitIndex + 1);
        };
    }
}
