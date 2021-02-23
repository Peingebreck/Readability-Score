package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadabilityScore {
    final static int[] ages = {6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24, 25};

    public static void main(String[] args) {
        countUnits(args[0]);
    }

    static void countUnits(String path) {
        try {
            String text = new String(Files.readAllBytes(Paths.get(path)));
            int chars = text.replaceAll("\\s","").length();
            int words = text.split("\\s+").length;
            int sent = text.split("[.!?]+\\s*").length;
            int syl = 0;
            int polys = 0;
            int wordSyl;
            Pattern pattern = Pattern.compile("[aeiouy]+", Pattern.CASE_INSENSITIVE);
            boolean found;

            for (String word : text.split("\\s+")) {
                StringBuilder cut = new StringBuilder(word.replaceAll("\\W", ""));
                found = false;
                wordSyl = 0;
                if (cut.charAt(cut.length() - 1) == 'e') {
                    cut.deleteCharAt(cut.length() - 1);
                }
                Matcher matcher = pattern.matcher(cut);
                while (matcher.find()) {
                    syl++;
                    wordSyl++;
                    found = true;
                }
                if(!found) {
                    syl++;
                }
                if (wordSyl > 2) {
                    polys++;
                }
            }
            printUnits(text, chars, words, sent, syl, polys);
            chooseIndex(chars, words, sent, syl, polys);
        } catch (IOException e) {
            System.out.println("Cannot read file: " + e.getMessage());
        }
    }

    static void printUnits(String text, int chars, int words, int sent, int syl, int polys) {
        System.out.printf("The text is:\n%s\n\n", text);
        System.out.printf("Words: %d\n", words);
        System.out.printf("Sentences: %d\n", sent);
        System.out.printf("Characters: %d\n", chars);
        System.out.printf("Syllables: %d\n", syl);
        System.out.printf("Polysyllables: %d\n", polys);
    }

    static void chooseIndex(int chars, int words, int sent, int syl, int polys) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String choice = sc.next();
        if ("ARI".equals(choice)) {
            countARI(chars, words, sent);
        } else if ("FK".equals(choice)) {
            countFK(words, sent, syl);
        } else if ("SMOG".equals(choice)) {
            countSMOG(polys, sent);
        } else if ("CL".equals(choice)) {
            countCL(chars, words, sent);
        } else if ("all".equals(choice)) {
            double index1 = countARI(chars, words, sent);
            double index2 = countFK(words, sent, syl);
            double index3 = countSMOG(polys, sent);
            double index4 = countCL(chars, words, sent);
            System.out.printf("\n\nThis text should be understood in average by %.2f-year-olds.", (index1 + index2 + index3 + index4) / 4);
        } else {
            System.out.println("Incorrect choice");
        }
    }

    static double countARI(int chars, int words, int sent) {
        double score = 4.71 * ((double) chars / words) + 0.5 * ((double) words / sent) - 21.43;
        int index = Math.round(score) > 14 ? 13 : (int) (Math.round(score) - 1);
        System.out.printf("\nAutomated Readability Index: %.2f (about %d-year-olds).", score, ages[index]);
        return ages[index];
    }

    static double countFK(int words, int sent, int syl) {
        double score = 0.39 * ((double) words / sent) + 11.8 * ((double) syl / words) - 15.59;
        int index = Math.round(score) > 14 ? 13 : (int) (Math.round(score) - 1);
        System.out.printf("\nFlesch–Kincaid readability tests: %.2f (about %d-year-olds).", score, ages[index]);
        return ages[index];
    }

    static double countSMOG(int polys, int sent) {
        double score = 1.043 * Math.sqrt(polys * (30 / (double) sent)) + 3.1291;
        int index = Math.round(score) > 14 ? 13 : (int) (Math.round(score) - 1);
        System.out.printf("\nSimple Measure of Gobbledygook: %.2f (about %d-year-olds).", score, ages[index]);
        return ages[index];
    }

    static double countCL(int chars, int words, int sent) {
        double l = (double) chars / words * 100;
        double s = (double) sent / words * 100;
        double score = 0.0588 * l - 0.296 * s - 15.8;
        int index = Math.round(score) > 14 ? 13 : (int) (Math.round(score) - 1);
        System.out.printf("\nColeman–Liau index: %.2f (about %d-year-olds).", score, ages[index]);
        return ages[index];
    }
}
