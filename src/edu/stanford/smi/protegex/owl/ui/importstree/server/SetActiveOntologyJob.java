package edu.stanford.smi.protegex.owl.ui.importstree.server;

import java.lang.reflect.Proxy;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;

public class SetActiveOntologyJob extends ProtegeJob {
    public static String ALLOW_MULTIUSER_SET_ACTIVE_ONTOLOGY = "allow.multiuser.client.set.active.ontology";
    private OWLOntology ontology;
    
    public SetActiveOntologyJob(OWLModel owlModel, OWLOntology ontology) {
        super(owlModel);
        this.ontology = ontology;
    }

    @Override
    public Boolean run() throws ProtegeException {
        OWLModel owlModel = (OWLModel) getKnowledgeBase();
        
        boolean isAllowed = ApplicationProperties.getBooleanProperty(ALLOW_MULTIUSER_SET_ACTIVE_ONTOLOGY, false);
        if (!isAllowed || !((DefaultOWLOntology) ontology).isAssociatedTriplestoreEditable()) {
            return false;
        }
        
        FrameStoreManager fsm = owlModel.getFrameStoreManager();
        ActiveOntologyFrameStoreHandler activeOntologyHandler = fsm.getFrameStoreFromClass(ActiveOntologyFrameStoreHandler.class);
        if (activeOntologyHandler == null) {
            activeOntologyHandler = new ActiveOntologyFrameStoreHandler(owlModel);
            FrameStore toInsert = (FrameStore) Proxy.newProxyInstance(OWLModel.class.getClassLoader(), 
                                                                      new Class [] { FrameStore.class },
                                                                      activeOntologyHandler);
            fsm.insertFrameStore(toInsert, 1);
        }
        activeOntologyHandler.setActiveOntology(ontology);
        return true;
    }
    
    @Override
    public Boolean execute() throws ProtegeException {
        return (Boolean) super.execute();
    }
    
    @Override
    public void localize(KnowledgeBase kb) {
        super.localize(kb);
        if (ontology != null) {
            LocalizeUtils.localize(ontology, kb);
        }
    }

}
