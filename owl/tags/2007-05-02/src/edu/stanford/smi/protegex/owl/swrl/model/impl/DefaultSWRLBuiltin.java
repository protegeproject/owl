package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;

import javax.swing.*;
import java.util.Set;

public class DefaultSWRLBuiltin extends DefaultOWLIndividual implements SWRLBuiltin {

    public DefaultSWRLBuiltin(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultSWRLBuiltin() {
    }


    public Icon getIcon() {
        return SWRLIcons.getBuiltinIcon();
    }


    public void getReferencedInstances(Set set) {
    }
}
