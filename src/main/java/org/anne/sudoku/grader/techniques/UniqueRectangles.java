package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class UniqueRectangles extends SolvingTechnique {
    public UniqueRectangles() {
        super("Unique Rectangles");
    }

    private Grid grid;
    private StringBuilder sb;

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        this.grid = grid;
        this.sb = sb;
        for (Cell[] rectangle : grid.getRectangles()) {
            List<Cell> changed = applyRule1(rectangle);
            if (!changed.isEmpty()) return changed;
            changed = applyRule2(rectangle);
            if (!changed.isEmpty()) return changed;
            changed = applyRule2b(rectangle);
            if (!changed.isEmpty()) return changed;
            changed = applyRule2c(rectangle);
            if (!changed.isEmpty()) return changed;
            changed = applyRule3(rectangle);
            if (!changed.isEmpty()) return changed;
        }
        return List.of();
    }

    private List<Cell> applyRule1(Cell[] rectangle) {
        // cell1 and cell2 are in the same box
        if (rectangle[0].getBox() != rectangle[1].getBox()) return List.of();
        List<Integer> biValues = rectangle[0].getCandidates();
        // One of the other two cells must be bi-value
        if (!rectangle[2].isBiValue() && !rectangle[3].isBiValue()) return List.of();
        // The only non-bi-value cell is the one that can be changed
        Cell cell = !rectangle[2].isBiValue() ? rectangle[2] : rectangle[3];
        var removed = cell.removeCandidates(biValues);
        if (!removed.isEmpty()) {
            log(sb, "Unique Rectangle Type 1 found: %s, %s, %s, %s. Removed candidates %s from %s%n", rectangle[0], rectangle[1], rectangle[2], rectangle[3], removed, cell);
            incrementCounter();
            return List.of(cell);
        }
        return List.of();
    }

    private List<Cell> applyRule2(Cell[] rectangle) {
        // cell1 and cell2 are in the same box
        if (rectangle[0].getBox() != rectangle[1].getBox()) return List.of();
        // cell3 and cell4 are tri-value cells and share the same candidates
        if (rectangle[2].getCandidateCount() != 3 || !rectangle[3].getCandidates().equals(rectangle[2].getCandidates())) return List.of();
        int digit = rectangle[2].getCandidates().stream().filter(c -> !rectangle[0].isCandidate(c)).findFirst().orElseThrow();
        var changed = grid.getCommonPeersWithCandidate(rectangle[2], rectangle[3], digit);
        if (!changed.isEmpty()) {
            log(sb, "Unique Rectangle Type 2 found: %s, %s, %s, %s. Removed candidates %d from %s%n", rectangle[0], rectangle[1], rectangle[2], rectangle[3], digit, changed);
            changed.forEach(cell -> cell.removeCandidate(digit));
            incrementCounter();
            return changed;
        }
        return List.of();
    }

    private List<Cell> applyRule2b(Cell[] rectangle) {
        // cell1 and cell2 are not in the same box
        if (rectangle[0].getBox() == rectangle[1].getBox()) return List.of();
        // cell3 and cell4 are tri-value cells and share the same candidates
        if (rectangle[2].getCandidateCount() != 3 || !rectangle[3].getCandidates().equals(rectangle[2].getCandidates())) return List.of();
        int digit = rectangle[2].getCandidates().stream().filter(c -> !rectangle[0].isCandidate(c)).findFirst().orElseThrow();
        var changed = grid.getCommonPeersWithCandidate(rectangle[2], rectangle[3], digit);
        if (!changed.isEmpty()) {
            log(sb, "Unique Rectangle Type 2b found: %s, %s, %s, %s. Removed candidates %d from %s%n", rectangle[0], rectangle[1], rectangle[2], rectangle[3], digit, changed);
            changed.forEach(cell -> cell.removeCandidate(digit));
            incrementCounter();
            return changed;
        }
        return List.of();
    }

    private List<Cell> applyRule2c(Cell[] rectangle) {
        // TODO: Implement Rule 2c. Currently, rectangle doesn't allow to check for this rule
        return List.of();
    }

    private List<Cell> applyRule3(Cell[] rectangle) {
        // cell1 and cell2 are not in the same box
        if (rectangle[0].getBox() == rectangle[1].getBox()) return List.of();
        // cell3 and cell4 are tri-value cells and have different candidates
        if (rectangle[2].getCandidateCount() != 3 || rectangle[3].getCandidateCount() != 3) return List.of();
        if (rectangle[2].getCandidates().equals(rectangle[3].getCandidates())) return List.of();
        UnitType unitType = rectangle[0].getRow() == rectangle[1].getRow() ? UnitType.ROW : UnitType.COL;
        Cell[] cells = grid.getCells(unitType, rectangle[2].getUnitIndex(unitType));
        List<Integer> pseudoBiValueCellCandidates = new ArrayList<>();
        pseudoBiValueCellCandidates.add(rectangle[2].getCandidates().stream().filter(d -> !rectangle[0].isCandidate(d)).findFirst().orElseThrow());
        pseudoBiValueCellCandidates.add(rectangle[3].getCandidates().stream().filter(d -> !rectangle[0].isCandidate(d)).findFirst().orElseThrow());
        pseudoBiValueCellCandidates.sort(Integer::compareTo);
        // Find a cell that is bi-value and has the same candidates as the pseudo bi-value cells in the cell3 and cell4 unit
        Cell biValueCell = Arrays.stream(cells)
                .filter(Cell::isBiValue)
                .filter(cell -> cell.getCandidates().equals(pseudoBiValueCellCandidates))
                .findFirst()
                .orElse(null);
        // We can remove the pseudo bi-value candidates from the row/col
        if (biValueCell == null) return List.of();
        List<Cell> changed = new ArrayList<>();
        for (Cell cell : cells) {
             if (cell == biValueCell || cell == rectangle[2] || cell == rectangle[3]) continue;
             var removed = cell.removeCandidates(pseudoBiValueCellCandidates);
             if (!removed.isEmpty()) {
                 changed.add(cell);
                 log(sb, "Removed candidates %s from %s%n", removed, cell);
             }
        }
        if (!changed.isEmpty()) {
            incrementCounter();
            log(sb, 0, "Unique Rectangle Type 3 found: %s, %s, %s, %s.%n", rectangle[0], rectangle[1], rectangle[2], rectangle[3]);
            return changed;
        }
        return List.of();
    }
}
