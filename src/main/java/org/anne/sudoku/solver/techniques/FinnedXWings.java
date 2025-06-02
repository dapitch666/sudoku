package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.*;

import java.util.*;

public class FinnedXWings extends SolvingTechnique {
    public FinnedXWings() {
        super("Finned X-Wings", Grade.INSANE);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        for (int digit = 1; digit <= 9; digit++) {
            Set<Formation> formations = findRectangles(digit);
            for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
                for (Formation formation : formations) {
                    // Check if the formation is a finned X-Wing and find the fin
                    Cell[] finnedCells = findFinnedCells(formation, digit, unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW);
                    if (finnedCells.length == 0) continue;
                    int box = finnedCells[0].getBox();
                    List<Integer> unitIndices = Arrays.stream(formation.cells())
                            .mapToInt(cell -> cell.getUnitIndex(unitType))
                            .distinct()
                            .boxed()
                            .toList();
                    List<Cell> changed = new ArrayList<>();
                    // Remove the candidate pointed by the finned cells
                    for (Cell cell : grid.getCells(
                            Predicates.containsCandidate(digit)
                                    .and(c -> c.getBox() == box && unitIndices.contains(c.getUnitIndex(unitType)))
                                    .and(Predicates.in(formation.cells()).negate()))) {
                        cell.removeCandidate(digit);
                        changed.add(cell);
                    }
                    if (changed.isEmpty()) continue;
                    log("Finned X-Wing found in %s%n- Removed candidate %d from %s%n", formation, digit, changed);
                    incrementCounter();
                    return changed;
                }
            }
        }
        return List.of();
    }

    private Cell[] findFinnedCells(Formation formation, int digit, UnitType unitType) {
        List<Integer> unitIndices = Arrays.stream(formation.cells())
                .mapToInt(cell -> cell.getUnitIndex(unitType))
                .distinct()
                .boxed()
                .toList();
        List<Integer> boxes = Arrays.stream(formation.cells())
                .mapToInt(Cell::getBox)
                .distinct()
                .boxed()
                .toList();
        var cells = grid.getCells(Predicates.containsCandidate(digit)
                .and(Predicates.in(formation.cells()).negate())
                .and(cell -> unitIndices.contains(cell.getUnitIndex(unitType))));
        if (cells.length == 1 || (cells.length == 2 && cells[0].getBox() == cells[1].getBox())
                && boxes.contains(cells[0].getBox())) {
            return cells;
        }
        return new Cell[0];
    }

    private Set<Formation> findRectangles(int digit) {
        Set<Formation> formations = new HashSet<>();
        Cell[] cells = grid.getCells(Predicates.containsCandidate(digit));
        for (Cell cellA : cells) {
            for (Cell cellB : Arrays.stream(cells)
                    .filter(cell -> cell.getBox() != cellA.getBox() &&
                            (cell.getRow() == cellA.getRow() || cell.getCol() == cellA.getCol()))
                    .toList()) {
                UnitType unitType = cellA.getRow() == cellB.getRow() ? UnitType.COL : UnitType.ROW;
                for (Cell cellC : Arrays.stream(cells)
                        .filter(cell -> cell.getBox() != cellA.getBox() && cell.getUnitIndex(unitType) == cellA.getUnitIndex(unitType))
                        .toList()) {
                    Cell cellD = grid.findFourthCorner(cellA, cellB, cellC);
                    if (!cellD.hasCandidate(digit) || cellD.isSolved()) continue;
                    formations.add(new Formation(cellA, cellB, cellC, cellD));
                }
            }
        }
        return formations;
    }
}
