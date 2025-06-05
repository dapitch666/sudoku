package org.anne.sudoku.model;

import java.util.Arrays;
import java.util.HashSet;

import static org.anne.sudoku.model.UnitType.COL;
import static org.anne.sudoku.model.UnitType.ROW;

public record GroupedCell(Cell... cells) {
    public UnitType unitType() {
        if (Arrays.stream(cells).map(Cell::getRow).distinct().count() == 1) return ROW;
        if (Arrays.stream(cells).map(Cell::getCol).distinct().count() == 1) return COL;
        throw new IllegalStateException("GroupedCell must be in a single row or column");
    }

    public int unitIndex() {
        return unitType() == ROW ? cells[0].getRow() : cells[0].getCol();
    }

    public int box() {
        return cells[0].getBox();
    }

    public boolean isSingleCell() {
        return cells.length == 1;
    }

    public boolean intersects(GroupedCell other) {
        return Arrays.stream(cells).anyMatch(Arrays.asList(other.cells)::contains);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GroupedCell(Cell[] cells1))) return false;
        return new HashSet<>(Arrays.asList(cells)).equals(new HashSet<>(Arrays.asList(cells1)));
    }

    @Override
    public int hashCode() {
        return Arrays.stream(cells).mapToInt(Cell::hashCode).sum();
    }

    @Override
    public String toString() {
        return Arrays.toString(cells);
    }
}
