package org.anne.sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NetBuilderTest {
    private static final Map<String, List<String>> links = new HashMap<>();
    static {
        links.put("A", List.of("B"));
        links.put("B", List.of("A", "C", "I"));
        links.put("C", List.of("B", "D"));
        links.put("D", List.of("C", "E"));
        links.put("E", List.of("D", "F", "G"));
        links.put("F", List.of("E"));
        links.put("G", List.of("E", "H", "I"));
        links.put("H", List.of("G"));
        links.put("I", List.of("G", "B", "J"));
        links.put("J", List.of("K", "I"));
        links.put("K", List.of("J"));
        links.put("L", List.of("M"));
        links.put("M", List.of("L"));
        links.put("N", List.of("O"));
        links.put("O", List.of("N"));
    }

    @Test
    void testNetBuilder() {
        NetBuilder<String> netBuilder = new NetBuilder<>(links);
        List<Chain<String>> chains = netBuilder.getChains();

        assertEquals(3, chains.size());
    }
}