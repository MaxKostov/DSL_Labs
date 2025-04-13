# Lab Report: Regular Expression Generator

### Course: Formal Languages & Finite Automata
### Author: Maxim Costov

---

## Introduction
Regular expressions (regex) are powerful tools for pattern matching and text manipulation. They provide a concise and flexible means for matching strings of text, such as particular characters, words, or patterns of characters. In this lab, we explore the dynamic generation of valid strings based on given regular expressions, along with the processing steps involved.

---

## Objectives

1. **Understand Regular Expressions**:
    - Learn the syntax and semantics of regular expressions.
    - Explore their applications in text processing and validation.

2. **Dynamic String Generation**:
    - Implement a Java program that interprets regular expressions dynamically to generate valid strings.
    - Handle quantifiers, alternations, and optional symbols.

3. **Processing Steps Visualization**:
    - Track and display the sequence of operations performed during regex processing.
    - Ensure clarity in how each part of the regex is interpreted.

4. **Limitations and Edge Cases**:
    - Limit the repetition of symbols (e.g., Kleene star) to a reasonable number (e.g., 5 times).
    - Handle nested structures and complex regex patterns.

---

## Implementation

### Variant 3:
- J*K(L|M|N)*O?(P|Q)^3
- A*B(C|D|E)F(G|H|i)^2
- O(P|Q|R)*2(3|4)

### 1. **Regex Components Handled**
The implemented `RegexGenerator` class supports the following regex constructs:

- **Groups with Quantifiers**: `(X|Y|Z)^n`
    - Generates `n` repetitions of one of the alternatives `X`, `Y`, or `Z`.
- **Kleene Star (`*`)**:
    - `(X|Y|Z)*`: Repeats the group 0 to 5 times.
    - `X*`: Repeats the symbol `X` 0 to 5 times.
- **Optional Symbols (`?`)**:
    - `X?`: Includes or excludes `X` randomly.
- **Alternations (`|`)**:
    - `(X|Y|Z)`: Randomly selects one alternative.

### 2. **Key Methods**

#### `generateString(String regex)`
The main method that processes the regex step-by-step:
1. Processes groups with quantifiers.
2. Handles Kleene star operations.
3. Resolves optional symbols.
4. Expands alternations.

```java
public static String generateString(String regex) {
    addProcessingStep("Starting to process regex: " + regex);
    regex = processGroupsWithQuantifiers(regex);
    regex = processGroupKleeneStars(regex);
    regex = processSimpleKleeneStars(regex);
    regex = processOptionalSymbols(regex);
    regex = processAlternatives(regex);
    addProcessingStep("Final result: " + regex);
    return regex;
}
```

#### `processGroupsWithQuantifiers(String regex)`
Handles patterns like `(a|b)^3` by repeating a randomly chosen alternative `n` times.

```java
private static String processGroupsWithQuantifiers(String regex) {
    Pattern pattern = Pattern.compile("\\(([^()]+)\\)\\^(\\d+)");
    Matcher matcher = pattern.matcher(regex);
    while (matcher.find()) {
        String group = matcher.group(1);
        int repetitions = Integer.parseInt(matcher.group(2));
        String[] alternatives = group.split("\\|");
        StringBuilder replacement = new StringBuilder();
        for (int i = 0; i < repetitions; i++) {
            String chosen = alternatives[random.nextInt(alternations.length)];
            replacement.append(chosen);
        }
        regex = regex.substring(0, matcher.start()) + replacement + regex.substring(matcher.end());
        matcher = pattern.matcher(regex); // Reset matcher
    }
    return regex;
}
```

#### `processGroupKleeneStars(String regex)`
Processes `(a|b)*` by repeating the group 0 to 5 times.

```java
private static String processGroupKleeneStars(String regex) {
    Pattern pattern = Pattern.compile("\\(([^()]+)\\)\\*");
    Matcher matcher = pattern.matcher(regex);
    while (matcher.find()) {
        String group = matcher.group(1);
        String[] alternatives = group.split("\\|");
        int reps = random.nextInt(MAX_KLEENE_REPETITIONS + 1);
        StringBuilder replacement = new StringBuilder();
        for (int i = 0; i < reps; i++) {
            replacement.append(alternatives[random.nextInt(alternatives.length)]);
        }
        regex = regex.substring(0, matcher.start()) + replacement + regex.substring(matcher.end());
        matcher = pattern.matcher(regex);
    }
    return regex;
}
```

#### `processOptionalSymbols(String regex)`
Handles `X?` by randomly including or excluding `X`.

```java
private static String processOptionalSymbols(String regex) {
    Pattern pattern = Pattern.compile("([^\\?])\\?");
    Matcher matcher = pattern.matcher(regex);
    while (matcher.find()) {
        char symbol = matcher.group(1).charAt(0);
        boolean include = random.nextBoolean();
        String replacement = include ? String.valueOf(symbol) : "";
        regex = regex.substring(0, matcher.start()) + replacement + regex.substring(matcher.end());
        matcher = pattern.matcher(regex);
    }
    return regex;
}
```

---

## Results

### Example Input/Output

**Input Regexes**:
1. `(a|b)^3`
2. `(x|y|z)*`
3. `a?b*c`

**Generated Outputs**:
1. `aba` (from `(a|b)^3`)
2. `yxz` (from `(x|y|z)*`, 3 repetitions)
3. `abbbc` (from `a?b*c`, `a` included, `b` repeated 3 times)

**Processing Steps**:  
For regex `(a|b)^3`:
```
Step 1: Starting to process regex: (a|b)^3  
Step 2: Found group with quantifier: (a|b)^3  
Step 3: Repetition 1: Chose 'a' from alternatives a, b  
Step 4: Repetition 2: Chose 'b' from alternatives a, b  
Step 5: Repetition 3: Chose 'a' from alternatives a, b  
Step 6: After processing group with quantifier: aba  
Step 7: Final result: aba  
```
---

## Conclusion

This lab enhanced my understanding of regular expressions and their dynamic interpretation. The implemented `RegexGenerator` successfully processes complex regex patterns, generates valid strings, and provides clear processing steps. Future improvements could include support for more regex constructs (e.g., character classes, anchors).

---

## References

1. Oracle Java Documentation: [Regular Expressions](https://docs.oracle.com/javase/tutorial/essential/regex/)
2. "Mastering Regular Expressions" by Jeffrey Friedl.

---

[GitHub Repository](https://github.com/MaxKostov/DSL_Labs)
