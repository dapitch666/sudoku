package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class NakedTriples extends SolvingTechnique {
    public NakedTriples() {
        super("Naked Triples", Grade.EASY);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Cell[] cells = grid.getCells(Predicates.inUnit(unitType, unitIndex)
                        .and(cell -> cell.getCandidateCount() == 2 || cell.getCandidateCount() == 3));
                for (int i = 0; i < cells.length; i++) {
                    for (int j = i + 1; j < cells.length; j++) {
                        for (int k = j + 1; k < cells.length; k++) {
                            Cell[] tripleCells = {cells[i], cells[j], cells[k]};
                            BitSet triple = new BitSet(9);
                            Arrays.stream(tripleCells).forEach(c -> triple.or(c.candidates()));
                            if (triple.cardinality() != 3) continue;

                            Map<Cell, BitSet> map = new HashMap<>();
                            for (Cell cell : grid.getCells(Predicates.inUnit(unitType, unitIndex)
                                    .and(Predicates.unsolvedCells)
                                    .and(c -> !Arrays.asList(tripleCells).contains(c)))) {
                                BitSet removed = cell.removeCandidates(triple);
                                if (removed.isEmpty()) continue;
                                map.put(cell, removed);
                            }
                            if (map.isEmpty()) continue;
                            incrementCounter();
                            log("Naked triple %s in %s, on cells %s%n", triple, unitType.toString(unitIndex), Arrays.toString(tripleCells));
                            map.keySet().forEach(cell -> log("- Removed %s from %s%n", map.get(cell), cell));
                            changed.addAll(map.keySet());
                        }
                    }
                }
            }
        }
        return changed;
    }
}
