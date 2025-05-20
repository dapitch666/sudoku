package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.ArrayList;
import java.util.List;

import static org.anne.sudoku.solver.techniques.Helper.mergeArrays;

public class SwordFish extends SolvingTechnique {
    public SwordFish() {
        super("Sword Fish", Grade.HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
            for (int digit = 1; digit <= 9; digit++) {
                List<Cell[]> candidateUnits = findCandidateUnits(unitType, digit);
                for (int i = 0; i < candidateUnits.size(); i++) {
                    for (int j = i + 1; j < candidateUnits.size(); j++) {
                        for (int k = j + 1; k < candidateUnits.size(); k++) {
                            List<Cell> swordfish = mergeArrays(candidateUnits.get(i), candidateUnits.get(j), candidateUnits.get(k));
                            List<Integer> distinctUnits = getDistinctUnits(swordfish, unitType);
                            if (distinctUnits.size() != 3) continue;

                            List<Cell> changed = getChangedCells(unitType, digit, swordfish, distinctUnits);
                            if (changed.isEmpty()) continue;
                            for (Cell cell : changed) {
                                cell.removeCandidate(digit);
                            }
                            log("Swordfish in %s%n- Removed candidate {%d} from %s%n", swordfish, digit, changed);
                            incrementCounter();
                            return changed;
                        }
                    }
                }
            }
        }
        return List.of();
    }

    private List<Cell[]> findCandidateUnits(UnitType unitType, int digit) {
        List<Cell[]> candidateUnits = new ArrayList<>();
        for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
            Cell[] cells = grid.getCells(Predicates.inUnit(unitType, unitIndex).and(Predicates.containsCandidate(digit)));
            if (cells.length == 2 || cells.length == 3) {
                candidateUnits.add(cells);
            }
        }
        return candidateUnits;
    }

    private List<Integer> getDistinctUnits(List<Cell> cells, UnitType unitType) {
        return cells.stream()
                .map(cell -> cell.getUnitIndex(unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW))
                .distinct()
                .toList();
    }

    private List<Cell> getChangedCells(UnitType unitType, int digit, List<Cell> swordfish, List<Integer> distinctUnits) {
        List<Cell> changed = new ArrayList<>();
        for (int index : distinctUnits) {
            changed.addAll(List.of(grid.getCells(
                    Predicates.inUnit(unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW, index)
                            .and(Predicates.containsCandidate(digit))
                            .and(cell -> !swordfish.contains(cell))
            )));
        }
        return changed;
    }
}