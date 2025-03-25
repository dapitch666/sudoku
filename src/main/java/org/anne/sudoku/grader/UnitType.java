package org.anne.sudoku.grader;

public enum UnitType {
    ROW, COL, BOX;

    public String toString(int unitIndex) {
        return switch (this) {
            case ROW -> "Row " + "ABCDEFGHJ".charAt(unitIndex);
            case COL -> "Col " + (unitIndex + 1);
            case BOX -> "Box " + (unitIndex + 1);
        };
    }
}
