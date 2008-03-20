package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDataRange;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLSystemFrames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDifferentIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLSameIndividualAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLVariable;

public class FrameCreatorUtility {
	private static transient Logger log = Log.getLogger(FrameCreatorUtility.class);

    public static Frame createFrameWithType(OWLModel owlModel, FrameID id, String typeUri) {
        Frame frame = ((KnowledgeBase) owlModel).getFrame(id);
        OWLSystemFrames systemFrames = owlModel.getSystemFrames();

        if (frame != null)
            return frame;

        Frame type = owlModel.getFrame(typeUri);

        if (type == null)
            return null;

        //write here all the java class names

        //maybe remove the anon condition
        if (typeUri.equals(OWL.Ontology.getURI())) {
            frame = new DefaultOWLOntology(owlModel, id );                          
        }
        else if (typeUri.equals(OWL.Class.getURI())) {
            frame = new DefaultOWLNamedClass(owlModel, id );                           
        } else if (typeUri.equals(OWL.DeprecatedClass.getURI())) {
        	frame = new DefaultOWLNamedClass(owlModel, id );
        }
        else if (typeUri.equals(OWL.DatatypeProperty.getURI())) {
            frame = new DefaultOWLDatatypeProperty(owlModel, id);
        }
        else if (typeUri.equals(OWL.ObjectProperty.getURI())) {
            frame = new DefaultOWLObjectProperty(owlModel, id);
        }
        else if (typeUri.equals(OWL.TransitiveProperty.getURI())) {
            frame = new DefaultOWLObjectProperty(owlModel, id);                     
        }
        else if (typeUri.equals(OWL.SymmetricProperty.getURI())) {
            frame = new DefaultOWLObjectProperty(owlModel, id);                     
        }
        else if (typeUri.equals(OWL.AnnotationProperty.getURI())) {
            frame = new DefaultRDFProperty(owlModel, id);  //should this be abstract owl prop?
        }
        else if (typeUri.equals(OWL.InverseFunctionalProperty.getURI())) {
            frame = new DefaultOWLObjectProperty(owlModel, id);                     
        }
        else if (typeUri.equals(OWL.FunctionalProperty.getURI())) {
            frame = new DefaultRDFProperty(owlModel, id);                   
            ((DefaultRDFProperty)frame).setFunctional(true);                
        }
        else if (typeUri.equals(OWL.AllDifferent.getURI())) {
            frame = new DefaultOWLAllDifferent(owlModel, id);
        }
        else if (typeUri.equals(OWL.DataRange.getURI())) {
            frame = new DefaultOWLDataRange(owlModel, id);
        }
        else if (typeUri.equals(RDF.Property.getURI())) {
            frame = new DefaultRDFProperty(owlModel, id);
        }  else if (typeUri.equals(OWL.DeprecatedProperty.getURI())) {
        	//is this correct? Hopefully it will be swizzled later, if needed
        	frame = new DefaultRDFProperty(owlModel, id);
        }
        else if (typeUri.equals(RDF.List.getURI())) {
            frame = new DefaultRDFList(owlModel, id);
        } 
        else if (typeUri.equals(RDFS.Class.getURI())) {
            frame = new DefaultRDFSNamedClass(owlModel, id);
        }
        else if (typeUri.equals(RDFS.Datatype.getURI()))  {
            frame = new DefaultRDFSDatatype(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.ATOM_LIST)) {
            frame = new DefaultSWRLAtomList(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.BUILTIN_ATOM)) {
            frame = new DefaultSWRLBuiltinAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.BUILTIN)) {
            frame = new DefaultSWRLBuiltin(owlModel, id);
        }               
        else if (typeUri.equals(SWRLNames.Cls.CLASS_ATOM)) {
            frame = new DefaultSWRLClassAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.DATA_RANGE_ATOM)) {
            frame = new DefaultSWRLDataRangeAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM)) {
            frame = new DefaultSWRLDatavaluedPropertyAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM)) {
            frame = new DefaultSWRLDifferentIndividualsAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.IMP)) {
            frame = new DefaultSWRLImp(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM)) {
            frame = new DefaultSWRLIndividualPropertyAtom(owlModel, id);
        }   
        else if (typeUri.equals(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM)) {
            frame = new DefaultSWRLSameIndividualAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.VARIABLE)) {
            frame = new DefaultSWRLVariable(owlModel, id);
        }

        else {
            //maybe this is an RDF individual
            frame = new DefaultOWLIndividual(owlModel, id);
        }
        
        frame.assertFrameName();
    	
        if (log.getLevel() == Level.FINE) {
    		log.fine("Created frame: " + frame);
    	}
        
        if (!hasDirectType((Instance)frame, (Cls)type)) {
        	if (log.getLevel() == Level.FINE) {
        		log.fine("Adding direct type to " + frame + " type: " + type);
        	}
        	addInstanceType((Instance)frame, (Cls)type);
        	addOwnSlotValue(frame, systemFrames.getRdfTypeProperty(), type);
        }

        return frame;

    }


    public static boolean addInstanceType(Instance inst, Cls type) {
        if (inst == null || type == null) {                     
            return false;
        }

        Slot typeSlot = inst.getKnowledgeBase().getSystemFrames().getDirectTypesSlot();
        addOwnSlotValue(inst, typeSlot , type);
        addOwnSlotValue(type, inst.getKnowledgeBase().getSystemFrames().getDirectInstancesSlot(), inst);

        return true;
    }

    public static boolean removeInstanceType(Instance inst, Cls type) {
        Slot typeSlot = inst.getKnowledgeBase().getSystemFrames().getDirectTypesSlot();
        ParserUtil.getSimpleFrameStore(inst).removeDirectOwnSlotValue(inst, typeSlot , type);
        ParserUtil.getSimpleFrameStore(type).removeDirectOwnSlotValue(type, inst.getKnowledgeBase().getSystemFrames().getDirectInstancesSlot(), inst);
        return true;
    }

    
    public static boolean hasDirectType(Instance inst, Cls type) {
    	return ParserUtil.getSimpleFrameStore(inst).getDirectTypes(inst).contains(type);
    }
    
    
    public static boolean createSubclassOf(Cls cls, Cls superCls) {
        if (cls == null || superCls == null) {
            //Log.getLogger().warning("Error at creating subclass of relationship. Cls: " + cls + " Superclass: " + superCls);
            return false;
        }
        ParserUtil.getSimpleFrameStore(cls).addDirectSuperclass(cls, superCls);
        return true;
    }

    public static boolean createSubpropertyOf(Slot slot, Slot superSlot) {
        if (slot == null || superSlot == null) {
            return false;
        }
        ParserUtil.getSimpleFrameStore(slot).addDirectSuperslot(slot, superSlot);

        return true;
    }

    public static boolean addOwnSlotValue(Frame frame, Slot slot, Object value) {
        if (frame == null || slot == null) {
            return false;
        }
        ParserUtil.getSimpleFrameStore(frame).addDirectOwnSlotValue(frame, slot, value);
        return true;
    }

    public static Collection<Cls> getDirectTypes(Instance instance) {
        return ParserUtil.getSimpleFrameStore(instance).getDirectTypes(instance);
    }

    public static boolean addDirectTypeAndSwizzle(Instance instance, Cls type) {
    	//ParserUtil.getSimpleFrameStore(instance).addDirectType(instance, type);
        instance.addDirectType(type);
        return true;
    }

}
