package lsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DOLSystem {

    /**
     * Set of available characters.
     */
    private Set<Character> alphabet;
    /**
     * Production rule of each character of the alphabet.
     */
    public Map<Character, String> rules;
    /**
     * Initial configuration of L-system.
     */
    protected String axiom;
    /**
     * Container to hold the current state of the L-system.
     */
    protected String code;


    public DOLSystem(String axiom, Map<Character, String> rules) {
        this.axiom = axiom;
        this.rules = rules;
        if (this.rules == null) {
            this.rules = new HashMap<Character, String>();
            this.rules.put('F', "FF");
            this.rules.put('+', "+");
            this.rules.put('-', "-");
            this.rules.put('[', "[");
            this.rules.put(']', "]");
            this.rules.put('G', "F+[[G]-G]-F[-FG]+G");
        }

        this.alphabet = this.rules.keySet();
    }

    /**
     * Generate the code by applying the rules until the
     * description has a given length.
     *
     * @param        max        maximal length of iteractions.
     */
    public void iterate(int max) {

        StringBuilder result = new StringBuilder(axiom);

        for (int i = 0; i < max; i++) {
            String tmp = result.toString();
            result = new StringBuilder();
            for (int j = 0; j < tmp.length(); j++) {
                result.append(rules.get(tmp.charAt(j)));
            }
        }

        code = result.toString();
    }

    public String getCode() {
        return code;
    }
}
