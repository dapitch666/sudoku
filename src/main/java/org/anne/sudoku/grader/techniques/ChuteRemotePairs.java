package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class ChuteRemotePairs implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
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
                                            sb.append(String.format("Chute remote pair %s in %s and %s. Removed %s from %s%n", remotePair, cell1.getPosition(), cell2.getPosition(), i, cell.getPosition()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!changed.isEmpty()) incrementCounter(counter);
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
