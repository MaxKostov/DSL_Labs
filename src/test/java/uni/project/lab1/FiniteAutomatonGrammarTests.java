package uni.project.lab1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class FiniteAutomatonGrammarTests {
    @Test
    public void testFiniteAutomaton_AcceptsString() {
        List<String> Q = Arrays.asList("q0", "q1", "q2");
        List<String> Sigma = Arrays.asList("a", "b");
        String q0 = "q0";
        String F = "q2";

        HashMap<HashMap<String, String>, String> delta = new HashMap<>();

        HashMap<String, String> transition1 = new HashMap<>();
        transition1.put("q0", "a");
        delta.put(transition1, "q1");

        HashMap<String, String> transition2 = new HashMap<>();
        transition2.put("q1", "b");
        delta.put(transition2, "q2");

        FiniteAutomaton fa = new FiniteAutomaton(Q, Sigma, delta, q0, F);

        assertTrue(fa.stringBelongToLanguage("ab"));
    }

    @Test
    public void testFiniteAutomaton_RejectsString() {
        List<String> Q = Arrays.asList("q0", "q1", "q2");
        List<String> Sigma = Arrays.asList("a", "b");
        String q0 = "q0";
        String F = "q2";

        HashMap<HashMap<String, String>, String> delta = new HashMap<>();

        HashMap<String, String> transition1 = new HashMap<>();
        transition1.put("q0", "a");
        delta.put(transition1, "q1");

        HashMap<String, String> transition2 = new HashMap<>();
        transition2.put("q1", "b");
        delta.put(transition2, "q2");

        FiniteAutomaton fa = new FiniteAutomaton(Q, Sigma, delta, q0, F);

        assertFalse(fa.stringBelongToLanguage("aa"));
    }

    @Test
    public void testGrammar_GenerateString() {
        List<String> VN = Arrays.asList("S", "A");
        List<String> VT = Arrays.asList("a", "b");
        String startVariable = "S";

        HashMap<String, List<String>> rules = new HashMap<>();
        rules.put("S", Arrays.asList("aA", "b"));
        rules.put("A", Arrays.asList("a", "b"));

        Grammar grammar = new Grammar(VN, VT, startVariable, rules);

        String generatedString = grammar.generteString();

        assertNotNull(generatedString);
        System.out.println(generatedString);
        assertTrue(generatedString.matches("[ab]+"));
    }

    @Test
    public void testGrammar_ToFiniteAutomaton() {
        List<String> VN = Arrays.asList("S", "A");
        List<String> VT = Arrays.asList("a", "b");
        String startVariable = "S";

        HashMap<String, List<String>> rules = new HashMap<>();
        rules.put("S", Arrays.asList("aA", "b"));
        rules.put("A", Arrays.asList("a", "b"));

        Grammar grammar = new Grammar(VN, VT, startVariable, rules);

        FiniteAutomaton fa = grammar.toFiniteAutomaton();

        assertNotNull(fa);
        assertEquals(startVariable, fa.q0);
        assertTrue(fa.Sigma.containsAll(VT));
        assertTrue(fa.Q.containsAll(VN));
        assertTrue(fa.Q.contains("X"));

        assertTrue(fa.stringBelongToLanguage("ab"));
        assertFalse(fa.stringBelongToLanguage("ba"));
    }
}
