package edu.stanford.smi.protegex.owl.model.util;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;

import java.util.*;

/**
 * A utility visitor class to copy resources by their slot values.
 * This class is used to generate replicas of a class description
 * and will only copy anonymous classes, leaving references to named
 * classes, properties and individuals. Slots that are NOT copied include
 * all sub/superclass slots and several others including :NAME
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         10-Jan-2006
 */
public class ResourceCopier extends OWLModelVisitorAdapter {

    private List copyStack = new LinkedList();

    protected static String[] doNotCopySlots = {
            Model.Slot.DIRECT_SUBCLASSES,
            Model.Slot.DIRECT_SUPERCLASSES,
            RDFSNames.Slot.SUB_CLASS_OF,
            OWLNames.Slot.EQUIVALENT_CLASS,
            Model.Slot.NAME
            //Model.Slot.DIRECT_TEMPLATE_SLOTS,
            //Model.Slot.DIRECT_TYPES,
            //RDFNames.Slot.TYPE,
            //Model.Slot.DIRECT_INSTANCES
    };

    /**
     * Returns the last copy of the last visited resource.  Note
     * that repeatedly calling this method does not create
     * multiple copies.
     */
    public RDFResource getCopy() {
        return (RDFResource) copyStack.get(copyStack.size() - 1);
    }



    //////////////////////////////////// DO NOT copy the below - give a reference

    public void visitOWLNamedClass(OWLNamedClass source) {
        visitResourceToBeReferenced(source);
    }

    public void visitOWLIndividual(OWLIndividual source) {
        visitResourceToBeReferenced(source);
    }

    public void visitOWLObjectProperty(OWLObjectProperty source) {
        visitResourceToBeReferenced(source);
    }

    public void visitOWLDatatypeProperty(OWLDatatypeProperty source) {
        visitResourceToBeReferenced(source);
    }

    public void visitRDFSNamedClass(RDFSNamedClass source) {
        visitResourceToBeReferenced(source);
    }

    public void visitRDFIndividual(RDFIndividual source) {
        visitResourceToBeReferenced(source);
    }

    public void visitRDFProperty(RDFProperty source) {
        visitResourceToBeReferenced(source);
    }

    public void visitRDFDatatype(RDFSDatatype source) {
        visitResourceToBeReferenced(source);
    }

    protected void visitResourceToBeReferenced(RDFResource source){
        if (!source.isSystem()){

            OWLModel owlModel = source.getOWLModel();

            TripleStoreModel tsm = owlModel.getTripleStoreModel();

            TripleStore ts = tsm.getHomeTripleStore(source);
            TripleStore activeTs = tsm.getActiveTripleStore();

            if (activeTs != ts){

                /* @@TODO the following should operate on the ts.getName()
                   but this does always return the ontology name (xml:base) */

                String ns = ts.getDefaultNamespace();
                String homeOntologyName = ns.substring(0, ns.length()-1);

                ns = activeTs.getDefaultNamespace();
                String activeOntologyName = ns.substring(0, ns.length()-1);

                OWLOntology activeOnt = owlModel.getOWLOntologyByURI(activeOntologyName);

                if (!activeOnt.getImports().contains(homeOntologyName)){
                    activeOnt.addImports(homeOntologyName);
                }
            }
        }

        copyStack.add(source);
    }

    public void visitRDFSLiteral(RDFSLiteral source) {
        copyStack.add(source);
    }

    public void visitRDFUntypedResource(RDFUntypedResource source) {
        copyStack.add(source);
    }

    //////////////////////////////////// DO copy below

    /////////////////////////////////// anonymous classes

    public void visitOWLUnionClass(OWLUnionClass source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLUnionClass());
    }

    public void visitOWLIntersectionClass(OWLIntersectionClass source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLIntersectionClass());
    }

    public void visitOWLEnumeratedClass(OWLEnumeratedClass source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLEnumeratedClass());
    }

    public void visitOWLComplementClass(OWLComplementClass source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLComplementClass());
    }

    /////////////////////////////////// restrictions

    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLSomeValuesFrom());
    }

    public void visitOWLAllValuesFrom(OWLAllValuesFrom source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLAllValuesFrom());
    }

    public void visitOWLCardinality(OWLCardinality source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLCardinality());
    }

    public void visitOWLMaxCardinality(OWLMaxCardinality source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLMaxCardinality());
    }

    public void visitOWLMinCardinality(OWLMinCardinality source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLMinCardinality());
    }

    public void visitOWLHasValue(OWLHasValue source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLHasValue());
    }

    /////////////////////////////////// rdf components

    public void visitRDFList(RDFList source) {
        if (source == source.getOWLModel().getRDFNil()) {
            copyStack.add(source);
        }
        else {
            copyMultipleSlotValues(source, source.getOWLModel().createRDFList());
        }
    }

    /////////////////////////////////// other

    public void visitOWLAllDifferent(OWLAllDifferent source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLAllDifferent());
    }

    public void visitOWLDataRange(OWLDataRange source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLDataRange());
    }

    public void visitOWLOntology(OWLOntology source) {
        String newName = null;
        int i = 2;
        do {
            newName = source.getName() + i;
            i++;
        }
        while (source.getOWLModel().getRDFResource(newName) != null);
        copyMultipleSlotValues(source, source.getOWLModel().createOWLOntology(newName));
    }

    /////////////////////////////////// Utility methods


    public void copyMultipleSlotValues(RDFResource source, RDFResource target) {
        KnowledgeBase kb = source.getOWLModel();

        Collection doNotCopySlotsList = new ArrayList();
        for (int i = 0; i < doNotCopySlots.length; i++) {
            doNotCopySlotsList.add(kb.getSlot(doNotCopySlots[i]));
        }

        // TODO: I think this could be replaced with kb.getOwnSlots(source).iterator() to
        // get rid of the deprecation warning.
        Iterator slots = source.getOwnSlots().iterator();
        while (slots.hasNext()) {
            Slot slot = (Slot) slots.next();
            if (!doNotCopySlotsList.contains(slot)) {
                copySlotValues(source, target, slot);
            }
        }
        copyStack.add(target);
    }

    public void copySlotValues(RDFResource source, RDFResource target, Slot slot) {
        Collection values = source.getDirectOwnSlotValues(slot);
        if ((values != null) && (values.size() > 0)) {
            // check if values themselves need to be cloned
            Collection newvalues = new LinkedList();
            for (Iterator i = values.iterator(); i.hasNext();) {
                Object value = i.next();
                if (value instanceof RDFResource) {
                    ((RDFResource) value).accept(this);
                    value = (RDFResource) copyStack.get(copyStack.size() - 1);
                }
                newvalues.add(value);
            }
            target.setDirectOwnSlotValues(slot, newvalues);
        }
    }
}
