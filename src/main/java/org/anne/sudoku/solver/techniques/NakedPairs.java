package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class NakedPairs extends SolvingTechnique {
    public NakedPairs() {
        super("Naked Pairs", Grade.VERY_EASY);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Cell[] cells = grid.getCells(Predicates.inUnit(unitType, unitIndex).and(Predicates.biValueCells));
                for (int i = 0; i < cells.length; i++) {
                    for (int j = i + 1; j < cells.length; j++) {
                        if (!cells[i].candidates().equals(cells[j].candidates())) continue;
                        BitSet pair = cells[i].candidates();
                        Cell[] pairCells = {cells[i], cells[j]};
                        Map<Cell, BitSet> map = new HashMap<>();
                        for (Cell cell : grid.getCells(Predicates.inUnit(unitType, unitIndex).and(Predicates.unsolvedCells)
                                .and(c -> !Arrays.asList(pairCells).contains(c)))) {
                            BitSet removed = cell.removeCandidates(pair.stream().boxed().toList());
                            if (removed.isEmpty()) continue;
                            map.put(cell, removed);
                        }
                        if (map.isEmpty()) continue;
                        incrementCounter();
                        log("Naked pair %s in %s, on cells %s%n", pair, unitType.toString(unitIndex), Arrays.toString(pairCells));
                        map.keySet().forEach(cell -> log("- Removed candidate(s) %s from %s%n", map.get(cell), cell));
                        changed.addAll(map.keySet());
                    }
                }
            }
        }
        return changed;
    }
}
