package uni.project;

import uni.project.lab1.FiniteAutomaton;
import uni.project.lab1.Grammar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> VN = Arrays.asList(new String[]{"S", "B", "D"});

        List<String> VT = Arrays.asList(new String[]{"a", "b", "c"});

        String startVariable = "S";

        HashMap<String, List<String>> hashMap = new HashMap<>();
        hashMap.put("S", Arrays.asList("aB", "bB"));
        hashMap.put("B", Arrays.asList("bD", "cB", "aS"));
        hashMap.put("D", Arrays.asList("b", "aD"));


        Grammar grammar = new Grammar(VN, VT, startVariable, hashMap);
        System.out.println("Random generated string: " + grammar.generteString());

        FiniteAutomaton fa = grammar.toFiniteAutomaton();

        System.out.println(" ");
        System.out.println("Finite automaton: ");
        System.out.println(fa);
        System.out.println(fa.stringBelongToLanguage("asfdsfsdf"));
        System.out.println(fa.stringBelongToLanguage(grammar.generteString()));
    }
}
