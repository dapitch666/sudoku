package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class ChuteRemotePairs extends SolvingTechnique {
    public ChuteRemotePairs() {
        super("Chute Remote Pairs", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (int index = 0; index < 3; index++) {
            for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
                List<Cell> chute = new ArrayList<>(List.of(grid.getCells(unitType, index * 3)));
                chute.addAll(List.of(grid.getCells(unitType, index * 3 + 1)));
                chute.addAll(List.of(grid.getCells(unitType, index * 3 + 2)));
                for (Cell cell1 : chute) {
                    if (cell1.getCandidateCount() == 2) {
                        for (Cell cell2 : chute) {
                            if (cell2 != cell1 && cell2.getCandidateCount() == 2 && cell1.getCandidates().equals(cell2.getCandidates()) && cell1.getBox() != cell2.getBox()) {
                                List<Integer> remotePair = cell1.getCandidates();
                                Set<Integer> intSet = new HashSet<>();
                                for (Cell cell : chute) {
                                    if (cell.getRow() != cell1.getRow() && cell.getRow() != cell2.getRow() && cell.getCol() != cell2.getCol()
                                            && cell.getCol() != cell1.getCol() && cell.getBox() != cell1.getBox() && cell.getBox() != cell2.getBox()) {
                                        intSet.addAll(cell.getCandidates());
                                        if (cell.isSolved()) {
                                            intSet.add(cell.getValue());
                                        }
                                    }
                                }
                                if (remotePair.stream().filter(intSet::contains).count() == 1) {
                                    int i = remotePair.stream().filter(intSet::contains).findFirst().orElseThrow();
                                    List<Cell> cell1Peers = Arrays.stream(grid.getPeers(cell1)).toList();
                                    List<Cell> peers = Arrays.stream(grid.getPeers(cell2)).filter(cell1Peers::contains).toList();
                                    for (Cell cell : peers) {
                                        if (cell.removeCandidate(i)) {
                                            changed.add(cell);
                                            log("Chute remote pair %s in %s and %s. Removed %s from %s%n", remotePair, cell1, cell2, i, cell);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!changed.isEmpty()) incrementCounter();
        return changed;
    }
}
