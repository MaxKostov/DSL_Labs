package uni.project.lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexGenerator {
    private static final int MAX_KLEENE_REPETITIONS = 5;
    private static final Random random = new Random();
    private static final StringBuilder processingSteps = new StringBuilder();
    private static int stepCounter = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> regexes = new ArrayList<>();

        System.out.println("Enter regular expressions (one per line, empty line to finish):");
        String input;
        while (!(input = scanner.nextLine()).isEmpty()) {
            regexes.add(input);
        }

        System.out.println("\n--- Generated valid strings ---");
        for (String regex : regexes) {
            processingSteps.setLength(0);
            stepCounter = 1;
            String generatedString = generateString(regex);
            System.out.println("Regex: " + regex);
            System.out.println("Generated string: " + generatedString);
            System.out.println("\nProcessing steps:");
            System.out.println(processingSteps.toString());
            System.out.println("------------------------\n");
        }

        scanner.close();
    }

    public static String generateString(String regex) {
        addProcessingStep("Starting to process regex: " + regex);

        // Handle groups with quantifiers first
        regex = processGroupsWithQuantifiers(regex);

        // Handle group Kleene star operations like (X|Y|Z)*
        regex = processGroupKleeneStars(regex);

        // Handle simple Kleene star operations like X*
        regex = processSimpleKleeneStars(regex);

        // Handle optional symbols (?)
        regex = processOptionalSymbols(regex);

        // Handle alternative choices (|)
        regex = processAlternatives(regex);

        // At this point, the regex should be a plain string
        addProcessingStep("Final result: " + regex);
        return regex;
    }

    private static String processGroupsWithQuantifiers(String regex) {
        // Pattern to find groups with quantifiers like (X|Y|Z)^n
        Pattern pattern = Pattern.compile("\\(([^()]+)\\)\\^(\\d+)");
        Matcher matcher = pattern.matcher(regex);

        while (matcher.find()) {
            String group = matcher.group(1);
            int repetitions = Integer.parseInt(matcher.group(2));

            addProcessingStep("Found group with quantifier: (" + group + ")^" + repetitions);

            String[] alternatives = group.split("\\|");
            StringBuilder replacement = new StringBuilder();

            for (int i = 0; i < repetitions; i++) {
                String chosen = alternatives[random.nextInt(alternatives.length)];
                replacement.append(chosen);
                addProcessingStep("Repetition " + (i+1) + ": Chose '" + chosen + "' from alternatives " + String.join(", ", alternatives));
            }

            regex = regex.substring(0, matcher.start()) + replacement + regex.substring(matcher.end());
            addProcessingStep("After processing group with quantifier: " + regex);

            // Reset matcher with the new regex
            matcher = pattern.matcher(regex);
        }

        return regex;
    }

    private static String processGroupKleeneStars(String regex) {
        // Pattern to find group Kleene star operations like (X|Y|Z)*
        Pattern pattern = Pattern.compile("\\(([^()]+)\\)\\*");
        Matcher matcher = pattern.matcher(regex);

        while (matcher.find()) {
            String group = matcher.group(1);
            String[] alternatives = group.split("\\|");
            int repetitions = random.nextInt(MAX_KLEENE_REPETITIONS + 1); // 0 to MAX_KLEENE_REPETITIONS

            addProcessingStep("Found group Kleene star for group (" + group + "), choosing " + repetitions + " repetitions (limited to " + MAX_KLEENE_REPETITIONS + ")");

            StringBuilder replacement = new StringBuilder();
            for (int i = 0; i < repetitions; i++) {
                String chosen = alternatives[random.nextInt(alternatives.length)];
                replacement.append(chosen);
                addProcessingStep("Repetition " + (i+1) + ": Chose '" + chosen + "' from alternatives " + String.join(", ", alternatives));
            }

            regex = regex.substring(0, matcher.start()) + replacement + regex.substring(matcher.end());
            addProcessingStep("After processing group Kleene star: " + regex);

            // Reset matcher with the new regex
            matcher = pattern.matcher(regex);
        }

        return regex;
    }

    private static String processSimpleKleeneStars(String regex) {
        // Pattern to find simple Kleene star operations like X*
        Pattern pattern = Pattern.compile("([^\\(\\)\\*])\\*");
        Matcher matcher = pattern.matcher(regex);

        while (matcher.find()) {
            char symbol = matcher.group(1).charAt(0);
            int repetitions = random.nextInt(MAX_KLEENE_REPETITIONS + 1); // 0 to MAX_KLEENE_REPETITIONS

            addProcessingStep("Found simple Kleene star for symbol '" + symbol + "', choosing " + repetitions + " repetitions (limited to " + MAX_KLEENE_REPETITIONS + ")");

            StringBuilder replacement = new StringBuilder();
            for (int i = 0; i < repetitions; i++) {
                replacement.append(symbol);
            }

            regex = regex.substring(0, matcher.start()) + replacement + regex.substring(matcher.end());
            addProcessingStep("After processing simple Kleene star: " + regex);

            // Reset matcher with the new regex
            matcher = pattern.matcher(regex);
        }

        return regex;
    }

    private static String processOptionalSymbols(String regex) {
        // Pattern to find optional symbols like X?
        Pattern pattern = Pattern.compile("([^\\?])\\?");
        Matcher matcher = pattern.matcher(regex);

        while (matcher.find()) {
            char symbol = matcher.group(1).charAt(0);
            boolean include = random.nextBoolean();

            addProcessingStep("Found optional symbol '" + symbol + "', decided to " + (include ? "include" : "exclude") + " it");

            String replacement = include ? String.valueOf(symbol) : "";
            regex = regex.substring(0, matcher.start()) + replacement + regex.substring(matcher.end());
            addProcessingStep("After processing optional symbol: " + regex);

            // Reset matcher with the new regex
            matcher = pattern.matcher(regex);
        }

        return regex;
    }

    private static String processAlternatives(String regex) {
        // Pattern to find alternatives like (X|Y|Z) without quantifiers
        Pattern pattern = Pattern.compile("\\(([^()]+)\\)");
        Matcher matcher = pattern.matcher(regex);

        while (matcher.find()) {
            String group = matcher.group(1);

            // Skip if it's followed by a quantifier (already processed)
            if (matcher.end() < regex.length() && (regex.charAt(matcher.end()) == '^' || regex.charAt(matcher.end()) == '*')) {
                continue;
            }

            addProcessingStep("Found alternatives group: (" + group + ")");

            String[] alternatives = group.split("\\|");
            String chosen = alternatives[random.nextInt(alternatives.length)];

            addProcessingStep("Chose '" + chosen + "' from alternatives " + String.join(", ", alternatives));

            regex = regex.substring(0, matcher.start()) + chosen + regex.substring(matcher.end());
            addProcessingStep("After processing alternatives: " + regex);

            // Reset matcher with the new regex
            matcher = pattern.matcher(regex);
        }

        return regex;
    }

    private static void addProcessingStep(String step) {
        processingSteps.append("Step ").append(stepCounter++).append(": ").append(step).append("\n");
    }
}