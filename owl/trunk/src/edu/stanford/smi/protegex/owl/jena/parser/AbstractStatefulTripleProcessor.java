package edu.stanford.smi.protegex.owl.jena.parser;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

public abstract class AbstractStatefulTripleProcessor {
	
	protected TripleProcessor processor;
	
	protected OWLModel owlModel;	
	protected UndefTripleManager undefTripleManager;
	
	//protected boolean importing;
	
	//protected TripleStore tripleStore;
	
	protected SimpleFrameStore simpleFrameStore;
		

	public AbstractStatefulTripleProcessor(TripleProcessor processor) {
		this.processor = processor;
		this.owlModel = processor.getOWLModel();
		this.undefTripleManager = processor.getUndefTripleManager();
		//this.importing = processor.isImporting();
		this.simpleFrameStore = ParserUtil.getSimpleFrameStore(owlModel);
		//this.tripleStore = processor.getTripleStore();
	}
	
	
	protected Frame createRestriction(String restrName, String predName, TripleStore ts) {
		Frame restriction = getFrame(restrName);

		if (restriction != null)
			return restriction;

		FrameID id = new FrameID(restrName);
		restriction = RestrictionCreatorUtility.createRestriction(owlModel, id, predName, ts);

		if (restriction != null) {
			checkUndefinedResources(restrName);
		}

		if (isImporting(ts)) {
			restriction.setIncluded(true);
		}

		return restriction;
	}
	

	protected void checkUndefinedResources(String uri) {
		processor.checkUndefinedResources(uri);
	}
	
	protected Frame getFrame(String name) {
		return simpleFrameStore.getFrame(name);
	}
	
	public void doPostProcessing(){
		// do nothing by default
	}
	
	public boolean isImporting(TripleStore ts) {
		return !ts.equals(owlModel.getTripleStoreModel().getTopTripleStore());
	}
	
}
