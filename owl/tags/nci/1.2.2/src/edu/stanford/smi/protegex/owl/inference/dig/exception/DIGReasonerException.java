package edu.stanford.smi.protegex.owl.inference.dig.exception;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 4, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DIGReasonerException extends Exception {

    public DIGReasonerException(String message) {
        super(message);
    }


    public DIGReasonerException(String message, Throwable cause) {
        super(message, cause);
    }
}

