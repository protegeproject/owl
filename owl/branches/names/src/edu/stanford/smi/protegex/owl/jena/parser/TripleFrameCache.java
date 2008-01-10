package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

 // TODO: Find a solution for double slot value entries!! Checking at runtime slows down performance a lot!

 // TODO: fix ranges with datatypes 
 // TODO: Use getFrame from the MNFS
 // TODO: Use java objects rather than strings for the frames-owl mapping
 // TODO: ClassCastException at sortSubclasses NCI Th. 
 // TODO: Complete the FrameCreatorUtility.createClassWithTYpe with the rest of type
 // TODO: Try to use reflection to see how fast it is
 // TODO: Try to create Java objects as soon as possible (by using heuristics) 
 // TODO: Try to process remaining undef triples by using heuristics
 // TODO: Process each triple in a try catch


// TODO: (done) Put owl:Thing as superclass to all classes without superclass
// TODO: (done) Add not-implemented facets/prop (range, equiv classes)
// TODO: (done) fix the XSD datatypes (they are now xsd:string instead of the fully qualified name)
// TODO: (done) rdf:type is added several times

public class TripleFrameCache {
	private static final transient Logger log = Log.getLogger(TripleFrameCache.class);
	
	private UndefTripleManager undefTripleManager = new UndefTripleManager();

	private SuperClsCache superClsCache = new SuperClsCache();
	private MultipleTypesInstanceCache multipleTypesInstanceCache = new MultipleTypesInstanceCache();	
	
	private OWLModel owlModel;
	private TripleStore tripleStore;
	
	private static int noMultipleTypes = 0;

	
	public TripleFrameCache(OWLModel owlModel) {
		this(owlModel, owlModel.getTripleStoreModel().getTopTripleStore());
	}

	public TripleFrameCache(OWLModel owlModel, TripleStore tripleStore) {
		this.owlModel = owlModel;
		this.tripleStore = tripleStore;
		
		FrameCreatorUtility.setSimpleFrameStore((SimpleFrameStore)((DefaultKnowledgeBase)owlModel).getTerminalFrameStore());
		
	}
	
	
	public boolean processTriple(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Process Triple " + subj + " " + pred + " " + obj);
		}
		
		String predName = ParserUtility.getResourceName(pred);		
		Slot predSlot = (Slot) ((KnowledgeBase) owlModel).getFrame(predName);

