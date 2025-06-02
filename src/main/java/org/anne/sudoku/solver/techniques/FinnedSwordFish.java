package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.anne.sudoku.solver.techniques.Helper.mergeArrays;

public class FinnedSwordFish extends SolvingTechnique {
    public FinnedSwordFish() {
        super("Finned SwordFish", Grade.INSANE);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
            for (int digit = 1; digit <= 9; digit++) {
                List<Cell[]> candidateUnits = findCandidateUnits(grid, unitType, digit);
                for (int i = 0; i < candidateUnits.size(); i++) {
                    for (int j = i + 1; j < candidateUnits.size(); j++) {
                        for (int k = j + 1; k < candidateUnits.size(); k++) {

                            List<Cell> swordfish = mergeArrays(candidateUnits.get(i), candidateUnits.get(j), candidateUnits.get(k));
                            var unitCells = groupCellsByUnit(unitType.opposite(), swordfish);

                            List<Cell> fins = findFins(unitCells);
                            if (fins.isEmpty()) continue;

                            Formation formation = getSwordfishFormation(swordfish, fins);

                            List<Integer> boxes = Helper.getDistinctUnits(UnitType.BOX, formation.cells());
                            if (!boxes.contains(fins.getFirst().getBox()))
                                continue; // Fins must be in the same box as the swordfish
                            List<Cell> changed = Arrays.stream(grid.getCells(Predicates.inUnit(UnitType.BOX, fins.getFirst().getBox())
                                            .and(Predicates.containsCandidate(digit))
                                            .and(cell -> unitCells.containsKey(cell.getUnitIndex(unitType.opposite())))
                                            .and(cell -> !Helper.getDistinctUnits(unitType.opposite(), fins).contains(cell.getUnitIndex(unitType.opposite())))
                                            .and(Predicates.in(swordfish).negate())))
                                    .toList();
                            if (changed.isEmpty()) continue;

                            for (Cell cell : changed) {
                                cell.removeCandidate(digit);
                            }
                            log("Finned Swordfish (%s) on %d in %s with fins %s%n- Removed candidate {%d} from %s%n",
                                    unitType == UnitType.ROW ? "row" : "col", digit, formation, fins, digit, changed);
                            incrementCounter();
                            return changed;
                        }
                    }
                }
            }
        }
        return List.of();
    }

    private Formation getSwordfishFormation(List<Cell> swordfish, List<Cell> fins) {
        Set<Cell> formationCells = swordfish.stream()
                .filter(cell -> !fins.contains(cell))
                .collect(Collectors.toSet());
        List<Integer> rows = Helper.getDistinctUnits(UnitType.ROW, formationCells.stream().toList());
        List<Integer> cols = Helper.getDistinctUnits(UnitType.COL, formationCells.stream().toList());
        for (int row : rows) {
            for (int col : cols) {
                formationCells.add(grid.getCell(row, col));
            }
        }
        return new Formation(formationCells.toArray(Cell[]::new));
    }

    private List<Cell> findFins(Map<Integer, List<Cell>> unitCells) {
        if (unitCells.size() <= 3 ||
                unitCells.values().stream().filter(cells -> cells.size() >= 2).count() != 3) {
            return List.of(); // Not enough units or not exactly 3 units with 2 or more cells
        }
        List<Cell> fins = unitCells.values().stream()
                .filter(cells -> cells.size() == 1)
                .flatMap(List::stream)
                .toList();
        if (!fins.isEmpty() && fins.size() <= 2 && Helper.getDistinctUnits(UnitType.BOX, fins).size() == 1) {
            return fins;
        }
        return List.of();
    }

    private List<Cell[]> findCandidateUnits(Grid grid, UnitType unitType, int digit) {
        List<Cell[]> candidateUnits = new ArrayList<>();
        for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
            Cell[] cells = grid.getCells(Predicates.inUnit(unitType, unitIndex).and(Predicates.containsCandidate(digit)));
            if (cells.length >= 2 && cells.length <= 5) {
                candidateUnits.add(cells);
            }
        }
        return candidateUnits;
    }

    private Map<Integer, List<Cell>> groupCellsByUnit(UnitType unitType, List<Cell> cells) {
        Map<Integer, List<Cell>> unitCells = new java.util.HashMap<>();
        for (Cell cell : cells) {
            int unitIndex = cell.getUnitIndex(unitType);
            unitCells.computeIfAbsent(unitIndex, _ -> new ArrayList<>()).add(cell);
        }
        return unitCells;
    }
}