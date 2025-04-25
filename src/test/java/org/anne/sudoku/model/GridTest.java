package org.anne.sudoku.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    @Test
    void testGridInitialization() {
        Grid grid = new Grid("85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.");
        assertEquals(8, grid.get(0, 0));
        assertEquals(2, grid.getCell(24).getRow());
        assertEquals(6, grid.getCell(24).getCol());
        assertEquals(2, grid.getCell(24).getBox());
        assertEquals(22, grid.getSolvedCells().length);
    }

    @Test
    void testInvalidGridLength() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Grid("123456789"));
        String expectedMessage = "Invalid puzzle length";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testInvalidGridCharacter() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Grid("A85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4"));
        String expectedMessage = "Invalid puzzle format";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}