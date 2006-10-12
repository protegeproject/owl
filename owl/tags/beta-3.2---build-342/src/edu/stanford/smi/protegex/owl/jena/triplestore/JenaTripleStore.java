package edu.stanford.smi.protegex.owl.jena.triplestore;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.InMemoryFrameDb;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.Record;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.AbstractTripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;

import java.util.*;

/**
 * A TripleStore that acts as a view on an existing NarrowFrameStore.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaTripleStore extends AbstractTripleStore {


    public JenaTripleStore(OWLModel owlModel, NarrowFrameStore frameStore, TripleStoreModel tripleStoreModel) {
        super(owlModel, tripleStoreModel, frameStore);
    }


    public boolean equals(Object obj) {
        if (obj instanceof JenaTripleStore) {
            return frameStore.getName().equals(((JenaTripleStore) obj).frameStore.getName());
        }
        else {
            return false;
        }
    }


    public Iterator listTriples() {
	    // TODO: This could be optimised so that a custom Iterator is used.
        KnowledgeBase kb = owlModel;
        Collection<Slot> ignoreProperties = new HashSet<Slot>();
        ignoreProperties.add(owlModel.getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES));
        ignoreProperties.add(kb.getSlot(Model.Slot.DIRECT_INSTANCES));
        ignoreProperties.add(kb.getSlot(Model.Slot.DIRECT_TYPES));
        ignoreProperties.add(kb.getSlot(ProtegeNames.Slot.CLASSIFICATION_STATUS));
        ignoreProperties.add(kb.getSlot(ProtegeNames.Slot.INFERRED_SUBCLASSES));
        ignoreProperties.add(kb.getSlot(ProtegeNames.Slot.INFERRED_SUPERCLASSES));
        ignoreProperties.add(kb.getSlot(ProtegeNames.Slot.INFERRED_TYPE));
        List triples = new ArrayList();
        Collection records = ((InMemoryFrameDb) frameStore).getRecords();
        for (Iterator it = records.iterator(); it.hasNext();) {
            Record record = (Record) it.next();
            Frame subject = record.getFrame();
            if (subject instanceof RDFResource) {
                Slot predicate = record.getSlot();
                // System.out.println("-- " + subject.getName() + " . " + predicate.getName());
                if (predicate instanceof RDFProperty) {
                    if (record.getFacet() == null && !record.isTemplate() && !ignoreProperties.contains(predicate)) {
                        List values = record.getValues();
                        for (Iterator vit = values.iterator(); vit.hasNext();) {
                            Object object = vit.next();
                            if (object instanceof String && DefaultRDFSLiteral.isRawValue((String) object)) {
                                object = new DefaultRDFSLiteral(owlModel, (String) object);
                            }
                            Triple triple = new DefaultTriple((RDFResource) subject, (RDFProperty) predicate, object);
                            triples.add(triple);
                        }
                    }
                }
            }
        }
        return triples.iterator();
    }
}
