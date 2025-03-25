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
                new ChuteRemotePairs()
                );
    }

    public static void main(String[] args) {
        // ManualSolver manualSolver = new ManualSolver("...7.5..6....4..81....3..5..41.....8.6.....2.5.....43.....7....978.5....3..2.1...");
        ManualSolver manualSolver = new ManualSolver("...9.5..........12.6.....5.39..5..6....3....4.4..6..85.3.....9.85..1.......2.7...");
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
            // System.out.println("Step " + steps + ":");

            List<Cell> changedCells = new ArrayList<>();
            for (SolvingTechnique technique : techniques) {
                if (technique.getName().equals("Naked Singles")) {
                    changedCells.addAll(technique.apply(grid, null, -1, sb));
                } else if (technique.getName().equals("XWings")) {
                    changedCells.addAll(technique.apply(grid, UnitType.ROW, -1, sb));
                    changedCells.addAll(technique.apply(grid, UnitType.COLUMN, -1, sb));
                } else if (technique.getName().equals("Chute Remote Pairs")) {
                    for (int i = 0; i < 3; i++) {
                        changedCells.addAll(technique.apply(grid, UnitType.ROW, i, sb));
                        changedCells.addAll(technique.apply(grid, UnitType.COLUMN, i, sb));
                    }
                } else {
                    for (UnitType unitType : UnitType.values()) {
                        for (int i = 0; i < 9; i++) {
                            changedCells.addAll(technique.apply(grid, unitType, i, sb));
                        }
                    }
                }
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
