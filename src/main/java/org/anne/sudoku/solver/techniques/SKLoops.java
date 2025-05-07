package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.*;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SKLoops extends SolvingTechnique {
    public SKLoops() {
        super("SK Loops", Grade.VERY_HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Cross[]> rectangles = findRectangles(grid);
        for (Cross[] rectangle : rectangles) {
            var loops = getLoops(rectangle);
            var lockedCells = Arrays.stream(rectangle)
                    .flatMap(cross -> Arrays.stream(cross.getCells()))
                    .toList();
            for (List<BitSet> loop : loops) {
                // Check if the loop is valid and apply the technique
                if (isValidLoop(loop, rectangle)) {
                    Link[] links = new Link[8];
                    links[0] = new Link(UnitType.ROW, rectangle[0].getRow(), loop.get(0));
                    links[1] = new Link(UnitType.BOX, rectangle[1].getBox(), loop.get(1));
                    links[2] = new Link(UnitType.COL, rectangle[1].getCol(), loop.get(2));
                    links[3] = new Link(UnitType.BOX, rectangle[2].getBox(), loop.get(3));
                    links[4] = new Link(UnitType.ROW, rectangle[2].getRow(), loop.get(4));
                    links[5] = new Link(UnitType.BOX, rectangle[3].getBox(), loop.get(5));
                    links[6] = new Link(UnitType.COL, rectangle[3].getCol(), loop.get(6));
                    links[7] = new Link(UnitType.BOX, rectangle[0].getBox(), loop.get(7));
                    List<Cell> changed = eliminateCandidates(links, lockedCells);
                    if (!changed.isEmpty()) {
                        log(0, "SK Loop type %s-%s-%s-%s-%s-%s-%s-%s- on %s, %s, %s, %s (%s)%n",
                                links[0].lockedCandidates.cardinality(),
                                links[1].lockedCandidates.cardinality(),
                                links[2].lockedCandidates.cardinality(),
                                links[3].lockedCandidates.cardinality(),
                                links[4].lockedCandidates.cardinality(),
                                links[5].lockedCandidates.cardinality(),
                                links[6].lockedCandidates.cardinality(),
                                links[7].lockedCandidates.cardinality(),
                                rectangle[0].center, rectangle[1].center, rectangle[2].center, rectangle[3].center,
                                loop);
                        incrementCounter();
                        return changed;
                    }
                }
            }
        }
        return List.of();
    }

    private List<Cell> eliminateCandidates(Link[] links, List<Cell> lockedCells) {
        /* Given a rectangle of crosses, we can eliminate:
         *  - in each of its blocks: any inner candidate-Number of this block that is not in any
         *    end of the cross in this block,
         *  - in each “central” row of consecutive row-aligned crosses: any horizontal outer candidate-Number
         *    (they are the same for the two crosses) that is not in any end of the crosses in the two blocks,
         *  - in each “central” column of consecutive column-aligned crosses: any vertical outer candidate-Number
         *    (they are the same for the two crosses) that is not in any end of the crosses in the two blocks.
         */
        List<Cell> changed = new ArrayList<>();
        for (Link link : links) {
            for (Cell cell : grid.getCells(Predicates.inUnit(link.unitType, link.unitIndex)
                    .and(cell -> !lockedCells.contains(cell)))) {

                BitSet removedCandidates = cell.removeCandidates(link.lockedCandidates);
                if (removedCandidates.isEmpty()) continue;
                changed.add(cell);
                log("%s removed from %s%n", removedCandidates, cell);
            }
        }
        return changed;
    }

    private boolean isValidLoop(List<BitSet> loop, Cross[] rectangle) {
        // The loop is valid if the sum of the links should be equal to 16.
        // For each solved or given cell, the link count should be increased by one.
        return loop.stream().mapToInt(BitSet::cardinality).sum() + Arrays.stream(rectangle).mapToInt(Cross::getSolvedCellsCount).sum() == 16;
    }

    private List<Cross[]> findRectangles(Grid grid) {
        List<Cross[]> rectangles = new ArrayList<>();
        var crosses = getCrosses(grid.getCells(Predicates.solvedCells));
        for (int i = 0; i < crosses.size(); i++) {
            for (int j = i + 1; j < crosses.size(); j++) {
                for (int k = j + 1; k < crosses.size(); k++) {
                    for (int l = k + 1; l < crosses.size(); l++) {
                        Cross[] rectangle = {crosses.get(i), crosses.get(j), crosses.get(l), crosses.get(k)};
                        if (isValidRectangle(rectangle)) {
                            rectangles.add(rectangle);
                        }
                    }
                }
            }
        }
        return rectangles;
    }

    private List<List<BitSet>> getLoops(Cross[] rectangle) {
        List<List<BitSet>> loops = new ArrayList<>();

        // Define the links between the crosses
        Cross first = rectangle[0];
        Cross second = rectangle[1];
        Cross third = rectangle[2];
        Cross fourth = rectangle[3];

        // Define the order of links
        List<Pair<BitSet>> links = List.of(
                new Pair<>(first.rowCandidates(), second.rowCandidates()),
                new Pair<>(second.rowCandidates(), second.colCandidates()),
                new Pair<>(second.colCandidates(), third.colCandidates()),
                new Pair<>(third.colCandidates(), third.rowCandidates()),
                new Pair<>(third.rowCandidates(), fourth.rowCandidates()),
                new Pair<>(fourth.rowCandidates(), fourth.colCandidates()),
                new Pair<>(fourth.colCandidates(), first.colCandidates()),
                new Pair<>(first.colCandidates(), first.rowCandidates())
        );

        // Recursively build loops from the links
        buildLoops(loops, new ArrayList<>(), links, 0);

        return loops;
    }

    private void buildLoops(List<List<BitSet>> loops, List<BitSet> currentLoop, List<Pair<BitSet>> links, int index) {
        if (index == links.size()) {
            // Base case: Check if the loop is complete and add it to the list of loops
            BitSet lastLoop = (BitSet) currentLoop.getLast().clone();
            lastLoop.or(currentLoop.getFirst());
            if (lastLoop.equals(links.getFirst().first())) {
                loops.add(new ArrayList<>(currentLoop));
            }
            return;
        }

        Pair<BitSet> link = links.get(index);
        BitSet first = link.first();
        BitSet second = link.second();

        // Find common candidates between the two parts of the link, excluding the previous link
        BitSet commonCandidates = (BitSet) first.clone();
        commonCandidates.and(second);
        if (!currentLoop.isEmpty()) commonCandidates.andNot(currentLoop.getLast());

        // Generate all subsets of the common candidates with size between 1 and 3
        List<BitSet> subsets = generateSubsets(commonCandidates);

        for (BitSet subset : subsets) {
            // Ensure all candidates are used
            if (!currentLoop.isEmpty() && currentLoop.getLast().cardinality() + subset.cardinality() != first.cardinality()) {
                continue;
            }

            // Add the subset to the current loop and recurse
            currentLoop.add(subset);
            buildLoops(loops, currentLoop, links, index + 1);
            currentLoop.removeLast(); // Backtrack
        }
    }

    private List<BitSet> generateSubsets(BitSet candidates) {
        List<BitSet> subsets = new ArrayList<>();
        int[] candidateArray = candidates.stream().toArray();

        // Generate subsets using combinations
        for (int size = 1; size <= 3; size++) {
            generateCombinations(candidateArray, size, 0, new BitSet(), subsets);
        }

        return subsets;
    }

    private void generateCombinations(int[] candidates, int size, int start, BitSet current, List<BitSet> subsets) {
        if (current.cardinality() == size) {
            subsets.add((BitSet) current.clone());
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            current.set(candidates[i]);
            generateCombinations(candidates, size, i + 1, current, subsets);
            current.clear(candidates[i]); // Backtrack
        }
    }

    private List<Cross> getCrosses(Cell[] cells) {
        List<Cross> crosses = new ArrayList<>();
        for (Cell cell : cells) {
            Cell[] rowCells = grid.getCells(Predicates.inUnit(UnitType.ROW, cell.getRow()).and(Predicates.inUnit(UnitType.BOX, cell.getBox())).and(c -> c != cell));
            Cell[] colCells = grid.getCells(Predicates.inUnit(UnitType.COL, cell.getCol()).and(Predicates.inUnit(UnitType.BOX, cell.getBox())).and(c -> c != cell));
            Cross cross = new Cross(cell, rowCells, colCells);
            if (cross.isValid()) {
                crosses.add(cross);
            }
        }
        return crosses;
    }

    private boolean isValidRectangle(Cross[] rectangle) {
        return Arrays.stream(rectangle).map(Cross::getRow).distinct().count() == 2
                && Arrays.stream(rectangle).map(Cross::getCol).distinct().count() == 2
                && Arrays.stream(rectangle).map(Cross::getBox).distinct().count() == 4;
    }

    record Cross(Cell center, Cell[] rowCells, Cell[] colCells) {
        int getRow() {
            return center.getRow();
        }

        int getCol() {
            return center.getCol();
        }

        int getBox() {
            return center.getBox();
        }

        Cell[] getCells() {
            return new Cell[]{rowCells[0], rowCells[1], colCells[0], colCells[1]};
        }

        BitSet rowCandidates() {
            BitSet candidates = new BitSet(9);
            for (Cell cell : rowCells) {
                if (!cell.isSolved()) {
                    candidates.or(cell.candidates());
                }
            }
            return candidates;
        }

        BitSet colCandidates() {
            BitSet candidates = new BitSet(9);
            for (Cell cell : colCells) {
                if (!cell.isSolved()) {
                    candidates.or(cell.candidates());
                }
            }
            return candidates;
        }

        public boolean isValid() {
            if (Arrays.stream(rowCells).filter(Predicates.solvedCells).count() > 1 ||
                    Arrays.stream(colCells).filter(Predicates.solvedCells).count() > 1) {
                return false;
            }
            for (Cell cell : getCells()) {
                if (!cell.isSolved() && cell.getCandidateCount() > 4) {
                    return false;
                }
            }
            return true;
        }

        public int getSolvedCellsCount() {
            return (int) Arrays.stream(getCells()).filter(Cell::isSolved).count();
        }
    }

    record Link(UnitType unitType, int unitIndex, BitSet lockedCandidates) {
    }
}