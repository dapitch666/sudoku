package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    static Predicate <List<Cell>> listSizeEqualsTwo = (possibleCells) -> possibleCells.size() == 2;
    static Predicate <List<Cell>> listSizeEqualsTree = (possibleCells) -> possibleCells.size() == 3;
    static Predicate <List<Cell>> listSizeEqualsFour = (possibleCells) -> possibleCells.size() == 4;
    static Predicate <List<Cell>> listSizeLessThanThree = (possibleCells) -> possibleCells.size() == 2 || possibleCells.size() == 3;
    static Predicate <List<Cell>> listSizeLessThanFour = (possibleCells) -> possibleCells.size() >= 2 && possibleCells.size() <= 4;
}
