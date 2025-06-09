package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Predicates;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The general terms the rule for the pattern is as follows:
 * 1. Find a 2-cell or 3-cell group inside a box that is also aligned on a row or column - call it group C
 * 2. C contains a set of candidates, V, which must be two or more than the number of cells in C (N+2, N+3 ALS etc).
 * 3. We need to find at least one bi-value cell (or larger ALS) in the row or column which only contains candidates from set V, called D
 * 4. We need to find at least one bi-value cell (or larger ALS) in the box which only contains candidates from set V, called E
 * 5. The candidates in D and E must be different.
 * 6. Remove any candidates common to C+D not in the cells covered by C or D in the row or column
 * 7. Remove any candidates common to C+E not in the cells covered by C or E in the box
 */
public class SueDeCoq extends SolvingTechnique {
    public SueDeCoq() {
        super("Sue-de-Coq", Grade.INSANE);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Cell> changed = new ArrayList<>();

        for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {

            // Iterate through all boxes in the grid
            for (int boxIndex = 0; boxIndex < 9; boxIndex++) {
                List<Cell> boxCells = List.of(grid.getCells(Predicates.inUnit(UnitType.BOX, boxIndex)
                        .and(Predicates.unsolvedCells)));

                // Find potential group C (2-cell or 3-cell group aligned with row/column)
                List<Cell[]> potentialGroupsC = findGroupsC(boxCells, unitType);

                for (Cell[] groupC : potentialGroupsC) {
                    BitSet candidatesV = Helper.mergedCandidates(groupC);

                    // Ensure V meets the criteria (N+2, N+3 ALS etc.)
                    if (candidatesV.cardinality() < groupC.length + 2) continue;

                    // Find group D (row/column) and group E (box)
                    Cell[] groupD = findGroupD(groupC, candidatesV, unitType);
                    if (groupD.length == 0) continue;
                    BitSet candidatesD = Helper.mergedCandidates(groupD);
                    if (candidatesD.cardinality() != groupD.length + 1) continue;

                    Cell[] groupE = findGroupE(groupC, candidatesV, unitType);
                    BitSet candidatesE = Helper.mergedCandidates(groupE);

                    if (groupE.length == 0 || candidatesD.intersects(candidatesE) || candidatesE.cardinality() != groupE.length + 1) continue;

                    // Perform candidate eliminations
                    changed.addAll(removeCandidatesOutsideGroup(groupC, groupD, candidatesD, unitType));
                    changed.addAll(removeCandidatesOutsideGroup(groupC, groupE, candidatesE, UnitType.BOX));

                    if (!changed.isEmpty()) {
                        log(0, "%s with candidates %s, %s in %s with candidates %s, %s in BOX with candidates %s%n", Arrays.toString(groupC), candidatesV, Arrays.toString(groupD), unitType, candidatesD, Arrays.toString(groupE), candidatesE);
                        incrementCounter();
                        return changed;
                    }
                }
            }
        }

        return List.of();
    }

    private List<Cell[]> findGroupsC(List<Cell> boxCells, UnitType unitType) {
        // Find 2-cell or 3-cell groups aligned with row/column
        return boxCells.stream()
                .collect(Collectors.groupingBy(cell -> cell.getUnitIndex(unitType)))
                .values().stream()
                .filter(group -> group.size() == 2 || group.size() == 3)
                .map(group -> group.toArray(new Cell[0]))
                .collect(Collectors.toList());
    }

    private Cell[] findGroupD(Cell[] groupC, BitSet candidatesV, UnitType unitType) {
        // Find bi-value cells or larger ALS in the row/column containing only candidates from V
        return Arrays.stream(grid.getCells(Predicates.inUnit(unitType, groupC[0].getUnitIndex(unitType))
                        .and(Predicates.inUnit(UnitType.BOX, groupC[0].getBox()).negate())
                        .and(Predicates.unsolvedCells)))
                .filter(cell -> cell.candidates().stream().allMatch(candidatesV::get))
                .toList()
                .toArray(new Cell[0]);
    }

    private Cell[] findGroupE(Cell[] groupC, BitSet candidatesV, UnitType unitType) {
        // Find bi-value cells or larger ALS in the box containing only candidates from V
        int boxIndex = groupC[0].getBox();
        return Arrays.stream(grid.getCells(Predicates.inUnit(UnitType.BOX, boxIndex)
                        .and(Predicates.inUnit(unitType, groupC[0].getUnitIndex(unitType)).negate())
                        .and(Predicates.unsolvedCells)))
                .filter(cell -> cell.candidates().stream().allMatch(candidatesV::get))
                .toList()
                .toArray(new Cell[0]);
    }

    private List<Cell> removeCandidatesOutsideGroup(Cell[] groupC, Cell[] group, BitSet candidates, UnitType unitType) {
        List<Cell> changed = new ArrayList<>();
        int unitIndex = groupC[0].getUnitIndex(unitType);
        Cell[] cells = grid.getCells(Predicates.inUnit(unitType, unitIndex)
                .and(Predicates.in(groupC).negate())
                .and(Predicates.in(group).negate()));

        for (Cell cell : cells) {
            BitSet removed = cell.removeCandidates(candidates);
            if (!removed.isEmpty()) {
                changed.add(cell);
                log(String.format("- Removed candidates %s from cell %s%n", removed, cell));
            }
        }

        return changed;
    }
}