package org.anne.sudoku.grader;

import org.anne.sudoku.grader.techniques.*;
import org.anne.sudoku.utils.PrintUtils;

import java.util.*;

public class ManualSolver {
    Grid grid;
    List<SolvingTechnique> techniques;

    public ManualSolver(String puzzle) {
        this.grid = new Grid(puzzle);
        this.techniques = List.of(
                new NakedSingles(),
                new HiddenSingles(),
                new NakedPairs(),
                new NakedTriples(),
                new HiddenPairs(),
                new HiddenTriples(),
                new NakedQuads(),
                new HiddenQuads(),
                new PointingPairs(),
                new BoxLineReduction(),
                new XWings(),
                new ChuteRemotePairs(),
                new SimpleColoring(),
                new YWings(),
                new RectangleElimination(),
                new SwordFish(),
                new XyzWings(),
                new BiValueUniversalGrave(),
                new XCycles(),
                new XYChains()
                );
    }

    public static void main(String[] args) {
        // ManualSolver manualSolver = new ManualSolver(".4...58..7...1.9....3..71..4..7......5.9.8.4......2..8..95..7......2...5..41...9.");
        // ManualSolver manualSolver = new ManualSolver(".........89.632..4..2.9.8...7....6..9....5..8..1....3...3.1.2..6..873.19.........");
        ManualSolver manualSolver = new ManualSolver(".3..7.45..728...9....91..6....5.....3.......7.....6....8..21....5...924..29.6..3.");
        System.out.println(PrintUtils.printOne(manualSolver.grid.currentState()));
        manualSolver.solve();
        System.out.println(PrintUtils.printOne(manualSolver.grid.currentState()));
    }

    void solve() {
        int steps = 0;
        boolean changed;
        StringBuilder sb = new StringBuilder();
        do {
            if (grid.isSolved()) {
                printCounters();
                break;
            }
            changed = false;
            steps++;
            grid.checkForSolvedCells();
            grid.showPossible();
            System.out.println(grid);
            List<Cell> changedCells = new ArrayList<>();
            for (SolvingTechnique technique : techniques) {
                changedCells.addAll(technique.apply(grid, sb));
                if (changedCells.isEmpty()) {
                    continue;
                }
                System.out.printf("Step %d: %s%n", steps, technique.getName());
                for (Cell cell : changedCells) {
                    if (cell.getCandidateCount() == 1) {
                        cell.setValue(cell.getFirstCandidate());
                        sb.append(String.format("Last candidate, %d, in %s changed to solution%n", cell.getValue(), cell));
                    }
                }
                System.out.println(sb);
                sb.setLength(0);
                changed = true;
                break;
            }
        } while (changed);
    }

    private void printCounters() {
        System.out.println();
        for (SolvingTechnique technique : techniques) {
            System.out.println(technique.getName() + ": " + technique.getCounter());
        }
    }

    public int getCounter(String techniqueName) {
        for (SolvingTechnique technique : techniques) {
            if (technique.getName().equals(techniqueName)) {
                return technique.getCounter();
            }
        }
        return 0;
    }
}
