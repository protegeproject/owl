package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

//TODO: remove the untyped type if a type definition is found somewhere else
//TODO: In post-processing create untyped - create in the right ts
//TODO: Check if the untyped types are written out
//TODO: Optimize the creation of untyped things
//TODO: Add owl:Thing also for untyped classes
//TODO: Use StringBuffer in place of String for all namespace methods

//-- later --
//TODO: Postprocessing GCI - refactor in their own class
//TODO: Process each triple in a try catch
//TODO: Each post process in a try catch
//TODO: Timing logger for the parser


public class TripleProcessor {

	private OWLModel owlModel;
	private TripleStore tripleStore;
	
	private TripleProcessorForResourceObjects processorResourceObjs;
	private TripleProcessorForLiteralObjects processorLiteralObjs;
	private TripleProcessorForUntypedResources untypedProcessor;
	
	private UndefTripleManager undefTripleManager;
	
	private boolean importing;	


	public TripleProcessor(OWLModel owlModel) {
		this(owlModel, owlModel.getTripleStoreModel().getTopTripleStore());
	}

	public TripleProcessor(OWLModel owlModel, TripleStore tripleStore) {
		this.owlModel = owlModel;
		this.tripleStore = tripleStore;

		this.undefTripleManager = ((AbstractOWLModel)owlModel).getUndefTripleManager();
		this.importing = !tripleStore.equals(owlModel.getTripleStoreModel().getTopTripleStore());
		
		//should come as the last in the initialization
		this.processorResourceObjs = new TripleProcessorForResourceObjects(this);
		this.processorLiteralObjs = new TripleProcessorForLiteralObjects(this);
		this.untypedProcessor = new TripleProcessorForUntypedResources(this);
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
	
	public void addUndefTriple(AResource subj, AResource pred, AResource obj, String undefName, boolean alreadyInUndef, TripleStore ts) {
		if (!alreadyInUndef) {		  
			undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, obj, undefName, ts));
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

	
	public void doPostProcessing() {
		processUndefTriples();
		processorResourceObjs.doPostProcessing();
		processorLiteralObjs.doPostProcessing();
	}


	public void processUndefTriples() {		
		untypedProcessor.processUndefTriples();		
	}
	
	public void createUntypedResources() {
		untypedProcessor.createUntypedResources();
	}
	

}
