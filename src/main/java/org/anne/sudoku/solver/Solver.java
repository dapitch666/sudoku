package org.anne.sudoku.solver;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.solver.techniques.*;

import java.util.ArrayList;
import java.util.List;

public class Solver {
    Grid grid;
    List<SolvingTechnique> techniques;
    Grade highestDifficulty = Grade.UNKNOWN;

    public Solver(String puzzle) {
        this.grid = new Grid(puzzle);
        this.techniques = List.of(
                // VERY_EASY
                new NakedSingles(),
                new HiddenSingles(),
                new NakedPairs(),
                // EASY
                new NakedTriples(),
                new HiddenPairs(),
                // MODERATE
                new HiddenTriples(),
                new NakedQuads(),
                new HiddenQuads(),
                new IntersectionRemoval(),
                // HARD
                new XWings(),
                new ChuteRemotePairs(),
                new SimpleColoring(),
                new YWings(),
                new RectangleElimination(),
                new SwordFish(),
                new XYZWings(),
                new BiValueUniversalGrave(),
                // VERY_HARD
                new XCycles(),
                new XYChains(),
                new ThreeDMedusa(),
                new JellyFish(),
                new UniqueRectangles(),
                new Fireworks(),
                new SKLoops(),
                new ExtendedUniqueRectangles(),
                new HiddenUniqueRectangles(),
                new WXYZWings(),
                new SubsetExclusion(),
                // INSANE
                new Exocets(),
                new GroupedXCycles(),
                new FinnedXWings(),
                new FinnedSwordFish()
        );
    }

    public void solve() {
        int steps = 0;
        boolean changed;
        StringBuilder sb = new StringBuilder();
        do {
            if (grid.isSolved()) {
                System.out.println();
                techniques.forEach(SolvingTechnique::printCounters);
                break;
            }
            changed = false;
            steps++;
            grid.checkForSolvedCells();
            grid.showPossible();
//            System.out.println(grid);
            List<Cell> changedCells = new ArrayList<>();
            for (SolvingTechnique technique : techniques) {
                changedCells.addAll(technique.apply(grid));
                if (changedCells.isEmpty()) {
                    continue;
                }
                if (technique.getDifficulty().getLevel() > highestDifficulty.getLevel()) {
                    highestDifficulty = technique.getDifficulty();
                }
                System.out.printf("Step %d: %s%n%s", steps, technique.getName(), technique.getLog());
                for (Cell cell : changedCells) {
                    if (cell.getCandidateCount() == 1) {
                        grid.set(cell.index(), cell.getFirstCandidate(), false);
                        sb.append(String.format("Last candidate {%d} in %s changed to solution%n", cell.getValue(), cell));
                    }
                }
                System.out.println(sb);
                sb.setLength(0);
                changed = true;
                break;
            }
        } while (changed);
    }

    public int getCounter(String techniqueName) {
        for (SolvingTechnique technique : techniques) {
            if (technique.getName().equals(techniqueName)) {
                return technique.getCounter();
            }
        }
        return 0;
    }

    public Grade getGrade() {
        return highestDifficulty;
    }

    public Grid getGrid() {
        return grid;
    }

}
