package uni.project.lab1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GrammarTest {
    private Grammar regularGrammar;
    private Grammar contextFreeGrammar;

    @BeforeEach
    void setUp() {
        // Setup a regular grammar (Type 3)
        List<String> regularVN = Arrays.asList("S", "A");
        List<String> regularVT = Arrays.asList("a", "b");
        HashMap<String, List<String>> regularRules = new HashMap<>();
        regularRules.put("S", Arrays.asList("aA", "bS"));
        regularRules.put("A", Arrays.asList("b", "aS"));

        regularGrammar = new Grammar(regularVN, regularVT, "S", regularRules);

        // Setup a context-free grammar (Type 2)
        List<String> cfVN = Arrays.asList("S", "A", "B");
        List<String> cfVT = Arrays.asList("a", "b");
        HashMap<String, List<String>> cfRules = new HashMap<>();
        cfRules.put("S", Arrays.asList("AB", "aA"));
        cfRules.put("A", Arrays.asList("aA", "b"));
        cfRules.put("B", Arrays.asList("bB", "ε"));

        contextFreeGrammar = new Grammar(cfVN, cfVT, "S", cfRules);
    }

    @Test
    void testGenerateString() {
        for (int i = 0; i < 10; i++) {
            String generatedString = regularGrammar.generateString();
            assertNotNull(generatedString);
            assertFalse(generatedString.isEmpty());
        }
    }

    @Test
    void testDetermineGrammarType() {
        assertEquals("Type 3", regularGrammar.determineGrammarType());
        assertEquals("Type 2", contextFreeGrammar.determineGrammarType());
    }

    @Test
    void testIsRegularProduction() {
        // This would require testing a private method, so we'll check via the public method
        assertEquals("Type 3", regularGrammar.determineGrammarType());
    }
}