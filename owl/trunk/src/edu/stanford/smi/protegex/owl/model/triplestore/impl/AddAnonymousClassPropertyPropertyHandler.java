package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class AddAnonymousClassPropertyPropertyHandler extends AbstractAddPropertyValueHandler {

    private RDFProperty directInstancesSlot;

    private RDFProperty directTypesSlot;

    private Class javaType;

    private KnowledgeBase kb;

    private Cls newType;

    private TripleStoreModel tripleStoreModel;


    AddAnonymousClassPropertyPropertyHandler(ProtegeTripleAdder adder,
                                             Cls newType,
                                             Class javaType,
                                             TripleStoreModel tripleStoreModel) {
        super(adder);
        this.javaType = javaType;
        this.kb = newType.getKnowledgeBase();
        this.newType = newType;
        this.tripleStoreModel = tripleStoreModel;
        KnowledgeBase kb = newType.getKnowledgeBase();
        directInstancesSlot = new DefaultRDFProperty(kb, Model.SlotID.DIRECT_INSTANCES);
        directTypesSlot = new DefaultRDFProperty(kb, Model.SlotID.DIRECT_TYPES);
    }


    public void handleAdd(RDFResource subject, Object object) {
        removeDirectTypes(subject);
        if (subject.getClass() != javaType) {
            try {
                subject = (RDFResource) javaType.getConstructor(new Class[]{
                        KnowledgeBase.class,
                        FrameID.class
                }).newInstance(new Object[]{
                        kb,
                        ((Frame) subject).getFrameID()
                });
            }
            catch (Exception ex) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            }
            tripleStoreModel.replaceJavaObject(subject);
            // ProtegeOWLParser.resourceName2Frame.put(ProtegeOWLParser.currentNode, subject);
        }
        if (adder.addValue(subject, directTypesSlot, newType)) {
            adder.addValueFast(newType, directInstancesSlot, subject);
        }
    }


    private void removeDirectTypes(RDFResource subject) {
        for (Iterator it = tripleStoreModel.getTripleStores().iterator(); it.hasNext();) {
            TripleStore tripleStore = (TripleStore) it.next();
            for (Iterator vit = tripleStore.listObjects(subject, directTypesSlot); vit.hasNext();) {
                RDFResource type = (RDFResource) vit.next();
                tripleStore.remove(subject, directTypesSlot, type);
                tripleStore.remove(type, directInstancesSlot, subject);
            }
        }
    }
}
