package uni.project.lab1;

import java.util.HashMap;
import java.util.List;

public class FiniteAutomaton {
    List<String> Q;
    List<String> Sigma;
    HashMap<HashMap<String, String>, String> delta;
    String q0;
    String F;

    public FiniteAutomaton(List<String> Q, List<String> Sigma, HashMap<HashMap<String, String>, String> delta, String q0, String F) {
        this.Q = Q;
        this.Sigma = Sigma;
        this.delta = delta;
        this.q0 = q0;
        this.F = F;
    }

    public boolean stringBelongToLanguage(final String inputString) {
        String currentState = q0;
        Character cursor;
        for (int i = 0; i < inputString.length(); i++) {
            cursor = inputString.charAt(i);
            HashMap<String, String> key = new HashMap<>();
            key.put(currentState, String.valueOf(cursor));
            String value = delta.get(key);
            if (value == null) return false;
            currentState = value;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Q: " + Q.toString() + "\n");
        res.append("Sigma: " + Sigma.toString() + "\n");
        res.append("delta: " + delta.toString() + "\n");
        res.append("q0: " + q0 + "\n");
        res.append("F: " + F + "\n");
        return res.toString();
    }
}
