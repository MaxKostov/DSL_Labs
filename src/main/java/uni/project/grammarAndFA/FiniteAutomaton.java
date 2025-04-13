package uni.project.grammarAndFA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Collections;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Desktop;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class FiniteAutomaton {
    List<String> Q; // States
    List<String> Sigma; // Alphabet
    HashMap<HashMap<String, String>, List<String>> delta; // Transition function (updated)
    String q0; // Initial state
    List<String> F; // Final states (updated)

    public FiniteAutomaton(List<String> Q, List<String> Sigma, HashMap<HashMap<String, String>, List<String>> delta, String q0, List<String> F) {
        this.Q = Q;
        this.Sigma = Sigma;
        this.delta = delta;
        this.q0 = q0;
        this.F = F;
    }

    public boolean stringBelongToLanguage(final String inputString) {
        Set<String> currentStates = new HashSet<>();
        currentStates.add(q0);

        for (int i = 0; i < inputString.length(); i++) {
            char cursor = inputString.charAt(i);
            String symbol = String.valueOf(cursor);
            Set<String> nextStates = new HashSet<>();

            for (String state : currentStates) {
                HashMap<String, String> key = new HashMap<>();
                key.put(state, symbol);
                List<String> transitions = delta.getOrDefault(key, Collections.emptyList());
                nextStates.addAll(transitions);
            }

            currentStates = nextStates;
            if (currentStates.isEmpty()) return false;
        }

        currentStates.retainAll(F);
        return !currentStates.isEmpty();
    }

    public Grammar convertToGrammar() {
        List<String> VN = new ArrayList<>(Q);
        List<String> VT = new ArrayList<>(Sigma);
        String startVariable = q0;
        HashMap<String, List<String>> rules = new HashMap<>();

        for (String state : Q) rules.put(state, new ArrayList<>());

        for (Map.Entry<HashMap<String, String>, List<String>> entry : delta.entrySet()) {
            HashMap<String, String> key = entry.getKey();
            List<String> nextStates = entry.getValue();
            String fromState = key.keySet().iterator().next();
            String symbol = key.get(fromState);

            for (String toState : nextStates) {
                String production = symbol + toState;
                rules.get(fromState).add(production);
                if (F.contains(toState)) rules.get(fromState).add(symbol);
            }
        }

        for (String finalState : F) rules.get(finalState).add("ε");
        return new Grammar(VN, VT, startVariable, rules);
    }


    public boolean isDeterministic() {
        for (HashMap<String, String> key : delta.keySet()) {
            String symbol = key.values().iterator().next();
            if (symbol.equals("ε")) return false;
        }

        for (List<String> nextStates : delta.values()) {
            if (nextStates.size() > 1) return false;
        }

        return true;
    }


    private String toDotLanguage() {
        StringBuilder dot = new StringBuilder("digraph finite_automaton {\n    rankdir=LR;\n");
        dot.append("    node [shape=circle, fontname=\"Arial\"];\n    edge [fontname=\"Arial\"];\n");
        dot.append("    start [shape=point, style=invis];\n");

        // Add states with styling
        for (String state : Q) {
            String attributes = "";
            if (F.contains(state)) attributes = "shape=doublecircle, color=\"#2E86C1\", style=filled, fillcolor=\"#D6EAF8\"";
            else if (state.equals(q0)) attributes = "color=\"#229954\", style=filled, fillcolor=\"#D5F5E3\"";
            else attributes = "color=\"#5D6D7E\"";
            dot.append(String.format("    \"%s\" [%s];\n", state, attributes));
        }

        dot.append("    start -> \"" + q0 + "\";\n");

        // Build transitions
        Map<String, Map<String, List<String>>> transitions = new HashMap<>();
        for (Map.Entry<HashMap<String, String>, List<String>> entry : delta.entrySet()) {
            HashMap<String, String> key = entry.getKey();
            String from = key.keySet().iterator().next();
            String symbol = key.get(from);
            for (String to : entry.getValue()) {
                transitions.putIfAbsent(from, new HashMap<>());
                transitions.get(from).putIfAbsent(to, new ArrayList<>());
                transitions.get(from).get(to).add(symbol);
            }
        }

        // Append transitions to DOT
        for (String from : transitions.keySet()) {
            for (String to : transitions.get(from).keySet()) {
                String labels = String.join(", ", transitions.get(from).get(to));
                dot.append(String.format("    \"%s\" -> \"%s\" [label=\"%s\"];\n", from, to, labels));
            }
        }

        dot.append("}");
        return dot.toString();
    }

    public boolean saveToDotFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(toDotLanguage());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving DOT file: " + e.getMessage());
            return false;
        }
    }

    public boolean visualizeInWindow() {
        try {
            File tempDotFile = File.createTempFile("automaton_", ".dot");
            File tempImageFile = File.createTempFile("automaton_", ".png");

            tempDotFile.deleteOnExit();
            tempImageFile.deleteOnExit();

            if (!saveToDotFile(tempDotFile.getAbsolutePath())) {
                return false;
            }

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "dot", "-Tpng", tempDotFile.getAbsolutePath(), "-o", tempImageFile.getAbsolutePath()
            );

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(tempDotFile);
                    JOptionPane.showMessageDialog(null,
                            "Graphviz is not installed or failed. Opening the DOT file instead.\n" +
                                    "You can visualize it using an online tool like GraphvizOnline.",
                            "Visualization Information", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                }
                return false;
            }

            SwingUtilities.invokeLater(() -> {
                try {
                    showAutomatonVisualization(tempImageFile);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            "Error loading the image: " + e.getMessage(),
                            "Visualization Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            return true;
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during visualization: " + e.getMessage());

            showDotTextWindow();

            return false;
        }
    }

    private void showAutomatonVisualization(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);

        JFrame frame = new JFrame("Finite Automaton Visualization");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(image.getWidth(), image.getHeight());
            }
        };

        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "Visualization of Finite Automaton\n" +
                                "Initial State: " + q0 + "\n" +
                                "Final State: " + F + "\n" +
                                "Number of States: " + Q.size() + "\n" +
                                "Alphabet Size: " + Sigma.size() + "\n" +
                                "Is Deterministic: " + isDeterministic(),
                        "Automaton Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private void showDotTextWindow() {
        SwingUtilities.invokeLater(() -> {
            // Create a text area with the DOT representation
            JTextArea textArea = new JTextArea(toDotLanguage());
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            JFrame frame = new JFrame("Finite Automaton DOT Representation");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new JScrollPane(textArea));
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);

            JOptionPane.showMessageDialog(frame,
                    "Graphviz could not generate an image visualization.\n" +
                            "Displaying the DOT language representation instead.\n" +
                            "You can copy this text and use it with an online tool like GraphvizOnline.",
                    "Visualization Information",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public FiniteAutomaton convertToDFA() {
        // Track DFA states (each state is a set of NDFA states)
        Map<Set<String>, String> dfaStateMap = new HashMap<>();
        List<String> dfaStates = new ArrayList<>();
        List<String> dfaSigma = new ArrayList<>(Sigma);
        dfaSigma.remove("ε"); // Remove epsilon from alphabet
        HashMap<HashMap<String, String>, List<String>> dfaDelta = new HashMap<>();

        // Initial state is epsilon closure of q0
        Set<String> initialState = epsilonClosure(Collections.singleton(q0));
        String dfaQ0 = createDfaStateName(initialState);
        dfaStateMap.put(initialState, dfaQ0);
        dfaStates.add(dfaQ0);

        Queue<Set<String>> unprocessed = new LinkedList<>();
        unprocessed.add(initialState);

        while (!unprocessed.isEmpty()) {
            Set<String> current = unprocessed.poll();
            String currentDfaState = dfaStateMap.get(current);

            for (String symbol : dfaSigma) {
                // Compute move + epsilon closure
                Set<String> next = epsilonClosure(move(current, symbol));

                if (next.isEmpty()) continue;

                // Get or create DFA state name
                String nextDfaState = dfaStateMap.computeIfAbsent(next, k -> {
                    String name = createDfaStateName(k);
                    dfaStates.add(name);
                    unprocessed.add(k);
                    return name;
                });

                // Add transition to delta
                HashMap<String, String> key = new HashMap<>();
                key.put(currentDfaState, symbol);
                dfaDelta.put(key, Collections.singletonList(nextDfaState));
            }
        }

        // Determine final states (any set containing original final state)
        List<String> dfaF = new ArrayList<>();
        for (Set<String> stateSet : dfaStateMap.keySet()) {
            for (String fState : F) {
                if (stateSet.contains(fState)) {
                    dfaF.add(dfaStateMap.get(stateSet));
                    break;
                }
            }
        }

        return new FiniteAutomaton(dfaStates, dfaSigma, dfaDelta, dfaQ0, dfaF);
    }

    private String createDfaStateName(Set<String> states) {
        List<String> sorted = new ArrayList<>(states);
        Collections.sort(sorted);
        return "q" + String.join("", sorted);
    }

    private Set<String> epsilonClosure(Set<String> states) {
        Set<String> closure = new HashSet<>(states);
        Stack<String> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            String state = stack.pop();
            HashMap<String, String> key = new HashMap<>();
            key.put(state, "ε");
            for (String nextState : delta.getOrDefault(key, Collections.emptyList())) {
                if (closure.add(nextState)) {
                    stack.push(nextState);
                }
            }
        }
        return closure;
    }

    private Set<String> move(Set<String> states, String symbol) {
        Set<String> result = new HashSet<>();
        for (String state : states) {
            HashMap<String, String> key = new HashMap<>();
            key.put(state, symbol);
            result.addAll(delta.getOrDefault(key, Collections.emptyList()));
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Q: ").append(Q.toString()).append("\n");
        res.append("Sigma: ").append(Sigma.toString()).append("\n");
        res.append("delta: ").append(delta.toString()).append("\n");
        res.append("q0: ").append(q0).append("\n");
        res.append("F: ").append(F).append("\n");
        return res.toString();
    }
}