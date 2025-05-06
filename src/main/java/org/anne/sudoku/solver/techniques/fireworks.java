package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;

import java.util.*;
import java.util.stream.Collectors;

public class fireworks extends SolvingTechnique {
    public fireworks() {
        super("Fireworks", Grade.VERY_HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        for (Cell cell : grid.getCells(Predicates.unsolvedCells)) {
            Map<Integer, List<Cell>> fireworks = new HashMap<>();

            Cell[] peers = grid.getCells(Predicates.peers(cell).and(Predicates.unsolvedCells)
                    .and(Predicates.inUnit(UnitType.BOX, cell.getBox()).negate()));

            Map<Integer, List<Cell>> map = new HashMap<>();
            for (int digit : cell.getCandidates()) {
                List<Cell> cells = new ArrayList<>();
                for (Cell peer : peers) {
                    if (peer.hasCandidate(digit)) {
                        cells.add(peer);
                    }
                }
                if (!cells.isEmpty()) {
                    map.put(digit, cells);
                }
            }
            if (map.size() >= 3) {
                for (Map.Entry<Integer, List<Cell>> entry : map.entrySet()) {
                    int digit = entry.getKey();
                    List<Cell> cells = entry.getValue();
                    if (cells.size() == 2) {
                        Cell cell1 = cells.get(0);
                        Cell cell2 = cells.get(1);
                        if (!cell1.isPeer(cell2)) {
                            fireworks.put(digit, List.of(cell, cell1, cell2));
                        }
                    } else if (cells.size() == 1) {
                        fireworks.put(digit, List.of(cell, cells.getFirst()));
                    }
                }
            }
            if (fireworks.size() == 3) {
                List<Cell> changed = new ArrayList<>();
                List<Integer> candidates = new ArrayList<>(fireworks.keySet());
                Set<Cell> cells = fireworks.values().stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());
                if (cells.size() != 3) {
                    continue;
                }
                for (Cell c : cells) {
                    List<Integer> removed = c.removeAllBut(candidates);
                    if (!removed.isEmpty()) {
                        changed.add(c);
                        log("Removed candidates %s from %s%n",removed, c);
                    }
                }
                if (!changed.isEmpty()) {
                    log(0, "FireWorks in %s on %s%n", cells, candidates);
                    incrementCounter();
                    return changed;
                }
            }
        }
        return List.of();
    }
}
