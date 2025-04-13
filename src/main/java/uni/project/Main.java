package uni.project;

import uni.project.grammarAndFA.Grammar;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> VN = Arrays.asList("S", "A", "B", "C", "D");
        List<String> VT = Arrays.asList("a", "b");
        String startVariable = "S";

        HashMap<String, List<String>> hashMap = new HashMap<>();
        hashMap.put("S", Arrays.asList("bA", "AC"));
        hashMap.put("A", Arrays.asList("AbAa", "bS", "BC"));
        hashMap.put("B", Arrays.asList("BbaA", "a", "bSa"));
        hashMap.put("C", Arrays.asList("ε"));
        hashMap.put("D", Arrays.asList("AB"));

        Grammar grammar = new Grammar(VN, VT, startVariable, hashMap);
        System.out.println("Original Grammar:");
        System.out.println(grammar.toString());

        // Transform the grammar to CNF
        Grammar cnfGrammar = grammar.toChomskyNormalForm();

        System.out.println("\nTransformed Grammar in Chomsky Normal Form:");
        System.out.println(cnfGrammar.toString());




//        FiniteAutomaton fa = grammar.toFiniteAutomaton();
//        System.out.println(fa.isDeterministic());
//        //fa.visualizeInWindow();
//
//        System.out.println(" ");
//        System.out.println("Finite automaton: ");
//        System.out.println(fa);
//        System.out.println(fa.stringBelongToLanguage("asfdsfsdf"));
//        System.out.println(fa.stringBelongToLanguage(grammar.generateString()));
//
//        List<String> Q1 = List.of("q0", "q1", "q2", "q3");
//        List<String> Sigma1 = List.of("a", "b", "c");
//        String q01 = "q0";
//        List<String> F1 = List.of("q3");
//
//        HashMap<HashMap<String, String>, List<String>> delta1 = new HashMap<>();
//        delta1.put(new HashMap<>(Map.of("q0", "a")), List.of("q1"));
//        delta1.put(new HashMap<>(Map.of("q1", "b")), List.of("q2"));
//        delta1.put(new HashMap<>(Map.of("q2", "c")), List.of("q0"));
//        delta1.put(new HashMap<>(Map.of("q1", "a")), List.of("q3"));
//        delta1.put(new HashMap<>(Map.of("q0", "b")), List.of("q2"));
//        delta1.put(new HashMap<>(Map.of("q2", "c")), List.of("q3"));
//
//        FiniteAutomaton fa1 = new FiniteAutomaton(Q1, Sigma1, delta1, q01, F1);
//        Grammar grammar1 = fa1.convertToGrammar();
//        System.out.println(" ");
//        System.out.println(grammar1.toString());
//        System.out.println(grammar1.determineGrammarType());
//        //fa1.visualizeInWindow();
//
//        List<String> Q = List.of("q0", "q1", "q2");
//        List<String> Sigma = List.of("a", "b");
//        String q0 = "q0";
//        List<String> F = List.of("q2");
//
//        HashMap<HashMap<String, String>, List<String>> delta = new HashMap<>();
//        delta.put(new HashMap<>(Map.of("q0", "a")), List.of("q0", "q1"));
//        delta.put(new HashMap<>(Map.of("q0", "b")), List.of("q1"));
//        delta.put(new HashMap<>(Map.of("q1", "a")), List.of("q2"));
//        delta.put(new HashMap<>(Map.of("q1", "b")), List.of("q0", "q2"));
//        delta.put(new HashMap<>(Map.of("q2", "a")), List.of("q2"));
//
//
//        System.out.println(" ");
//        FiniteAutomaton ndfa = new FiniteAutomaton(Q, Sigma, delta, q0, F);
//        System.out.println(ndfa.isDeterministic());
//        System.out.println(ndfa.toString());
//        ndfa.visualizeInWindow();
//        FiniteAutomaton dfa = ndfa.convertToDFA();
//        System.out.println(dfa.toString());
//        System.out.println(dfa.isDeterministic());
//        dfa.visualizeInWindow();
    }
}
