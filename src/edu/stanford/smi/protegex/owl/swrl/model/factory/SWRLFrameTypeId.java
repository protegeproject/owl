/**
 * 
 */
package edu.stanford.smi.protegex.owl.swrl.model.factory;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDifferentIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLVariable;


public enum SWRLFrameTypeId {
    SWRL_BUILTIN_ATOM(DefaultSWRLBuiltinAtom.class),
    SWRL_CLASS_ATOM(DefaultSWRLClassAtom.class),
    SWRL_DATARANGE_ATOM(DefaultSWRLDataRangeAtom.class),
    SWRL_DATAVALUED_PROPERTY_ATOM(DefaultSWRLDatavaluedPropertyAtom.class),
    SWRL_INDIVIDUAL_PROPERTY_ATOM(DefaultSWRLIndividualPropertyAtom.class),
    SWRL_DIFFERENTINDIVIDUALS(DefaultSWRLDifferentIndividualsAtom.class),
    SWRL_BUILTIN(DefaultSWRLBuiltin.class),
    SWRL_IMP(DefaultSWRLImp.class),
    SWRL_VARIABLE(DefaultSWRLVariable.class);
    
    public final static int SWRL_FRAME_TYPE_ID_BASE = 42; // leave some room for expansion of the owl java class ids.
    public final static int SWRL_FRAME_TYPE_ID_END  = SWRL_FRAME_TYPE_ID_BASE + SWRLFrameTypeId.values().length - 1;
    
    private Class<? extends Instance> javaClass;
    
    private SWRLFrameTypeId(Class<? extends Instance> javaClass) {
        this.javaClass = javaClass;
    }
    
    public int getFrameTypeId() {
        return SWRL_FRAME_TYPE_ID_BASE + ordinal();
    }
    
    public Class<? extends Instance> getJavaClass() {
        return javaClass;
    }
}