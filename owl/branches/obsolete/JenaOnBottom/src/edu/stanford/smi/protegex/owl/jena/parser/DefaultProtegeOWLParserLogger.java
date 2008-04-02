package edu.stanford.smi.protegex.owl.jena.parser;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultProtegeOWLParserLogger implements ProtegeOWLParserLogger {

    private int count = 0;


    private final static String PREFIX = "[ProtegeOWLParser] ";


    public void logImport(String uri, String physicalURL) {
        String msg = PREFIX + "Importing " + uri;
        if (!uri.equals(physicalURL)) {
            msg += " (from " + physicalURL + ")";
        }
        System.out.println(msg);
    }


    public void logTripleAdded(RDFResource subject, RDFProperty predicate, Object object) {
        //if(object instanceof Frame) {
        // System.out.println(" + " + subject.getName() + " " + predicate.getName() + " " + ((Frame)object).getName());
        //}
        //else {
        // System.out.println(" + " + subject.getName() + " " + predicate.getName() + " " + object);
        //}
        if ((++count % 10000) == 0) {
            System.out.println(PREFIX + "Triple " + count);
        }
    }


    public void logWarning(String message) {
        System.err.println(PREFIX + "Warning: " + message);
    }
}
