# Lab Report: Intro to Formal Languages and Finite Automata

### Course: Formal Languages & Finite Automata
### Author: Maxim Costov

---

## Overview
This lab focuses on understanding formal languages, regular grammars, and finite automata. We explore the components of formal languages, implement a grammar, generate valid strings, convert the grammar to a finite automaton, and verify if strings belong to the language.

---

## Objectives

1. **Understanding Formal Languages**:  
   A formal language consists of:
    - **Alphabet (VT)**: Set of valid characters.
    - **Vocabulary**: Set of valid words.
    - **Grammar (P)**: Set of rules/constraints.

2. **Project Setup**:
    - Created a GitHub repository to store and manage the project.
    - Chose Java for implementation due to its robustness and familiarity.
    - Reports are stored separately for easy verification.

3. **Grammar Implementation**:
    - **Variant 11 Grammar Definition**:
        - Non-terminal symbols (VN): \( \{S, B, D\} \)
        - Terminal symbols (VT): \( \{a, b, c\} \)
        - Production rules (P):
            - S → aB
            - S → bB
            - B → bD
            - D → b
            - D → aD
            - B → cB
            - B → aS

    - **Tasks Completed**:
        - Implemented a `Grammar` class to define and manage the grammar.
        - Added a function to generate 5 valid strings.
        - Converted the grammar to a finite automaton.
        - Implemented a method in the finite automaton to check string validity.

---

## Theoretical Background on Finite Automaton

A finite automaton is a mathematical model of computation used to represent and recognize regular languages. It consists of a finite set of states, a finite set of input symbols (alphabet), transitions between states based on input symbols, an initial state, and a set of accepting (final) states.

There are two main types of finite automata:

1. **Deterministic Finite Automaton (DFA)**: For each state and input symbol, there is exactly one transition to a next state.
2. **Non-deterministic Finite Automaton (NFA)**: For a given state and input symbol, there can be multiple possible next states.

Key components of a finite automaton:

- **Q**: Finite set of states.
- **Σ**: Finite set of input symbols (alphabet).
- **δ**: Transition function \( \delta: Q \times \Sigma \rightarrow Q \), describing state transitions based on input symbols.
- **q0**: Initial state (\( q_0 \in Q \)).
- **F**: Set of accepting (final) states (\( F \subseteq Q \)).

A string is accepted by a finite automaton if there is a sequence of transitions from the initial state to an accepting state after processing all input symbols.

Finite automata are fundamental in the study of formal languages, as they correspond precisely to regular languages. Regular grammars can be transformed into finite automata and vice versa, which is a key concept in the theory of computation.

---

## Implementation

### 1. Grammar Class
Implemented a class `Grammar` with methods to generate valid strings and convert the grammar to a finite automaton.

#### Here is my `generateString` method:
```java
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
            sb.replace(cursor, cursor + 1, replacement);}
    }
    return sb.toString();
}
```

#### With this method, I convert a regular grammar class into a finite automaton class:
```java
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
```

### 2. Finite Automaton Class
#### Checking if a string belongs to the language:
```java
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
```

### 3. Main Class
Demonstrated the functionality by generating strings and verifying them.
```java
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
```

### Output:
![img.png](images/img.png)

### Unittests:
#### Test results:
![img.png](images/img1.png)

---

## Results

The implementation successfully generated valid strings based on the defined grammar and verified string membership using the finite automaton. Example output of 5 valid strings includes `abb`, `acbab`, `bcbabb`, `ababb`, and `bacbabb`. Verification of string membership using the finite automaton showed that valid strings like `abb` were accepted, while invalid strings like `abc` were rejected.

---

## Conclusion

This lab was a valuable experience in understanding and applying the fundamental concepts of formal languages and finite automata. Through the implementation of a grammar and its conversion into a finite automaton, I gained practical insights into how formal grammars can define languages and how finite automata can be used to verify string membership. This hands-on approach solidified my understanding of the relationship between grammars and automata and their significance in the theory of computation.

---

## Repository Link
[GitHub Repository](https://github.com/MaxKostov/DSL_Labs)

---

## References
1. Java Documentation: [https://docs.oracle.com/javase/](https://docs.oracle.com/javase/)

