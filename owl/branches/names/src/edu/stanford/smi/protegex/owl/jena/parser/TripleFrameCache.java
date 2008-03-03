package edu.stanford.smi.protegex.owl.jena.parser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.arp.ALiteral;
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
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
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
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
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
	
	   
    public enum TripleStatus {
        INCOMPLETE, 
        IN_KNOWLEDGE_BASE, 
        DUPLICATE_TRIPLE, 
        OTHER_TRIPLE_WILL_RESOLVE,
        REQUIRES_MULTI_TYPES_PROCESSING,
        UNDEF_NEEDS_POSTPROCESS;
    };
	
	/*
	 * I don't know if this works.  It is a hack for an ugly situation.  The 
	 * w3 specs say that the name of an ontology in a file is the first ontology
	 * declaration occurring in the file.  Presumably this would be the first ontology
	 * declaration found if we parse the file with an xml parser.  This variable is
	 * based on the guess that this will the first ontology declaration found by Jena's 
	 * ARQ parser.  I believe that Jena has trouble with this issue also.
	 */
	private boolean ontologyFound = false;
	
	private UndefTripleManager undefTripleManager = new UndefTripleManager();

	private SuperClsCache superClsCache = new SuperClsCache();
	private MultipleTypesInstanceCache multipleTypesInstanceCache = new MultipleTypesInstanceCache();
	private Set<RDFProperty> createdProperties = new HashSet<RDFProperty>();
	
	private OWLModel owlModel;
	private TripleStore tripleStore;
	
	private boolean importing;

	private Collection<RDFProperty> possibleGCIPredicates = new ArrayList<RDFProperty>();
	
	private Collection<RDFSClass> gciAxioms = new ArrayList<RDFSClass>();
	
	/*
	 * enjoy...
	 */
	private Map<String, Cls> objectToNamedLogicalClassSurrogate = new HashMap<String, Cls>();

	
	public TripleFrameCache(OWLModel owlModel) {
		this(owlModel, owlModel.getTripleStoreModel().getTopTripleStore());
	}

	public TripleFrameCache(OWLModel owlModel, TripleStore tripleStore) {
		this.owlModel = owlModel;
		this.tripleStore = tripleStore;
		importing = !tripleStore.equals(owlModel.getTripleStoreModel().getTopTripleStore());
		
		possibleGCIPredicates.add(owlModel.getOWLDisjointWithProperty());
		possibleGCIPredicates.add(owlModel.getRDFSSubClassOfProperty());
		possibleGCIPredicates.add(owlModel.getOWLEquivalentClassProperty());
	}
	
	
	public boolean processTriple(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
	    return new TripleProcessorForResourceObjects(subj, pred, obj, alreadyInUndef).processTriple();
	}
	
	/*
	 * The entire purpose of this class is to break up the processTriple method above.
	 * This is a slightly weird approach - we will see what Tania says...
	 */
	private class TripleProcessorForResourceObjects {
	    
	    private AResource subj;
	    private String    subjName;
	    private Frame     subjFrame;
	    
	    private AResource pred; 
	    private String    predName;
	    private Slot      predSlot;
	    
	    private AResource obj;
	    private String    objName;
	    private Frame     objFrame;
	    
	    private boolean alreadyInUndef;
	    
	    
	    public TripleProcessorForResourceObjects(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
	        if (log.isLoggable(Level.FINER)) {
	            log.finer("Process Triple " + subj + " " + pred + " " + obj);
	        }
	        this.subj = subj;
	        this.pred = pred;
	        this.obj = obj;
	        this.alreadyInUndef = alreadyInUndef;

	        predName = ParserUtility.getResourceName(pred);      
	        predSlot = (Slot) ((KnowledgeBase) owlModel).getFrame(predName);
	        if (predSlot != null) {
	            //do some checks if it already exists and is twice defined?
	            subjName = ParserUtility.getResourceName(subj);
	            subjFrame = owlModel.getFrame(subjName);

	            objName = ParserUtility.getResourceName(obj);
	            objFrame = owlModel.getFrame(objName);
	        }
	    }
	    
	    public boolean processTriple() {
	        TripleStatus status = TripleStatus.INCOMPLETE;

	        if (handlePredUndefs() == TripleStatus.UNDEF_NEEDS_POSTPROCESS) return false;

	        status = handleSetTypeAndCreation();
	        if (status != TripleStatus.INCOMPLETE) return status != TripleStatus.UNDEF_NEEDS_POSTPROCESS;
	        
	        status = handleSubjObjUndefs();
            if (status != TripleStatus.INCOMPLETE) return status != TripleStatus.UNDEF_NEEDS_POSTPROCESS;

	        handleOntologyDeclaration();
	        
	        handleSuperClassCacheUpdate();
	        
	        status = handleEquivalentClassesOrProperties();
	        if (status != TripleStatus.INCOMPLETE) return status != TripleStatus.UNDEF_NEEDS_POSTPROCESS;
	        
	        handleGeneralizedConceptInclusions();
	        
	        addTriple();
	        
	        return true;
	    }
	    
	    @SuppressWarnings("deprecation")
        private TripleStatus handleSetTypeAndCreation() {
	        TripleStatus status = TripleStatus.INCOMPLETE;
	        if (predName.equals(OWL.imports.getURI())) {
	            OWLImportsCache.addOWLImport(subjName, objName);
	        } else  if (predName.equals(RDF.type.getURI()) ) { //creation
	            status = handleSetType();
	        }//split this in two conditions and two methods
	        else if (predName.equals(RDF.first.getURI()) || predName.equals(RDF.rest.getURI())) {
	            if (log.isLoggable(Level.FINE)) {
	                log.fine("Triple <" + subjName + ", " + predName + ", " + objName + "> signals RDFList creation");
	            }
	            createRDFList(subjName, predName, objName);
	        } 
	        else  if (OWLFramesMapping.getLogicalPredicatesNames().contains(predName)) {
	            status = handleCreateLogicalClass();
	        } 
	        else if (OWLFramesMapping.getRestrictionPredicatesNames().contains(predName)) {
	            subjFrame = createRestriction(subjName, predName);
	        }
	           //do this nicer
            subjFrame = owlModel.getFrame(subjName);        
            objFrame = owlModel.getFrame(objName);
            
	        return status;
	    }
	    
	    /*
	     * The simple story is that this routine would create the subject
	     * frame by just calling createLogicalClass.  But life isn't that easy.
	     * It is possible that the the logical class being created is a named 
	     * class.  This is awkward in Protege3.  So we avoid representing this 
	     * logical class as a named class.  We create a new unnamed class to hold
	     * the logical expression and state that the named class is equivalent.
	     * 
	     * But the pain isn't over.  The triple may be processed more than once. 
	     * So we need to keep track of whether it has been seen before.  This  is awkward
	     * because we need to track the whole triple.  It is possible that a named
	     * class is a logical class in more than one different way.  But I think that 
	     * I can track duplicates by using the object of the triple.
	     * 
	     * Had enough?  Here is another jolt!  I worried about the possibility that 
	     *             owlModel.getNextAnonymousResourceName()
	     * would generate an anonymous name that would later appear in the parsed ontology
	     * (generated by ParserUtility.getResourceName).  So I looked in the two routines
	     * and hacked the latter to always provide a different name than the former.
	     * 
	     * This is a nasty little routine and maybe one day I will be punished 
	     * for it.
	     */   
	    private TripleStatus handleCreateLogicalClass() {
            Frame oldSubjFrame = subjFrame;
            boolean logicalClassIsNamed = false;
            if (!subj.isAnonymous()) { 
                if (subjFrame == null || objFrame == null) { 
                    addUndefTriple();
                    return TripleStatus.UNDEF_NEEDS_POSTPROCESS;
                }
                subjFrame = objectToNamedLogicalClassSurrogate.get(objName);
                if (subjFrame != null) {
                    subjName = subjFrame.getName();
                    return TripleStatus.INCOMPLETE;
                }
                subjName = owlModel.getNextAnonymousResourceName(); // can this cause conflicts??
                logicalClassIsNamed = true;
            }
            subjFrame = createLogicalClass(subjName, predName);
            if (logicalClassIsNamed) {
                objectToNamedLogicalClassSurrogate.put(objName, (Cls) subjFrame);
                FrameCreatorUtility.addOwnSlotValue(oldSubjFrame, owlModel.getOWLEquivalentClassProperty(), subjFrame);
                FrameCreatorUtility.createSubclassOf((Cls) subjFrame, (Cls) oldSubjFrame);
                FrameCreatorUtility.createSubclassOf((Cls) oldSubjFrame, (Cls) subjFrame);
            }
            return TripleStatus.INCOMPLETE;
	    }
	    
	    @SuppressWarnings("deprecation")
        private TripleStatus handleSetType() {
            if (objName.equals(OWL.Restriction.getURI())) {
                return TripleStatus.OTHER_TRIPLE_WILL_RESOLVE;
            }
            
            if (subj.isAnonymous() && objName.equals(OWL.Class.getURI())) {
                return TripleStatus.OTHER_TRIPLE_WILL_RESOLVE;
            }
            
            if (objFrame == null) {
                addUndefTriple();
                return TripleStatus.UNDEF_NEEDS_POSTPROCESS;
            }
            
            //find a better way to handle this...
            boolean subjAlreadyExists = owlModel.getFrame(subjName) != null;
            
            subjFrame = createFrameWithType(subjName, (Cls)objFrame, subj.isAnonymous());
            //add to frame to the cache of classes with no superclass
            if (!subjAlreadyExists && !subj.isAnonymous() && (objName.equals(OWL.Class.getURI()) || objName.equals(RDFS.Class.getURI())) ) {
                superClsCache.addFrame(subjFrame);
            }
            if (!subjAlreadyExists && subjFrame instanceof RDFProperty) {
                createdProperties.add((RDFProperty) subjFrame);
            }

            if (subjAlreadyExists && objFrame instanceof Cls) {
                //what should happen if objFrame is not a class? Give a warning
                //this is another rdf:type for this resource
                //FrameCreatorUtility.setInstanceType((Instance) subjFrame, (Cls) objFrame);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("found an alternative type for " + subjFrame + " = " + objFrame);
                }
                multipleTypesInstanceCache.addType((Instance)subjFrame, (Cls)objFrame);
                return TripleStatus.REQUIRES_MULTI_TYPES_PROCESSING;
            }
            return TripleStatus.INCOMPLETE;
	    }
	    
	    private TripleStatus handlePredUndefs() {
	        if (predSlot == null) {
	            if (!alreadyInUndef) {
	                if (log.isLoggable(Level.FINE)) {
	                    log.fine("\tdeferring triple because predicate is not yet defined");
	                }
	                undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, obj, predName));
	            }
	            return TripleStatus.UNDEF_NEEDS_POSTPROCESS;
	        }
	        return TripleStatus.INCOMPLETE;
	    }
	    
	    private TripleStatus handleSubjObjUndefs() {
	        //checking and adding to undefined
	        if (subjFrame == null) {
	            if (log.isLoggable(Level.FINE)) {
	                log.fine("\tdeferring triple because subject is not yet defined");
	            }
	            addUndefTriple();
	            return TripleStatus.UNDEF_NEEDS_POSTPROCESS;
	        }


	        if (objFrame == null) {
	            addUndefTriple();
	            if (log.isLoggable(Level.FINE)) {
	                log.fine("^^^ Should add undef triple: " + subj + " " + pred + " " + obj + " undef:" + objName);
	            }
	            return TripleStatus.UNDEF_NEEDS_POSTPROCESS;
	        }
	        return TripleStatus.INCOMPLETE;
	    }
	    
	    private void handleOntologyDeclaration() {
	           // guessing that the ontology for the parsed file is the first ontology found
            if (objName.equals(OWL.Ontology.getURI()) && predName.equals(RDF.type.getURI()) && !ontologyFound ) {
                tripleStore.setName(subjName);
                tripleStore.addIOAddress(subjName);
                ontologyFound = true;
            }
	    }
	    
	    private void handleSuperClassCacheUpdate() {
	        //If this is a rdfs:subclass of, then remove it from the cache of classes with no superclasses
	        if (predName.equals(RDFS.subClassOf.getURI()) && !obj.isAnonymous()) {
	            superClsCache.removeFrame(subjFrame);
	        }
	    }
	    
	    private TripleStatus handleEquivalentClassesOrProperties() {
	         //special treatment of equivalent classes
            if (predName.equals(OWL.equivalentClass.getURI())) {
                FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame);
                FrameCreatorUtility.createSubclassOf((Cls)subjFrame,(Cls) objFrame);
                FrameCreatorUtility.createSubclassOf((Cls)objFrame,(Cls) subjFrame);
                return TripleStatus.IN_KNOWLEDGE_BASE;
            } 
            else if (predName.equals(OWL.equivalentProperty.getURI())) {
                FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame);
                FrameCreatorUtility.createSubpropertyOf((Slot)subjFrame,(Slot) objFrame);
                FrameCreatorUtility.createSubpropertyOf((Slot)objFrame,(Slot) subjFrame);
                return TripleStatus.IN_KNOWLEDGE_BASE;
            }
            return TripleStatus.INCOMPLETE;
	    }
	    
	    /*
	     * This is not a good method but it would take too long to change it.
	     * The protege 4 method is cleaner but makes it a little hard to find the
	     * axioms until somebody tells you how.
	     */
	    private void handleGeneralizedConceptInclusions() {
	        if (subjFrame instanceof RDFSClass && ((RDFSClass) subjFrame).isAnonymous()
	                && possibleGCIPredicates.contains(predSlot)) {
	            OWLNamedClass axiom = owlModel.createOWLNamedClass(null);
	            FrameCreatorUtility.addOwnSlotValue(axiom, owlModel.getOWLEquivalentClassProperty(), subjFrame);
	            FrameCreatorUtility.createSubclassOf((Cls) subjFrame, axiom);
	            FrameCreatorUtility.createSubclassOf(axiom, (Cls) subjFrame);
	            subjFrame = axiom;
	            gciAxioms.add(axiom);  // need to name these later
	        }
	    }
	    
	    @SuppressWarnings("deprecation")
        private void addTriple() {
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
	    }
	    
	    private void addUndefTriple() {
	        TripleFrameCache.this.addUndefTriple(subj, pred, obj, objName, alreadyInUndef);
	    }
	}



	private void addUndefTriple(AResource subj, AResource pred, AResource obj, String undefName, boolean alreadyInUndef) {
		if (!alreadyInUndef) {
		    TripleStore activeTripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
			undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, obj, undefName)); //check this!!
		}		
	}

	public boolean processTriple(AResource subj, AResource pred, ALiteral lit, boolean alreadyInUndef) {
	    if (log.isLoggable(Level.FINER)) {
	        log.finer("Processing triple with literal: " + subj + " " + pred + " " + lit);
	    }

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
		//	FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, ((DefaultRDFSLiteral)rdfsLiteral).getRawValue());
		//}
		FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, AbstractOWLModel.convertRDFSLiteralToInternalFormat(rdfsLiteral));
		
		return true;
		
	}
	
	
	
	//reimplement this method

	//special treatment of RDFList. Move this to a utility class
	@SuppressWarnings("deprecation")
    private void createRDFList(String subjName, String predName, String objName) {
		
		Frame subjList = owlModel.getFrame(subjName);
		
		//applies both for rdf:fist and rdf:rest
		if (subjList == null) {
			//move this to a RDFListCreator
			FrameID id = new FrameID(subjName);
			
			Frame listFrame = FrameCreatorUtility.createFrameWithType(owlModel, id, RDF.List.getURI(), true);
			if (importing) {
			    listFrame.setIncluded(true); // ineffective in client-server or db mode
			}
			
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
		if (importing) {
		    frame.setIncluded(true); // doesn't have any effect in client-server or db mode
		}

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
		
		if (importing) { restriction.setIncluded(true); }
						
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
		if (importing) { logClass.setIncluded(true); }
						
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
	    processMetaClasses();
		processInferredSuperclasses();
		processClsesWithoutSupercls();
		processInstancesWithMultipleTypes();
		processCreatedProperties();
		processGeneralizedConceptInclusions();
		
		//dump what you have not processed:
		getUndefTripleManager().dumpUndefTriples(Level.FINE);
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
		
		log.info("Postprocess: Process classes without superclasses (" + superClsCache.getCachedFramesWithNoSuperclass().size() + " classes) ... ");
		
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

	@SuppressWarnings({ "deprecation", "unchecked" })
    private void processInferredSuperclasses(){
		long time0 = System.currentTimeMillis();
				
		OWLNamedClass owlClassClass = owlModel.getOWLNamedClassClass();
		
		log.info("Postprocess: Add inferred superclasses (" + owlModel.getInstanceCount(owlClassClass) + " classes) ... ");
		
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
	        FrameCreatorUtility.addDirectTypeAndSwizzle(instance, cls);
	    }

	}
	
	private void processCreatedProperties() {
	    for (RDFProperty property : createdProperties) {
	        if (property.getDirectDomain().isEmpty()) {
	            FrameCreatorUtility.addOwnSlotValue(property, owlModel.getSystemFrames().getDirectDomainSlot(), owlModel.getOWLThingClass());
	            FrameCreatorUtility.addOwnSlotValue(owlModel.getOWLThingClass(), owlModel.getSystemFrames().getDirectTemplateSlotsSlot(), property);
	        }
	    }
	}
	
	private void processGeneralizedConceptInclusions() {
	    // now try to give them a good name
	    NamespaceManager names = owlModel.getNamespaceManager();
	    String namespace = names.getDefaultNamespace();
	    if (namespace == null && owlModel.getDefaultOWLOntology() != null) {
	        namespace = owlModel.getDefaultOWLOntology().getName() + "#";
	    }
	    String axiomPrefix = namespace + "Axiom";
	    int counter = 0;

	    if (namespace != null) {
	        for (RDFSClass gci : gciAxioms) {
	            while (owlModel.getFrame(axiomPrefix + counter) != null) {
	                counter++;
	            }
	            gci = owlModel.getOWLNamedClass(gci.getName());
	            gci.rename(axiomPrefix + counter);
	        }
	    }
	}
	
	@SuppressWarnings("unchecked")
    private void processMetaClasses() {
	    Collection rdfsMetaClasses = new HashSet(owlModel.getRDFSNamedClassClass().getSubclasses(true));
	    Collection owlMetaClasses = new HashSet(owlModel.getOWLNamedClassClass().getSubclasses(true));
	    Collection deprecatedOwlMetaClasses = new HashSet(owlModel.getOWLDeprecatedClassClass().getSubclasses(true));
	    
	    rdfsMetaClasses.removeAll(owlMetaClasses);
	    rdfsMetaClasses.remove(owlModel.getOWLNamedClassClass());
	    rdfsMetaClasses.remove(owlModel.getOWLDeprecatedClassClass());
	    rdfsMetaClasses.remove(owlModel.getRDFSNamedClassClass());
	    owlMetaClasses.removeAll(deprecatedOwlMetaClasses);
	    owlMetaClasses.remove(owlModel.getOWLNamedClassClass());
	    deprecatedOwlMetaClasses.remove(owlModel.getOWLDeprecatedClassClass());
	 
	    addTypeToMetaClassInstances(rdfsMetaClasses, owlModel.getRDFSNamedClassClass());
	    addTypeToMetaClassInstances(owlMetaClasses, owlModel.getOWLNamedClassClass());
	    addTypeToMetaClassInstances(deprecatedOwlMetaClasses, owlModel.getOWLDeprecatedClassClass());
	}
	
	@SuppressWarnings("unchecked")
    private void addTypeToMetaClassInstances(Collection metaClasses, Cls type) {
	    for (Object o : metaClasses) {
	        Cls metaCls = (Cls) o;
	        for (Instance i : metaCls.getInstances()) {
	            if (!i.getDirectTypes().contains(type))
	                i.addDirectType(type);
	        }
	    }
	}
	
}
