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
                new SwordFish()//,
                //new XCycles()
                );
    }

    public static void main(String[] args) {
        ManualSolver manualSolver = new ManualSolver("4..8....3..6.1.4.9.....5....1..6..92...3.1...64..5..8....6.....9.7.8.1..8....9..4");
        // ManualSolver manualSolver = new ManualSolver(".3.6.9.2....28....1.......9......65372..6..91365......2.......7....16....1.5.7.4.");
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
                        sb.append(String.format("Last candidate, %d, in %s changed to solution%n", cell.getValue(), cell.getPosition()));
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
