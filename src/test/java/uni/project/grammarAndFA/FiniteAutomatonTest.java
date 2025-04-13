package uni.project.grammarAndFA;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FiniteAutomatonTest {
    private FiniteAutomaton deterministicFA;
    private FiniteAutomaton nonDeterministicFA;

    @BeforeEach
    void setUp() {
        // Setup a simple deterministic finite automaton for binary strings ending with 01
        List<String> deterministicStates = Arrays.asList("q0", "q1", "q2");
        List<String> sigma = Arrays.asList("0", "1");
        HashMap<HashMap<String, String>, List<String>> deterministicDelta = new HashMap<>();

        // Transition for 0 from q0 to q0
        HashMap<String, String> key1 = new HashMap<>();
        key1.put("q0", "0");
        deterministicDelta.put(key1, Arrays.asList("q0"));

        // Transition for 1 from q0 to q1
        HashMap<String, String> key2 = new HashMap<>();
        key2.put("q0", "1");
        deterministicDelta.put(key2, Arrays.asList("q1"));

        // Transition for 0 from q1 to q2
        HashMap<String, String> key3 = new HashMap<>();
        key3.put("q1", "0");
        deterministicDelta.put(key3, Arrays.asList("q2"));

        // Final state
        List<String> finalStates = Arrays.asList("q2");

        deterministicFA = new FiniteAutomaton(deterministicStates, sigma, deterministicDelta, "q0", finalStates);

        // Setup a non-deterministic finite automaton
        List<String> ndStates = Arrays.asList("q0", "q1", "q2");
        List<String> ndSigma = Arrays.asList("0", "1", "ε");
        HashMap<HashMap<String, String>, List<String>> ndDelta = new HashMap<>();

        // Epsilon transition from q0 to q1
        HashMap<String, String> ndKey1 = new HashMap<>();
        ndKey1.put("q0", "ε");
        ndDelta.put(ndKey1, Arrays.asList("q1"));

        // Multiple transitions
        HashMap<String, String> ndKey2 = new HashMap<>();
        ndKey2.put("q0", "1");
        ndDelta.put(ndKey2, Arrays.asList("q0", "q1"));

        List<String> ndFinalStates = Arrays.asList("q1", "q2");

        nonDeterministicFA = new FiniteAutomaton(ndStates, ndSigma, ndDelta, "q0", ndFinalStates);
    }

    @Test
    void testIsDeterministic() {
        assertTrue(deterministicFA.isDeterministic());
        assertFalse(nonDeterministicFA.isDeterministic());
    }

    @Test
    void testConvertToDFA() {
        FiniteAutomaton dfa = nonDeterministicFA.convertToDFA();
        assertNotNull(dfa);
        assertTrue(dfa.isDeterministic());
    }

    @Test
    void testSaveToDotFile() {
        String tempFilePath = System.getProperty("java.io.tmpdir") + "/test_automaton.dot";
        assertTrue(deterministicFA.saveToDotFile(tempFilePath));
    }
}