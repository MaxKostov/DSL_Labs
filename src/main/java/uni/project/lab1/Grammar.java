package uni.project.lab1;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VN: ").append(VN.toString()).append("\n");
        sb.append("VT: ").append(VT.toString()).append("\n");
        sb.append("startVariable: ").append(startVariable).append("\n");
        sb.append("P: ").append(hashMap.toString()).append("\n");
        return sb.toString();
    }
}