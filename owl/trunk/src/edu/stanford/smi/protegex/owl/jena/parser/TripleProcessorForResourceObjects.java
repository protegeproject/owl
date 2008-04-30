package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

class TripleProcessorForResourceObjects extends AbstractStatefulTripleProcessor {
	private static final transient Logger log = Log.getLogger(TripleProcessorForResourceObjects.class);

	public enum TripleStatus {
		TRIPLE_PROCESSING_SHOULD_CONTINUE, 
		TRIPLE_PROCESSING_COMPLETE, 
		TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
	};


	public TripleProcessorForResourceObjects(TripleProcessor processor) {
		super(processor);
			
	}

	public boolean processTriple(AResource subj, AResource pred, AResource obj, TripleStore ts, boolean alreadyInUndef) {
		return new InternalTripleProcessorForResourceObjects(subj, pred, obj, ts, alreadyInUndef).processTriple();
	}


	private class InternalTripleProcessorForResourceObjects {

		private AResource subj;
		private String subjName;
		private Frame subjFrame;

		private AResource pred;
		private String predName;
		private Slot predSlot;

		private AResource obj;
		private String objName;
		private Frame objFrame;

		private TripleStore tripleStore;
		
		private boolean alreadyInUndef;

		public InternalTripleProcessorForResourceObjects(AResource subj, AResource pred, AResource obj,
				TripleStore ts, boolean alreadyInUndef) {
			if (log.isLoggable(Level.FINER)) {
				log.finer("Process Triple " + subj + " " + pred + " " + obj);
			}
			this.subj = subj;
			this.pred = pred;
			this.obj = obj;
			this.alreadyInUndef = alreadyInUndef;
			this.tripleStore = ts;

			predName = ParserUtil.getResourceName(pred);
			predSlot = (Slot) ((KnowledgeBase) owlModel).getFrame(predName);
			if (predSlot != null) {
				// do some checks if it already exists and is twice defined?
				subjName = ParserUtil.getResourceName(subj);
				subjFrame = getFrame(subjName);

				objName = ParserUtil.getResourceName(obj);
				objFrame = getFrame(objName);
			}
		}

		public boolean processTriple() {
			TripleStatus status;

			status = handlePredUndefs();
			switch (status) {
			case TRIPLE_PROCESSING_COMPLETE:
				return true;
			case TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS:
				return false;
			}

			status = handleSetTypeAndCreation();
			switch (status) {
			case TRIPLE_PROCESSING_COMPLETE:
				return true;
			case TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS:
				return false;
			}

			status = handleSubjObjUndefs();
			switch (status) {
			case TRIPLE_PROCESSING_COMPLETE:
				return true;
			case TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS:
				return false;
			}

			handleOntologyDeclaration();

			handleSuperClassCacheUpdate();

			status = handleEquivalentClassesOrProperties();
			switch (status) {
			case TRIPLE_PROCESSING_COMPLETE:
				return true;
			case TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS:
				return false;
			}

			handleGeneralizedConceptInclusions();

			addTriple();

			return true;
		}

		private TripleStatus handleSetTypeAndCreation() {
			TripleStatus status = TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
			
			if (predName.equals(OWL.imports.getURI())) {
				OWLImportsCache.addOWLImport(subjName, objName);
			} else if (predName.equals(RDF.type.getURI())) { // creation
				status = handleSetType();
			}
			else if (predName.equals(RDF.first.getURI()) || predName.equals(RDF.rest.getURI())) {
				if (log.isLoggable(Level.FINE)) {
					log.fine("Triple <" + subjName + ", " + predName + ", " + objName + "> signals RDFList creation");
				}
				createRDFList();
			} else if (OWLFramesMapping.getLogicalPredicatesNames().contains(predName)) {
				status = handleCreateLogicalClass();
			} else if (OWLFramesMapping.getRestrictionPredicatesNames().contains(predName)) {
				subjFrame = createRestriction(subjName, predName, tripleStore);
			}

			subjFrame = getFrame(subjName);
			objFrame = getFrame(objName);

			return status;
		}

