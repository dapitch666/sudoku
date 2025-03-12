package org.anne.sudoku.crawler;

import org.anne.sudoku.Grade;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThonkyGrader {

    private static final String URL = "https://www.thonky.com/sudoku/evaluate-sudoku?puzzlebox=";
    private static final Pattern GRADE_PATTERN = Pattern.compile("Score: (\\d+)");

    public Grade getGrade(String puzzle) throws IOException {
        String url = URL + puzzle;
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsByTag("big");
        if (elements.isEmpty()) {
            throw new IOException("Grade not found");
        }
        for (Element element : elements) {
            String text = element.text();
            Matcher matcher = GRADE_PATTERN.matcher(text);
            if (matcher.find()) {
                int score = Integer.parseInt(matcher.group(1));
                return Grade.fromLevel(score);
            }
        }
        return Grade.UNKNOWN;
    }

    public static void main(String[] args) {
        ThonkyGrader grader = new ThonkyGrader();
        try {
            // String puzzle = ".4........73..56..6...1....96..4..3.....3.....8.....275...6...4..89.427.......15.";
            String puzzle = "5.4.6.......2...8...81....9.395...67....7.91.47...1.....13....5.....92.3.8.7.....";
            System.out.println(grader.getGrade(puzzle));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
