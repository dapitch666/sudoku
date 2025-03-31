package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.List;

public class BiValueUniversalGrave implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        int unsolvedCells = grid.getUnsolvedCells().length;
        Cell[] cells = grid.getCellsWithThreeCandidates();
        if (cells.length != 1) return List.of();
        if (grid.getCellsWithTwoCandidates().length == unsolvedCells - 1) {
            Cell cell = cells[0];
            int[] candidates = cell.getCandidates().stream().mapToInt(Integer::intValue).toArray();
            for (int digit : candidates) {
                if (grid.getCellsInUnitWithCandidate(digit, UnitType.ROW, cell.getRow()).length > 2
                || grid.getCellsInUnitWithCandidate(digit, UnitType.COL, cell.getCol()).length > 2
                || grid.getCellsInUnitWithCandidate(digit, UnitType.BOX, cell.getBox()).length > 2) {
                    cell.removeAllBut(List.of(digit));
                    sb.append(String.format("BUG found in %s. %d must be the solution%n", cell.getPosition(), digit));
                    incrementCounter(counter);
                    return List.of(cell);
                }
            }
        }
        return List.of();
    }

    @Override
    public int getCounter() {
        return counter[0];
    }

}
