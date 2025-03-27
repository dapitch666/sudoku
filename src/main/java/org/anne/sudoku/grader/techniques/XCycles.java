package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;

import java.util.*;


// TODO: Get a better understanding of X-Cycles as they don't seem to alternate between strong and weak links.

public class XCycles implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changedCells = new ArrayList<>();

        // Try each digit
        for (int digit = 1; digit <= 9; digit++) {
            if (digit == 6) {
                System.out.println("here");
            }
            List<XCycle> allCycles = findXCyclesForDigit(grid, digit);
            for (XCycle cycle : allCycles) {
                if (cycle.isNice) {
                    // Eliminate candidates based on the nice cycle
                    changedCells.addAll(eliminateCandidates(grid, cycle, sb));
                    if (!changedCells.isEmpty()) {
                        sb.insert(0, String.format("X-Cycles of %d found on chain %s:%n", digit, cycle.cells.stream().map(Cell::getPosition).toList()));
                        return changedCells;
                    }
                }
            }
        }
        return changedCells;
    }

    private List<XCycle> findXCyclesForDigit(Grid grid, int digit) {
        List<XCycle> cycles = new ArrayList<>();
        Map<Cell, List<Cell>> strongLinks = grid.findStrongLinks(digit);
        Map<Cell, List<Cell>> weakLinks = grid.findWeakLinks(digit);

        // Find cycles using strong and weak links
        for (Cell start : strongLinks.keySet()) {
            findCycles(start, start, digit, new ArrayList<>(), strongLinks, weakLinks, cycles, true);
        }

        return cycles;
    }

    private void findCycles(Cell start, Cell current, int digit, List<Cell> path, Map<Cell, List<Cell>> strongLinks, Map<Cell, List<Cell>> weakLinks, List<XCycle> cycles, boolean isStrong) {
        path.add(current);

        Map<Cell, List<Cell>> links = isStrong ? strongLinks : weakLinks;

        for (Cell next : links.getOrDefault(current, Collections.emptyList())) {
            if (next.equals(start) && path.size() > 2) {
                cycles.add(new XCycle(new ArrayList<>(path), digit, true));
            } else if (!path.contains(next)) {
                findCycles(start, next, digit, path, strongLinks, weakLinks, cycles, !isStrong);
            }
        }

        path.removeLast();
    }

    private List<Cell> eliminateCandidates(Grid grid, XCycle cycle, StringBuilder sb) {
        List<Cell> changedCells = new ArrayList<>();
        Set<Cell> cycleCells = new HashSet<>(cycle.cells);

        for (Cell cell : cycleCells) {
            for (Cell peer : grid.getPeers(cell)) {
                if (!cycleCells.contains(peer) && peer.isCandidate(cycle.digit)) {
                    if (peer.removeCandidate(cycle.digit)) {
                        changedCells.add(peer);
                        sb.append(String.format("%d removed from %s%n", cycle.digit, peer.getPosition()));
                    }
                }
            }
        }

        counter[0]++;
        return changedCells;
    }

    public static class XCycle {
        List<Cell> cells;
        int digit;
        boolean isNice;

        public XCycle(List<Cell> cells, int digit, boolean isNice) {
            this.cells = cells;
            this.digit = digit;
            this.isNice = isNice;
        }

        @Override
        public String toString() {
            return String.format("X-Cycle on %d (%sloop, length %d) found on cells %s%n", digit, isNice ? "Nice " : "", cells.size(), cells.stream().map(Cell::getPosition).toList());
        }
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
