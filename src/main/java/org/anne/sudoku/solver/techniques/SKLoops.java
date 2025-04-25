package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;
import java.util.stream.Stream;

public class SKLoops extends SolvingTechnique {
    public SKLoops() {
        super("SK Loops", Grade.VERY_HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<PseudoCell[]> rectangles = findRectangles(grid);
        for (PseudoCell[] rectangle : rectangles) {
            var loops = getLoops(rectangle);
            for (var loop : loops) {
                List<Cell> changed = eliminateCandidates(loop);
                if (!changed.isEmpty()) {
                    log(0, "SK Loop in %s on %s%n", Arrays.toString(rectangle), loop);
                    incrementCounter();
                    return changed;
                }
            }
/*
            List<Cell> changed = new ArrayList<>();
            for (int i : List.of(0, 4)) { // Row eliminations
                List<Cell> cells = Arrays.stream(grid.getCells(UnitType.ROW, pairs.get(i).row))
                        .filter(cell -> cell.getBox() != pairs.get(i).box && cell.getBox() != pairs.get((i + 1) % pairs.size()).box)
                        .filter(Cell::isNotSolved)
                        .toList();
                for (Cell cell : cells) {
                    for (int digit : loop.get(i)) {
                        if (cell.isCandidate(digit)) {
                            cell.removeCandidate(digit);
                            changed.add(cell);
                            log("%d removed from %s%n", digit, cell);
                        }
                    }
                }
            }
            for (int i : List.of(2, 6)) { // Column eliminations
                List<Cell> cells = Arrays.stream(grid.getCells(UnitType.COL, pairs.get(i).col))
                        .filter(cell -> cell.getBox() != pairs.get(i).box && cell.getBox() != pairs.get((i + 1) % pairs.size()).box)
                        .filter(Cell::isNotSolved)
                        .toList();
                for (Cell cell : cells) {
                    for (int digit : loop.get(i)) {
                        if (cell.isCandidate(digit)) {
                            cell.removeCandidate(digit);
                            changed.add(cell);
                            log("%d removed from %s%n", digit, cell);
                        }
                    }
                }
            }
            for (int i : List.of(1, 3, 5, 7)) { // Box eliminations
                List<Cell> cells = Arrays.stream(grid.getCells(UnitType.BOX, pairs.get(i).box))
                        .filter(cell -> !List.of(pairs.get(i).cell1, pairs.get(i).cell2, pairs.get((i + 1) % pairs.size()).cell1, pairs.get((i + 1) % pairs.size()).cell2).contains(cell))
                        .filter(Cell::isNotSolved)
                        .toList();
                for (Cell cell : cells) {
                    for (int digit : loop.get(i)) {
                        if (cell.isCandidate(digit)) {
                            cell.removeCandidate(digit);
                            changed.add(cell);
                            log("%d removed from %s%n", digit, cell);
                        }
                    }
                }
            }
            if (!changed.isNotSolved()) {
                log(0, "SK Loop in %s on %s%n", Arrays.getPuzzle(rectangle), loop);
                incrementCounter();
                return changed;
            }*/
        }
        return List.of();
    }

    private List<Cell> eliminateCandidates(List<List<Integer>> loop) {
        /*Theorem 13.1: Given a belt of crosses, one can eliminate:
        - in each of its blocks: any inner candidate-Number of this block that is not in any
        end of the cross in this block,
                - in each “central” row of consecutive row-aligned crosses: any horizontal outer
        candidate-Number (they are the same for the two crosses) that is not in any end of
        the crosses in the two blocks,
        - in each “central” column of consecutive column-aligned crosses: any vertical
        outer candidate-Number (they are the same for the two crosses) that is not in any
        end of the crosses in the two blocks.*/
        return List.of();
    }

    /*private int countCandidates(Link[] loop) {
        int count = 0;
        for (Link link : loop) {
            count += link.candidates().size();
        }
        return count;
    }*/

    private List<PseudoCell[]> findRectangles(Grid grid) {
        List<PseudoCell[]> rectangles = new ArrayList<>();
        Cell[] solvedCells = grid.getSolvedCells();
        for (int i = 0; i < solvedCells.length; i++) {
            PseudoCell cellA = new PseudoCell(solvedCells[i], getCellsInBoxAndUnit(solvedCells[i], UnitType.ROW),
                    getCellsInBoxAndUnit(solvedCells[i], UnitType.ROW));
            for (int j = i + 1; j < solvedCells.length; j++) {
                PseudoCell cellB = new PseudoCell(solvedCells[j], getCellsInBoxAndUnit(solvedCells[j], UnitType.ROW),
                        getCellsInBoxAndUnit(solvedCells[j], UnitType.ROW));
                for (int k = j + 1; k < solvedCells.length; k++) {
                    PseudoCell cellD = new PseudoCell(solvedCells[k], getCellsInBoxAndUnit(solvedCells[k], UnitType.ROW),
                            getCellsInBoxAndUnit(solvedCells[k], UnitType.ROW));
                    for (int l = k + 1; l < solvedCells.length; l++) {
                        PseudoCell cellC = new PseudoCell(solvedCells[l], getCellsInBoxAndUnit(solvedCells[l], UnitType.ROW),
                                getCellsInBoxAndUnit(solvedCells[l], UnitType.ROW));
                        PseudoCell[] rectangle = {cellA, cellB, cellC, cellD};
                        if (isValidRectangle(rectangle)) {
                            rectangles.add(rectangle);
                        }
                    }
                }
            }
        }
        return rectangles;
    }

    private boolean isValidRectangle(PseudoCell[] rectangle) {
        if (Arrays.stream(rectangle).map(PseudoCell::row).distinct().count() != 2 ||
                Arrays.stream(rectangle).map(PseudoCell::col).distinct().count() != 2 ||
                Arrays.stream(rectangle).map(PseudoCell::box).distinct().count() != 4) {
            return false;
        }
        // If one row or one column contains more than 1 solved cell, no loop can be formed;
        if (Arrays.stream(rectangle)
                .flatMap(pseudoCell -> Arrays.stream(pseudoCell.rowCells()))
                .filter(Cell::isSolved)
                .count() > 1) {
            return false;
        }
        return Arrays.stream(rectangle)
                .flatMap(pseudoCell -> Arrays.stream(pseudoCell.colCells()))
                .filter(Cell::isSolved)
                .count() <= 1;
    }

    /*private List<Pair> getPairs(Cell[] rectangle) {
        return List.of(
                getPairsInBoxAndUnit(rectangle[0], UnitType.ROW),
                getPairsInBoxAndUnit(rectangle[1], UnitType.ROW),
                getPairsInBoxAndUnit(rectangle[1], UnitType.COL),
                getPairsInBoxAndUnit(rectangle[2], UnitType.COL),
                getPairsInBoxAndUnit(rectangle[2], UnitType.ROW),
                getPairsInBoxAndUnit(rectangle[3], UnitType.ROW),
                getPairsInBoxAndUnit(rectangle[3], UnitType.COL),
                getPairsInBoxAndUnit(rectangle[0], UnitType.COL)
        );
    }*/

    private List<List<List<Integer>>> getLoops(PseudoCell[] rectangle) {
        Link[] loop = new Link[8];
        loop[0] = new Link(rectangle[0].colCells(), rectangle[0].rowCells(), UnitType.BOX);
        loop[1] = new Link(rectangle[0].rowCells(), rectangle[1].rowCells(), UnitType.ROW);
        loop[2] = new Link(rectangle[1].rowCells(), rectangle[1].colCells(), UnitType.BOX);
        loop[3] = new Link(rectangle[1].colCells(), rectangle[2].colCells(), UnitType.COL);
        loop[4] = new Link(rectangle[2].colCells(), rectangle[2].rowCells(), UnitType.BOX);
        loop[5] = new Link(rectangle[2].rowCells(), rectangle[3].rowCells(), UnitType.ROW);
        loop[6] = new Link(rectangle[3].rowCells(), rectangle[3].boxCells(), UnitType.BOX);
        loop[7] = new Link(rectangle[3].colCells(), rectangle[0].colCells(), UnitType.COL);
        return getCombinations(loop);


        /*for (int i = 0; i < pairs.size(); i++) {
            if (i % 2 != 0) {
                links.add(new ArrayList<>());
                continue;
            }
            Pair pair1 = pairs.get(i);
            Pair pair2 = pairs.get((i + 1) % pairs.size());
            List<Integer> commonCandidates = new ArrayList<>(pair1.commonCandidates());
            // TODO: test each combination of common candidates
            commonCandidates.retainAll(pair2.commonCandidates());
            links.add(commonCandidates);
        }
        for (int i = 1; i < links.size(); i += 2) {
            List<Integer> link = links.get(i);
            List<Integer> link1 = links.get(i - 1);
            List<Integer> link2 = links.get((i + 1) % links.size());
            Set<Integer> candidates = new HashSet<>(pairs.get(i).allCandidates());
            candidates.addAll(pairs.get((i + 1) % pairs.size()).allCandidates());
            candidates.removeIf(d -> link1.contains(d) || link2.contains(d));
            link.addAll(candidates);
        }
        return links;*/
    }

    private List<List<List<Integer>>> getCombinations(Link[] loop) {
        List<List<List<Integer>>> result = new ArrayList<>();
        List<List<Integer>> currentCombination = new ArrayList<>(Collections.nCopies(loop.length, null));
        int totalCandidates = 16 - countSolvedCells(loop); // Adjust total for solved cells
        generateCombinations(loop, 0, totalCandidates, currentCombination, result);
        return result;
    }

    private void generateCombinations(Link[] loop, int index, int remainingCandidates,
                                      List<List<Integer>> currentCombination, List<List<List<Integer>>> result) {
        if (index == loop.length) {
            if (remainingCandidates == 0) {
                result.add(new ArrayList<>(currentCombination));
            }
            return;
        }

        Set<Integer> candidates = new HashSet<>(getCommonCandidates(loop[index].cells1(), loop[index].cells2()));
        if (candidates.isEmpty()) return;

        for (Set<Integer> subset : generateSubsets(candidates, 1, 3)) { // Generate subsets of size 1 to 3
            if (index > 0 && !Collections.disjoint(subset, currentCombination.get(index - 1))) {
                continue; // Skip if consecutive links share candidates
            }
            if (remainingCandidates - subset.size() < 0) {
                continue; // Skip if exceeding total candidates
            }

            currentCombination.set(index, new ArrayList<>(subset));
            generateCombinations(loop, index + 1, remainingCandidates - subset.size(), currentCombination, result);
            currentCombination.set(index, null); // Backtrack
        }
    }

    private int countSolvedCells(Link[] loop) {
        int count = 0;
        for (Link link : loop) {
            for (Cell cell : link.cells1()) {
                if (cell.isSolved()) count++;
            }
            for (Cell cell : link.cells2()) {
                if (cell.isSolved()) count++;
            }
        }
        return count;
    }

    private Set<Set<Integer>> generateSubsets(Set<Integer> candidates, int minSize, int maxSize) {
        List<Integer> candidateList = new ArrayList<>(candidates);
        Set<Set<Integer>> subsets = new HashSet<>();
        generateSubsetsRecursive(candidateList, 0, new HashSet<>(), subsets, minSize, maxSize);
        return subsets;
    }

    private void generateSubsetsRecursive(List<Integer> candidates, int index, Set<Integer> current,
                                          Set<Set<Integer>> subsets, int minSize, int maxSize) {
        if (current.size() >= minSize && current.size() <= maxSize) {
            subsets.add(new HashSet<>(current));
        }
        if (current.size() == maxSize || index == candidates.size()) {
            return;
        }
        current.add(candidates.get(index));
        generateSubsetsRecursive(candidates, index + 1, current, subsets, minSize, maxSize);
        current.remove(candidates.get(index));
        generateSubsetsRecursive(candidates, index + 1, current, subsets, minSize, maxSize);
    }

    private static Set<Integer> getCommonCandidates(Cell[]... cellsArrays) {
        if (cellsArrays == null || cellsArrays.length == 0) return new HashSet<>();
        Set<Integer> commonCandidates = new HashSet<>(getAllCandidates(cellsArrays[0]));
        for (int i = 1; i < cellsArrays.length; i++) {
            commonCandidates.retainAll(getAllCandidates(cellsArrays[i]));
        }
        return commonCandidates;
    }

    private static List<Integer> getAllCandidates(Cell... cells) {
        return Arrays.stream(cells).flatMap(cell -> cell.getCandidates().stream()).distinct().toList();
    }

    private List<Set<Integer>> eachCombination(Set<Integer> possibilities) {
        List<Set<Integer>> result = new ArrayList<>();
        eachCombination(result, possibilities, new HashSet<>(), 0);
        return result;
    }

    private void eachCombination(List<Set<Integer>> result, Set<Integer> total, Set<Integer> current, int cursor) {
        List<Integer> list = new ArrayList<>(total);
        for (int i = cursor; i < list.size(); i++) {
            current.add(list.get(i));
            result.add(new HashSet<>(current));
            eachCombination(result, total, current, i + 1);
            current.remove(list.get(i));
        }
    }

    private int getSolvedCellsCount(PseudoCell[] rectangle) {
        return (int) Arrays.stream(rectangle)
                .flatMap(cell -> Stream.of(cell.cell1, cell.cell2, cell.cell3, cell.cell4))
                .filter(Cell::isSolved)
                .count();
    }

    private Pair getPairsInBoxAndUnit(Cell cell, UnitType unitType) {
        List<Cell> cells = new ArrayList<>();
        for (Cell peer : grid.getPeers(cell)) {
            if (peer.getBox() != cell.getBox()) continue;
            if (unitType == UnitType.ROW && peer.getRow() == cell.getRow()) {
                cells.add(peer);
            } else if (unitType == UnitType.COL && peer.getCol() == cell.getCol()) {
                cells.add(peer);
            }
        }
        return new Pair(cells.getFirst(), cells.getLast());
    }

    private Cell[] getCellsInBoxAndUnit(Cell cell, UnitType unitType) {
        List<Cell> cells = new ArrayList<>();
        for (Cell peer : grid.getPeers(cell)) {
            if (peer.getBox() != cell.getBox()) continue;
            if (unitType == UnitType.ROW && peer.getRow() == cell.getRow()) {
                cells.add(peer);
            } else if (unitType == UnitType.COL && peer.getCol() == cell.getCol()) {
                cells.add(peer);
            }
        }
        return cells.toArray(Cell[]::new);
    }

    record Pair(Cell cell1, Cell cell2, int row, int col, int box, List<Integer> leftCandidates,
                List<Integer> rightCandidates) {
        Pair(Cell cell1, Cell cell2) {
            this(cell1, cell2,
                    cell1.getRow() == cell2.getRow() ? cell1.getRow() : -1,
                    cell1.getCol() == cell2.getCol() ? cell1.getCol() : -1,
                    cell1.getBox() == cell2.getBox() ? cell1.getBox() : -1,
                    new ArrayList<>(), new ArrayList<>());
        }

/*        List<Integer> commonCandidates() {
            List<Integer> candidates = new ArrayList<>(cell1.getCandidates());
            candidates.removeIf(d -> !cell2.isCandidate(d));
            return new ArrayList<>(candidates);
        }*/

        List<Integer> commonCandidates() {
            List<Integer> candidates = new ArrayList<>();
            for (int digit = 1; digit <= 9; digit++) {
                if ((cell1.isSolved() || cell1.hasCandidate(digit)) && (cell2.isSolved() || cell2.hasCandidate(digit))) {
                    candidates.add(digit);
                }
            }
            return candidates;
        }

        Set<Integer> allCandidates() {
            Set<Integer> allCandidates = new HashSet<>(cell1.getCandidates());
            allCandidates.addAll(cell2.getCandidates());
            return allCandidates;
        }

        void setLeftCandidates(List<Integer> candidates) {
            leftCandidates.addAll(candidates);
        }

        void setRightCandidates(List<Integer> candidates) {
            rightCandidates.addAll(candidates);
        }

        @Override
        public String toString() {
            TreeSet<String> uniqueRows = new TreeSet<>(List.of(cell1.toString().substring(0, 1), cell2.toString().substring(0, 1)));
            TreeSet<String> uniqueCols = new TreeSet<>(List.of(cell1.toString().substring(1), cell2.toString().substring(1)));
            return String.join("", uniqueRows) + String.join("", uniqueCols);
        }
    }

    record PseudoCell(Cell cell, int row, int col, int box, Cell cell1, Cell cell2, Cell cell3, Cell cell4) {
        /*
        * Definition: a cross is defined by the following two sets of data and conditions:
        * 1) a pattern of cells:
        * – a block b;
        * – a row r and a column c that both intersect b; the intersection of r and c will be
        * called the “center” of the cross;
        * – two different cells, each in both row r and block b, and none equal to the
        * center of the cross; they will be called the horizontal ends of the cross;
        * – two different cells, each in both column c and block b, and none equal to the
        * center of the cross; they will be called the vertical ends of the cross.
        * The “center” of a cross is a conceptual center, it does not have to be the physical
        * center of block b. However, by a proper puzzle isomorphism, any “cross” can be
        * made to look like a physical cross (whence the name we have chosen for them). Notice that the
        * above conditions imply that the four “ends of the cross” are different cells.
        * 2) a pattern of candidates in the four ends of the cross:
        * – two different “horizontal outer” candidate-Numbers;
        * – two different “vertical outer” candidate-Numbers; (each of them may be equal
        * to an horizontal outer one);
        * – two different “inner” candidate-Numbers, each different from any of the
        * (horizontal and vertical) outer candidate-Numbers;
        * – none of the four ends is decided;
        * – each of the two horizontal ends of the cross contains only inner and horizontal
        * outer candidate-Numbers; each of the inner and horizontal outer candidate-Numbers
        * appears in at least one of the two horizontal ends of the cross;
        * – each of the two vertical ends of the cross contains only inner and vertical outer
        * candidate-Numbers; each of the inner and vertical outer candidate-Numbers appears
        * in at least one of the two horizontal ends of the cross.
        * Definitions:
        *   - Outer candidates: candidates that appear in the same row or the same col (across 2 crosses)
        *   - Inner candidates: candidates that appear only in the
        * */
        PseudoCell(Cell cell, Cell[] rowCells, Cell[] colCells) {
            this(cell, cell.getRow(), cell.getCol(), cell.getBox(), rowCells[0], rowCells[1], colCells[0], colCells[1]);
        }

        Cell[] rowCells() {
            return new Cell[]{cell1, cell2};
        }

        Cell[] colCells() {
            return new Cell[]{cell3, cell4};
        }

        Cell[] boxCells() {
            return new Cell[]{cell1, cell2, cell3, cell4};
        }
    }

    record Link(Cell[] cells1, Cell[] cells2, UnitType unitType) {
    }
}