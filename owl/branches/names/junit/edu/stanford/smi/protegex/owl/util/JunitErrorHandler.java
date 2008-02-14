package edu.stanford.smi.protegex.owl.util;

import java.util.logging.Level;

import junit.framework.TestCase;
import edu.stanford.smi.protege.util.ErrorHandler;
import edu.stanford.smi.protege.util.Log;

public class JunitErrorHandler implements ErrorHandler<Throwable> {
    private TestCase test;
    private boolean ignoreWarnings;
    
    public JunitErrorHandler(TestCase test, boolean ignoreWarnings) {
        this.test = test;
        this.ignoreWarnings = ignoreWarnings;
    }

    public void error(Throwable e) throws Throwable {
        fail(e);
    }

    public void fatalError(Throwable e) throws Throwable {
        fail(e);
    }

    public void warning(Throwable e) throws Throwable {
        if (!ignoreWarnings) {
            fail(e);
        }
    }
    
    private void fail(Throwable e) {
        Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        test.fail(e.getMessage());
    }

}
