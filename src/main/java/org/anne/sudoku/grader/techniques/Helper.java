package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.model.Cell;

import java.util.*;
import java.util.function.Predicate;

public class Helper {

    public static Map<Integer, List<Cell>> getPossibleCellsMap(List<Cell> unit, Predicate<List<Cell>> predicate) {
        Map<Integer, List<Cell>> map = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            List<Cell> possibleCells = new ArrayList<>();
            for (Cell cell : unit) {
                if (cell.isCandidate(i)) {
                    possibleCells.add(cell);
                }
            }
            if (predicate.test(possibleCells)) {
                map.put(i, possibleCells);
            }
        }
        return map;
    }

    public static Map<Integer, List<Cell>> getPossibleCellsMap(Cell[] unit, Predicate<List<Cell>> predicate) {
        return getPossibleCellsMap(Arrays.stream(unit).toList(), predicate);
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
}
