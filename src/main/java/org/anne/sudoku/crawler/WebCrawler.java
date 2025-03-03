package org.anne.sudoku.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

public class WebCrawler {

    private static final String URL = "https://www.websudoku.com/?";

    public record PuzzleSolution(String mask, String solution) {
        public String puzzle() {
            StringBuilder sb = new StringBuilder(mask.length());
            for (int i = 0; i < mask.length(); i++) {
                sb.append(mask.charAt(i) == '0' ? '.' : solution.charAt(i));
            }
            return sb.toString();
        }
    }

    public PuzzleSolution fetchPuzzleAndSolution(String url) throws IOException {
        Document framesetDoc = Jsoup.connect(url).get();
        Elements frameElements = framesetDoc.select("frame");

        if (frameElements.isEmpty()) {
            throw new IOException("No frames found");
        }
        if (frameElements.size() > 2) {
            throw new IOException("Multiple frames found");
        }
        String frameUrl = frameElements.getFirst().attr("src");

        Document document = Jsoup.connect(frameUrl).get();

        Element solutionElement = document.getElementById("cheat"); // Adjust the selector based on the actual HTML structure
        Element maskElement = document.getElementById("editmask");

        if (maskElement == null || solutionElement == null) {
            throw new IOException("Puzzle or solution not found in the frames");
        }

        String solution = solutionElement.val();
        String mask = maskElement.val();

        return new PuzzleSolution(mask, solution);
    }

    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler();
        Random random = new Random();
        int id = random.nextInt(Integer.MAX_VALUE);
        try {
            for (int level = 1; level <= 4; level++) {
                PuzzleSolution puzzleSolution = crawler.fetchPuzzleAndSolution(URL + "level=" + level + "&set_id=" + id);
                System.out.printf("\"%s, %s\",%n", puzzleSolution.puzzle(), puzzleSolution.solution());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}