		/*
		 * The simple story is that this routine would create the subject frame
		 * by just calling createLogicalClass. But life isn't that easy. It is
		 * possible that the the logical class being created is a named class.
		 * This is awkward in Protege3. So we avoid representing this logical
		 * class as a named class. We create a new unnamed class to hold the
		 * logical expression and state that the named class is equivalent.
		 * 
		 * But the pain isn't over. The triple may be processed more than once.
		 * So we need to keep track of whether it has been seen before. This is
		 * awkward because we need to track the whole triple. It is possible
		 * that a named class is a logical class in more than one different way.
		 * But I think that I can track duplicates by using the object of the
		 * triple.
		 * 
		 * Had enough? Here is another jolt! I worried about the possibility
		 * that owlModel.getNextAnonymousResourceName() would generate an
		 * anonymous name that would later appear in the parsed ontology
		 * (generated by ParserUtility.getResourceName). So I looked in the two
		 * routines and hacked the latter to always provide a different name
		 * than the former.
		 * 
		 * This is a nasty little routine and maybe one day I will be punished
		 * for it.
		 */
		private TripleStatus handleCreateLogicalClass() {
			Frame oldSubjFrame = subjFrame;
			boolean logicalClassIsNamed = false;
			if (!subj.isAnonymous()) {
				if (subjFrame == null) {
					addUndefTriple(subjName);
					return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
				} 
				
				if (objFrame == null) {
					addUndefTriple(objName);
					return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
				}
				
				subjFrame = globalParserCache.getObjectToNamedLogicalClassSurrogate().get(objName);
				if (subjFrame != null) {
					subjName = subjFrame.getName();
					return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
				}
				
				// can this cause conflicts??
				subjName = owlModel.getNextAnonymousResourceName(); 
				logicalClassIsNamed = true;
			}
			
			subjFrame = createLogicalClass();
			
			if (logicalClassIsNamed) {
				globalParserCache.getObjectToNamedLogicalClassSurrogate().put(objName, (Cls) subjFrame);
				FrameCreatorUtility.addOwnSlotValue(oldSubjFrame, owlModel.getOWLEquivalentClassProperty(), subjFrame, tripleStore);
				FrameCreatorUtility.createSubclassOf((Cls) subjFrame, (Cls) oldSubjFrame, tripleStore);
				FrameCreatorUtility.createSubclassOf((Cls) oldSubjFrame, (Cls) subjFrame, tripleStore);
			}
			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}


		private TripleStatus handleSetType() {
			if (objName.equals(OWL.Restriction.getURI())) {
				return TripleStatus.TRIPLE_PROCESSING_COMPLETE;
			}

			if (subj.isAnonymous() && objName.equals(OWL.Class.getURI())) {
				return TripleStatus.TRIPLE_PROCESSING_COMPLETE;
			}

			if (objFrame == null) {
				addUndefTriple(objName);
				return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
			}

			// find a better way to handle this...
			boolean subjAlreadyExists = getFrame(subjName) != null;

			subjFrame = createFrameWithType(subjName, (Cls) objFrame, subj.isAnonymous());
			
			//add the rdf:type triple if not already there
			if (!FrameCreatorUtility.hasOwnSlotValue(subjFrame, predSlot, objFrame)) {				
				FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame, tripleStore);
			}
			
			// add to frame to the cache of classes with no superclass
			if (!subjAlreadyExists && !subj.isAnonymous()
					&& (objName.equals(OWL.Class.getURI()) || objName.equals(RDFS.Class.getURI()))) {
				globalParserCache.getSuperClsCache().addFrame(subjFrame);
			}
		
			if (subjAlreadyExists && objFrame instanceof Cls) {				
				if (log.isLoggable(Level.FINE)) {
					log.fine("found an alternative type for " + subjFrame + " = " + objFrame);
				}
				globalParserCache.getMultipleTypesInstanceCache().addType((Instance) subjFrame, (Cls) objFrame);
				return TripleStatus.TRIPLE_PROCESSING_COMPLETE;
			}
			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}

		private TripleStatus handlePredUndefs() {
			if (predSlot == null) {
				if (!alreadyInUndef) {
					if (log.isLoggable(Level.FINE)) {
						log.fine("\tdeferring triple because predicate is not yet defined");
					}
					globalParserCache.addUndefTriple(new UndefTriple(subj, pred, obj, predName, tripleStore));
				}
				return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
			}
			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}

		private TripleStatus handleSubjObjUndefs() {
			if (subjFrame == null) {
				if (log.isLoggable(Level.FINE)) {
					log.fine("\tDeferring triple because subject is not yet defined");
				}
				addUndefTriple(subjName);
				return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
			}

			if (objFrame == null) {
				addUndefTriple(objName);
				if (log.isLoggable(Level.FINE)) {
					log.fine("+++ Add undef triple: " + subj + " " + pred + " " + obj + " undef:" + objName);
				}
				return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
			}
			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}

		private void handleOntologyDeclaration() {
			// guessing that the ontology for the parsed file is the first
			// ontology found
			if (objName.equals(OWL.Ontology.getURI()) && predName.equals(RDF.type.getURI()) 
					&& tripleStore.getName() == null) {
				tripleStore.setName(subjName);
				tripleStore.addIOAddress(subjName);
			}
		}

		private void handleSuperClassCacheUpdate() {
			// If this is a rdfs:subclass of, then remove it from the cache of
			// classes with no superclasses
			if (predName.equals(RDFS.subClassOf.getURI()) && !obj.isAnonymous()) {
				globalParserCache.getSuperClsCache().removeFrame(subjFrame);
			}
		}

