package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;


//TODO: Metaclass postprocessing: keep track of subclasses of owl:Class and post process them. Test self-referencing metaclasses
//TODO: Postprocessing GCI - refactor in their own class
//TODO: Postprcessing undef triples -> create them as Untyped resources (swizzle)
//TODO: Solution for copyFacetedValues not to happen too many times. Maybe use flag in AbstractOWLModel
//TODO: Check range and domain of properties
//TODO: Timing logger for the parser

//-- later --
//TODO: check ranges with datatypes 
//TODO: Use java objects rather than strings for the frames-owl mapping
//TODO: Process each triple in a try catch


public class TripleProcessor {

	private OWLModel owlModel;
	private TripleStore tripleStore;
	
	private TripleProcessorForResourceObjects processorResourceObjs;
	private TripleProcessorForLiteralObjects processorLiteralObjs;
	
	private UndefTripleManager undefTripleManager;
	
	private boolean importing;	


	public TripleProcessor(OWLModel owlModel) {
		this(owlModel, owlModel.getTripleStoreModel().getTopTripleStore());
	}

	public TripleProcessor(OWLModel owlModel, TripleStore tripleStore) {
		this.owlModel = owlModel;
		this.tripleStore = tripleStore;

		this.undefTripleManager = new UndefTripleManager();
		this.importing = !tripleStore.equals(owlModel.getTripleStoreModel().getTopTripleStore());
		//should come as the last in the initialization
		this.processorResourceObjs = new TripleProcessorForResourceObjects(this);
		this.processorLiteralObjs = new TripleProcessorForLiteralObjects(this);		
	}


	public boolean processTriple(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		return processorResourceObjs.processTriple(subj, pred, obj, alreadyInUndef);
	}

	public boolean processTriple(AResource subj, AResource pred, ALiteral lit, boolean alreadyInUndef) {
		return processorLiteralObjs.processTriple(subj, pred, lit, alreadyInUndef);
	}

	
	public OWLModel getOWLModel() {
		return owlModel;
	}
	
	public void addUndefTriple(AResource subj, AResource pred, AResource obj, String undefName, boolean alreadyInUndef) {
		if (!alreadyInUndef) {		  
			undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, obj, undefName)); //check this!!
		}		
	}
	
	protected void checkUndefinedResources(String uri) {		
		Collection<UndefTriple> undefTriples = undefTripleManager.getUndefTriples(uri); 

		for (Iterator<UndefTriple> iter = undefTriples.iterator(); iter.hasNext();) {
			UndefTriple undefTriple = (UndefTriple) iter.next();
			Object obj = undefTriple.getTripleObj();

			boolean success = false;

			if (obj instanceof AResource) {	
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (AResource) undefTriple.getTripleObj(), true);
			} else if (obj instanceof ALiteral) {
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (ALiteral) undefTriple.getTripleObj(), true);
			}

			if (success) {			
				iter.remove();
				undefTripleManager.removeUndefTriple(uri, undefTriple);
			}
		}
	}
	

	public UndefTripleManager getUndefTripleManager() {
		return undefTripleManager;
	}	

	
	public TripleStore getTripleStore() {
		return tripleStore;
	}
	
	public boolean isImporting() {
		return importing;
	}


	public void processUndefTriples() {
		for (Iterator<UndefTriple> iter = getUndefTripleManager().getUndefTriples().iterator(); iter.hasNext();) {
			UndefTriple undefTriple = (UndefTriple) iter.next();
			Object obj = undefTriple.getTripleObj();
			
			boolean success = false;

			if (obj instanceof AResource) {			
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (AResource) undefTriple.getTripleObj(), true);
			} else if (obj instanceof ALiteral) {
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (ALiteral) undefTriple.getTripleObj(), true);
			}	

			if (success) {
				getUndefTripleManager().removeUndefTriple(undefTriple.getUndef(), undefTriple);
			}
		}
	}


	public void doPostProcessing() {
		processorResourceObjs.doPostProcessing();
		processorLiteralObjs.doPostProcessing();
	}

}
