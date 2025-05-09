package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class NakedQuads extends SolvingTechnique {
    public NakedQuads() {
        super("Naked Quads", Grade.MODERATE);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Cell[] cells = grid.getCells(Predicates.inUnit(unitType, unitIndex)
                        .and(cell -> cell.getCandidateCount() >= 2 && cell.getCandidateCount() <= 4));
                for (int i = 0; i < cells.length; i++) {
                    for (int j = i + 1; j < cells.length; j++) {
                        for (int k = j + 1; k < cells.length; k++) {
                            for (int l = k + 1; l < cells.length; l++) {
                                Cell[] quadCells = {cells[i], cells[j], cells[k], cells[l]};
                                BitSet quad = new BitSet(9);
                                Arrays.stream(quadCells).forEach(c -> quad.or(c.candidates()));
                                if (quad.cardinality() != 4) continue;

                                Map<Cell, BitSet> map = new HashMap<>();
                                for (Cell cell : grid.getCells(Predicates.inUnit(unitType, unitIndex)
                                        .and(Predicates.unsolvedCells)
                                        .and(c -> !Arrays.asList(quadCells).contains(c)))) {
                                    BitSet removed = cell.removeCandidates(quad);
                                    if (removed.isEmpty()) continue;
                                    map.put(cell, removed);
                                }
                                if (map.isEmpty()) continue;
                                incrementCounter();
                                log("Naked quad %s in %s, on cells %s%n",
                                        quad, unitType.toString(unitIndex), Arrays.toString(quadCells));
                                map.keySet().forEach(cell -> log("- Removed candidate(s) %s from %s%n", map.get(cell), cell));
                                changed.addAll(map.keySet());
                            }
                        }
                    }
                }
            }
        }
        return changed;
    }
}
