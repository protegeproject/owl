package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;

import javax.swing.*;
import java.util.Set;

public class DefaultSWRLVariable extends DefaultOWLIndividual implements SWRLVariable {

    public DefaultSWRLVariable(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultSWRLVariable() {
    }


    public String getBrowserText() {
        return "?" + getName();
    }


    public Icon getIcon() {
        return SWRLIcons.getVariableIcon();
    }


    public void getReferencedInstances(Set set) {
    }
}
