package org.anne.sudoku.crawler;


import org.anne.sudoku.Grade;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SudokuWiki {
    private static final Pattern GRADE_PATTERN = Pattern.compile("Score: (\\d+)");

    public static void main(String []args) throws Exception{
        SudokuWiki sudokuwiki = new SudokuWiki();
        System.out.println(sudokuwiki.getGrade("000000000560000079790106030300907000209803507000405008010604082620000045000000000"));
    }

    public Grade getGrade(String puzzle) {
        String formData = getFormData(puzzle);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.sudokuwiki.org/ServerSolver.aspx?k=0"))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Document doc = org.jsoup.Jsoup.parse(response.body());
            String grade = doc.select("div").text();
            /*Elements elements = doc.children();
            for (Element element : elements) {
                Matcher matcher = GRADE_PATTERN.matcher(element.text());
                if (matcher.find()) {
                    int score = Integer.parseInt(matcher.group(1));
                    System.out.println("Score: " + score + " Grade: " + Grade.fromScore(score) + " (" + grade + ")");
                    return Grade.fromScore(score);
                }
            }*/
            System.out.println(grade + "(" + puzzle + ")");
            return Grade.fromSudokuWiki(grade);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormData(String puzzle) {
        Map<String, String> formData = new HashMap<>();
        formData.put("coordmode", "1");
        formData.put("curchain", "1");
        formData.put("curur", "1");
        formData.put("ff", "1");
        formData.put("fullreport", "0");
        formData.put("gors", "1");
        formData.put("k", "0");
        formData.put("mapno", "0");
        // formData.put("packedpuzzle", "S9B01epepepep07ep09epep03epep02epepep08epep0906epep05epepepep0503epep09epepep01epep08epepep0206epepepep04epepep03epepepepepep01epep04epepepepepep07epep07epepep03epep");
        formData.put("packedpuzzle", packedPuzzle(puzzle));
        formData.put("strat", "XWG");
        formData.put("stratmask", "");
        formData.put("version", "2.32");
        return getFormDataAsString(formData);
    }

    private static String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (!formBodyBuilder.isEmpty()) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }

    private static String packedPuzzle(String puzzle) {
        StringBuilder stringBuilder = new StringBuilder("S9B");
        for (int i = 0; i < puzzle.length(); i++) {
            char c = puzzle.charAt(i);
            if (!Character.isDigit(c) || c == '0') {
                stringBuilder.append("ep");
            } else {
                stringBuilder.append("0").append(c);
            }
        }
        return stringBuilder.toString();
    }
}
