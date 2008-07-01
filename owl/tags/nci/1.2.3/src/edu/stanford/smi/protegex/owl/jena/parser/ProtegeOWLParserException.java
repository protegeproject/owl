package edu.stanford.smi.protegex.owl.jena.parser;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeOWLParserException extends RuntimeException {

    private String message;

    private String suggestion;


    public ProtegeOWLParserException(String message, String suggestion) {
        super(message);
        this.message = message;
        this.suggestion = suggestion;
    }


    public String toString() {
        String str = message;
        if (suggestion != null) {
            str += "\nSuggestion: " + suggestion;
        }
        return str;
    }
}
