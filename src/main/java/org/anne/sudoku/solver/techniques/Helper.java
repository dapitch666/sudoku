package org.anne.sudoku.solver.techniques;

import java.util.*;
import java.util.function.Predicate;

import org.anne.sudoku.model.*;

public class Helper {

    public static Map<Integer, List<Cell>> getPossibleCellsMap(List<Cell> unit, BitSet digits, Predicate<List<Cell>> predicate) {
        Map<Integer, List<Cell>> map = new HashMap<>();
        for (int digit : digits.stream().toArray()) {
            List<Cell> possibleCells = new ArrayList<>();
            for (Cell cell : unit) {
                if (cell.hasCandidate(digit)) {
                    possibleCells.add(cell);
                }
            }
            if (predicate.test(possibleCells)) {
                map.put(digit, possibleCells);
            }
        }
        return map;
    }

    public static BitSet mergedCandidates(Cell... cells) {
        BitSet merged = new BitSet();
        for (Cell cell : cells) {
            merged.or(cell.candidates());
        }
        return merged;
    }

    public static Map<Integer, List<Cell>> getPossibleCellsMap(Cell[] unit, Predicate<List<Cell>> predicate) {
        BitSet digits = new BitSet();
        digits.set(1, 10);
        return getPossibleCellsMap(Arrays.stream(unit).toList(), digits, predicate);
    }

    public static Map<Integer, List<Cell>> getPossibleCellsMap(Cell[] unit, BitSet digits, Predicate<List<Cell>> predicate) {
        return getPossibleCellsMap(Arrays.stream(unit).toList(), digits, predicate);
    }

    @SafeVarargs
    static <T> List<T> mergeArrays(T[]... arrays) {
        List<T> merged = new ArrayList<>();
        for (T[] array : arrays) {
            merged.addAll(List.of(array));
        }
        return merged;
    }

    public static int findFourthBox(int b1, int b2, int b3) {
        // Convert indices to (x,y) coordinates
        int x1 = b1 % 3;
        int y1 = b1 / 3;
        int x2 = b2 % 3;
        int y2 = b2 / 3;
        int x3 = b3 % 3;
        int y3 = b3 / 3;

        // Find the point that is common to two segments (the corner)
        int xCorner, yCorner, xOther1, yOther1, xOther2, yOther2;

        if (x1 == x2 || x1 == x3 || y1 == y2 || y1 == y3) {
            if (x1 == x2 || y1 == y2) {
                xCorner = x1;
                yCorner = y1;
                xOther1 = x2;
                yOther1 = y2;
                xOther2 = x3;
                yOther2 = y3;
            } else {
                xCorner = x1;
                yCorner = y1;
                xOther1 = x3;
                yOther1 = y3;
                xOther2 = x2;
                yOther2 = y2;
            }
        } else {
            xCorner = x2;
            yCorner = y2;
            xOther1 = x1;
            yOther1 = y1;
            xOther2 = x3;
            yOther2 = y3;
        }

        // Calculate coordinates of the 4th point
        int x4 = xOther1 + (xOther2 - xCorner);
        int y4 = yOther1 + (yOther2 - yCorner);

        // Convert coordinates back to index
        return y4 * 3 + x4;
    }

    public static List<Integer> getDistinctUnits(UnitType unitType, Cell... cells) {
        return Arrays.stream(cells)
                .map(cell -> cell.getUnitIndex(unitType))
                .distinct()
                .toList();
    }

    public static List<Integer> getDistinctUnits(UnitType unitType, List<Cell> cells) {
        return getDistinctUnits(unitType, cells.toArray(new Cell[0]));
    }
}