		private TripleStatus handleEquivalentClassesOrProperties() {
			// special treatment of equivalent classes
			if (predName.equals(OWL.equivalentClass.getURI())) {
				FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame, tripleStore);
				FrameCreatorUtility.createSubclassOf((Cls) subjFrame, (Cls) objFrame, tripleStore);
				FrameCreatorUtility.createSubclassOf((Cls) objFrame, (Cls) subjFrame, tripleStore);
				return TripleStatus.TRIPLE_PROCESSING_COMPLETE;
			} else if (predName.equals(OWL.equivalentProperty.getURI())) {
				FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame, tripleStore);
				FrameCreatorUtility.createSubpropertyOf((Slot) subjFrame, (Slot) objFrame, tripleStore);
				FrameCreatorUtility.createSubpropertyOf((Slot) objFrame, (Slot) subjFrame, tripleStore);
				return TripleStatus.TRIPLE_PROCESSING_COMPLETE;
			}
			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}

		/*
		 * This is not a good method but it would take too long to change it.
		 * The protege 4 method is cleaner but makes it a little hard to find
		 * the axioms until somebody tells you how.
		 */
		private void handleGeneralizedConceptInclusions() {
			if (subjFrame instanceof RDFSClass && ((RDFSClass) subjFrame).isAnonymous()
					&& globalParserCache.getPossibleGCIPredicates().contains(predSlot)) {
				OWLNamedClass axiom = owlModel.createOWLNamedClass(null);
				FrameCreatorUtility.addOwnSlotValue(axiom, owlModel.getOWLEquivalentClassProperty(), subjFrame, tripleStore);
				FrameCreatorUtility.createSubclassOf((Cls) subjFrame, axiom, tripleStore);
				FrameCreatorUtility.createSubclassOf(axiom, (Cls) subjFrame, tripleStore);
				subjFrame = axiom;
				globalParserCache.getGciAxioms().add(axiom); // need to name these later
			}
		}

		private void addTriple() {
			
			//this should not be necessary - it will be deleted after refactoring the status
			if (predSlot.equals(owlModel.getRDFTypeProperty())) {
				return;
			}
			
			// add what it is really in the triple			
			FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame, tripleStore);
			// add frame correspondent
			String frameMapSlot = OWLFramesMapping.getFramesSlotMapName(predName);
			if (frameMapSlot != null) {
				FrameCreatorUtility.addOwnSlotValue(subjFrame, (Slot)getFrame(frameMapSlot), objFrame, tripleStore);
			}
			// add frame pair (inverse) correspondent
			String frameMapInvSlot = OWLFramesMapping.getFramesInvSlotMapName(predName);
			if (frameMapInvSlot != null) {
				FrameCreatorUtility.addOwnSlotValue(objFrame, (Slot)getFrame(frameMapInvSlot), subjFrame, tripleStore);
			}
		}

		private void addUndefTriple(String undef) {
			processor.addUndefTriple(subj, pred, obj, undef, alreadyInUndef, tripleStore);
		}

		// special treatment of RDFList
		private void createRDFList() {
			Frame subjList = getFrame(subjName);
		
			// applies both for rdf:fist and rdf:rest
			if (subjList == null) {
				// move this to a RDFListCreator
				FrameID id = new FrameID(subjName);
		
				Frame listFrame = FrameCreatorUtility.createFrameWithType(owlModel, id, RDF.List.getURI(), tripleStore);
				if (isImporting(tripleStore)) {
					listFrame.setIncluded(true); // ineffective in client-server or db mode
				}
		
				if (listFrame != null) {
					checkUndefinedResources(subjName);
				}
			}
		
			if (!predName.equals(RDF.rest.getURI())) {
				return;
			}
		
			Frame objList = getFrame(objName);
			if (objList == null) {
				FrameID id = new FrameID(objName);
		
				Frame listFrame = FrameCreatorUtility.createFrameWithType(owlModel, id, RDF.List.getURI(), tripleStore);
		
				if (listFrame != null) {
					checkUndefinedResources(objName);
				}
			}
		}

		private Frame createFrameWithType(String frameUri, Cls type, boolean isSubjAnon) {
			Frame frame = getFrame(frameUri);
			
			if (frame != null) {
				if (!FrameCreatorUtility.hasOwnSlotValue(frame, owlModel.getSystemFrames().getNameSlot(), frameUri)) {
					FrameCreatorUtility.addOwnSlotValue(frame, owlModel.getSystemFrames().getNameSlot(), frameUri, tripleStore);
					Collection<Cls> types = FrameCreatorUtility.getDirectTypes((Instance) frame);
					if (types == null || !types.contains(type)) {
						FrameCreatorUtility.addInstanceType((Instance) frame, type, tripleStore);
					}
				}
				return frame;
			}
		
			FrameID id = new FrameID(frameUri);
		
			frame = FrameCreatorUtility.createFrameWithType(owlModel, id, type.getName(), tripleStore);
			if (isImporting(tripleStore)) {
				frame.setIncluded(true); // doesn't have any effect in
				// client-server or db mode
			}
		
			if (frame != null) {
				checkUndefinedResources(frameUri);
			}
		
			return frame;
		}

		private Frame createLogicalClass() {
			Frame logClass = getFrame(subjName);
		
			if (logClass != null)
				return logClass;
		
			FrameID id = new FrameID(subjName);
			logClass = LogicalClassCreatorUtility.createLogicalClass(owlModel, id, predName, tripleStore);
		
			if (logClass != null) {
				checkUndefinedResources(subjName);
			}
			if (isImporting(tripleStore)) {
				logClass.setIncluded(true);
			}
		
			return logClass;
		}

	} // end InternalProcessorForResourceObjects class
	
}
