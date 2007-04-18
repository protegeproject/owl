package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom;

public abstract class DefaultSWRLAtom extends DefaultOWLIndividual implements SWRLAtom {

    public DefaultSWRLAtom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    } // DefaultSWRLAtom


    public DefaultSWRLAtom() {
    }

} // DefaultSWRLAtom


