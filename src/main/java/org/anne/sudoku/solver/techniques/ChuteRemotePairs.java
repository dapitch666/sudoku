package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class ChuteRemotePairs extends SolvingTechnique {
    public ChuteRemotePairs() {
        super("Chute Remote Pairs", Grade.HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Cell> changed = new ArrayList<>();
        for (int index = 0; index < 3; index++) {
            for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
                List<Cell> chute = getChute(unitType, index);
                for (Cell cell1 : chute) {
                    if (!cell1.isBiValue()) continue;
                    for (Cell cell2 : chute) {
                        if (!isValidRemotePair(cell1, cell2)) continue;

                        BitSet remotePairCandidates = getRemotePairCandidates(chute, cell1, cell2);
                        if (processSingleCandidate(cell1, cell2, remotePairCandidates, changed)) return changed;
                        if (processDoubleElimination(cell1, cell2, remotePairCandidates, changed)) return changed;
                    }
                }
            }
        }
        return List.of();
    }

    private List<Cell> getChute(UnitType unitType, int index) {
        List<Cell> chute = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            chute.addAll(List.of(grid.getCells(Predicates.inUnit(unitType, index * 3 + i))));
        }
        return chute;
    }

    private boolean isValidRemotePair(Cell cell1, Cell cell2) {
        return cell1.candidates().equals(cell2.candidates()) && cell1 != cell2 && !cell1.isPeer(cell2);
    }

    private BitSet getRemotePairCandidates(List<Cell> chute, Cell cell1, Cell cell2) {
        BitSet candidates = new BitSet(9);
        for (Cell cell : chute) {
            if (cell == cell1 || cell == cell2 || cell.isPeer(cell1) || cell.isPeer(cell2)) continue;
            if (cell.isSolved()) {
                candidates.set(cell.getValue());
            } else {
                candidates.or(cell.candidates());
            }
        }
        candidates.and(cell1.candidates());
        return candidates;
    }

    private boolean processSingleCandidate(Cell cell1, Cell cell2, BitSet candidates, List<Cell> changed) {
        if (candidates.cardinality() != 1) return false;
        int digit = candidates.nextSetBit(0);
        for (Cell cell : grid.getCells(Predicates.isPeerOf(cell1).and(Predicates.isPeerOf(cell2))
                .and(Predicates.containsCandidate(digit)))) {
            cell.removeCandidate(digit);
            changed.add(cell);
        }
        if (!changed.isEmpty()) {
            incrementCounter();
            log("Remote pair %s in %s and %s%n- Removed candidate %d from %s%n", cell1.candidates(), cell1, cell2, digit, changed);
            return true;
        }
        return false;
    }

    private boolean processDoubleElimination(Cell cell1, Cell cell2, BitSet candidates, List<Cell> changed) {
        if (!candidates.isEmpty()) return false;
        BitSet digits = (BitSet) cell1.candidates().clone();
        for (Cell cell : grid.getCells(Predicates.isPeerOf(cell1).and(Predicates.isPeerOf(cell2))
                .and(c -> c.candidates().intersects(digits)))) {
            BitSet removed = cell.removeCandidates(digits);
            if (!removed.isEmpty()) {
                log("- Removed candidate(s) %s from %s%n", removed, cell);
                changed.add(cell);
            }
        }
        if (!changed.isEmpty()) {
            incrementCounter();
            log(0, "Remote pair %s (double elimination) in %s and %s%n", digits, cell1, cell2);
            return true;
        }
        return false;
    }
}