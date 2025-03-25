package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;

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
}
