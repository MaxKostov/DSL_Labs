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

    public String generteString() {
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
                sb.replace(cursor, cursor + 1, replacement);}
        }
        return sb.toString();
    }


    public FiniteAutomaton toFiniteAutomaton() {
        List<String> sigma = new ArrayList<>(VT);
        List<String> Q = new ArrayList<>(VN);
        Q.add("X");
        String q0 = startVariable;
        String finalState = "X";
        HashMap<HashMap<String, String>, String> delta  = new HashMap<>();
        Set<String> keySet = hashMap.keySet();
        for (String key : keySet) {
            List<String> values = hashMap.get(key);
            for (String value : values) {
                if (value.length() == 2) {
                    HashMap<String, String> transitionKey = new HashMap<>();
                    transitionKey.put(key, String.valueOf(value.charAt(0)));

                    delta.put(transitionKey, String.valueOf(value.charAt(1)));
                } else if (value.length() == 1) {
                    HashMap<String, String> transitionKey = new HashMap<>();
                    transitionKey.put(key, value);

                    delta.put(transitionKey, finalState);
                }
            }
        }
        return new FiniteAutomaton(Q, sigma, delta, q0, finalState);
    }
}
