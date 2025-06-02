package org.anne.sudoku.model;


import java.util.Arrays;
import java.util.Comparator;

public record Formation(Cell... cells) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Formation(Cell[] cells1))) return false;
        return cells.length == cells1.length && Arrays.stream(cells)
                .allMatch(cell -> Arrays.asList(cells1).contains(cell));
    }

    @Override
    public int hashCode() {
        return Arrays.stream(cells).mapToInt(Cell::hashCode).sum();
    }

    @Override
    public String toString() {
        return Arrays.stream(cells).sorted(Comparator.comparingInt(Cell::index)).toList().toString();
    }
}
