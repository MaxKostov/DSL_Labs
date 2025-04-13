package uni.project.grammarAndFA;

import java.util.*;

public class Grammar {
    private final List<String> VN;
    private final List<String> VT;
    private final String startVariable;
    private final HashMap<String, List<String>> hashMap;

    public Grammar(List<String> VN, List<String> VT, String startVariable, HashMap<String, List<String>> hashMap) {
        this.VN = VN;
        this.VT = VT;
        this.startVariable = startVariable;
        this.hashMap = hashMap;
    }

    public String generateString() {
        StringBuilder sb = new StringBuilder(startVariable);
        int cursor = 0;
        while (cursor < sb.length()) {
            char c = sb.charAt(cursor);
            if (Character.isLowerCase(c)) {
                cursor++;
            } else {
                List<String> possibleValues = hashMap.get(String.valueOf(c));
                if (possibleValues == null || possibleValues.isEmpty()) {
                    throw new IllegalStateException("No production rule for non-terminal: " + c);
                }
                String replacement = possibleValues.get((int) (Math.random() * possibleValues.size()));
                sb.replace(cursor, cursor + 1, replacement);
            }
        }
        return sb.toString();
    }

    public FiniteAutomaton toFiniteAutomaton() {
        List<String> sigma = new ArrayList<>(VT);
        List<String> Q = new ArrayList<>(VN);
        Q.add("X");
        String q0 = startVariable;
        List<String> F = Collections.singletonList("X");

        HashMap<HashMap<String, String>, List<String>> delta = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : hashMap.entrySet()) {
            String nonTerminal = entry.getKey();
            List<String> productions = entry.getValue();

            for (String production : productions) {
                if (production.equals("ε")) {
                    HashMap<String, String> key = new HashMap<>();
                    key.put(nonTerminal, "ε");
                    delta.computeIfAbsent(key, k -> new ArrayList<>()).add("X");
                    continue;
                }

                if (production.length() == 1 && VT.contains(production)) {
                    HashMap<String, String> key = new HashMap<>();
                    key.put(nonTerminal, production);
                    delta.computeIfAbsent(key, k -> new ArrayList<>()).add("X");
                }
                else if (production.length() == 2) {
                    char terminal = production.charAt(0);
                    char nextState = production.charAt(1);
                    String terminalStr = String.valueOf(terminal);
                    String nextStateStr = String.valueOf(nextState);

                    if (VT.contains(terminalStr) && VN.contains(nextStateStr)) {
                        HashMap<String, String> key = new HashMap<>();
                        key.put(nonTerminal, terminalStr);
                        delta.computeIfAbsent(key, k -> new ArrayList<>()).add(nextStateStr);
                    }
                }
            }
        }

