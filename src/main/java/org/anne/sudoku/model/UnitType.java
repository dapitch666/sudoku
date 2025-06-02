package org.anne.sudoku.model;

public enum UnitType {
    ROW, COL, BOX;

    public String toString(int unitIndex) {
        return switch (this) {
            case ROW -> "Row " + "ABCDEFGHJ".charAt(unitIndex);
            case COL -> "Col " + (unitIndex + 1);
            case BOX -> "Box " + (unitIndex + 1);
        };
    }

    public UnitType opposite() {
        return switch (this) {
            case ROW -> COL;
            case COL -> ROW;
            case BOX -> BOX; // BOX has no opposite
        };
    }
}
