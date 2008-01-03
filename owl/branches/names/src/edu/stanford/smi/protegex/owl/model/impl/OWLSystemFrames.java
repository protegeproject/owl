package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.SystemFrames;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNames;

public class OWLSystemFrames extends SystemFrames {
	private OWLModel owlModel;
	
	public OWLSystemFrames(OWLModel owlModel) {
		super(owlModel);
		this.owlModel = owlModel;
		createModel();
	}
	
	private void createModel() {
	    createNamedClass(OWLNames.Cls.THING);
	    
	    createRDFProperty(RDFSNames.Slot.SUB_PROPERTY_OF);
	    createRDFProperty(OWLNames.Slot.INVERSE_OF);
	}
	
	
	private void createNamedClass(String name) {
	    FrameID id = new FrameID(name);
	    OWLNamedClass cls = new DefaultOWLNamedClass(owlModel, id);
	    addFrame(id, cls);
	}
	
	private void createRDFProperty(String name) {
	    FrameID id = new FrameID(name);
	    RDFProperty property = new DefaultRDFProperty(owlModel, id);
	    addFrame(id, property);
	}
	
	
	/* **********************************************************************
	 * Modified SystemFrames calls.
	 */
	@Override
	public OWLNamedClass getRootCls() {
		return (OWLNamedClass) getFrame(new FrameID(OWLNames.Cls.THING));
	}

	@Override
	public RDFProperty getDirectSuperslotsSlot() {
	    return (RDFProperty) getFrame(new FrameID(RDFSNames.Slot.SUB_PROPERTY_OF));
	}
	
    @Override
	public RDFProperty getInverseSlotSlot() {
        return (RDFProperty) getFrame(new FrameID(OWLNames.Slot.INVERSE_OF));
    }
}
