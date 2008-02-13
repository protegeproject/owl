package edu.stanford.smi.protegex.owl.jena.creator;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.factory.AbstractOwlProjectCreator;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.menu.OWLMenuProjectPlugin;

public class NewOwlProjectCreator extends AbstractOwlProjectCreator {
    private String ontologyName;
    
    public NewOwlProjectCreator() {
        this(new JenaKnowledgeBaseFactory());
    }
    
    public NewOwlProjectCreator(JenaKnowledgeBaseFactory factory) {
        super(factory);
    }

    @Override
    public Project create() {
        Collection errors = new ArrayList();
        Project project = Project.createNewProject(factory, errors);
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        
        if (ontologyName == null) {
            ontologyName = ProtegeNames.DEFAULT_DEFAULT_BASE;
        }
        owlModel.getNamespaceManager().setDefaultNamespace(ontologyName + "#");
        String defaultOntologyName = ontologyName;
        if (defaultOntologyName.endsWith("#")) {
            defaultOntologyName = defaultOntologyName.substring(0, defaultOntologyName.length() - 1);
        }       
        owlModel.getSystemFrames().getOwlOntologyClass().createInstance(defaultOntologyName);
        
        TripleStoreModel  tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
        activeTripleStore.setOriginalXMLBase(defaultOntologyName);
        activeTripleStore.setName(defaultOntologyName);
        owlModel.resetOntologyCache();
        
        addViewSettings(project.getSources());
        
        return project;
    }

    /*
     * ---------------------------------------------------------------------
     * setters and getters
     */
    
    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }
    
    

}
