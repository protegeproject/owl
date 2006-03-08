package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         24-Feb-2006
 */
public class HiddenClassFind extends DefaultFind{

    public HiddenClassFind(OWLModel owlModel, int type) {
        super(owlModel, type);
    }

    protected boolean isValidFrameToSearch(Frame f) {
        System.out.println("f = " + f);
        System.out.println("f.isVisible() = " + f.isVisible());
        return f instanceof Cls && !f.isVisible();
    }

    public String getDescription() {
        return "Find Hidden Class";
    }
}