        return new FiniteAutomaton(Q, sigma, delta, q0, F);
    }

    public String determineGrammarType() {
        boolean isRegular = true;
        boolean isContextFree = true;
        boolean isContextSensitive = true;

        for (Map.Entry<String, List<String>> entry : hashMap.entrySet()) {
            String leftSide = entry.getKey();
            List<String> rightSides = entry.getValue();

            if (!VN.contains(leftSide) || leftSide.length() != 1) {
                isRegular = false;
                isContextFree = false;
            }

            for (String rightSide : rightSides) {
                if (isRegular && !isRegularProduction(leftSide, rightSide)) {
                    isRegular = false;
                }

                if (leftSide.length() > rightSide.length() && !rightSide.equals("ε")) {
                    isContextSensitive = false;
                }
            }
        }

        if (isRegular) return "Type 3";
        if (isContextFree) return "Type 2";
        if (isContextSensitive) return "Type 1";
        return "Type 0";
    }

    private boolean isRegularProduction(String leftSide, String rightSide) {
        if (!VN.contains(leftSide) || leftSide.length() != 1) {
            return false;
        }

        if (rightSide.length() == 1 && VT.contains(rightSide)) {
            return true;
        }

        if (rightSide.length() == 2) {
            char first = rightSide.charAt(0);
            char second = rightSide.charAt(1);
            String firstStr = String.valueOf(first);
            String secondStr = String.valueOf(second);

            boolean isRightLinear = VT.contains(firstStr) && VN.contains(secondStr);

            boolean isLeftLinear = VN.contains(firstStr) && VT.contains(secondStr);

            return isRightLinear || isLeftLinear;
        }

        return false;
    }

    /**
     * Converts the grammar to Chomsky Normal Form (CNF)
     * In CNF, all productions are of the form:
     * 1. A → BC (where B and C are non-terminals)
     * 2. A → a (where a is a terminal)
     * 3. S → ε (only if S is the start variable and doesn't appear on the right side of any production)
     *
     * @return A new Grammar in Chomsky Normal Form
     */
    public Grammar toChomskyNormalForm() {
        // Create deep copies to avoid modifying the original grammar
        List<String> newVN = new ArrayList<>(VN);
        List<String> newVT = new ArrayList<>(VT);
        String newStartVariable = startVariable;
        HashMap<String, List<String>> newProductions = new HashMap<>();

        // Deep copy productions
        for (Map.Entry<String, List<String>> entry : hashMap.entrySet()) {
            newProductions.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        // Step 1: Add a new start symbol S0 if S appears on the right side of any production
        // or if S can derive epsilon
        boolean needNewStart = false;
        // Check if start variable appears on the right side of any production
        for (List<String> productions : hashMap.values()) {
            for (String production : productions) {
                if (production.contains(startVariable)) {
                    needNewStart = true;
                    break;
                }
            }
            if (needNewStart) break;
        }

        // Check if start variable can derive epsilon
        if (hashMap.containsKey(startVariable)) {
            List<String> startProductions = hashMap.get(startVariable);
            if (startProductions.contains("ε")) {
                needNewStart = true;
            }
        }

        // Add new start variable if needed
        String originalStart = startVariable;
        if (needNewStart) {
            String newStart = "S0";
            // Find a unique name for the new start variable
            while (newVN.contains(newStart)) {
                newStart = newStart + "0";
            }
            newVN.add(newStart);
            newStartVariable = newStart;
            newProductions.put(newStart, new ArrayList<>(Collections.singletonList(originalStart)));
        }

        // Step 2: Remove epsilon productions
        eliminateEpsilonProductions(newVN, newProductions);

        // Step 3: Remove unit productions (A → B where B is a non-terminal)
        eliminateUnitProductions(newVN, newProductions);

        // Step 4: Eliminate inaccessible symbols
        eliminateInaccessibleSymbols(newVN, newVT, newStartVariable, newProductions);

        // Step 5: Eliminate non-productive symbols
        eliminateNonProductiveSymbols(newVN, newVT, newProductions);

        // Step 6: Replace terminals in productions with more than one symbol
        Map<String, String> terminalToNonTerminal = new HashMap<>();
        int terminalVarCounter = 1;

        for (String nonTerminal : new ArrayList<>(newProductions.keySet())) {
            List<String> productions = newProductions.get(nonTerminal);
            List<String> newProductionList = new ArrayList<>();

            for (String production : productions) {
                if (production.length() > 1) {
                    StringBuilder newProduction = new StringBuilder();

                    for (int i = 0; i < production.length(); i++) {
                        String symbol = production.substring(i, i + 1);

                        if (newVT.contains(symbol)) {
                            // Replace terminal with a new non-terminal
                            if (!terminalToNonTerminal.containsKey(symbol)) {
                                String newNonTerminal = "T" + terminalVarCounter++;
                                // Ensure the new non-terminal is unique
                                while (newVN.contains(newNonTerminal)) {
                                    newNonTerminal = "T" + terminalVarCounter++;
                                }

                                newVN.add(newNonTerminal);
                                terminalToNonTerminal.put(symbol, newNonTerminal);
                                newProductions.put(newNonTerminal, new ArrayList<>(Collections.singletonList(symbol)));
                            }

                            newProduction.append(terminalToNonTerminal.get(symbol));
                        } else {
                            newProduction.append(symbol);
                        }
                    }

                    newProductionList.add(newProduction.toString());
                } else {
                    newProductionList.add(production);
                }
            }

            newProductions.put(nonTerminal, newProductionList);
        }

        // Step 7: Break down productions with more than 2 non-terminals
        // Fixed implementation to avoid infinite loops
        HashMap<String, List<String>> finalProductions = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : newProductions.entrySet()) {
            finalProductions.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        int varCounter = 1;

        // Process all non-terminals
        for (String nonTerminal : new ArrayList<>(finalProductions.keySet())) {
            List<String> productions = finalProductions.get(nonTerminal);
            List<String> np = new ArrayList<>();

            for (String production : productions) {
                if (production.length() > 2) {
                    // Create a sequence of binary productions
                    String currentNT = nonTerminal;
                    for (int i = 0; i < production.length() - 2; i++) {
                        String nextNT = "V" + varCounter++;
                        // Ensure unique variable name
                        while (newVN.contains(nextNT)) {
                            nextNT = "V" + varCounter++;
                        }
                        newVN.add(nextNT);

                        // Add production A -> BC where C is the new non-terminal
                        if (i == 0) {
                            // First production from the original non-terminal
                            np.add(production.substring(0, 1) + nextNT);
                        } else {
                            // Add production to proper non-terminal
                            finalProductions.computeIfAbsent(currentNT, k -> new ArrayList<>())
                                    .add(production.substring(i, i + 1) + nextNT);
                        }

                        currentNT = nextNT;
                    }

                    // Add the final production for the last two symbols
                    finalProductions.computeIfAbsent(currentNT, k -> new ArrayList<>())
                            .add(production.substring(production.length() - 2));
                } else {
                    np.add(production);
                }
            }

            // Replace the original productions with the new ones
            if (!np.isEmpty()) {
                finalProductions.put(nonTerminal, np);
            }
        }

        return new Grammar(newVN, newVT, newStartVariable, finalProductions);
    }

    /**
     * Eliminates epsilon productions from the grammar
     *
     * @param vn The non-terminal symbols
     * @param productions The production rules
     */
    private void eliminateEpsilonProductions(List<String> vn, HashMap<String, List<String>> productions) {
        // Find all nullable non-terminals (those that can derive epsilon)
        Set<String> nullable = new HashSet<>();
        boolean changed;

        do {
            changed = false;
            for (String nonTerminal : vn) {
                if (!nullable.contains(nonTerminal) && productions.containsKey(nonTerminal)) {
                    List<String> nonTerminalProductions = productions.get(nonTerminal);

                    // Check if the non-terminal directly produces epsilon
                    if (nonTerminalProductions.contains("ε")) {
                        nullable.add(nonTerminal);
                        changed = true;
                    } else {
                        // Check if the non-terminal produces only nullable non-terminals
                        for (String production : nonTerminalProductions) {
                            boolean allNullable = true;
                            for (int i = 0; i < production.length(); i++) {
                                String symbol = production.substring(i, i + 1);
                                if (!nullable.contains(symbol)) {
                                    allNullable = false;
                                    break;
                                }
                            }

                            if (allNullable && !production.isEmpty()) {
                                nullable.add(nonTerminal);
                                changed = true;
                                break;
                            }
                        }
                    }
                }
            }
        } while (changed);

        System.out.println("Nullable symbols: " + nullable);

        // Remove epsilon productions and add new productions
        for (String nonTerminal : vn) {
            if (productions.containsKey(nonTerminal)) {
                List<String> nonTerminalProductions = productions.get(nonTerminal);
                nonTerminalProductions.remove("ε"); // Remove epsilon productions

                // Add new productions to account for nullable non-terminals
                List<String> newProductions = new ArrayList<>();
                for (String production : nonTerminalProductions) {
                    addAllCombinations(production, nullable, newProductions);
                }

                nonTerminalProductions.addAll(newProductions);
                // Remove duplicates
                productions.put(nonTerminal, new ArrayList<>(new HashSet<>(nonTerminalProductions)));
            }
        }
    }

    /**
     * Adds all possible combinations of a production by removing nullable non-terminals
     *
     * @param production The production to process
     * @param nullable Set of nullable non-terminals
     * @param result List to store the resulting productions
     */
    private void addAllCombinations(String production, Set<String> nullable, List<String> result) {
        List<Integer> nullablePositions = new ArrayList<>();

        // Find positions of nullable non-terminals in the production
        for (int i = 0; i < production.length(); i++) {
            String symbol = production.substring(i, i + 1);
            if (nullable.contains(symbol)) {
                nullablePositions.add(i);
            }
        }

        // Generate all possible combinations of removing nullable non-terminals
        if (!nullablePositions.isEmpty()) {
            // Generate all combinations of removed positions (power set)
            int combinations = 1 << nullablePositions.size();

            for (int i = 1; i < combinations; i++) { // Start from 1 to avoid empty removal
                StringBuilder newProduction = new StringBuilder(production);
                int offset = 0;

                for (int j = 0; j < nullablePositions.size(); j++) {
                    if ((i & (1 << j)) != 0) {
                        int pos = nullablePositions.get(j) - offset;
                        newProduction.deleteCharAt(pos);
                        offset++;
                    }
                }

                if (!newProduction.toString().isEmpty()) {
                    result.add(newProduction.toString());
                }
            }
        }
    }

    /**
     * Eliminates unit productions (A → B where B is a non-terminal)
     *
     * @param vn The non-terminal symbols
     * @param productions The production rules
     */
    private void eliminateUnitProductions(List<String> vn, HashMap<String, List<String>> productions) {
        // Find all unit pairs (A, B) such that A ⇒* B
        Map<String, Set<String>> unitPairs = new HashMap<>();

        // Initialize with reflexive unit pairs
        for (String nonTerminal : vn) {
            Set<String> pairs = new HashSet<>();
            pairs.add(nonTerminal); // A ⇒* A
            unitPairs.put(nonTerminal, pairs);
        }

        // Compute all unit pairs using transitive closure
        boolean changed;
        do {
            changed = false;

            for (String a : vn) {
                Set<String> aPairs = new HashSet<>(unitPairs.get(a));

                for (String b : new HashSet<>(aPairs)) {
                    if (productions.containsKey(b)) {
                        List<String> bProductions = productions.get(b);

                        for (String production : bProductions) {
                            // Check if it's a unit production
                            if (production.length() == 1 && vn.contains(production)) {
                                String c = production;
                                if (unitPairs.get(a).add(c)) {
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        } while (changed);

        System.out.println("Unit pairs: " + unitPairs);

        // Replace unit productions
        HashMap<String, List<String>> newProductions = new HashMap<>();

        for (String nonTerminal : vn) {
            List<String> nonUnitProductions = new ArrayList<>();

            // For each unit pair (A, B), add all non-unit productions of B to A
            if (unitPairs.containsKey(nonTerminal)) {
                for (String unitNonTerminal : unitPairs.get(nonTerminal)) {
                    if (productions.containsKey(unitNonTerminal)) {
                        for (String production : productions.get(unitNonTerminal)) {
                            // Add only non-unit productions
                            if (!(production.length() == 1 && vn.contains(production))) {
                                nonUnitProductions.add(production);
                            }
                        }
                    }
                }
            }

            if (!nonUnitProductions.isEmpty()) {
                newProductions.put(nonTerminal, new ArrayList<>(new HashSet<>(nonUnitProductions)));
            } else {
                newProductions.put(nonTerminal, new ArrayList<>());
            }
        }

        // Replace old productions with new ones
        productions.clear();
        productions.putAll(newProductions);
    }

    /**
     * Eliminates inaccessible symbols from the grammar
     *
     * @param vn The non-terminal symbols
     * @param vt The terminal symbols
     * @param startVariable The start variable
     * @param productions The production rules
     */
    private void eliminateInaccessibleSymbols(List<String> vn, List<String> vt, String startVariable,
                                              HashMap<String, List<String>> productions) {
        // Find all accessible symbols using a breadth-first search
        Set<String> accessible = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        // Start with the start variable
        accessible.add(startVariable);
        queue.add(startVariable);

        // BFS to find all accessible symbols
        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (productions.containsKey(current)) {
                for (String production : productions.get(current)) {
                    for (int i = 0; i < production.length(); i++) {
                        String symbol = production.substring(i, i + 1);

                        // If the symbol is non-terminal and not yet visited
                        if (vn.contains(symbol) && !accessible.contains(symbol)) {
                            accessible.add(symbol);
                            queue.add(symbol);
                        }
                        // If the symbol is terminal
                        else if (vt.contains(symbol)) {
                            accessible.add(symbol);
                        }
                    }
                }
            }
        }

        System.out.println("Accessible symbols: " + accessible);

        // Remove inaccessible non-terminals and their productions
        List<String> inaccessibleNT = new ArrayList<>();
        for (String nonTerminal : new ArrayList<>(vn)) {
            if (!accessible.contains(nonTerminal)) {
                inaccessibleNT.add(nonTerminal);
                productions.remove(nonTerminal);
            }
        }
        vn.removeAll(inaccessibleNT);

        // Remove inaccessible terminals
        List<String> inaccessibleT = new ArrayList<>();
        for (String terminal : new ArrayList<>(vt)) {
            if (!accessible.contains(terminal)) {
                inaccessibleT.add(terminal);
            }
        }
        vt.removeAll(inaccessibleT);
    }

    /**
     * Eliminates non-productive symbols from the grammar
     *
     * @param vn The non-terminal symbols
     * @param vt The terminal symbols
     * @param productions The production rules
     */
    private void eliminateNonProductiveSymbols(List<String> vn, List<String> vt,
                                               HashMap<String, List<String>> productions) {
        // Find all productive symbols
        Set<String> productive = new HashSet<>(vt); // All terminals are productive
        boolean changed;

        do {
            changed = false;
            for (String nonTerminal : new ArrayList<>(vn)) {
                if (!productive.contains(nonTerminal) && productions.containsKey(nonTerminal)) {
                    List<String> nonTermProductions = productions.get(nonTerminal);

                    for (String production : nonTermProductions) {
                        boolean allProductive = true;

                        for (int i = 0; i < production.length(); i++) {
                            String symbol = production.substring(i, i + 1);
                            if (!productive.contains(symbol)) {
                                allProductive = false;
                                break;
                            }
                        }

                        if (allProductive) {
                            productive.add(nonTerminal);
                            changed = true;
                            break;
                        }
                    }
                }
            }
        } while (changed);

        System.out.println("Productive symbols: " + productive);

        // Remove non-productive non-terminals and their productions
        List<String> nonProductiveNT = new ArrayList<>();
        for (String nonTerminal : new ArrayList<>(vn)) {
            if (!productive.contains(nonTerminal)) {
                nonProductiveNT.add(nonTerminal);
                productions.remove(nonTerminal);
            }
        }
        vn.removeAll(nonProductiveNT);

        // Remove productions with non-productive symbols
        for (String nonTerminal : new ArrayList<>(productions.keySet())) {
            List<String> productionList = productions.get(nonTerminal);
            List<String> toRemove = new ArrayList<>();

            for (String production : productionList) {
                for (int i = 0; i < production.length(); i++) {
                    String symbol = production.substring(i, i + 1);
                    if (!productive.contains(symbol)) {
                        toRemove.add(production);
                        break;
                    }
                }
            }

            productionList.removeAll(toRemove);

            // Remove the non-terminal if it has no productions left
            if (productionList.isEmpty()) {
                productions.remove(nonTerminal);
                vn.remove(nonTerminal);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grammar {\n");
        sb.append("  VN = ").append(VN).append("\n");
        sb.append("  VT = ").append(VT).append("\n");
        sb.append("  S = ").append(startVariable).append("\n");
        sb.append("  P = {\n");
        for (Map.Entry<String, List<String>> entry : hashMap.entrySet()) {
            sb.append("    ").append(entry.getKey()).append(" → ");
            sb.append(String.join(" | ", entry.getValue())).append("\n");
        }
        sb.append("  }\n");
        sb.append("}");
        return sb.toString();
    }
}