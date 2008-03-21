package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

class TriplePostProcessor extends AbstractStatefulTripleProcessor {
	private static final transient Logger log = Log.getLogger(TriplePostProcessor.class);
	
	public TriplePostProcessor(TripleProcessor processor) {
		super(processor);
	}

	
	/***************************************
	 *  Post-processsing per triplestore
	 ***************************************/
	
	/*
	 * When the post-processing methods per triplestore are called,
	 * the just-parsed triplestore is active.
	 * So, everything is written in the right (just parsed) triplestore.
	 */
	
	@SuppressWarnings("deprecation")
	public void processMetaclasses() {		
		RDFSNamedClass rdfsClass = owlModel.getRDFSNamedClassClass();
		
		log.info("Postprocess: Process metaclasses (" + rdfsClass.getSubclassCount() + " classes) ... ");
		long time0 = System.currentTimeMillis();
		
		for (Iterator iterator = rdfsClass.getSubclasses(true).iterator(); iterator.hasNext();) {
			RDFSNamedClass metaclass = (RDFSNamedClass) iterator.next();
		
			if (!metaclass.isSystem()) {
				for (Iterator iterator2 = metaclass.getInstances(false).iterator(); iterator2.hasNext();) {					
					Instance inst = (Instance) iterator2.next();
					ParserUtil.getSimpleFrameStore(owlModel).swizzleInstance(inst);
				}
			}
		}
		
		log.info("(" + (System.currentTimeMillis() - time0) + " ms)");
	}

	public void processSubclassesOfRdfList(){
		RDFSNamedClass rdfListCls = owlModel.getRDFListClass();
				
		log.info("Postprocess: Process subclasses of rdf:List (" + rdfListCls.getSubclassCount() + " classes) ... ");
		long time0 = System.currentTimeMillis();
		
		for (Iterator iterator = rdfListCls.getSubclasses(true).iterator(); iterator.hasNext();) {
			RDFSNamedClass listCls = (RDFSNamedClass) iterator.next();
		
			if (!listCls.isSystem()) {
				for (Iterator iterator2 = listCls.getInstances(false).iterator(); iterator2.hasNext();) {
					Instance inst = (Instance) iterator2.next();
					ParserUtil.getSimpleFrameStore(owlModel).swizzleInstance(inst);
				}
			}
		}
		
		log.info("(" + (System.currentTimeMillis() - time0) + " ms)");
	}
	
	
	public void processClsesWithoutSupercls(SuperClsCache superClsCache) {
		log.info("Postprocess: Process classes without superclasses (" + superClsCache.getCachedFramesWithNoSuperclass().size() + " classes) ... ");
		long time0 = System.currentTimeMillis();
		
		for (Iterator<Frame> iter = superClsCache.getCachedFramesWithNoSuperclass().iterator(); iter.hasNext();) {
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


	@SuppressWarnings({"unchecked", "deprecation"})
	public void processInferredSuperclasses(SuperClsCache superClsCache){
		OWLNamedClass owlClassClass = owlModel.getOWLNamedClassClass();

		log.info("Postprocess: Add inferred superclasses (" + owlModel.getInstanceCount(owlClassClass) + " classes) ... ");
		long time0 = System.currentTimeMillis();

		for (Iterator iterator = owlClassClass.getInstances().iterator(); iterator.hasNext();) {
			Object obj = iterator.next();

			try {
				OWLNamedClass namedClass = (OWLNamedClass) obj;
				Collection<Cls> inferredSuperclasses = getInferredSuperClasses(namedClass);
								
				if (inferredSuperclasses.size() > 0) {
					superClsCache.removeFrame(namedClass);
				}
				
				for (Cls inferredSupercls : inferredSuperclasses) {
					if (!FrameCreatorUtility.hasSuperclass(namedClass, inferredSupercls)) {
						FrameCreatorUtility.createSubclassOf(namedClass, inferredSupercls);
					}
				}

			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING, " Error at processing " + obj, e);
			}			
		}
		
		//TODO: this should be done only at the very end
		//if at the end there are classes that do not have a parent, add them under owl:Thing
		for (Iterator iterator = superClsCache.getCachedFramesWithNoSuperclass().iterator(); iterator.hasNext();) {
			Frame cls = (Frame) iterator.next();
			if (cls instanceof RDFSNamedClass) {
				if (!FrameCreatorUtility.hasSuperclass((RDFSNamedClass)cls, owlModel.getOWLThingClass())) {
					FrameCreatorUtility.createSubclassOf((RDFSNamedClass)cls, owlModel.getOWLThingClass());
					iterator.remove();					
				}
			} else {
				if (log.isLoggable(Level.FINE)) {
					log.fine("Class without parent: " + cls);
				}
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


	public void processInstancesWithMultipleTypes(MultipleTypesInstanceCache multipleTypesInstanceCache) {
		Set<Instance> instancesWithMultipleTypes = multipleTypesInstanceCache.getInstancesWithMultipleTypes();

		log.info("Postprocess: Instances with multiple types (" + instancesWithMultipleTypes.size() + " instances) ... ");
		long time0 = System.currentTimeMillis();

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

		for (Cls type : typesSet) {
			FrameCreatorUtility.addDirectTypeAndSwizzle(instance, type);
		}

	}

	//TODO: reimplement this to work with the NFS (maybe..)
	@SuppressWarnings("deprecation")
	public void processDomainAndRange() {
		for (Iterator iterator = owlModel.getUserDefinedRDFProperties().iterator(); iterator.hasNext();) {
			RDFProperty property = (RDFProperty) iterator.next();
			//domain
			if (property.getDirectDomain().isEmpty()) {
				FrameCreatorUtility.addOwnSlotValue(property, owlModel.getSystemFrames().getDirectDomainSlot(), owlModel.getOWLThingClass());
				FrameCreatorUtility.addOwnSlotValue(owlModel.getOWLThingClass(), owlModel.getSystemFrames().getDirectTemplateSlotsSlot(), property);
			}		
			//range
			owlModel.getFrameStoreManager().getRangeUpdateFrameStore().synchronizeRDFRangeWithProtegeAllowedValues(property);
		}
	}

	public void processGeneralizedConceptInclusions(Collection<RDFSClass> gciAxioms) {
		log.info("Postprocess: Generalized Concept Inclusion (" + gciAxioms.size() + " axioms) ... ");
		long time0 = System.currentTimeMillis();

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
				while (getFrame(axiomPrefix + counter) != null) {
					counter++;
				}
				gci = owlModel.getOWLNamedClass(gci.getName());
				gci.rename(axiomPrefix + counter);
			}
		}
		
		log.info("(" + (System.currentTimeMillis() - time0) + " ms)");
	}


}