		if (predSlot == null) {
			if (!alreadyInUndef) {
				undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, obj, predName));
			}
			return false;
		}

		//do some checks if it already exists and is twice defined?
		String subjName = ParserUtility.getResourceName(subj);
		Frame subjFrame = owlModel.getFrame(subjName);
		
		String objName = ParserUtility.getResourceName(obj);
		Frame objFrame = owlModel.getFrame(objName);
		
		if (predName.equals(OWL.imports.getURI())) {
			OWLImportsCache.addOWLImport(subjName, objName);
		} else	if (predName.equals(RDF.type.getURI()) ) { //creation
			if (objName.equals(OWL.Restriction.getURI())) {
				return true;
			}
			
			if (subj.isAnonymous() && objName.equals(OWL.Class.getURI())) {
				return true;
			}
			
			if (objFrame == null) {
				addUndefTriple(subj, pred, obj, objName, alreadyInUndef);
				return false;
			}
			
			//find a better way to handle this...
			boolean subjAlreadyExists = owlModel.getFrame(subjName) != null;
			
			subjFrame = createFrameWithType(subjName, (Cls)objFrame, subj.isAnonymous());
			//add to frame to the cache of classes with no superclass
			if (!subjAlreadyExists && !subj.isAnonymous() && (objName.equals(OWL.Class.getURI()) || objName.equals(RDFS.Class.getURI())) ) {
				superClsCache.addFrame(subjFrame);
			}
	

			if (subjAlreadyExists && objFrame instanceof Cls) {
				//what should happen if objFrame is not a class? Give a warning
				//this is another rdf:type for this resource
				//FrameCreatorUtility.setInstanceType((Instance) subjFrame, (Cls) objFrame);
				multipleTypesInstanceCache.addType((Instance)subjFrame, (Cls)objFrame);
				return true;
			}
			
		}//split this in two conditions and two methods
		else if (predName.equals(RDF.first.getURI()) || predName.equals(RDF.rest.getURI())) {
			createRDFList(subjName, predName, objName);
		} else	if (OWLFramesMapping.getLogicalPredicatesNames().contains(predName)) {
			subjFrame = createLogicalClass(subjName, predName);
		} else if (OWLFramesMapping.getRestrictionPredicatesNames().contains(predName)) {
			subjFrame = createRestriction(subjName, predName);
		}
		
		//do this nicer
		subjFrame = owlModel.getFrame(subjName);		
		objFrame = owlModel.getFrame(objName);
		

		//checking and adding to undefined
		if (subjFrame == null) {
		    addUndefTriple(subj, pred, obj, subjName, alreadyInUndef);
		    if (log.isLoggable(Level.FINE)) {
		        log.fine("^^^ Should add undef triple: " + subj + " " + pred + " " + obj + " undef:" + subjName);
		    }
		    return false;
		}
		
		
		if (objFrame == null) {
			addUndefTriple(subj, pred, obj, objName, alreadyInUndef);
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("^^^ Should add undef triple: " + subj + " " + pred + " " + obj + " undef:" + objName);
                        }
			return false;
		}


		if (objName.equals(OWL.Ontology.getURI()) && predName.equals(RDF.type.getURI()) ) {
			tripleStore.setName(subjName);
		}
		
		
		//If this is a rdfs:subclass of, then remove it from the cache of classes with no superclasses
		if (predName.equals(RDFS.subClassOf.getURI()) && !obj.isAnonymous()) {
			superClsCache.removeFrame(subjFrame);
		}
		
		else	
			//special treatment of equivalent classes
			if (predName.equals(OWL.equivalentClass.getURI())) {
				FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame);
				FrameCreatorUtility.createSubclassOf((Cls)subjFrame,(Cls) objFrame);
				FrameCreatorUtility.createSubclassOf((Cls)objFrame,(Cls) subjFrame);
				return true;
			} else if (predName.equals(OWL.equivalentProperty.getURI())) {
				FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame);
				FrameCreatorUtility.createSubpropertyOf((Slot)subjFrame,(Slot) objFrame);
				FrameCreatorUtility.createSubpropertyOf((Slot)objFrame,(Slot) subjFrame);
				return true;
			}
		 
		
		//add what it is really in the triple
		FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame);
		//add frame correspondent
		String frameMapSlot = OWLFramesMapping.getFramesSlotMapName(predName);
		if (frameMapSlot != null) {
			FrameCreatorUtility.addOwnSlotValue(subjFrame, owlModel.getSlot(frameMapSlot), objFrame);
		}
		//add frame pair (inverse) correspondent
		String frameMapInvSlot = OWLFramesMapping.getFramesInvSlotMapName(predName);
		if (frameMapInvSlot != null) {
			FrameCreatorUtility.addOwnSlotValue(objFrame, owlModel.getSlot(frameMapInvSlot), subjFrame);
		}
		
		return true;
							
	}



	private void addUndefTriple(AResource subj, AResource pred, AResource obj, String undefName, boolean alreadyInUndef) {
		if (!alreadyInUndef) {
		    TripleStore activeTripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
			undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, obj, undefName)); //check this!!
		}		
	}

	public boolean processTriple(AResource subj, AResource pred, ALiteral lit, boolean alreadyInUndef) {
	    if (log.isLoggable(Level.FINE)) {
	        log.fine("Processing triple: " + subj + " " + pred + " " + lit);
	    }
		
		//TT:just for testing
		/*if (true) {
			return true;
		}*/
		
		String predName = ParserUtility.getResourceName(pred);		
		Slot predSlot = (Slot) owlModel.getFrame(predName);

		if (predSlot == null) {
			if (!alreadyInUndef) {
				undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, lit, predName));
			}
			return false;
		}

		//do some checks if it already exists and is twice defined?
		String subjName = ParserUtility.getResourceName(subj);
		Frame subjFrame = owlModel.getFrame(subjName);

		//check the order of these calls
		if (OWLFramesMapping.getRestrictionPredicatesNames().contains(predName)) {
			subjFrame = createRestriction(subjName, predName);
		}
		
		subjFrame = owlModel.getFrame(subjName);

		//checking and adding to undefined
		if (subjFrame == null) {
			if (!alreadyInUndef) {
				undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, lit, subjName));
			}
			return false;
		}
		
		RDFSLiteral rdfsLiteral = createRDFSLiteral(lit, (RDFProperty) predSlot);
		
		if (rdfsLiteral == null) {
			return false;
		}
		
				
		//add what it is really in the triple
		//fix this! add the raw value!!
		//String stringValue = rdfsLiteral.getString();
		
		//if (stringValue != null) {
			FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, ((DefaultRDFSLiteral)rdfsLiteral).getRawValue());
		//}
		
		return true;
		
	}
	
	
	
	//reimplement this method

	//special treatment of RDFList. Move this to a utility class
	private void createRDFList(String subjName, String predName, String objName) {
		
		Frame subjList = owlModel.getFrame(subjName);
		
		//applies both for rdf:fist and rdf:rest
		if (subjList == null) {
			//move this to a RDFListCreator
			FrameID id = new FrameID(subjName);
			
			Frame listFrame = FrameCreatorUtility.createFrameWithType(owlModel, id, RDF.List.getURI(), true);
			
			if (listFrame != null) {
				checkUndefinedResources(subjName);
			}
		}

		if (!predName.equals(RDF.rest.getURI())) {
			return;
		}
		
		Frame objList = owlModel.getFrame(objName);
		
		if (objList == null) {
			//move this to a RDFListCreator
			FrameID id = new FrameID(objName);

			Frame listFrame = FrameCreatorUtility.createFrameWithType(owlModel, id, RDF.List.getURI(), true);
			
			if (listFrame != null) {
				checkUndefinedResources(objName);
			}					
		}		
	}

	
		
	private Frame createFrameWithType(String frameUri, Cls type, boolean isSubjAnon) {
	
		Frame frame = owlModel.getFrame(frameUri);
		
		if (frame != null) return frame;
					
		FrameID id = new FrameID(frameUri);

		frame = FrameCreatorUtility.createFrameWithType(owlModel, id, type, isSubjAnon);

		//multipleTypesInstanceCache.addType((Instance)frame, type);
		
		if (frame != null) {
			checkUndefinedResources(frameUri);
		}
		
		return frame;
	}
	
	private Frame createRestriction(String restrName, String predName) {
		Frame restriction = owlModel.getFrame(restrName);
		
		if (restriction != null)
			return restriction;
		
		FrameID id = new FrameID(restrName);
		restriction = RestrictionCreatorUtility.createRestriction(owlModel, id, predName);
		
		if (restriction != null){		
			checkUndefinedResources(restrName);
		}
						
		return restriction;
	}
	

	private Frame createLogicalClass(String logClassName, String predName) {
		Frame logClass = owlModel.getFrame(logClassName);
		
		if (logClass != null)
			return logClass;
		
		FrameID id = new FrameID(logClassName);
		logClass = LogicalClassCreatorUtility.createLogicalClass(owlModel, id, predName);
		
		if (logClass != null){		
			checkUndefinedResources(logClassName);
		}
						
		return logClass;
	}
	

	private void checkUndefinedResources(String uri) {
	    TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
	    TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
	    
		Collection undefTriples = undefTripleManager.getUndefTriples(uri); 
		
		for (Iterator iter = undefTriples.iterator(); iter.hasNext();) {
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
	


	public void processUndefTriples() {
	    TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
	    TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
	    
		for (Iterator iter = getUndefTripleManager().getUndefTriples().iterator(); iter.hasNext();) {
			UndefTriple undefTriple = (UndefTriple) iter.next();
			
			boolean success = false;
			
			Object obj = undefTriple.getTripleObj();
			
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


	
	//move somewhere
	// copied from old parser
	private RDFSLiteral createRDFSLiteral(ALiteral literal, RDFProperty property) {
		if(literal.getLang() != null && literal.getLang().length() > 0) {
			//return owlModel.createRDFSLiteral(literal.toString(), literal.getLang());
			return DefaultRDFSLiteral.create(owlModel, literal.toString(), literal.getLang());
		}
		else if(literal.getDatatypeURI() != null) {
			RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(literal.getDatatypeURI());
			if(datatype == null) {
				//return owlModel.createRDFSLiteral(literal.toString());
				return DefaultRDFSLiteral.create(owlModel, literal.toString());
			}
			else {
				//return owlModel.createRDFSLiteral(literal.toString(), datatype);
				return DefaultRDFSLiteral.create(owlModel, literal.toString(), datatype);
			}
		}
		else {
//			If literal has no datatype, make a qualified guess using the property's range
			RDFResource range = property.getRange();
			if(range instanceof RDFSDatatype) {
				RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(range.getURI());
				//return owlModel.createRDFSLiteral(literal.toString(), datatype);
				return DefaultRDFSLiteral.create(owlModel, literal.toString(), datatype);
			}
			else {
				return DefaultRDFSLiteral.create(owlModel, literal.toString());
				//return owlModel.createRDFSLiteral(literal.toString());
			}
		}
	}
	
// ============================================ Post processing =================================================================
	
	public void doPostProcessing() {
		//processAddPrefixesToOntology();
		processInferredSuperclasses();
		processClsesWithoutSupercls();
		processInstancesWithMultipleTypes();
		
		//dump what you have not processed:
		getUndefTripleManager().dumpUndefTriples();
	}
	
	
	

	private void processAddPrefixesToOntology() {
		Slot prefixesSlot = owlModel.getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES);
		
		//check whether this is OK at imports..
		OWLOntology defaultOntology = owlModel.getDefaultOWLOntology();
		
		for (String prefix : owlModel.getNamespaceManager().getPrefixes()) {
			String value = prefix + ":" + owlModel.getNamespaceManager().getNamespaceForPrefix(prefix);
			defaultOntology.addOwnSlotValue(prefixesSlot, value);
		}		
	
	}

	private void processClsesWithoutSupercls() {
		long time0 = System.currentTimeMillis();
		
		System.out.print("Postprocess: Process classes without superclasses (" + superClsCache.getCachedFramesWithNoSuperclass().size() + " classes) ... ");
		
		for (Iterator iter = superClsCache.getCachedFramesWithNoSuperclass().iterator(); iter.hasNext();) {
			Frame frame = (Frame) iter.next();
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("processClsesWithoutSupercls: No declared supercls: " + frame);
                        }
			if (frame instanceof Cls) {
				Cls cls = (Cls) frame;				
				FrameCreatorUtility.createSubclassOf(cls, owlModel.getOWLThingClass());
			}
		}
				
		superClsCache.clearCache();
		
		log.info("(" + (System.currentTimeMillis() - time0) + " ms)");
	}

	private void processInferredSuperclasses(){
		long time0 = System.currentTimeMillis();
				
		OWLNamedClass owlClassClass = owlModel.getOWLNamedClassClass();
		
		System.out.print("Postprocess: Add inferred superclasses (" + owlModel.getInstanceCount(owlClassClass) + " classes) ... ");
		
		for (Iterator iterator = owlClassClass.getInstances().iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			
			try {				
				OWLNamedClass namedClass = (OWLNamedClass) obj;
				Collection<Cls> inferredSuperclasses = getInferredSuperClasses(namedClass);
				
				if (inferredSuperclasses.size() > 0) {
					superClsCache.removeFrame(namedClass);
				}
				
				for (Cls inferredSupercls : inferredSuperclasses) {
					FrameCreatorUtility.createSubclassOf(namedClass, inferredSupercls);
				}
				
			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING, " Error at processing " + obj, e);
			}			
		}
		
		log.info("(" + (System.currentTimeMillis() - time0) + " ms)");
		
	}

	
	private Collection<Cls> getInferredSuperClasses(OWLNamedClass namedClass) {
		Collection<Cls> inferredSuperClasses = new ArrayList<Cls>();
		
		//make this into a recursive function
		Collection<RDFSClass> equivClasses = namedClass.getEquivalentClasses();
		for (RDFSClass equivClass : equivClasses) {
			if (equivClass instanceof RDFSNamedClass) {
				inferredSuperClasses.add(equivClass);
			} else if (equivClass instanceof OWLIntersectionClass) {
				//add operands if defined
				Collection<RDFSClass> operands = ((OWLIntersectionClass)equivClass).getOperands();
				
				for (RDFSClass operand : operands) {
					if (operand instanceof RDFSNamedClass) {
						inferredSuperClasses.add(operand);
					}					
				}				
			}
		}
				
		return inferredSuperClasses;
	}

	
	private void processInstancesWithMultipleTypes() {
            long time0 = System.currentTimeMillis();
		
            Set<Instance> instancesWithMultipleTypes = multipleTypesInstanceCache.getInstancesWithMultipleTypes();
		
            log.info("Postprocess: Instances with multiple types (" + instancesWithMultipleTypes.size() + " instances) ... ");

            for (Instance instance : instancesWithMultipleTypes) {
                Set<Cls> typesSet = multipleTypesInstanceCache.getTypesForInstanceAsSet(instance);
                adjustTypesOfInstance(instance, typesSet);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("process instance with multiple types" + instance + ": " + typesSet);
		}
            }
            log.info("(" + (System.currentTimeMillis() - time0) + " ms)");
	}

	private void adjustTypesOfInstance(Instance instance, Set<Cls> typesSet) {
		Collection<Cls> existingTypes = FrameCreatorUtility.getDirectTypes(instance);
		typesSet.removeAll(existingTypes); // types to add
		
		for (Cls cls : typesSet) {
			//FrameCreatorUtility.addInstanceType(instance, cls);
			FrameCreatorUtility.addDirectTypeAndSwizzle(instance, cls);
		}
		
	}

	
	
	
}
